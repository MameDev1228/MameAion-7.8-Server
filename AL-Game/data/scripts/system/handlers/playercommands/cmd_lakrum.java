package playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.WorldPlayTimeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class cmd_lakrum extends PlayerCommand {
    public cmd_lakrum() { super("lakrum"); }

    @Override
    public void execute(Player player, String... params) {
        TeleportService2.teleportTo(player, 800050000, 1050.0000f, 551.0000f, 304.0000f, (byte) 90);
        WorldPlayTimeService.ensureTimelessTime(player);
        PacketSendUtility.sendMessage(player, "ラクルムへ移動しました。砂時計時間は消費されません。");
    }

    @Override
    public void onFail(Player player, String message) {}
}
