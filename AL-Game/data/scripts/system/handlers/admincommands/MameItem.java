package admincommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.AdminService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * MameAion GM helper for granting pre-enchanted items.
 *
 * Syntax:
 *   //mameitem <itemId|itemLink> [count] [enchant]
 *   //mameitem <player> <itemId|itemLink> [count] [enchant]
 */
public class MameItem extends AdminCommand {

    private static final Pattern ITEM_ID_PATTERN = Pattern.compile("(\\d{9})");

    public MameItem() {
        super("mameitem");
    }

    @Override
    public void execute(Player admin, String... params) {
        if (params.length < 1) {
            onFail(admin, null);
            return;
        }

        Player receiver = admin;
        int itemIndex = 0;

        if (!looksLikeItem(params[0]) && params.length >= 2) {
            Player found = World.getInstance().findPlayer(Util.convertName(params[0]));
            if (found != null) {
                receiver = found;
                itemIndex = 1;
            }
        }

        int itemId;
        long count = 1L;
        int enchantLevel = 0;

        try {
            itemId = parseItemId(params[itemIndex]);
            if (params.length > itemIndex + 1) {
                count = Long.parseLong(params[itemIndex + 1]);
            }
            if (params.length > itemIndex + 2) {
                enchantLevel = parseEnchant(params[itemIndex + 2]);
            }
        } catch (Exception e) {
            PacketSendUtility.sendMessage(admin, "Invalid mameitem parameter.");
            onFail(admin, null);
            return;
        }

        if (count < 1) {
            PacketSendUtility.sendMessage(admin, "Count must be >= 1.");
            return;
        }
        if (enchantLevel < 0) {
            PacketSendUtility.sendMessage(admin, "Enchant level must be >= 0.");
            return;
        }
        if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null) {
            PacketSendUtility.sendMessage(admin, "Item id is incorrect: " + itemId);
            return;
        }
        if (!AdminService.getInstance().canOperate(admin, receiver, itemId, "command //mameitem")) {
            return;
        }

        long notAdded = enchantLevel > 0
            ? ItemService.addItemAndEnchant(receiver, itemId, count, enchantLevel)
            : ItemService.addItem(receiver, itemId, count);

        if (notAdded == 0) {
            String msg = "MameItem granted: " + count + " x [item:" + itemId + "]" + (enchantLevel > 0 ? " +" + enchantLevel : "") + " to " + receiver.getName() + ".";
            PacketSendUtility.sendMessage(admin, msg);
            if (receiver != admin) {
                PacketSendUtility.sendMessage(receiver, "GM granted: " + count + " x [item:" + itemId + "]" + (enchantLevel > 0 ? " +" + enchantLevel : "") + ".");
            }
        } else {
            PacketSendUtility.sendMessage(admin, "Item couldn't be fully added. notAdded=" + notAdded);
        }
    }

    private static boolean looksLikeItem(String value) {
        return value != null && (value.matches("\\d{9}") || value.contains("[item:") || value.contains("[@item:"));
    }

    private static int parseItemId(String value) {
        Matcher matcher = ITEM_ID_PATTERN.matcher(value);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return Integer.parseInt(value);
    }

    private static int parseEnchant(String value) {
        if (value.startsWith("+")) {
            value = value.substring(1);
        }
        return Integer.parseInt(value);
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "syntax //mameitem <itemId | itemLink> [count] [enchant]");
        PacketSendUtility.sendMessage(player, "syntax //mameitem <player> <itemId | itemLink> [count] [enchant]");
        PacketSendUtility.sendMessage(player, "example //mameitem 100000000 1 15");
        PacketSendUtility.sendMessage(player, "example //mameitem Mame 100000000 1 +15");
    }
}
