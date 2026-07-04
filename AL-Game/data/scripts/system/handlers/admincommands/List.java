package admincommands;

import java.util.Iterator;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

public class List extends AdminCommand {

    public List() {
        super("list");
    }

    @Override
    public void execute(Player admin, String... params) {
        int count = 0;
        PacketSendUtility.sendMessage(admin, "===== Online Players =====");
        for (Iterator<Player> it = World.getInstance().getPlayersIterator(); it.hasNext();) {
            Player player = it.next();
            if (player == null) {
                continue;
            }
            count++;
            PacketSendUtility.sendMessage(admin, count + ". " + player.getName() + " Lv" + player.getLevel() + " " + player.getRace() + " map=" + player.getWorldId() + " inst=" + player.getInstanceId());
        }
        PacketSendUtility.sendMessage(admin, "Total online: " + count);
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "syntax //list");
    }
}
