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
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetChannellingEffect")
public class TargetChannellingEffect extends AbstractOverTimeEffect
{
	@XmlAttribute
	protected Func mode = Func.ADD;
	
	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);
		final Creature effected = effect.getEffected();
		final Player effector = (Player) effect.getEffector();
		effector.getController().startStance(effect.getSkillId());
		PacketSendUtility.sendPacket(effector, new SM_PLAYER_STANCE(effector, 11));
		effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
		//Painful Paint.
		if (effect.getSkillId() == 5397 || effect.getSkillId() == 5398 || effect.getSkillId() == 5399 ||
			effect.getSkillId() == 5400 || effect.getSkillId() == 5401 || effect.getSkillId() == 5402 ||
			effect.getSkillId() == 5403 || effect.getSkillId() == 5404 || effect.getSkillId() == 5405 ||
			effect.getSkillId() == 5406) {
			effected.getMoveController().abortMove();
			effected.getController().cancelCurrentSkill();
			effect.setAbnormal(AbnormalState.OPENAERIAL.getId());
			effected.getEffectController().setAbnormal(AbnormalState.OPENAERIAL.getId());
		}
		//Chromatic Freedom & (Advanced) Chromatic Freedom.
		if (effect.getSkillId() == 5427 || effect.getSkillId() == 6287) {
			if (effected.isInState(CreatureState.RESTING)) {
				effected.unsetState(CreatureState.RESTING);
			}
			effected.getMoveController().abortMove();
			effected.getController().cancelCurrentSkill();
			effect.setAbnormal(AbnormalState.PETRIFICATION.getId());
			effected.getEffectController().setAbnormal(AbnormalState.PETRIFICATION.getId());
			PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
		}
		//Chromatic Time & Chroma Gravity & (Advanced) Chroma Gravity.
		if (effect.getSkillId() == 5428 || effect.getSkillId() == 5599 || effect.getSkillId() == 5844) {
			if (effect.getEffected().isFlying() || effect.getEffected().isInState(CreatureState.GLIDING)) {
				PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
				effected.getMoveController().abortMove();
			}
			effect.setAbnormal(AbnormalState.SNARE.getId());
			effected.getEffectController().setAbnormal(AbnormalState.SNARE.getId());
		}
		//Time Shackles & (Advanced) Time Shackles.
		if (effect.getSkillId() == 5600 || effect.getSkillId() == 5845) {
			if (effected.isInState(CreatureState.RESTING)) {
				effected.unsetState(CreatureState.RESTING);
			}
			effected.getMoveController().abortMove();
			effected.getController().cancelCurrentSkill();
			effect.setAbnormal(AbnormalState.ROOT.getId());
			effected.getEffectController().setAbnormal(AbnormalState.ROOT.getId());
			PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
			ActionObserver observer = new ActionObserver(ObserverType.ATTACKED) {
				@Override
				public void attacked(Creature creature) {
					if (Rnd.get(0, 100) > 25) {
						effected.getEffectController().removeEffect(effect.getSkillId());
					}
				}
			};
			effected.getObserveController().addObserver(observer);
			effect.setActionObserver(observer, position);
		}
	}
	
	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		final Player effector = (Player) effect.getEffector();
		effector.getController().startStance(0);
		PacketSendUtility.sendPacket(effector, new SM_PLAYER_STANCE(effector, 0));
		PacketSendUtility.sendPacket((Player) effector, new SM_SKILL_ACTIVATION(effect.getSkillId(), false));
		//Painful Paint.
		if (effect.getSkillId() == 5397 || effect.getSkillId() == 5398 || effect.getSkillId() == 5399 ||
		    effect.getSkillId() == 5400 || effect.getSkillId() == 5401 || effect.getSkillId() == 5402 ||
			effect.getSkillId() == 5403 || effect.getSkillId() == 5404 || effect.getSkillId() == 5405 || effect.getSkillId() == 5406) {
			effect.setIsPhysicalState(false);
			effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.OPENAERIAL.getId());
		}
		//Chromatic Freedom & (Advanced) Chromatic Freedom.
		if (effect.getSkillId() == 5427 || effect.getSkillId() == 6287) {
			effect.setIsPhysicalState(false);
			effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.PETRIFICATION.getId());
		}
		//Chromatic Time & Chroma Gravity & (Advanced) Chroma Gravity.
		if (effect.getSkillId() == 5428 || effect.getSkillId() == 5599 || effect.getSkillId() == 5844) {
			effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.SNARE.getId());
		}
		//Time Shackles & (Advanced) Time Shackles.
		if (effect.getSkillId() == 5600 || effect.getSkillId() == 5845) {
			ActionObserver observer = effect.getActionObserver(position);
			if (observer != null) {
				effect.getEffected().getObserveController().removeObserver(observer);
			}
			effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.ROOT.getId());
		}
	}
	
	public boolean calculate(Effect effect, DamageType damageType) {
        if (!super.calculate(effect, null, null)) {
            return false;
        }
		effect.addSucessEffect(this);
        int skillLvl = effect.getSkillLevel();
        int valueWithDelta = value + delta * skillLvl;
        ActionModifier modifier = getActionModifiers(effect);
        int accMod = this.accMod2 + this.accMod1 * skillLvl;
        int critAddDmg = this.critAddDmg2 + this.critAddDmg1 * skillLvl;
        switch (damageType) {
			case PHYSICAL:
                AttackUtil.calculateSkillResult(effect, valueWithDelta, modifier, this.getMode(), 0, accMod, this.critProbMod2, critAddDmg, true, false, false, false);
            break;
		}
        return true;
    }
	
	@Override
	public void onPeriodicAction(final Effect effect) {
		final Creature effector = effect.getEffector();
		//Colorful Jab.
		if (effect.getSkillId() == 5377 || effect.getSkillId() == 5378 || effect.getSkillId() == 5379 ||
		    effect.getSkillId() == 5380 || effect.getSkillId() == 5381 || effect.getSkillId() == 5382 ||
			effect.getSkillId() == 5383 || effect.getSkillId() == 5384 ||
			//Chromatic Wrath & Chroma Endurance.
			effect.getSkillId() == 5590 || effect.getSkillId() == 5591 ||
			//(Advanced) Chromatic Wrath & (Advanced) Chroma Endurance & (Advanced) Colorful Jab.
			effect.getSkillId() == 5836 || effect.getSkillId() == 5837 || effect.getSkillId() == 6297) {
			effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
			effect.getEffector().getLifeStats().increaseHp(TYPE.HP, effect.getReserved1() * 5 / 100, effect.getSkillId(), LOG.SPELLATKDRAIN);
			effect.getEffected().getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, effect.getReserved1(), false, LOG.ATTACK);
		}
		//Angry Colors.
		if (effect.getSkillId() == 5385 || effect.getSkillId() == 5386 || effect.getSkillId() == 5387 ||
		    effect.getSkillId() == 5388 || effect.getSkillId() == 5389 || effect.getSkillId() == 5390 ||
			effect.getSkillId() == 5391 || effect.getSkillId() == 5392 || effect.getSkillId() == 5393 ||
			effect.getSkillId() == 5394 || effect.getSkillId() == 5395 || effect.getSkillId() == 5396 ||
			//Painful Paint.
			effect.getSkillId() == 5397 || effect.getSkillId() == 5398 || effect.getSkillId() == 5399 ||
		    effect.getSkillId() == 5400 || effect.getSkillId() == 5401 || effect.getSkillId() == 5402 ||
			effect.getSkillId() == 5403 || effect.getSkillId() == 5404 || effect.getSkillId() == 5405 || effect.getSkillId() == 5406) {
			effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
		    effect.getEffected().getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, effect.getReserved1(), false, LOG.ATTACK);
		}
		//Chromatic Time & Chroma Gravity.
		if (effect.getSkillId() == 5428 || effect.getSkillId() == 5599) {
			effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
			effect.getEffected().getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, 7770, false, LOG.ATTACK);
		}
		//(Advanced) Chroma Gravity.
		if (effect.getSkillId() == 5844) {
			effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
			effect.getEffected().getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, 13990, false, LOG.ATTACK);
		}
		//Time Shackles.
		if (effect.getSkillId() == 5600) {
			effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
			effect.getEffected().getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, 7770, false, LOG.ATTACK);
		}
		//(Advanced) Time Shackles.
		if (effect.getSkillId() == 5845) {
			effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
			effect.getEffected().getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, 11180, false, LOG.ATTACK);
		}
	}
	
	public Func getMode() {
		return mode;
	}
}