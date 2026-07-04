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

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/
 
@AIName("Hyas")
public class HyasAI2 extends GeneralNpcAI2
{
    private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            final Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 15) {
                if (startedEvent.compareAndSet(false, true)) {
                    startHyas();
                }
            }
        }
    }
	
	private void startHyas() {
        if (!isAlreadyDead()) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!isAlreadyDead()) {
						if (getNpcId() == 799527) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
                                        //Damn! I'm always out of breath whenever I get to Taloc. Gasp... Gasp...
										sendMsg(390500, 0);
										//Gasp...Gasp, I can't stand it anymore! I'm sorry but I'll leave first.
										sendMsg(390501, 5000);
                                        getSpawnTemplate().setWalkerId("Hyas");
                                        setStateIfNot(AIState.WALKING);
                                        think();
                                        ThreadPoolManager.getInstance().schedule(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!isAlreadyDead()) {
                                                    getOwner().setState(1);
                                                    PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
                                                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (!isAlreadyDead()) {
                                                                deleteHyas();
                                                            }
                                                        }
                                                    }, 10000);
                                                }
                                            }
                                        }, 8000);
                                    }
                                }
                            }, 1500);
                        }
                    }
                }
            }, 1000);
        }
    }
	
	private void deleteHyas() {
        AI2Actions.deleteOwner(this);
    }
	
	private void sendMsg(int msg, int delay) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, delay);
    }
}