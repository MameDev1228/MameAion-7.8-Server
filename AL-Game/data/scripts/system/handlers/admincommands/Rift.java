/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.RiftService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import org.apache.commons.lang.math.NumberUtils;

/**
 * @author Ranastic
 */

public class Rift extends AdminCommand
{
	private static final String COMMAND_START = "start";
	private static final String COMMAND_STOP = "stop";
	
	public Rift() {
		super("rift");
	}
	
	@Override
	public void execute(Player player, String... params) {
		if (params.length == 0) {
			showHelp(player);
			return;
		} if (COMMAND_STOP.equalsIgnoreCase(params[0]) || COMMAND_START.equalsIgnoreCase(params[0])) {
			handleStartStopRift(player, params);
		}
	}
	
	protected void handleStartStopRift(Player player, String... params) {
		if (params.length != 2 || !NumberUtils.isDigits(params[1])) {
			showHelp(player);
			return;
		}
		int riftId = NumberUtils.toInt(params[1]);
		if (!isValidId(player, riftId)) {
			showHelp(player);
			return;
		} if (COMMAND_START.equalsIgnoreCase(params[0])) {
			PacketSendUtility.sendMessage(player, "<Rift Location> " + riftId + " started!");
			RiftService.getInstance().openRifts(riftId, true);
		} else if (COMMAND_STOP.equalsIgnoreCase(params[0])) {
			PacketSendUtility.sendMessage(player, "<Rift Location> " + riftId + " stopped!");
			RiftService.getInstance().closeRifts(riftId);
		}
	}
	
	protected boolean isValidId(Player player, int riftId) {
		if (!RiftService.getInstance().getRiftLocations().keySet().contains(riftId)) {
			PacketSendUtility.sendMessage(player, "Id " + riftId + " is invalid");
			return false;
		}
		return true;
	}
	
	protected void showHelp(Player player) {
		PacketSendUtility.sendMessage(player, "AdminCommand //rift start|stop <Id>");
	}
}