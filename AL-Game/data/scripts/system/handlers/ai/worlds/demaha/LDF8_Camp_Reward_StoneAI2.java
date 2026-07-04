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
package ai.worlds.demaha;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("LDF8_Camp_Reward_Stone")
public class LDF8_Camp_Reward_StoneAI2 extends NpcAI2
{
	@Override
    protected void handleDialogStart(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
    }
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000) {
		    switch (Rnd.get(1, 9)) {
				case 1:
					ItemService.addItem(player, 188120084, 2);
				break;
				case 2:
					ItemService.addItem(player, 188120085, 2);
				break;
				case 3:
					ItemService.addItem(player, 188120086, 2);
				break;
				case 4:
					ItemService.addItem(player, 188120087, 2);
				break;
				case 5:
					ItemService.addItem(player, 188120088, 2);
				break;
				case 6:
					ItemService.addItem(player, 188120089, 2);
				break;
				case 7:
					ItemService.addItem(player, 188120090, 2);
				break;
				case 8:
					ItemService.addItem(player, 188120091, 2);
				break;
				case 9:
					ItemService.addItem(player, 188120092, 2);
				break;
			}
		}
		getOwner().getController().onDelete();
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}