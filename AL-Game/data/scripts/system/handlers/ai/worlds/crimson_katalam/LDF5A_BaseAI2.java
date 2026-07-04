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
package ai.worlds.crimson_katalam;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.*;
import com.aionemu.gameserver.ai2.manager.*;
import com.aionemu.gameserver.ai2.poll.*;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("LDF5A_Base")
public class LDF5A_BaseAI2 extends AggressiveNpcAI2
{
	@Override
	public void think() {
		ThinkEventHandler.onThink(this);
	}
	
	@Override
    protected void handleSpawned() {
        super.handleSpawned();
		switch (getNpcId()) {
			//V701
			case 662500:
			    ldf5aUpV01();
			break;
			//V702
			case 662516:
			    ldf5aUpV02();
			break;
			//V703
			case 662532:
				ldf5aUpV03();
			break;
			//V704
			case 662548:
				ldf5aUpV04();
			break;
			//V705
			case 662564:
				ldf5aUpV05();
			break;
			//V706
			case 662580:
				ldf5aUpV06();
			break;
			//V707
			case 662596:
				ldf5aUpV07();
			break;
			//V708
			case 662612:
				ldf5aUpV08();
			break;
			//V709
			case 662628:
				ldf5aUpV09();
			break;
			//V710
			case 662644:
				ldf5aUpV10();
			break;
			//V711
			case 662660:
				ldf5aUpV11();
			break;
			//V712
			case 662676:
				ldf5aUpV12();
			break;
			//V713
			case 662692:
				ldf5aUpV13();
			break;
		}
    }
	
	@Override
	protected void handleDied() {
		DiedEventHandler.onDie(this);
		switch (getNpcId()) {
			
			
		}
		super.handleDied();
	}
	
	private void ldf5aUpV01() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제701 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv01, 0);
				//제701 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v01, 8000);
			}
		});
	}
	private void ldf5aUpV02() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제702 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv02, 0);
				//제702 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v02, 8000);
			}
		});
	}
	private void ldf5aUpV03() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제703 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv03, 0);
				//제703 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v03, 8000);
			}
		});
	}
	private void ldf5aUpV04() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제704 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv04, 0);
				//제704 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v04, 8000);
			}
		});
	}
	private void ldf5aUpV05() {	
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제705 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv05, 0);
				//제705 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v05, 8000);
			}
		});
	}
	private void ldf5aUpV06() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제706 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv06, 0);
				//제706 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v06, 8000);
			}
		});
	}
	private void ldf5aUpV07() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제707 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv07, 0);
				//제707 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v07, 8000);
			}
		});
	}
	private void ldf5aUpV08() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제708 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv08, 0);
				//제708 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v08, 8000);
			}
		});
	}
	private void ldf5aUpV09() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제709 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv09, 0);
				//제709 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v09, 8000);
			}
		});
	}
	private void ldf5aUpV10() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제710 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv10, 0);
				//제710 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v10, 8000);
			}
		});
	}
	private void ldf5aUpV11() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제711 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv11, 0);
				//제711 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v11, 8000);
			}
		});
	}
	private void ldf5aUpV12() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제712 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv12, 0);
				//제712 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v12, 8000);
			}
		});
	}
	private void ldf5aUpV13() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//브리트라 군단이 제713 기지를 점령하였습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_dr_eliv13, 0);
				//제713 기지의 점령 레벨(5레벨)이 최대치에 도달했습니다.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF5a_up5_v13, 8000);
			}
		});
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
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