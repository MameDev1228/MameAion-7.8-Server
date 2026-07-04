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
package ai.instance.tallocsHollow;

import ai.GeneralNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Writhing_Cocoon02")
public class Writhing_Cocoon02AI2 extends GeneralNpcAI2
{
	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            final Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 15) {
                if (startedEvent.compareAndSet(false, true)) {
                    startAbyla();
                }
            }
        }
    }
	
	private void startAbyla() {
        if (!isAlreadyDead()) {
            switch (getNpcId()) {
                case 730233: //Writhing Cocoon.
				    //Please, free me!
                    sendMsg(390508, 5000);
					//I am a Cleric. I can cast healing magic.
                    sendMsg(390509, 15000);
					//Let me know if you need my help.
                    sendMsg(390511, 20000);
                break;
            }
        }
    }
	
	@Override
    protected void handleDialogStart(Player player) {
        if (player.getInventory().getFirstItemByItemId(185000088) != null) { //Shishir's Corrosive Fluid.
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1097));
        }
    }
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 1012 && player.getInventory().decreaseByItemId(185000088, 1)) {
			switch (getNpcId()) {
				case 730233: //Writhing Cocoon.
				    spawn(799501, player.getX(), player.getY(), player.getZ(), (byte) player.getHeading()); //Abyla.
				break;
			}
		}
		getOwner().getController().onDelete();
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
	
	private void sendMsg(int msg, int delay) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, delay);
    }
}