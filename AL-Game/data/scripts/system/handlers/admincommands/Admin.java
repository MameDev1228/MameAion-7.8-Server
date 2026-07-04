package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.PlayerSweep;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class Admin extends AdminCommand
{
	public Admin() {
		super("admin");
	}

	@Override
	public void execute(Player player, String... params) {
		/*int value = Integer.parseInt(params[0]);
		int count = Integer.parseInt(params[1]);
        PacketSendUtility.sendPacket(player, new SM_QUNA_INSTANCE_BUFF(value, false));*/
        //PacketSendUtility.sendPacket(player, new SM_ROUND_TRIP());
        //PacketSendUtility.sendPacket(player, new SM_ENCHANT_DEVANION_SKILL(0,5097, 2, true));
        //PacketSendUtility.sendPacket(player, new SM_ENCHANT_DEVANION_SKILL(169501689,0, 0, false));
        //PacketSendUtility.sendPacket(player, new SM_TEST(0, 169501689));
        //PacketSendUtility.sendPacket(player, new SM_EMOTION_LIST((byte) 0, player.getEmotions().getEmotions()));
		//PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(14, 5, true, false));

		//AchievementService.getInstance().onUpdateAchievementAction(player, value, count, AchievementActionType.COLLECT_ITEM);

		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(107, SM_AUTO_GROUP.wnd_EntryIcon));
	}

	@Override
	public void onFail(Player player, String message) {
	}

	private void sendPacket(Player player, final String variable, final int floor) {
		//PacketSendUtility.sendPacket(player, new SM_CONDITION_VARIABLE(player, variable, floor));
		PacketSendUtility.sendPacket(player, new SM_QUNA_INSTANCE_BUFF(74, false));
	}

	public PlayerCommonData getCommonData(Player player){
		return player.getCommonData();
	}

	public PlayerSweep getPlayerSweep(Player player){
		return player.getPlayerShugoSweep();
	}
}