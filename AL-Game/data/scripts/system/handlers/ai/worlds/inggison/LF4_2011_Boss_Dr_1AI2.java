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
package ai.worlds.inggison;

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

@AIName("LF4_2011_Boss_Dr_1")
public class LF4_2011_Boss_Dr_1AI2 extends AggressiveNpcAI2
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
				Npc siegeGate1 = getPosition().getWorldMapInstance().getNpc(257194);
				Npc siegeGate2 = getPosition().getWorldMapInstance().getNpc(257241);
				if (siegeGate1 == null && siegeGate2 == null) {
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
							beritraFavor();
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
	
	private void protectiveShield() {
   		SkillEngine.getInstance().getSkill(getOwner(), 21791, 60, getOwner()).useNoAnimationSkill();
 	}
	
	private void beritraFavor() {
		SkillEngine.getInstance().getSkill(getOwner(), 21135, 60, getOwner()).useNoAnimationSkill();
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