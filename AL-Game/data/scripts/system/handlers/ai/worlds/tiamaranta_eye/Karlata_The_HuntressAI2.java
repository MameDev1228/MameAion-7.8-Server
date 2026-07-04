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

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Karlata_The_Huntress")
public class Karlata_The_HuntressAI2 extends AggressiveNpcAI2
{
	private Future<?> hideTask;
	private boolean canThink = true;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			karlataHideTask();
		}
	}
	
	private void karlataHideTask() {
		hideTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					karlataHide();
				}
			}
		}, 20000, 20000);
	}
	
	private void karlataHide() {
		canThink = false;
		//These are the Daevas sowing worry in our ranks? A jest, I am sure!
		sendMsg(1500397, getObjectId(), false, 0);
		SkillEngine.getInstance().getSkill(getOwner(), 19660, 60, getOwner()).useNoAnimationSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//Thy hand is too slow!
				sendMsg(1500398, getObjectId(), false, 2000);
				//Here, you fools!
				sendMsg(1500399, getObjectId(), false, 5000);
				getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
				getOwner().setVisualState(CreatureVisualState.HIDE1);
				PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()));
			}
		}, 2000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//Nyahahahaha!
				sendMsg(1500400, getObjectId(), false, 0);
				getOwner().getEffectController().removeAllEffects();
				getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
				getOwner().unsetVisualState(CreatureVisualState.HIDE1);
				PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()));
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
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							SkillEngine.getInstance().getSkill(getOwner(), 19661, 60, getTarget()).useNoAnimationSkill(); //Divine Grasp.
						}
					}, 3000);
				}
			}
		}, 10000);
	}
	
	private void cancelTask() {
		if (hideTask != null && !hideTask.isCancelled()) {
			hideTask.cancel(true);
		}
	}
	
	@Override
    protected void handleDespawned() {
        super.handleDespawned();
		cancelTask();
    }
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
	}
	
	@Override
    protected void handleBackHome() {
        super.handleBackHome();
		cancelTask();
		canThink = true;
		isHome.set(true);
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		cancelTask();
		//What have things come to?
		sendMsg(1500401, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}