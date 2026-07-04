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
package ai.worlds.silentera_canyon;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Raid_Drakan_Boss_80")
public class Ascended_Adjutant_AnuhartAI2 extends AggressiveNpcAI2
{
	private Future<?> task1;
	private Future<?> task2;
	private Future<?> task3;
	private Future<?> task4;
	private Future<?> task5;
	private Future<?> sendPacketTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	protected List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startTask(5);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[] {70, 40, 15, 10});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 70:
						chooseBuff(20938);
					break;
					case 40:
						chooseBuff(20939);
					break;
					case 15:
					    chooseBuff(20942);
					break;
					case 10:
						chooseBuff(20940);
						cancelTasks();
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void startTask(int taskId) {
		switch (taskId) {
			case 1:
				task1 = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//Wave Of Pain.
						AI2Actions.useSkill(Ascended_Adjutant_AnuhartAI2.this, 20745);
						startTask(2);
					}
				}, 12000);
			break;
			case 2:
				task2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//Sweeping Strike.
						AI2Actions.useSkill(Ascended_Adjutant_AnuhartAI2.this, 20746);
						startTask(3);
					}
				}, 10000);
			break;
			case 3:
				task2 = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//Adjutant's Strike.
						AI2Actions.useSkill(Ascended_Adjutant_AnuhartAI2.this, 20744);
						startTask(4);
					}
				}, 10000);
			break;
			case 4:
				task3 = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//Wave Of Pain.
						AI2Actions.useSkill(Ascended_Adjutant_AnuhartAI2.this, 20745);
						startTask(5);
					}
				}, 3000);
			break;
			case 5:
				task4 = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						///So many familiar faces, I see. Well, today wilt be thy final day!
						sendMsg(1500712, getObjectId(), false, 0);
						///Consider it an honor to die by my hand.
						sendMsg(1500713, getObjectId(), false, 5000);
						///I will crush you all!
						sendMsg(1500714, getObjectId(), false, 10000);
						//Defense Mark Burst.
						AI2Actions.targetSelf(Ascended_Adjutant_AnuhartAI2.this);
						AI2Actions.useSkill(Ascended_Adjutant_AnuhartAI2.this, 20749);
						SkillEngine.getInstance().getSkill(getOwner(), 20747, 60, getOwner()).useNoAnimationSkill();
						Npc bladeStorm = getPosition().getWorldMapInstance().getNpc(283099);
						if (bladeStorm == null) {
							//Blade Storm.
							spawn(283099, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						}
						startTask(6);
					}
				}, 4000);
			break;
			case 6:
				//Wave Of Pain.
				task5 = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						startTask(1);
						WorldMapInstance instance = getPosition().getWorldMapInstance();
						deleteNpcs(instance.getNpcs(283099));
					}
				}, 8000);
			break;
		}
	}
	
	private void cancelTasks() {
		if (task1 != null && !task1.isCancelled()) {
			task1.cancel(true);
		} if (task2 != null && !task2.isCancelled()) {
			task2.cancel(true);
		} if (task3 != null && !task3.isCancelled()) {
			task3.cancel(true);
		} if (task4 != null && !task4.isCancelled()) {
			task4.cancel(true);
		} if (task5 != null && !task5.isCancelled()) {
			task5.cancel(true);
		}
	}
	
	private void chooseBuff(int buff) {
		AI2Actions.targetSelf(this);
		AI2Actions.useSkill(this, buff);
	}
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTasks();
	}
	
	@Override
    protected void handleSpawned() {
        super.handleSpawned();
		addPercent();
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(final Player player) {
                sendPacketTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getWorldId() == getOwner().getWorldId()) {
                            if (getOwner().isSpawned()) {
                                PacketSendUtility.sendPacket(player, new SM_FLAG_INFO(1, getOwner()));
                            }
                        }
                    }
                }, 1000, 2000);
            }
        });
    }
	
	@Override
    protected void handleMoveValidate() {
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(Player player) {
                if (player.getWorldId() == getOwner().getWorldId()) {
                    PacketSendUtility.sendPacket(player, new SM_FLAG_INFO(1, getOwner()));
                }
            }
        });
    }
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		addPercent();
		cancelTasks();
		isHome.set(true);
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTasks();
		percents.clear();
		getOwner().getEffectController().removeAllEffects();
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}