package playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.WorldPlayTimeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class cmd_demaha extends PlayerCommand {
    public cmd_demaha() { super("demaha"); }

    @Override
    public void execute(Player player, String... params) {
        TeleportService2.teleportTo(player, 800060000, 943.0000f, 1625.0000f, 663.0000f, (byte) 59);
        WorldPlayTimeService.ensureTimelessTime(player);
        PacketSendUtility.sendMessage(player, "ドマハへ移動しました。砂時計時間は消費されません。");
    }

    @Override
    public void onFail(Player player, String message) {}
}
