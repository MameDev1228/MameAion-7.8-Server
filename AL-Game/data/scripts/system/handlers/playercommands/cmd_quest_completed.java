package playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class cmd_quest_completed extends PlayerCommand
{
	public cmd_quest_completed() {
		super("questcompleted");
	}
	
	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax .questcompleted <quest id>");
			return;
		}
		int id;
		try {
			id = Integer.valueOf(params[0]);
		} catch (NumberFormatException e)	{
			PacketSendUtility.sendMessage(player, "syntax .questcompleted <quest id>");
			return;
		}
		ClassChangeService.completeQuest(player, id);
	}
	
	@Override
	public void onFail(Player player, String message) {
	}
}