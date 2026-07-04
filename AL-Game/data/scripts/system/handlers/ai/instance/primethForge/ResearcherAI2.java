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
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/
 
@AIName("Researcher")
public class ResearcherAI2 extends GeneralNpcAI2
{
    private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            final Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 10) {
                if (startedEvent.compareAndSet(false, true)) {
                    startResearcher();
                }
            }
        }
    }
	
	private void startResearcher() {
        if (!isAlreadyDead()) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!isAlreadyDead()) {
						if (getNpcId() == 806844 || getNpcId() == 806845) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
                                        getSpawnTemplate().setWalkerId("Atis_Rith");
                                        setStateIfNot(AIState.WALKING);
                                        think();
										ThreadPoolManager.getInstance().schedule(new Runnable() {
											@Override
											public void run() {
												getPosition().getWorldMapInstance().getDoors().get(381).setOpen(true);
											}
										}, 30000);
										ThreadPoolManager.getInstance().schedule(new Runnable() {
											@Override
											public void run() {
												getPosition().getWorldMapInstance().getDoors().get(105).setOpen(true);
												WorldMapInstance instance = getPosition().getWorldMapInstance();
												killNpc(instance.getNpcs(837097));
												killNpc(instance.getNpcs(837098));
											}
										}, 55000);
										ThreadPoolManager.getInstance().schedule(new Runnable() {
											@Override
											public void run() {
												spawn(837096, 1374.0000f, 1086.0000f, 374.0000f, (byte) 30); //Primeth's Forge Exit.
											}
										}, 70000);
                                    }
                                }
                            }, 1000);
                        }
                    }
                }
            }, 1000);
        }
    }
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
}