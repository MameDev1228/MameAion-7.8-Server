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
package ai.instance.primethForge;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Liberated_Primeth1")
public class Liberated_Primeth1AI2 extends GeneralNpcAI2
{
	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            final Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 10) {
                if (startedEvent.compareAndSet(false, true)) {
                    startPrimeth();
                }
            }
        }
    }
	
	private void startPrimeth() {
        if (!isAlreadyDead()) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!isAlreadyDead()) {
						if (getNpcId() == 650019) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
                                        getSpawnTemplate().setWalkerId("Liberated_Primeth");
                                        setStateIfNot(AIState.WALKING);
                                        think();
										//Use the open entrance to move to the next area.
										sendMsg(1402781, 15000);
										//I'm coming back to my senses… I couldn't have done it without help.
										sendMsg(1502013, 20000);
										//I will not stand for this humiliation. The Balaur shall pay!
										sendMsg(1502025, 40000);
										ThreadPoolManager.getInstance().schedule(new Runnable() {
											@Override
											public void run() {
												getPosition().getWorldMapInstance().getDoors().get(92).setOpen(true);
											}
										}, 13500);
										ThreadPoolManager.getInstance().schedule(new Runnable() {
											@Override
											public void run() {
												deletePrimeth();
												spawn(650020, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
											}
										}, 45000);
                                    }
                                }
                            }, 1000);
                        }
                    }
                }
            }, 1000);
        }
    }
	
	private void deletePrimeth() {
        AI2Actions.deleteOwner(this);
    }
	
	private void sendMsg(int msg, int delay) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, delay);
    }
}