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
package ai.instance.infernalDrakenspireDepths;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Beritra")
public class BeritraAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private boolean canThink = true;
	private int curentPercent = 100;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private List<Integer> percents = new ArrayList<Integer>();
	
	private void beritraSeal() {
		SkillEngine.getInstance().getSkill(getOwner(), 21610, 60, getOwner()).useNoAnimationSkill(); //Dark Affinity.
		SkillEngine.getInstance().getSkill(getOwner(), 21611, 60, getOwner()).useNoAnimationSkill(); //Wall Of Blades.
		SkillEngine.getInstance().getSkill(getOwner(), 21612, 60, getOwner()).useNoAnimationSkill(); //Everlasting Life.
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
		beritraSeal();
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{60, 40});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 60:
					    //Not bad at all… but it's not over yet!
						sendMsg(1501273, getObjectId(), false, 0);
						//I'm not playing anymore!
						sendMsg(1501270, getObjectId(), false, 3000);
					    //Beritra uses Immortal Vitality to recover his health completely.
						PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Vritra_Human_03, 0);
						SkillEngine.getInstance().getSkill(getOwner(), 21618, 60, getOwner()).useNoAnimationSkill(); //Chains Of Command.
					break;
					case 40:
						//Not bad at all… but it's not over yet!
						sendMsg(1501273, getObjectId(), false, 0);
						//I'm not playing anymore!
						sendMsg(1501270, getObjectId(), false, 3000);
						//Beritra uses his Power.
						PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Vritra_Human_01, 0);
						SkillEngine.getInstance().getSkill(getOwner(), 20842, 60, getOwner()).useNoAnimationSkill(); //Grasping Darkness.
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								deleteBeritra();
								getOwner().getEffectController().removeAllEffects();
								WorldMapInstance instance = getPosition().getWorldMapInstance();
								killNpc(instance.getNpcs(855444));
								killNpc(instance.getNpcs(855445));
								killNpc(instance.getNpcs(855446));
								killNpc(instance.getNpcs(702697));
								killNpc(instance.getNpcs(702699));
								spawn(654689, 127.0000f, 508.0000f, 1749.0000f, (byte) 8); //Dragon Form.
								//Beritra transforms into a dragon.
								PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Vritra_Dragon_01, 0);
							}
						}, 2000);
					break;
				}
				percents.remove(percent);
				break;
			}
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
		switch (Rnd.get(1, 3)) {
			case 1:
			    drakenspireReaper();
			break;
			case 2:
			    drakenspireTomescale();
			break;
			case 3:
			    drakenspirePustule();
			break;
		}
	}
	
	private void drakenspireReaper() {
		//You're not too bad for an insect!
		sendMsg(1501269, getObjectId(), false, 0);
		//You insects think you have a chance against me?
		sendMsg(1501272, getObjectId(), false, 3000);
		if (getPosition().getWorldMapInstance().getNpc(855444) == null &&
		    getPosition().getWorldMapInstance().getNpc(855445) == null &&
			getPosition().getWorldMapInstance().getNpc(855446) == null) {
			//Dark Sign.
			AI2Actions.useSkill(this, 21607);
			rndSpawn(855444, 3);
		}
	}
	private void drakenspireTomescale() {
		//Go on, entertain me!
		sendMsg(1501274, getObjectId(), false, 0);
		//Please. Is this the best you can do?
		sendMsg(1501275, getObjectId(), false, 3000);
		if (getPosition().getWorldMapInstance().getNpc(855444) == null &&
		    getPosition().getWorldMapInstance().getNpc(855445) == null &&
			getPosition().getWorldMapInstance().getNpc(855446) == null) {
			//Dark Sign.
			AI2Actions.useSkill(this, 21607);
			rndSpawn(855445, 3);
		}
	}
	private void drakenspirePustule() {
		//Enough! You're wasting my time!
		sendMsg(1501276, getObjectId(), false, 0);
		//This will all have been in vain!
		sendMsg(1501277, getObjectId(), false, 3000);
		if (getPosition().getWorldMapInstance().getNpc(855444) == null &&
		    getPosition().getWorldMapInstance().getNpc(855445) == null &&
			getPosition().getWorldMapInstance().getNpc(855446) == null) {
			//Dark Sign.
			AI2Actions.useSkill(this, 21607);
			rndSpawn(855446, 3);
		}
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 8);
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
	
	private void deleteBeritra() {
        AI2Actions.deleteOwner(this);
    }
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		percents.clear();
		cancelTask();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		addPercent();
		beritraSeal();
		canThink = true;
		isHome.set(true);
		curentPercent = 100;
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(855444));
		killNpc(instance.getNpcs(855445));
		killNpc(instance.getNpcs(855446));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}