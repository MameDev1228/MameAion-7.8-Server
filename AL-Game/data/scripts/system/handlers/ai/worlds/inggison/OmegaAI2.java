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
package ai.worlds.inggison;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("omega")
public class OmegaAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
	}
	
	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					chooseClone();
				}
			}
		}, 5000, 60000);
	}
	
	private void chooseClone() {
		switch (Rnd.get(1, 5)) {
			case 1:
			    cloneOfPower();
			break;
			case 2:
			    cloneOfExplosion();
			break;
			case 3:
			    cloneOfHealing();
			break;
			case 4:
			    cloneOfPhysicalBarrier();
			break;
			case 5:
			    cloneOfMagicalBarrier();
			break;
		}
		//Destroy the invaders!
		sendMsg(342054, getObjectId(), false, 0);
		//Wake up, my golems!
		sendMsg(342055, getObjectId(), false, 4000);
		//Behold the Ancient Dragon's power!
		sendMsg(342056, getObjectId(), false, 8000);
	}
	
	private void cloneOfPower() {
		LF4RaidShowTimePhase1();
		SkillEngine.getInstance().getSkill(getOwner(), 19191, 60, getTarget()).useNoAnimationSkill(); //Omega's Transformation.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 19292, 60, getTarget()).useNoAnimationSkill(); //Crushing Earth.
				if (getPosition().getWorldMapInstance().getNpc(281945) == null &&
				    getPosition().getWorldMapInstance().getNpc(281946) == null &&
					getPosition().getWorldMapInstance().getNpc(281947) == null &&
					getPosition().getWorldMapInstance().getNpc(281948) == null &&
					getPosition().getWorldMapInstance().getNpc(281949) == null) {
					rndSpawn(281945, 3);
				}
			}
		}, 12000);
	}
	
	private void cloneOfExplosion() {
		LF4RaidShowTimePhase2();
		SkillEngine.getInstance().getSkill(getOwner(), 19191, 60, getTarget()).useNoAnimationSkill(); //Omega's Transformation.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 19292, 60, getTarget()).useNoAnimationSkill(); //Crushing Earth.
				if (getPosition().getWorldMapInstance().getNpc(281945) == null &&
				    getPosition().getWorldMapInstance().getNpc(281946) == null &&
					getPosition().getWorldMapInstance().getNpc(281947) == null &&
					getPosition().getWorldMapInstance().getNpc(281948) == null &&
					getPosition().getWorldMapInstance().getNpc(281949) == null) {
					rndSpawn(281946, 3);
				}
			}
		}, 12000);
	}
	
	private void cloneOfHealing() {
		LF4RaidShowTimePhase3();
		SkillEngine.getInstance().getSkill(getOwner(), 19191, 60, getTarget()).useNoAnimationSkill(); //Omega's Transformation.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(281945) == null &&
				    getPosition().getWorldMapInstance().getNpc(281946) == null &&
					getPosition().getWorldMapInstance().getNpc(281947) == null &&
					getPosition().getWorldMapInstance().getNpc(281948) == null &&
					getPosition().getWorldMapInstance().getNpc(281949) == null) {
					rndSpawn(281947, 3);
				}
			}
		}, 12000);
	}
	
	private void cloneOfPhysicalBarrier() {
		LF4RaidShowTimePhase4();
		SkillEngine.getInstance().getSkill(getOwner(), 19191, 60, getTarget()).useNoAnimationSkill(); //Omega's Transformation.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(281945) == null &&
				    getPosition().getWorldMapInstance().getNpc(281946) == null &&
					getPosition().getWorldMapInstance().getNpc(281947) == null &&
					getPosition().getWorldMapInstance().getNpc(281948) == null &&
					getPosition().getWorldMapInstance().getNpc(281949) == null) {
					rndSpawn(281948, 3);
				}
			}
		}, 12000);
	}
	
	private void cloneOfMagicalBarrier() {
		LF4RaidShowTimePhase4();
		SkillEngine.getInstance().getSkill(getOwner(), 19191, 60, getTarget()).useNoAnimationSkill(); //Omega's Transformation.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(281945) == null &&
				    getPosition().getWorldMapInstance().getNpc(281946) == null &&
					getPosition().getWorldMapInstance().getNpc(281947) == null &&
					getPosition().getWorldMapInstance().getNpc(281948) == null &&
					getPosition().getWorldMapInstance().getNpc(281949) == null) {
					rndSpawn(281949, 3);
				}
			}
		}, 12000);
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 10);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
	}
	
	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}
	
	private void LF4RaidShowTimePhase1() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Omega summons a creature.
					PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_LF4_RaidShowTime_Phase1, 12000);
				}
			}
		});
	}
	
	private void LF4RaidShowTimePhase2() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Omega summons a powerful creature.
					PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_LF4_RaidShowTime_Phase2, 12000);
				}
			}
		});
	}
	
	private void LF4RaidShowTimePhase3() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Omega summons a healing creature.
					PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_LF4_RaidShowTime_Phase3, 0);
				}
			}
		});
	}
	
	private void LF4RaidShowTimePhase4() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Omega summons a creature that creates barriers.
					PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_LF4_RaidShowTime_Phase4, 0);
				}
			}
		});
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	@Override
    protected void handleSpawned() {
        super.handleSpawned();
    }
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281945));
		killNpc(instance.getNpcs(281946));
		killNpc(instance.getNpcs(281947));
		killNpc(instance.getNpcs(281948));
		killNpc(instance.getNpcs(281949));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		//Omega...can't sympathize with...
		sendMsg(342061, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281945));
		killNpc(instance.getNpcs(281946));
		killNpc(instance.getNpcs(281947));
		killNpc(instance.getNpcs(281948));
		killNpc(instance.getNpcs(281949));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}