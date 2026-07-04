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
package ai.instance.miniumVault;

import ai.GeneralNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Minion")
public class MinionAI2 extends GeneralNpcAI2
{
    private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            final Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 15) {
                if (startedEvent.compareAndSet(false, true)) {
                    startMinion();
                }
            }
        }
    }
	
	private void startMinion() {
		if (!isAlreadyDead()) {
            switch (getNpcId()) {
				case 661350:
					//The minion robbed a box.
					PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDAbRe_Up3_urf_special07, 5000);
				break;
			} if (getNpcId() == 661350) {
				switch (Rnd.get(1, 2)) {
					case 1:
						getOwner().getMoveController().moveToPoint(502.0000f, 194.0000f, 179.0000f);
					break;
					case 2:
						getOwner().getMoveController().moveToPoint(525.0000f, 239.0000f, 179.0000f);
					break;
				}
				think();
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (!isAlreadyDead()) {
							despawnMinion();
						}
					}
				}, 3500);
			} else if (getNpcId() == 661351) {
				switch (Rnd.get(1, 2)) {
					case 1:
						getOwner().getMoveController().moveToPoint(551.0000f, 196.0000f, 179.0000f);
					break;
					case 2:
						getOwner().getMoveController().moveToPoint(525.0000f, 239.0000f, 179.0000f);
					break;
				}
				think();
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (!isAlreadyDead()) {
							despawnMinion();
						}
					}
				}, 3500);
			}
        }
    }
	
    private void despawnMinion() {
        AI2Actions.deleteOwner(this);
    }
}