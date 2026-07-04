package playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class cmd_duel extends PlayerCommand
{
    public cmd_duel() {
        super("duel");
    }
	
    @Override
    public void execute(Player player, String... params) {
        if (player.getLevel() < 50) {
            PacketSendUtility.sendSys3Message(player, "\uE005", "You must reached lvl 50 for use <Duel> map!!!");
            return;
        } else {
			TeleportService2.teleportTo(player, 320150000, 0, 385.00000f, 506.00000f, 66.0000f, (byte) 0);
		}
    }
	
	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}