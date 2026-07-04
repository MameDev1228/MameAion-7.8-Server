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
package ai.worlds.abyss_core.divineFortress;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Guardian_Deity_General_Orissan")
public class Guardian_Deity_General_OrissanAI2 extends AggressiveNpcAI2
{
	private Future<?> checkTask;
	private boolean canThink = true;
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void checkTurningTide() {
		checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Npc gateProtection1 = getPosition().getWorldMapInstance().getNpc(835769);
				Npc gateProtection2 = getPosition().getWorldMapInstance().getNpc(835770);
				Npc gateProtection3 = getPosition().getWorldMapInstance().getNpc(835771);
				Npc gateProtection4 = getPosition().getWorldMapInstance().getNpc(835772);
				Npc gateProtection5 = getPosition().getWorldMapInstance().getNpc(835773);
				if (gateProtection1 == null && gateProtection2 == null && gateProtection3 == null &&
				    gateProtection4 == null && gateProtection5 == null) {
					canThink = true;
					checkTask.cancel(true);
					for (Player player: getKnownList().getKnownPlayers().values()) {
						//The Guardian General Buff has been deactivated.
						PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_BLDF5_Fortress_GuardianHead_65_Al_PD, 0);
						//The Guardian General's Turning Tide Shield has been deactivated.
						PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_BLDF5_Fortress_GuardianHead_65_Al_Soff, 5000);
					}
					EffectController ef = getOwner().getEffectController();
					if (ef.hasAbnormalEffect(21791)) {
						ef.removeEffect(21791);
					}
				  	Creature creature = getAggroList().getMostHated();
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							getOwner().getEffectController().unsetAbnormal(AbnormalState.SLEEP.getId());
						}
					}, 3000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							overwhelmed();
							unsealedDivine();
							ereshkigalFury();
						}
					}, 5000);
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
	
	private void unsealedDivine() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//There is an ominous chill hovering over the fortress.\nYou must prepare for battle.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_Ab1_Page2_01, 5000);
				//The Returned Orissan Legion has appeared.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_Ab1_Page2_02, 15000);
			}
		});
	}
	
	private void protectiveShield() {
   		SkillEngine.getInstance().getSkill(getOwner(), 21791, 60, getOwner()).useNoAnimationSkill();
 	}
	
	private void overwhelmed() {
	    SkillEngine.getInstance().getSkill(getOwner(), 22681, 60, getOwner()).useNoAnimationSkill(); //Overwhelmed.
	}
	
	private void ereshkigalFury() {
	    SkillEngine.getInstance().getSkill(getOwner(), 22682, 60, getOwner()).useNoAnimationSkill(); //Ereshkigal's Fury.
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		checkTask.cancel(true);
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		protectiveShield();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				checkTurningTide();
				getOwner().getEffectController().setAbnormal(AbnormalState.SLEEP.getId());
			}
		}, 10000);
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		canThink = true;
		checkTask.cancel(true);
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		checkTask.cancel(true);
		getOwner().getEffectController().removeAllEffects();
	}
}