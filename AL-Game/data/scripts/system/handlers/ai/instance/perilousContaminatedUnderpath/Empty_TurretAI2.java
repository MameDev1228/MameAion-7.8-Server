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
package ai.instance.perilousContaminatedUnderpath;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Empty_Turret")
public class Empty_TurretAI2 extends NpcAI2
{
	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000) {
			switch (player.getWorldId()) {
				case 301631000: //Secret Hellpath 7.x
				    player.getInventory().decreaseByItemId(182007405, 1); //Bright Aether.
					spawn(836025, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				break;
				case 301632000: //Perilous Contaminated Underpath.
				    player.getInventory().decreaseByItemId(186000470, 1); //War Points.
					spawn(836025, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				break;
			}
		} if (dialogId == 10001) {
			switch (player.getWorldId()) {
				case 301631000: //Secret Hellpath 7.x
				    player.getInventory().decreaseByItemId(182007405, 2); //Bright Aether.
					spawn(836030, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				break;
				case 301632000: //Perilous Contaminated Underpath.
				    player.getInventory().decreaseByItemId(186000470, 2); //War Points.
					spawn(836030, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				break;
			}
		} if (dialogId == 10002) {
			switch (player.getWorldId()) {
				case 301631000: //Secret Hellpath 7.x
				    player.getInventory().decreaseByItemId(182007405, 3); //Bright Aether.
					spawn(836035, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				break;
				case 301632000: //Perilous Contaminated Underpath.
				    player.getInventory().decreaseByItemId(186000470, 3); //War Points.
					spawn(836035, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				break;
			}
		} if (dialogId == 10003) {
			switch (player.getWorldId()) {
				case 301631000: //Secret Hellpath 7.x
				    player.getInventory().decreaseByItemId(182007405, 3); //Bright Aether.
					spawn(836036, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				break;
				case 301632000: //Perilous Contaminated Underpath.
				    player.getInventory().decreaseByItemId(186000470, 3); //War Points.
					spawn(836036, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				break;
			}
		} if (dialogId == 10004) {
			switch (player.getWorldId()) {
				case 301631000: //Secret Hellpath 7.x
				    player.getInventory().decreaseByItemId(182007405, 5); //Bright Aether.
					spawn(836045, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				break;
				case 301632000: //Perilous Contaminated Underpath.
				    player.getInventory().decreaseByItemId(186000470, 5); //War Points.
					spawn(836045, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				break;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		AI2Actions.deleteOwner(this);
		return true;
	}
}