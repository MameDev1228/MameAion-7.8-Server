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
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.*;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Mutated_Daeva")
public class Mutated_DaevaAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	//private Future<?> checkTask1;
	//private Future<?> checkTask2;
	private boolean canThink = true;
	//private int curentPercent = 100;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	//private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			//Don't even think you're getting out of here alive!
			sendMsg(1502328, getObjectId(), false, 0);
			//This will be your grave! Kyahaha!
			sendMsg(1502329, getObjectId(), false, 5000);
		} if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
		//checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	/*
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{45});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 45:
						canThink = false;
						cancelTask();
						getOwner().getController().abortCast();
						EmoteManager.emoteStopAttacking(getOwner());
						getOwner().getController().cancelCurrentSkill();
						WorldMapInstance instance = getPosition().getWorldMapInstance();
						deleteNpcs(instance.getNpcs(858145));
						//Electromagnetic Field.
						deleteNpcs(instance.getNpcs(858141));
						deleteNpcs(instance.getNpcs(858142));
						deleteNpcs(instance.getNpcs(858143));
						deleteNpcs(instance.getNpcs(858144));
						deleteNpcs(instance.getNpcs(858149));
						deleteNpcs(instance.getNpcs(858153));
						deleteNpcs(instance.getNpcs(858226));
						deleteNpcs(instance.getNpcs(858227));
						//An intruder alarm is sounding in the Emergency Area.
						PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDLDF8_Lab_Boss_04_Skill_MSG_03, 0);
						//The electricity flowing in the ground is speeding up.
						PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDLDF8_Lab_Boss_04_Skill_MSG_04, 5000);
						switch (Rnd.get(1, 2)) {
							case 1: //Left Side.
								guardianOfTheSecretGateL();
								SkillEngine.getInstance().getSkill(getOwner(), 19968, 60, getOwner()).useNoAnimationSkill();
								getSpawnTemplate().setWalkerId("Mutated_Daeva_L");
					            WalkManager.startWalking(Mutated_DaevaAI2.this);
								getOwner().setState(1);
					            PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									@Override
									public void run() {
										checkSecretGateL();
									}
								}, 3000);
							break;
							case 2: //Right Side.
								guardianOfTheSecretGateR();
								SkillEngine.getInstance().getSkill(getOwner(), 19968, 60, getOwner()).useNoAnimationSkill();
								getSpawnTemplate().setWalkerId("Mutated_Daeva_R");
					            WalkManager.startWalking(Mutated_DaevaAI2.this);
								getOwner().setState(1);
					            PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									@Override
									public void run() {
										checkSecretGateR();
									}
								}, 3000);
							break;
						}
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void checkSecretGateL() {
		checkTask1 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Npc summonL80An = getPosition().getWorldMapInstance().getNpc(858135);
				if (summonL80An == null) {
					canThink = true;
					checkTask1.cancel(true);
					checkTask2.cancel(true);
					Creature creature = getAggroList().getMostHated();
					getOwner().getEffectController().removeAllEffects();
					//No more playing around!
					sendMsg(1502331, getObjectId(), false, 0);
					//Feel my wrath! Death shall be your repentance!
					sendMsg(1502334, getObjectId(), false, 5000);
					spawn(858137, 227.3565f, 195.3581f, 581.7133f, (byte) 0, 347); //Teleportation Portal [Left].
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							SkillEngine.getInstance().getSkill(getOwner(), 20569, 60, getTarget()).useNoAnimationSkill(); //Coiled Storm.
						}
					}, 3000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							startSkillTask();
							World.getInstance().updatePosition(getOwner(), 211.6373f, 256.3994f, 570.5197f, (byte) 30);
							PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_FORCED_MOVE(getOwner(), getOwner()));
						}
					}, 34000);
					if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
						setStateIfNot(AIState.FIGHT);
						think();
					} else {
						getOwner().setTarget(creature);
						getOwner().getGameStats().renewLastAttackTime();
						getOwner().getGameStats().renewLastAttackedTime();
						getOwner().getGameStats().renewLastChangeTargetTime();
						getOwner().getGameStats().renewLastSkillTime();
					}
				}
			}
		}, 1 * 500, 1 * 500);
	}
	private void checkSecretGateR() {
		checkTask2 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Npc summonR80An = getPosition().getWorldMapInstance().getNpc(858136);
				if (summonR80An == null) {
					canThink = true;
					checkTask1.cancel(true);
					checkTask2.cancel(true);
					Creature creature = getAggroList().getMostHated();
					getOwner().getEffectController().removeAllEffects();
					//No more playing around!
					sendMsg(1502331, getObjectId(), false, 0);
					//Feel my wrath! Death shall be your repentance!
					sendMsg(1502334, getObjectId(), false, 5000);
					spawn(858138, 195.5081f, 195.4476f, 581.7133f, (byte) 0, 348); //Teleportation Portal [Right].
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							SkillEngine.getInstance().getSkill(getOwner(), 20569, 60, getTarget()).useNoAnimationSkill(); //Coiled Storm.
						}
					}, 3000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							startSkillTask();
							World.getInstance().updatePosition(getOwner(), 211.6373f, 256.3994f, 570.5197f, (byte) 30);
							PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_FORCED_MOVE(getOwner(), getOwner()));
						}
					}, 34000);
					if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
						setStateIfNot(AIState.FIGHT);
						think();
					} else {
						getOwner().setTarget(creature);
						getOwner().getGameStats().renewLastAttackTime();
						getOwner().getGameStats().renewLastAttackedTime();
						getOwner().getGameStats().renewLastChangeTargetTime();
						getOwner().getGameStats().renewLastSkillTime();
					}
				}
			}
		}, 1 * 500, 1 * 500);
	}*/
	
	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					electromagneticField();
				}
			}
		}, 5000, 40000);
	}
	
	/*
	private void guardianOfTheSecretGateL() {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			Npc summonL80An = getPosition().getWorldMapInstance().getNpc(858135);
			if (summonL80An == null) {
				spawn(858135, 232.8672f, 200.8607f, 581.6518f, (byte) 51);
				spawn(858135, 222.2519f, 199.9694f, 581.6518f, (byte) 14);
				spawn(858135, 227.1145f, 208.4615f, 581.6518f, (byte) 91);
			}
		}
	}
	private void guardianOfTheSecretGateR() {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			Npc summonR80An = getPosition().getWorldMapInstance().getNpc(858136);
			if (summonR80An == null) {
				spawn(858136, 201.2686f, 200.8949f, 581.6518f, (byte) 50);
				spawn(858136, 195.4838f, 208.6383f, 581.6518f, (byte) 91);
				spawn(858136, 190.2037f, 200.2661f, 581.6518f, (byte) 14);
			}
		}
	}*/
	
	private void electromagneticField() {
		//Ahahaha, you'll only die faster!
		sendMsg(1502332, getObjectId(), false, 0);
		//I'll give you a taste of true pain!
		sendMsg(1502333, getObjectId(), false, 5000);
		//You can feel electricity flowing.
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDLDF8_Lab_Boss_04_Skill_MSG_01, 0);
		//The electricity flowing in the ground stops and a scorching heat begins to rise.
		PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDLDF8_Lab_Boss_04_Skill_MSG_02, 10000);
		switch (Rnd.get(1, 4)) {
			case 1:
			    //Electromagnetic Field [Visible Ground]
				spawn(858142, 205.1906f, 254.8265f, 570.5197f, (byte) 38);
				spawn(858142, 199.9133f, 250.9296f, 570.5197f, (byte) 46);
				spawn(858153, 217.7493f, 254.6473f, 570.5197f, (byte) 22); //safe zone
				spawn(858143, 228.9603f, 256.4793f, 570.5197f, (byte) 14);
				spawn(858143, 211.3520f, 264.1371f, 570.5197f, (byte) 30);
				spawn(858143, 201.7149f, 262.1583f, 570.5197f, (byte) 38);
				spawn(858143, 193.9385f, 256.5212f, 570.5197f, (byte) 46);
				spawn(858144, 187.8526f, 261.8430f, 570.5197f, (byte) 46);
				spawn(858144, 198.5929f, 269.6768f, 570.5197f, (byte) 38);
				spawn(858144, 211.4102f, 272.4322f, 570.5197f, (byte) 30);
				spawn(858144, 224.3753f, 269.5832f, 570.5197f, (byte) 22);
				spawn(858144, 235.0162f, 261.8508f, 570.5197f, (byte) 14);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//Electromagnetic Field [No-visible]
						spawn(858141, 205.1906f, 254.8265f, 570.5197f, (byte) 38);
						spawn(858141, 199.9133f, 250.9296f, 570.5197f, (byte) 46);
						spawn(858141, 228.9603f, 256.4793f, 570.5197f, (byte) 14);
						spawn(858141, 211.3520f, 264.1371f, 570.5197f, (byte) 30);
						spawn(858141, 201.7149f, 262.1583f, 570.5197f, (byte) 38);
						spawn(858141, 193.9385f, 256.5212f, 570.5197f, (byte) 46);
						spawn(858141, 187.8526f, 261.8430f, 570.5197f, (byte) 46);
						spawn(858141, 198.5929f, 269.6768f, 570.5197f, (byte) 38);
						spawn(858141, 211.4102f, 272.4322f, 570.5197f, (byte) 30);
						spawn(858141, 224.3753f, 269.5832f, 570.5197f, (byte) 22);
						spawn(858141, 235.0162f, 261.8508f, 570.5197f, (byte) 14);
						spawn(858149, 217.7493f, 254.6473f, 570.5197f, (byte) 22);
					}
				}, 3000);
			break;
			case 2:
			    //Electromagnetic Field [Visible Ground]
				spawn(858142, 217.7493f, 254.6473f, 570.5197f, (byte) 22);
				spawn(858142, 223.0010f, 251.0532f, 570.5197f, (byte) 14);
				spawn(858153, 205.1906f, 254.8265f, 570.5197f, (byte) 38); //safe zone
				spawn(858143, 228.9603f, 256.4793f, 570.5197f, (byte) 14);
				spawn(858143, 221.1719f, 262.1674f, 570.5197f, (byte) 22);
				spawn(858143, 211.3520f, 264.1371f, 570.5197f, (byte) 30);
				spawn(858143, 193.9385f, 256.5212f, 570.5197f, (byte) 46);
				spawn(858144, 187.8526f, 261.8430f, 570.5197f, (byte) 46);
				spawn(858144, 198.5929f, 269.6768f, 570.5197f, (byte) 38);
				spawn(858144, 211.4102f, 272.4322f, 570.5197f, (byte) 30);
				spawn(858144, 224.3753f, 269.5832f, 570.5197f, (byte) 22);
				spawn(858144, 235.0162f, 261.8508f, 570.5197f, (byte) 14);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//Electromagnetic Field [No-visible]
						spawn(858141, 217.7493f, 254.6473f, 570.5197f, (byte) 22);
						spawn(858141, 223.0010f, 251.0532f, 570.5197f, (byte) 14);
						spawn(858149, 205.1906f, 254.8265f, 570.5197f, (byte) 38);
						spawn(858141, 228.9603f, 256.4793f, 570.5197f, (byte) 14);
						spawn(858141, 221.1719f, 262.1674f, 570.5197f, (byte) 22);
						spawn(858141, 211.3520f, 264.1371f, 570.5197f, (byte) 30);
						spawn(858141, 193.9385f, 256.5212f, 570.5197f, (byte) 46);
						spawn(858141, 187.8526f, 261.8430f, 570.5197f, (byte) 46);
						spawn(858141, 198.5929f, 269.6768f, 570.5197f, (byte) 38);
						spawn(858141, 211.4102f, 272.4322f, 570.5197f, (byte) 30);
						spawn(858141, 224.3753f, 269.5832f, 570.5197f, (byte) 22);
						spawn(858141, 235.0162f, 261.8508f, 570.5197f, (byte) 14);
					}
				}, 3000);
			break;
			case 3:
			    //Electromagnetic Field [Visible Ground]
				spawn(858142, 217.7493f, 254.6473f, 570.5197f, (byte) 22);
				spawn(858142, 223.0010f, 251.0532f, 570.5197f, (byte) 14);
				spawn(858142, 211.4746f, 256.1063f, 570.5197f, (byte) 30);
				spawn(858142, 205.1906f, 254.8265f, 570.5197f, (byte) 38);
				spawn(858142, 199.9133f, 250.9296f, 570.5197f, (byte) 46);
				spawn(858143, 228.9603f, 256.4793f, 570.5197f, (byte) 14);
				spawn(858143, 221.1719f, 262.1674f, 570.5197f, (byte) 22);
				spawn(858143, 211.3520f, 264.1371f, 570.5197f, (byte) 30);
				spawn(858143, 193.9385f, 256.5212f, 570.5197f, (byte) 46);
				spawn(858227, 198.5929f, 269.6768f, 570.5197f, (byte) 38); //safe zone
				spawn(858144, 224.3753f, 269.5832f, 570.5197f, (byte) 22);
				spawn(858144, 235.0162f, 261.8508f, 570.5197f, (byte) 14);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//Electromagnetic Field [No-visible]
						spawn(858141, 217.7493f, 254.6473f, 570.5197f, (byte) 22);
						spawn(858141, 223.0010f, 251.0532f, 570.5197f, (byte) 14);
						spawn(858141, 211.4746f, 256.1063f, 570.5197f, (byte) 30);
						spawn(858141, 205.1906f, 254.8265f, 570.5197f, (byte) 38);
						spawn(858141, 199.9133f, 250.9296f, 570.5197f, (byte) 46);
						spawn(858141, 228.9603f, 256.4793f, 570.5197f, (byte) 14);
						spawn(858141, 221.1719f, 262.1674f, 570.5197f, (byte) 22);
						spawn(858141, 211.3520f, 264.1371f, 570.5197f, (byte) 30);
						spawn(858141, 193.9385f, 256.5212f, 570.5197f, (byte) 46);
						spawn(858149, 198.5929f, 269.6768f, 570.5197f, (byte) 38);
						spawn(858141, 224.3753f, 269.5832f, 570.5197f, (byte) 22);
						spawn(858141, 235.0162f, 261.8508f, 570.5197f, (byte) 14);
					}
				}, 3000);
			break;
			case 4:
			    //Electromagnetic Field [Visible Ground]
				spawn(858142, 217.7493f, 254.6473f, 570.5197f, (byte) 22);
				spawn(858142, 223.0010f, 251.0532f, 570.5197f, (byte) 14);
				spawn(858142, 211.4746f, 256.1063f, 570.5197f, (byte) 30);
				spawn(858142, 205.1906f, 254.8265f, 570.5197f, (byte) 38);
				spawn(858142, 199.9133f, 250.9296f, 570.5197f, (byte) 46);
				spawn(858143, 221.1719f, 262.1674f, 570.5197f, (byte) 22);
				spawn(858143, 211.3520f, 264.1371f, 570.5197f, (byte) 30);
				spawn(858143, 201.7149f, 262.1583f, 570.5197f, (byte) 38);
				spawn(858143, 193.9385f, 256.5212f, 570.5197f, (byte) 46);
				spawn(858144, 187.8526f, 261.8430f, 570.5197f, (byte) 46);
				spawn(858144, 198.5929f, 269.6768f, 570.5197f, (byte) 38);
				spawn(858144, 211.4102f, 272.4322f, 570.5197f, (byte) 30);
				spawn(858227, 235.0162f, 261.8508f, 570.5197f, (byte) 14); //safe zone
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//Electromagnetic Field [No-visible]
						spawn(858141, 217.7493f, 254.6473f, 570.5197f, (byte) 22);
						spawn(858141, 223.0010f, 251.0532f, 570.5197f, (byte) 14);
						spawn(858141, 211.4746f, 256.1063f, 570.5197f, (byte) 30);
						spawn(858141, 205.1906f, 254.8265f, 570.5197f, (byte) 38);
						spawn(858141, 199.9133f, 250.9296f, 570.5197f, (byte) 46);
						spawn(858141, 221.1719f, 262.1674f, 570.5197f, (byte) 22);
						spawn(858141, 211.3520f, 264.1371f, 570.5197f, (byte) 30);
						spawn(858141, 201.7149f, 262.1583f, 570.5197f, (byte) 38);
						spawn(858141, 193.9385f, 256.5212f, 570.5197f, (byte) 46);
						spawn(858141, 187.8526f, 261.8430f, 570.5197f, (byte) 46);
						spawn(858141, 198.5929f, 269.6768f, 570.5197f, (byte) 38);
						spawn(858141, 211.4102f, 272.4322f, 570.5197f, (byte) 30);
						spawn(858149, 235.0162f, 261.8508f, 570.5197f, (byte) 14);
					}
				}, 3000);
			break;
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(858141));
				deleteNpcs(instance.getNpcs(858142));
				deleteNpcs(instance.getNpcs(858143));
				deleteNpcs(instance.getNpcs(858144));
				deleteNpcs(instance.getNpcs(858149));
				deleteNpcs(instance.getNpcs(858153));
				deleteNpcs(instance.getNpcs(858226));
				deleteNpcs(instance.getNpcs(858227));
			}
		}, 25000);
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
		//percents.clear();
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		//addPercent();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		//addPercent();
		canThink = true;
		isHome.set(true);
		//curentPercent = 100;
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		//killNpc(instance.getNpcs(858135));
		//killNpc(instance.getNpcs(858136));
		deleteNpcs(instance.getNpcs(858141));
		deleteNpcs(instance.getNpcs(858142));
		deleteNpcs(instance.getNpcs(858143));
		deleteNpcs(instance.getNpcs(858144));
		deleteNpcs(instance.getNpcs(858149));
		deleteNpcs(instance.getNpcs(858153));
		deleteNpcs(instance.getNpcs(858226));
		deleteNpcs(instance.getNpcs(858227));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		//percents.clear();
		//I lost? Impossible!
		sendMsg(1502330, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		//killNpc(instance.getNpcs(858135));
		//killNpc(instance.getNpcs(858136));
		deleteNpcs(instance.getNpcs(858141));
		deleteNpcs(instance.getNpcs(858142));
		deleteNpcs(instance.getNpcs(858143));
		deleteNpcs(instance.getNpcs(858144));
		deleteNpcs(instance.getNpcs(858149));
		deleteNpcs(instance.getNpcs(858153));
		deleteNpcs(instance.getNpcs(858226));
		deleteNpcs(instance.getNpcs(858227));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}