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
package ai.worlds.tiamaranta_eye;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.*;
import com.aionemu.gameserver.ai2.manager.*;
import com.aionemu.gameserver.ai2.poll.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("sunayaka")
public class SunayakaAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private Future<?> sendPacketTask;
	private Future<?> sunayakaRageTask;
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	
	private void simmeringRage() {
   		getOwner().getEffectController().removeEffect(8763);
		SkillEngine.getInstance().getSkill(getOwner(), 20651, 1, getOwner()).useNoAnimationSkill(); //Simmering Rage.
 	}
	
 	private void rageOfTheDragonLords() {
   		SkillEngine.getInstance().getSkill(getOwner(), 8763, 1, getOwner()).useNoAnimationSkill(); //Rage Of The Dragon Lords
 	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			switch (getNpcId()) {
				case 218553: //Governor Sunayaka.
				case 219311: //Berserker Sunayaka.
					//Berserker Sunayaka goes berserk 15 minutes after the battle starts.
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1401459, 0);
					getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.isOnline()) {
								PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 900)); //15 Minutes.
							}
						}
					});
					sunayakaRageTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							//Berserker Sunayaka has gone berserk.
							NpcShoutsService.getInstance().sendMsg(getOwner(), 1401460, 0);
							getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									simmeringRage();
									if (player.isOnline()) {
										PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
									}
								}
							});
						}
					}, 900000); //15Min...
				break;
			}
		}
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void cancelTask() {
        if (sendPacketTask != null && !sendPacketTask.isCancelled()) {
            sendPacketTask.cancel(true);
        }
    }
	
	private void cancelSunayakaRageTask() {
		if (sunayakaRageTask != null && !sunayakaRageTask.isDone()) {
			sunayakaRageTask.cancel(true);
		}
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
  		rageOfTheDragonLords();
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
	protected void handleDespawned() {
		super.handleDespawned();
		cancelSunayakaRageTask();
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
            @Override
            public void visit(final Player player) {
                sendPacketTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getWorldId() == getOwner().getWorldId()) {
                            PacketSendUtility.sendPacket(player, new SM_FLAG_UPDATE(getOwner()));
                            getOwner().getController().onDelete();
                        }
                    }
                }, 1000, 2000);
            }
        });
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
				}
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
		canThink = true;
		isAggred.set(false);
		cancelSunayakaRageTask();
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
				}
			}
		});
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		treasureChest();
		addGp2000Player();
		cancelSunayakaRageTask();
		DiedEventHandler.onDie(this);
		ConquestService.getInstance().stopConquest(24);
		ConquestService.getInstance().stopConquest(25);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
			    rndSpawn(701481, 4);
			}
		}, 10000);
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 0));
				}
			}
		});
	}
	
	private void addGp2000Player() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (MathUtil.isIn3dRange(player, getOwner(), 25)) {
					AbyssPointsService.addGp(player, 2000);
				}
			}
		});
	}
	private void treasureChest() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//A treasure chest has appeared.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDAbRe_Core_NmdC_BoxSpawn, 10000);
			}
		});
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
	
	@Override
    protected void handleCreatureSee(Creature creature) {
        CreatureEventHandler.onCreatureSee(this, creature);
    }
	
	@Override
	protected void handleCreatureAggro(Creature creature) {
		if (canThink()) {
		    AggroEventHandler.onAggro(this, creature);
		}
	}
	
	@Override
	protected void handleFinishAttack() {
		AttackEventHandler.onFinishAttack(this);
	}
	
	@Override
	protected void handleAttackComplete() {
		AttackEventHandler.onAttackComplete(this);
	}
	
	@Override
    protected void handleTargetGiveup() {
        TargetEventHandler.onTargetGiveup(this);
    }
	
    @Override
    protected void handleTargetChanged(Creature creature) {
        TargetEventHandler.onTargetChange(this, creature);
    }
	
	@Override
	protected boolean handleGuardAgainstAttacker(Creature attacker) {
		return AggroEventHandler.onGuardAgainstAttacker(this, attacker);
	}
	
	@Override
	protected boolean handleCreatureNeedsSupport(Creature creature) {
		return AggroEventHandler.onCreatureNeedsSupport(this, creature);
	}
	
	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
			    return AIAnswers.POSITIVE;
			case SHOULD_REWARD:
			    return AIAnswers.POSITIVE;
			case SHOULD_REWARD_AP:
			    return AIAnswers.POSITIVE;
			case SHOULD_REWARD_GP:
			    return AIAnswers.POSITIVE;
			case CAN_RESIST_ABNORMAL:
			    return AIAnswers.POSITIVE;
			case CAN_ATTACK_PLAYER:
			    return AIAnswers.POSITIVE;
			default:
				return null;
		}
	}
	
	@Override
	public AttackIntention chooseAttackIntention() {
		VisibleObject currentTarget = getTarget();
		Creature mostHated = getAggroList().getMostHated();
		if (mostHated == null || mostHated.getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		} if (currentTarget == null || !currentTarget.getObjectId().equals(mostHated.getObjectId())) {
			onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
			return AttackIntention.SWITCH_TARGET;
		} if (getOwner().getObjectTemplate().getAttackRange() == 0) {
			NpcSkillEntry skill = getOwner().getSkillList().getRandomSkill();
			if (skill != null) {
				skillId = skill.getSkillId();
				skillLevel = skill.getSkillLevel();
				return AttackIntention.SKILL_ATTACK;
			}
		} else {
			NpcSkillEntry skill = SkillAttackManager.chooseNextSkill(this);
			if (skill != null) {
				skillId = skill.getSkillId();
				skillLevel = skill.getSkillLevel();
				return AttackIntention.SKILL_ATTACK;
			}
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
}