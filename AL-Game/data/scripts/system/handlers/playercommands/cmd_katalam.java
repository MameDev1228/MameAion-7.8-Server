package playercommands;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.WorldPlayTimeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class cmd_katalam extends PlayerCommand {
    public cmd_katalam() { super("katalam"); }

    @Override
    public void execute(Player player, String... params) {
        if (player.getRace() == Race.ELYOS) {
            TeleportService2.teleportTo(player, 800030000, 399.0000f, 2718.0000f, 142.0000f, (byte) 55);
        } else {
            TeleportService2.teleportTo(player, 800030000, 363.0000f, 385.0000f, 281.0000f, (byte) 116);
        }
        WorldPlayTimeService.ensureTimelessTime(player);
        PacketSendUtility.sendMessage(player, "カタラムへ移動しました。砂時計時間は消費されません。");
    }

    @Override
    public void onFail(Player player, String message) {}
}
