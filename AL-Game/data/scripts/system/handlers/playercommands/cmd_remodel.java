package playercommands;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.services.item.ItemRemodelService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class cmd_remodel extends PlayerCommand
{
    public cmd_remodel() {
        super("remodel");
    }
	
    public void executeCommand(Player player, String[] params) {
        if (params.length < 1 || params[0] == "") {
            PacketSendUtility.sendSys3Message(player, "\uE005", "Syntax: .remodel (Example: .remodel 110900443");
            return;
        } if (params.length == 1) {
            int itemId = Integer.parseInt(params[0]);
            if (!CustomConfig.MAME_REMODEL_FREE && !player.getInventory().decreaseByItemId(186020069, 5)) { // Ultimate Blood Mark.
                PacketSendUtility.sendSys3Message(player, "\uE005", "You need 5 <Ultimate Blood Mark> for remodel, can be found in Crimson Katalam quest reward");
                return;
            }
            if (remodelItem(player, itemId)) {
                PacketSendUtility.sendMessage(player, CustomConfig.MAME_REMODEL_FREE ? "Remodel complete. Cost: free." : "Successfully remodelled an item of the player!");
                PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedItemsWithoutStigma()), true);
            } else {
                PacketSendUtility.sendMessage(player, "Was not able to remodel an item of the player!");
            }
        }
    }
	
    private boolean remodelItem(Player player, int itemId) {
        ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(itemId);
        if (template == null) {
            return false;
        }
        Equipment equip = player.getEquipment();
        if (equip == null) {
            return false;
        } for (Item item : equip.getEquippedItemsWithoutStigmaOld()) {
            if (item.getItemTemplate().isWeapon()) {
                if (item.getItemTemplate().getWeaponType() == template.getWeaponType()) {
                    ItemRemodelService.systemRemodelItem(player, item, template);
                    PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
                    DAOManager.getDAO(InventoryDAO.class).store(item, player);
                    return true;
                }
            } else if (item.getItemTemplate().isArmor()) {
                if (item.getItemTemplate().getItemSlot() == template.getItemSlot()) {
                    ItemRemodelService.systemRemodelItem(player, item, template);
                    PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
                    DAOManager.getDAO(InventoryDAO.class).store(item, player);
                    return true;
                }
            }
        }
        return false;
    }
	
    @Override
    public void execute(Player player, String... params) {
        executeCommand(player, params);
    }
}