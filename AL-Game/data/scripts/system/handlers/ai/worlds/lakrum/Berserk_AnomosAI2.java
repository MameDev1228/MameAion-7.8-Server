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
package ai.worlds.lakrum;

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
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Berserk_Anomos")
public class Berserk_AnomosAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private Future<?> sendPacketTask;
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
					chooseAttack();
				}
			}
		}, 5000, 40000);
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 3)) {
			case 1:
			    frigidaReconnaissanceSoldier();
			break;
			case 2:
			    frigidaReconnaissanceSniper();
			break;
			case 3:
			    frigidaReconaissanceMedic();
			break;
		}
	}
	
	private void frigidaReconnaissanceSoldier() {
		//죽인다… 데바 … 죽인다… 죽여야 한다.
		sendMsg(1502236, getObjectId(), false, 0);
		//나는… 산다… 에레슈키갈님을… 위해.
		sendMsg(1502237, getObjectId(), false, 4000);
		//최강의… 정찰대장 아노… 모스.
		sendMsg(1502238, getObjectId(), false, 8000);
		//Shock Wave.
		AI2Actions.useSkill(this, 17269);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(655433) == null &&
				    getPosition().getWorldMapInstance().getNpc(655434) == null &&
					getPosition().getWorldMapInstance().getNpc(655435) == null) {
					rndSpawn(655433, 3);
				}
			}
		}, 2500);
	}
	private void frigidaReconnaissanceSniper() {
		//죽인다… 데바 … 죽인다… 죽여야 한다.
		sendMsg(1502236, getObjectId(), false, 0);
		//나는… 산다… 에레슈키갈님을… 위해.
		sendMsg(1502237, getObjectId(), false, 4000);
		//최강의… 정찰대장 아노… 모스.
		sendMsg(1502238, getObjectId(), false, 8000);
		//Shock Wave.
		AI2Actions.useSkill(this, 17269);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(655433) == null &&
				    getPosition().getWorldMapInstance().getNpc(655434) == null &&
					getPosition().getWorldMapInstance().getNpc(655435) == null) {
					rndSpawn(655434, 3);
				}
			}
		}, 2500);
	}
	private void frigidaReconaissanceMedic() {
		//죽인다… 데바 … 죽인다… 죽여야 한다.
		sendMsg(1502236, getObjectId(), false, 0);
		//나는… 산다… 에레슈키갈님을… 위해.
		sendMsg(1502237, getObjectId(), false, 4000);
		//최강의… 정찰대장 아노… 모스.
		sendMsg(1502238, getObjectId(), false, 8000);
		//Shock Wave.
		AI2Actions.useSkill(this, 17269);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(655433) == null &&
				    getPosition().getWorldMapInstance().getNpc(655434) == null &&
					getPosition().getWorldMapInstance().getNpc(655435) == null) {
					rndSpawn(655435, 3);
				}
			}
		}, 2500);
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
	
	@Override
    protected void handleSpawned() {
        super.handleSpawned();
		announceLDF7RaidStart();
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
	
	private void announceLDF7RaidStart() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//제 39군단 전초기지 앞 에서 알수 없는 힘이 감지되고 있습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_Event_LDF7_Raid_01, 0);
				//자아를 잃은 아노모스가 등장했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_Event_LDF7_Raid_02, 10000);
			}
		});
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
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
		cancelTask();
		isHome.set(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(655433));
		killNpc(instance.getNpcs(655434));
		killNpc(instance.getNpcs(655435));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		treasureChest();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				switch (Rnd.get(1, 2)) {
				    case 1:
						rndSpawn(656301, 2);
					break;
					case 2:
						rndSpawn(656302, 2);
					break;
				}
			}
		}, 10000);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(655433));
		killNpc(instance.getNpcs(655434));
		killNpc(instance.getNpcs(655435));
		DiedEventHandler.onDie(this);
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
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
	
	@Override
	public void think() {
		ThinkEventHandler.onThink(this);
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
			case SHOULD_RESPAWN:
			    return AIAnswers.POSITIVE;
			case SHOULD_REWARD:
			    return AIAnswers.POSITIVE;
			case SHOULD_REWARD_AP:
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