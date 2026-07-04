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
package ai.instance.stellinDevelopmentLab;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;


/****/
/** Author Rinzler, Ranastic (Encom)
/****/

@AIName("Shadon")
public class ShadonAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			getPosition().getWorldMapInstance().getDoors().get(162).setOpen(false);
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
					chooseAttack();
				}
			}
		}, 5000, 60000);
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 5)) {
			case 1:
			    labBoss02HumanFi();
			break;
			case 2:
			    labBoss02HumanAs();
			break;
			case 3:
			    labBoss02ShugoAs01();
			break;
			case 4:
			    labBoss02ShugoAs02();
			break;
			case 5:
			    labBoss02RatmanFi();
			break;
		}
		//Such weak trash like you will make perfect test subjects!
		sendMsg(1502320, getObjectId(), false, 0);
		//How dare you disturb my experiment! You shall pay!
		sendMsg(1502322, getObjectId(), false, 4000);
	}
	
	private void contaminatedPool() {
		//The alarm has sounded and the researchers are ready to fight.
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDLDF8_Lab_MSG_08, 0);
		if (getPosition().getWorldMapInstance().getNpc(858282) == null) { //Contaminated Pool.
			rndSpawn(858282, 5); //Contaminated Pool.
		}
	}
	
	private void labBoss02HumanFi() {
		contaminatedPool();
		Npc summon1 = (Npc) spawn(858284, 66.0000f, 309.0000f, 307.0000f, (byte) 90);
		Npc summon2 = (Npc) spawn(858285, 62.0000f, 309.0000f, 307.0000f, (byte) 90);
		///////////////////////////////////////////////////////
		summon1.getSpawn().setWalkerId("Lab_Boss_02_Summon_1");
		WalkManager.startWalking((NpcAI2) summon1.getAi2());
		///////////////////////////////////////////////////////
		summon2.getSpawn().setWalkerId("Lab_Boss_02_Summon_2");
		WalkManager.startWalking((NpcAI2) summon2.getAi2());
	}
	private void labBoss02HumanAs() {
		contaminatedPool();
		Npc summon1 = (Npc) spawn(858285, 66.0000f, 309.0000f, 307.0000f, (byte) 90);
		Npc summon2 = (Npc) spawn(858286, 62.0000f, 309.0000f, 307.0000f, (byte) 90);
		///////////////////////////////////////////////////////
		summon1.getSpawn().setWalkerId("Lab_Boss_02_Summon_1");
		WalkManager.startWalking((NpcAI2) summon1.getAi2());
		///////////////////////////////////////////////////////
		summon2.getSpawn().setWalkerId("Lab_Boss_02_Summon_2");
		WalkManager.startWalking((NpcAI2) summon2.getAi2());
	}
	private void labBoss02ShugoAs01() {
		contaminatedPool();
		Npc summon1 = (Npc) spawn(858286, 66.0000f, 309.0000f, 307.0000f, (byte) 90);
		Npc summon2 = (Npc) spawn(858287, 62.0000f, 309.0000f, 307.0000f, (byte) 90);
		///////////////////////////////////////////////////////
		summon1.getSpawn().setWalkerId("Lab_Boss_02_Summon_1");
		WalkManager.startWalking((NpcAI2) summon1.getAi2());
		///////////////////////////////////////////////////////
		summon2.getSpawn().setWalkerId("Lab_Boss_02_Summon_2");
		WalkManager.startWalking((NpcAI2) summon2.getAi2());
	}
	private void labBoss02ShugoAs02() {
		contaminatedPool();
		Npc summon1 = (Npc) spawn(858287, 66.0000f, 309.0000f, 307.0000f, (byte) 90);
		Npc summon2 = (Npc) spawn(858288, 62.0000f, 309.0000f, 307.0000f, (byte) 90);
		///////////////////////////////////////////////////////
		summon1.getSpawn().setWalkerId("Lab_Boss_02_Summon_1");
		WalkManager.startWalking((NpcAI2) summon1.getAi2());
		///////////////////////////////////////////////////////
		summon2.getSpawn().setWalkerId("Lab_Boss_02_Summon_2");
		WalkManager.startWalking((NpcAI2) summon2.getAi2());
	}
	private void labBoss02RatmanFi() {
		contaminatedPool();
		Npc summon1 = (Npc) spawn(858288, 66.0000f, 309.0000f, 307.0000f, (byte) 90);
		Npc summon2 = (Npc) spawn(858284, 62.0000f, 309.0000f, 307.0000f, (byte) 90);
		///////////////////////////////////////////////////////
		summon1.getSpawn().setWalkerId("Lab_Boss_02_Summon_1");
		WalkManager.startWalking((NpcAI2) summon1.getAi2());
		///////////////////////////////////////////////////////
		summon2.getSpawn().setWalkerId("Lab_Boss_02_Summon_2");
		WalkManager.startWalking((NpcAI2) summon2.getAi2());
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 5);
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
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
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
		getPosition().getWorldMapInstance().getDoors().get(162).setOpen(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(858282)); //Contaminated Pool.
		killNpc(instance.getNpcs(858284)); //Mutated Protector.
		killNpc(instance.getNpcs(858285)); //Mutated Protector.
		killNpc(instance.getNpcs(858286)); //Mutated Protector.
		killNpc(instance.getNpcs(858287)); //Mutated Protector.
		killNpc(instance.getNpcs(858288)); //Mutated Protector.
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		//There is only death for intruders....
		sendMsg(1502319, getObjectId(), false, 0);
		//No, my research cannot end here...!
		sendMsg(1502321, getObjectId(), false, 3000);
		getOwner().getEffectController().removeAllEffects();
		getPosition().getWorldMapInstance().getDoors().get(162).setOpen(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(858282)); //Contaminated Pool.
		killNpc(instance.getNpcs(858284)); //Mutated Protector.
		killNpc(instance.getNpcs(858285)); //Mutated Protector.
		killNpc(instance.getNpcs(858286)); //Mutated Protector.
		killNpc(instance.getNpcs(858287)); //Mutated Protector.
		killNpc(instance.getNpcs(858288)); //Mutated Protector.
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}