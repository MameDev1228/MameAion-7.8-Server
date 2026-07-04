package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

public class Tpme extends AdminCommand {

    public Tpme() {
        super("tpme");
    }

    @Override
    public void execute(Player admin, String... params) {
        if (params == null || params.length < 1) {
            PacketSendUtility.sendMessage(admin, "syntax //tpme <playername>");
            return;
        }
        Player target = findPlayer(params[0]);
        if (target == null) {
            PacketSendUtility.sendMessage(admin, "Player is not online: " + params[0]);
            return;
        }
        TeleportService2.teleportTo(target, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading());
        PacketSendUtility.sendMessage(admin, "Teleported " + target.getName() + " to you.");
        PacketSendUtility.sendMessage(target, "You have been teleported by a GM.");
    }

    private Player findPlayer(String name) {
        Player player = World.getInstance().findPlayer(name);
        return player != null ? player : World.getInstance().findPlayer(Util.convertName(name));
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "syntax //tpme <playername>");
    }
}
