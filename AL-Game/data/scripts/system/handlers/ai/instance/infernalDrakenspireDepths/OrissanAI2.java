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
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
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

@AIName("orissan")
public class OrissanAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private boolean canThink = true;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			ascensionDomination();
		} if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{40});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				percents.remove(percent);
				canThink = false;
				//Ugh! I can't control my power...!
				sendMsg(1501305, getObjectId(), false, 0);
				//I can't feel the blessing of Lord Ereshkigal!
				sendMsg(1501307, getObjectId(), false, 4000);
			}
			break;
		}
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
		switch (getNpcId()) {
			case 654673: //Exhausted Orissan.
			case 654674: //Reverted Orissan.
			case 654676: //Exhausted Orissan.
			    canThink = true;
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
			break;
			case 654672: //Immortal Orissan.
			case 654675: //Immortal Orissan.
				getOwner().setNpcType(NpcType.PEACE);
				//Immortal Orissan cannot be killed. The Ascended state lasts until Orissan becomes exhausted.
				PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_03, 0);
				//Orissan has become exhausted by Ascension Dominance.
				PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_05, 3000);
			break;
		}
	}
	
	private void ascensionDomination() {
        if (!isAlreadyDead()) {
            switch (getNpcId()) {
                case 654671: //Orissan.
				case 654672: //Immortal Orissan.
				case 654673: //Exhausted Orissan.
				case 654674: //Reverted Orissan.
				case 654675: //Immortal Orissan.
				case 654676: //Exhausted Orissan.
					//I am blessed by Lord Ereshkigal!
                    sendMsg(1501303, 0);
					//Do not expect mercy from Orissan!
                    sendMsg(1501304, 4000);
					//Ascension or no, I can take you!
                    sendMsg(1501306, 8000);
                break;
            }
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!isAlreadyDead()) {
						//Orissan.
						if (getNpcId() == 654671) {
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										canThink = false;
										getOwner().getController().abortCast();
										EmoteManager.emoteStopAttacking(getOwner());
										getOwner().getController().cancelCurrentSkill();
										//Orissan will soon be exhausted by Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_04, 2000);
                                    }
                                }
                            }, 175000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										//Orissan begins to Ascend through Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_01, 0);
										SkillEngine.getInstance().getSkill(getOwner(), 21885, 60, getOwner()).useNoAnimationSkill(); //Weaken Ascension Domination.
										setStateIfNot(AIState.WALKING);
										getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
										WalkManager.startWalking(OrissanAI2.this);
										getOwner().setState(1);
										PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
                                    }
                                }
                            }, 180000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										WorldMapInstance instance1 = getPosition().getWorldMapInstance();
										deleteNpcs(instance1.getNpcs(654671));
										//Orissan has Ascended through Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_02, 0);
										spawn(283174, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
										spawn(654672, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
                                    }
                                }
                            }, 182000);
                        }
						//Immortal Orissan.
						if (getNpcId() == 654672) {
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										//Orissan begins to Ascend through Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_01, 0);
										SkillEngine.getInstance().getSkill(getOwner(), 21885, 60, getOwner()).useNoAnimationSkill(); //Weaken Ascension Domination.
                                    }
                                }
                            }, 120000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										WorldMapInstance instance2 = getPosition().getWorldMapInstance();
										deleteNpcs(instance2.getNpcs(654672));
										//Orissan has Ascended through Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_02, 0);
										spawn(856554, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
										spawn(654673, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
                                    }
                                }
                            }, 122000);
                        }
						//Exhausted Orissan.
						if (getNpcId() == 654673) {
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										canThink = false;
										getOwner().getController().abortCast();
										EmoteManager.emoteStopAttacking(getOwner());
										getOwner().getController().cancelCurrentSkill();
										//Orissan begins to Ascend through Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_01, 0);
                                    }
                                }
                            }, 175000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										//Slay Orissan before the next Ascension Dominance begins.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_06, 0);
										SkillEngine.getInstance().getSkill(getOwner(), 21885, 60, getOwner()).useNoAnimationSkill(); //Weaken Ascension Domination.
										setStateIfNot(AIState.WALKING);
										getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
										WalkManager.startWalking(OrissanAI2.this);
										getOwner().setState(1);
										PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
                                    }
                                }
                            }, 180000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										WorldMapInstance instance3 = getPosition().getWorldMapInstance();
										deleteNpcs(instance3.getNpcs(654673));
										//Orissan has Ascended through Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_02, 0);
										spawn(283174, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
										spawn(654674, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
                                    }
                                }
                            }, 182000);
                        }
						//Reverted Orissan.
						if (getNpcId() == 654674) {
						    ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										canThink = false;
										getOwner().getController().abortCast();
										EmoteManager.emoteStopAttacking(getOwner());
										getOwner().getController().cancelCurrentSkill();
										//Orissan begins to Ascend through Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_01, 0);
                                    }
                                }
                            }, 175000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										//Slay Orissan before the next Ascension Dominance begins.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_06, 0);
										SkillEngine.getInstance().getSkill(getOwner(), 21885, 60, getOwner()).useNoAnimationSkill(); //Weaken Ascension Domination.
										setStateIfNot(AIState.WALKING);
										getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
										WalkManager.startWalking(OrissanAI2.this);
										getOwner().setState(1);
										PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
                                    }
                                }
                            }, 180000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										WorldMapInstance instance4 = getPosition().getWorldMapInstance();
										deleteNpcs(instance4.getNpcs(654674));
										//Orissan has Ascended through Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_02, 0);
										spawn(283174, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
										spawn(654675, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
                                    }
                                }
                            }, 182000);
                        }
						//Immortal Orissan.
						if (getNpcId() == 654675) {
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										//Orissan begins to Ascend through Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_01, 0);
										SkillEngine.getInstance().getSkill(getOwner(), 21885, 60, getOwner()).useNoAnimationSkill(); //Weaken Ascension Domination.
                                    }
                                }
                            }, 120000);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
										WorldMapInstance instance5 = getPosition().getWorldMapInstance();
										deleteNpcs(instance5.getNpcs(654675));
										//Orissan has Ascended through Ascension Dominance.
										PacketSendUtility.npcSendPacketTime(getOwner(), SM_SYSTEM_MESSAGE.STR_MSG_IDSeal_Immortal_02, 0);
										spawn(856554, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
										spawn(654676, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
                                    }
                                }
                            }, 122000);
                        }
                    }
                }
            }, 1000);
        }
    }
	
	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					frostBomb();
				}
			}
		}, 5000, 120000);
	}
	
	private void frostBomb() {
		//Summon Crystal.
		AI2Actions.useSkill(this, 21635);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(855699) == null) {
					rndSpawn(855699, 3);
				}
			}
		}, 2500);
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
		cancelTask();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		addPercent();
		canThink = true;
		isHome.set(true);
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		percents.clear();
		//Lord Ereshkigal!!
		sendMsg(1501308, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
	}
	
	private void sendMsg(int msg, int delay) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, delay);
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}