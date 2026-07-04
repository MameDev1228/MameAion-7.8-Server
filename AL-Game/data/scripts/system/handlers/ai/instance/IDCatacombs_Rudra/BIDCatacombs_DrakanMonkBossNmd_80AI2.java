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
package ai.instance.IDCatacombs_Rudra;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("BIDCatacombs_DrakanMonkBossNmd_80")
public class BIDCatacombs_DrakanMonkBossNmd_80AI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			getPosition().getWorldMapInstance().getDoors().get(535).setOpen(false);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{75, 50, 25, 5});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 75:
					    //The glory of Tiamat shalt spread across the world!
						sendMsg(1500083, getObjectId(), false, 0);
					    scheduleSkill(18913, 0); //Arcane Combustion.
						scheduleSkill(18912, 12000); //Seething Explosion.
						//Isbariya taps into his power to cause a massive explosion!
						PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDCatacombs_Boss_ArchPriest_Artifact_LightBoom, 0);
						//Isbariya releases his magical power!
						PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDCatacombs_Boss_ArchPriest_Artifact_DarkBoom, 12000);
					break;
					case 50:
					    sacrificialSoul();
					break;
					case 25:
						apocalypticEnergy();
					break;
					case 5:
						holyServant();
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void sacrificialSoul() {
		scheduleSkill(18959, 0); //Sixth Sense.
		scheduleSkill(18913, 6000); //Arcane Combustion.
		scheduleSkill(18912, 12000); //Seething Explosion.
		//Isbariya the Resolute inflicts a devastating curse.
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDCatacombs_Boss_ArchPriest_6phase, 6000);
		//Isbariya the Resolute has summoned a Bodyguard Commissioned Officer.
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDCatacombs_Boss_ArchPriest_5phase, 12000);
		Npc sacrificialSoul1 = (Npc) spawn(281645, 1383.6250f, 1403.2117f, 304.6748f, (byte) 18);
		Npc sacrificialSoul2 = (Npc) spawn(281645, 1380.8008f, 1405.5929f, 304.4929f, (byte) 18);
		Npc sacrificialSoul3 = (Npc) spawn(281645, 1377.3414f, 1408.1031f, 304.6748f, (byte) 17);
		///////////////////////////////////////////////////
		sacrificialSoul1.getSpawn().setWalkerId("Sacrificial_Soul_1");
		WalkManager.startWalking((NpcAI2) sacrificialSoul1.getAi2());
		/////////////////////////////////////////////////////
		sacrificialSoul2.getSpawn().setWalkerId("Sacrificial_Soul_2");
		WalkManager.startWalking((NpcAI2) sacrificialSoul2.getAi2());
		/////////////////////////////////////////////////////
		sacrificialSoul3.getSpawn().setWalkerId("Sacrificial_Soul_3");
		WalkManager.startWalking((NpcAI2) sacrificialSoul3.getAi2());
	}
	
	private void holyServant() {
		scheduleSkill(18913, 0); //Arcane Combustion.
		scheduleSkill(18912, 12000); //Seething Explosion.
		//Isbariya the Resolute has boosted his recovery power!
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDCatacombs_Boss_ArchPriest_3phase, 0);
		if (getPosition().getWorldMapInstance().getNpc(281659) == null &&
		    getPosition().getWorldMapInstance().getNpc(281660) == null) {
			rndSpawn(281659, 3);
		}
	}
	
	private void apocalypticEnergy() {
		AI2Actions.useSkill(this, 18993); //Tiamat's Chaos.
		//Fools! Now dost thou understand the might of the Balaur?
		sendMsg(1500084, getObjectId(), false, 0);
		//Isbariya the Resolute unleashes an intense power.
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDCatacombs_Boss_ArchPriest_4phase, 0);
		//Isbariya the Resolute has boosted his attack power!
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDCatacombs_Boss_ArchPriest_2phase, 8000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(281660) == null &&
					getPosition().getWorldMapInstance().getNpc(281659) == null) {
					rndSpawn(281660, 3);
				}
			}
		}, 2500);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281660));
			}
		}, 120000);
	}
	
	private void scheduleSkill(final int skillId, int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getTarget()).useNoAnimationSkill();
				}
			}
		}, delay);
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
		percents.clear();
    }
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}
	
	@Override
    protected void handleBackHome() {
        super.handleBackHome();
		addPercent();
		canThink = true;
		isAggred.set(false);
		curentPercent = 100;
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281645));
		killNpc(instance.getNpcs(281659));
		killNpc(instance.getNpcs(281660));
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		percents.clear();
		//No! The seal! It must not break, or...!
		sendMsg(1500085, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281645));
		killNpc(instance.getNpcs(281659));
		killNpc(instance.getNpcs(281660));
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}