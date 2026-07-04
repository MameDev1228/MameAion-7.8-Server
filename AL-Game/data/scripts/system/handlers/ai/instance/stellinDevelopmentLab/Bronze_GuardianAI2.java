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
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.EmotionType;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/****/
/** Author Rinzler, Ranastic (Encom)
/****/

@AIName("Bronze_Guardian")
public class Bronze_GuardianAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			getPosition().getWorldMapInstance().getDoors().get(194).setOpen(false);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{90, 70, 50, 30, 10});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				percents.remove(percent);
				canThink = false;
				//Intruder alert! Increasing security!
				sendMsg(1502323, getObjectId(), false, 0);
				getOwner().getController().abortCast();
				EmoteManager.emoteStopAttacking(getOwner());
				getOwner().getController().cancelCurrentSkill();
				//The alarm has detected intrudes, guards are now rushing in.
				PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDLDF8_Lab_Boss_03_Skill_MSG_01, 0);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
						chooseAttack();
						//The emergency system has been activated, more and more guards are coming.
						PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDLDF8_Lab_Boss_03_Skill_MSG_02, 0);
				  		setStateIfNot(AIState.WALKING);
						SkillEngine.getInstance().getSkill(getOwner(), 19968, 60, getOwner()).useNoAnimationSkill();
				  		getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
				  		WalkManager.startWalking(Bronze_GuardianAI2.this);
						getOwner().setState(1);
						PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
				  	}
			    }, 4000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
				  		canThink = true;
				  		EffectController ef = getOwner().getEffectController();
						if (ef.hasAbnormalEffect(19968)) {
							ef.removeEffect(19968);
						}
						Creature creature = getAggroList().getMostHated();
						if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
							setStateIfNot(AIState.FIGHT);
							think();
						} else {
							getMoveController().abortMove();
							getOwner().setTarget(creature);
							getOwner().getGameStats().renewLastAttackTime();
							getOwner().getGameStats().renewLastAttackedTime();
							getOwner().getGameStats().renewLastChangeTargetTime();
							getOwner().getGameStats().renewLastSkillTime();
							setStateIfNot(AIState.WALKING);
							getOwner().setState(1);
							getOwner().getMoveController().moveToTargetObject();
							PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
						}
				  	}
				}, 30000);
		    }
			break;
		}
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 2)) {
			case 1:
			    labBoss03Summon1();
			break;
			case 2:
			    labBoss03Summon2();
			break;
		}
		//Increasing defenses!
		sendMsg(1502326, getObjectId(), false, 0);
		//Attempting to eliminate intruders....
		sendMsg(1502327, getObjectId(), false, 3000);
	}
	
	private void labBoss03Summon1() {
		Npc summon1 = (Npc) spawn(858169, 60.0000f, 314.0000f, 399.0000f, (byte) 0);
		Npc summon2 = (Npc) spawn(858169, 92.0000f, 346.0000f, 399.0000f, (byte) 90);
		Npc summon3 = (Npc) spawn(858169, 92.0000f, 282.0000f, 399.0000f, (byte) 29);
		////////////////////////////////////////////////
		summon1.getSpawn().setWalkerId("Lab_Boss_03_Summon_1");
		WalkManager.startWalking((NpcAI2) summon1.getAi2());
		////////////////////////////////////////////////
		summon2.getSpawn().setWalkerId("Lab_Boss_03_Summon_2");
		WalkManager.startWalking((NpcAI2) summon2.getAi2());
		////////////////////////////////////////////////
		summon3.getSpawn().setWalkerId("Lab_Boss_03_Summon_3");
		WalkManager.startWalking((NpcAI2) summon3.getAi2());
	}
	
	private void labBoss03Summon2() {
		Npc summon4 = (Npc) spawn(858170, 60.0000f, 314.0000f, 399.0000f, (byte) 0);
		Npc summon5 = (Npc) spawn(858170, 92.0000f, 346.0000f, 399.0000f, (byte) 90);
		Npc summon6 = (Npc) spawn(858170, 92.0000f, 282.0000f, 399.0000f, (byte) 29);
		////////////////////////////////////////////////
		summon4.getSpawn().setWalkerId("Lab_Boss_03_Summon_1");
		WalkManager.startWalking((NpcAI2) summon4.getAi2());
		////////////////////////////////////////////////
		summon5.getSpawn().setWalkerId("Lab_Boss_03_Summon_2");
		WalkManager.startWalking((NpcAI2) summon5.getAi2());
		////////////////////////////////////////////////
		summon6.getSpawn().setWalkerId("Lab_Boss_03_Summon_3");
		WalkManager.startWalking((NpcAI2) summon6.getAi2());
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
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
		//Intruder eliminated! Switching to patrol mode!
		sendMsg(1502324, getObjectId(), false, 0);
		getPosition().getWorldMapInstance().getDoors().get(194).setOpen(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(858161));
		killNpc(instance.getNpcs(858162));
		killNpc(instance.getNpcs(858163));
		killNpc(instance.getNpcs(858164));
		killNpc(instance.getNpcs(858169));
		killNpc(instance.getNpcs(858170));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		//Warning! Warning! Security system breached! Breached...?
		sendMsg(1502325, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		getPosition().getWorldMapInstance().getDoors().get(194).setOpen(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(858161));
		killNpc(instance.getNpcs(858162));
		killNpc(instance.getNpcs(858163));
		killNpc(instance.getNpcs(858164));
		killNpc(instance.getNpcs(858169));
		killNpc(instance.getNpcs(858170));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}