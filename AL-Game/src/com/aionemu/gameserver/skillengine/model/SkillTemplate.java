/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.model;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.skillengine.action.Actions;
import com.aionemu.gameserver.skillengine.condition.ChainCondition;
import com.aionemu.gameserver.skillengine.condition.Condition;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.condition.HpCondition;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.EffectType;
import com.aionemu.gameserver.skillengine.effect.Effects;
import com.aionemu.gameserver.skillengine.periodicaction.PeriodicActions;
import com.aionemu.gameserver.skillengine.properties.Properties;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ATracer modified by Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "skillTemplate", propOrder = { "properties", "startconditions", "useconditions", "endconditions", "useequipmentconditions", "effects", "actions",
	"periodicActions", "motion" })
public class SkillTemplate {

	protected Properties properties;
	protected Conditions startconditions;
	protected Conditions useconditions;
	protected Conditions endconditions;
	protected Conditions useequipmentconditions;
	protected Effects effects;
	protected Actions actions;
	@XmlElement(name = "periodicactions")
	protected PeriodicActions periodicActions;
	protected Motion motion;

	@XmlAttribute(name = "skill_id", required = true)
	protected int skillId;
	@XmlAttribute(required = true)
	protected String name;
	@XmlAttribute(required = true)
	protected int nameId;
	@XmlAttribute
	protected String stack = "NONE";
	@XmlAttribute
	protected String skillgroup = "NONE";
	@XmlAttribute(name = "skill_group_name")
	protected String skill_group_name;
	@XmlAttribute(name = "group")
	protected String group;
	@XmlAttribute
	protected int delayId;
	@XmlAttribute
	protected int cooldownId;
	@XmlAttribute
	protected int lvl;
	@XmlAttribute(name = "skilltype", required = true)
	protected SkillType type = SkillType.NONE;
	@XmlAttribute(name = "skillsubtype", required = true)
	protected SkillSubType subType;
	@XmlAttribute(name = "skill_category")
	protected SkillCategory skillCategory = SkillCategory.NONE;
	@XmlAttribute(name = "tslot")
	protected SkillTargetSlot targetSlot;
	@XmlAttribute(name = "tslot_level")
	protected int targetSlotLevel;
	@XmlAttribute(name = "dispel_category")
	protected DispelCategoryType dispelCategory = DispelCategoryType.NONE;
	@XmlAttribute(name = "req_dispel_level")
	protected int reqDispelLevel;
	@XmlAttribute(name = "activation", required = true)
	protected ActivationAttribute activationAttribute;
	@XmlAttribute(required = true)
	protected int duration;
	@XmlAttribute(name = "toggle_timer")
    protected int toggleTimer;
	@XmlAttribute(name = "cooldown")
	protected int cooldown;
	@XmlAttribute(name = "cooldown_delta_lv")
	protected int cooldownDeltaLv;
	@XmlAttribute(name = "penalty_skill_id")
	protected int penaltySkillId;
	@XmlAttribute(name = "provoke_critical_id")
	protected int provokeCriticalId;
	@XmlAttribute(name = "provoke_skill_id")
	protected int provokeSkillId;
	@XmlAttribute(name = "pvp_damage")
	protected int pvpDamage;
	@XmlAttribute(name = "pvp_duration")
	protected int pvpDuration;
	@XmlAttribute(name = "chain_skill_prob", required = false)
	protected float chainSkillProb = 100f;
	@XmlAttribute(name = "cancel_rate")
	protected int cancelRate;
	@XmlAttribute(name = "stance")
	protected boolean stance;
	@XmlAttribute(name = "skillset_exception")
	protected int skillSetException;
	@XmlAttribute(name = "skillset_maxoccur")
	protected int skillSetMaxOccur;
	@XmlAttribute(name = "avatar")
	protected boolean isDeityAvatar;
	@XmlAttribute(name = "archdaeva")
	protected boolean isArchDaeva;
	@XmlAttribute(name = "battlefield")
	protected boolean isBattlefield;
	@XmlAttribute(name = "minion")
    protected boolean isMinion;
	@XmlAttribute(name = "is_minion_skill")
    protected boolean isMinionSkill;
	@XmlAttribute(name = "ground")
	protected boolean isGroundSkill;
	@XmlAttribute(name = "unpottable")
	protected boolean isUndispellableByPotions;
	@XmlAttribute(name = "ammospeed")
	protected int ammoSpeed;
	@XmlAttribute(name = "conflict_id")
	protected int conflictId;
	@XmlAttribute(name = "counter_skill")
	protected AttackStatus counterSkill = null;
	@XmlAttribute(name = "noremoveatdie")
	protected boolean noRemoveAtDie = false;
	@XmlAttribute(name = "no_save_on_logout")
	protected boolean noSaveOnLogout = false;
	@XmlAttribute(name = "provoke_after_critical")
	protected int provokeAfterCritical;
	@XmlAttribute(name = "provoke_self_after_critical")
	protected boolean provokeSelfAfterCritical;
	@XmlAttribute(name = "charge_set_name")
	protected String charge_set_name;
	@XmlAttribute(name = "stigma")
	protected StigmaType stigmaType = StigmaType.NONE;
	@XmlTransient
	protected HashMap<Integer, Integer> effectIds = null;
	@XmlAttribute(name = "use_battery")
	protected int useBattery;
	
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Gets the value of the startconditions property.
	 * 
	 * @return possible object is {@link Conditions }
	 */
	public Conditions getStartconditions() {
		return startconditions;
	}

	/**
	 * Gets the value of the useconditions property.
	 * 
	 * @return possible object is {@link Conditions }
	 */
	public Conditions getUseconditions() {
		return useconditions;
	}

	/**
	 * Gets the value of the endconditions property. ArchSoft-compatible optional schema hook.
	 */
	public Conditions getEndconditions() {
		return endconditions;
	}

	/**
	 * Gets the value of the useequipmentconditions property.
	 * 
	 * @return possible object is {@link Conditions }
	 */
	public Conditions getUseEquipmentconditions() {
		return useequipmentconditions;
	}

	/**
	 * Gets the value of the effects property.
	 * 
	 * @return possible object is {@link Effects }
	 */
	public Effects getEffects() {
		return effects;
	}

	/**
	 * Gets the value of the actions property.
	 * 
	 * @return possible object is {@link Actions }
	 */
	public Actions getActions() {
		return actions;
	}

	/**
	 * Gets the value of the periodicActions property.
	 * 
	 * @return possible object is {@link PeriodicActions }
	 */
	public PeriodicActions getPeriodicActions() {
		return periodicActions;
	}
	/**
	 * Gets the value of the motion property.
	 * 
	 * @return possible object is {@link Motion }
	 */
	public Motion getMotion() {
		return motion;
	}
	/**
	 * Gets the value of the skillId property.
	 */
	public int getSkillId() {
		return skillId;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the nameId
	 */
	public int getNameId() {
		return nameId;
	}

	/**
	 * @return the stack
	 */
	public String getStack() {
		return stack;
	}
	
	/**
	 * @return the group
	 */
	public String getGroup() {
		if (skill_group_name != null && !skill_group_name.isEmpty() && !"NONE".equals(skill_group_name)) {
			return skill_group_name;
		}
		if (group != null && !group.isEmpty() && !"NONE".equals(group)) {
			return group;
		}
		return skillgroup;
	}

	/**
	 * @return the lvl
	 */
	public int getLvl() {
		return lvl;
	}

	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link SkillType }
	 */
	public SkillType getType() {
		return type;
	}

	/**
	 * @return the subType
	 */
	public SkillSubType getSubType() {
		return subType;
	}

	public SkillCategory getSkillCategory() {
		return skillCategory;
	}

	/**
	 * @return the targetSlot
	 */
	public SkillTargetSlot getTargetSlot() {
		return targetSlot;
	}

	/**
	 * @return the targetSlot Level
	 */
	public int getTargetSlotLevel() {
		return targetSlotLevel;
	}

	/**
	 * @return the dispelCategory
	 */
	public DispelCategoryType getDispelCategory() {
		return dispelCategory;
	}

	/**
	 * @return the reqDispelLevel
	 */
	public int getReqDispelLevel() {
		return reqDispelLevel;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}
	
	public int getToggleTimer() {
        return toggleTimer;
    }
	
	public StigmaType getStigmaType() {
		return stigmaType;
	}

	/**
	 * @return the activationAttribute
	 */
	public ActivationAttribute getActivationAttribute() {
		return activationAttribute;
	}

	public boolean isPassive() {
		return activationAttribute == ActivationAttribute.PASSIVE;
	}

	public boolean isToggle() {
		return activationAttribute == ActivationAttribute.TOGGLE;
	}

	public boolean isProvoked() {
		return activationAttribute == ActivationAttribute.PROVOKED;
	}
	
	public boolean isMaintain() {
		return activationAttribute == ActivationAttribute.MAINTAIN;
	}

	public boolean isActive() {
		return activationAttribute == ActivationAttribute.ACTIVE;
	}

	/**
	 * @param position
	 * @return EffectTemplate
	 */
	public EffectTemplate getEffectTemplate(int position) {
		return effects != null && effects.getEffects().size() >= position ? effects.getEffects().get(position - 1) : null;

	}

	/**
	 * @return the cooldown
	 */
	public int getCooldown() {
		return cooldown;
	}

	/**
	 * ArchSoft-compatible cooldown calculation. Some 6.x/7.x templates store a
	 * base cooldown plus a per-level delta instead of writing every final level
	 * value directly into the cooldown attribute. Keep this as a pure scalar
	 * helper so CT ownership, delayId handling, and packet sync logic remain
	 * unchanged.
	 */
	public int getCooldownForLevel(int skillLevel) {
		int value = cooldown;
		if (cooldownDeltaLv != 0)
			value += cooldownDeltaLv * skillLevel;
		return Math.max(0, value);
	}

	public int getCooldownDeltaLv() {
		return cooldownDeltaLv;
	}

	/**
	 * @return the penaltySkillId
	 */
	public int getPenaltySkillId() {
		return penaltySkillId;
	}

	/**
	 * @return the provokeCriticalId
	 */
	public int getProvokeCriticalId() {
		return provokeCriticalId;
	}

	/**
	 * @return the provokeSkillId
	 */
	public int getProvokeSkillId() {
		return provokeSkillId;
	}

	/**
	 * @return the pvpDamage
	 */
	public int getPvpDamage() {
		return pvpDamage;
	}

	/**
	 * @return the pvpDuration
	 */
	public int getPvpDuration() {
		return pvpDuration;
	}

	/**
	 * @return chainSkillProb
	 */
	public float getChainSkillProb() {
		return chainSkillProb;
	}

	/**
	 * @return cancelRate
	 */
	public int getCancelRate() {
		return cancelRate;
	}

	/**
	 * @return stance
	 */
	public boolean isStance() {
		return stance;
	}
	
	/**
	 * @return skillSetException
	 */
	public int getSkillSetException() {
		return skillSetException;
	}

	/**
	 * @return skillSetMaxOccur
	 */
	public int getSkillSetMaxOccur() {
		return skillSetMaxOccur;
	}

	public boolean hasResurrectEffect() {
		return getEffects() != null && getEffects().isResurrect();
	}

	public boolean hasItemHealFpEffect() {
		return getEffects() != null && getEffects().isEffectTypePresent(EffectType.PROCFPHEALINSTANT);
	}

	public boolean hasEvadeEffect() {
		return getEffects() != null && getEffects().isEffectTypePresent(EffectType.EVADE);
	}

	public boolean hasRecallInstant() {
		return getEffects() != null && getEffects().isEffectTypePresent(EffectType.RECALLINSTANT);
	}

	public boolean hasHealEffect() {
        return getEffects() != null && (getEffects().isEffectTypePresent(EffectType.HEAL) || getEffects().isEffectTypePresent(EffectType.HEALINSTANT));
    }

    public boolean hasRandomMoveEffect() {
        return getEffects() != null && (getEffects().isEffectTypePresent(EffectType.RANDOMMOVELOC));
    }

	public int getDelayId() {
		return delayId > 0 ? delayId : getCooldownId();
	}

	public int getCooldownId() {
		return cooldownId > 0 ? cooldownId : skillId;
	}
	
	public boolean isDeityAvatar() {
		return isDeityAvatar;
	}
	
	public boolean isArchDaeva() {
		return isArchDaeva;
	}
	
	public boolean isBattlefield() {
		return isBattlefield;
	}
	
	public boolean isMinion() {
		return isMinion || isMinionSkill;
	}

	public boolean isMinionSkill() {
		return isMinionSkill;
	}
	
	public boolean isGroundSkill() {
		return isGroundSkill;
	}
	
	public AttackStatus getCounterSkill() {
		return counterSkill;
	}
	
	public boolean isUndispellableByPotions() {
		return isUndispellableByPotions;
	}

	public int getAmmoSpeed() {
		return ammoSpeed;
	}

	public int getConflictId() {
		return conflictId;
	}

	public boolean isNoRemoveAtDie() {
		return noRemoveAtDie;
	}

	public boolean isNoSaveOnLogout() {
		return noSaveOnLogout;
	}

	public int getProvokeAfterCritical() {
		return provokeAfterCritical;
	}

	public boolean isProvokeSelfAfterCritical() {
		return provokeSelfAfterCritical;
	}

	public String getChargeSetName() {
		return charge_set_name;
	}

	public int getEffectsDuration(int skillLevel) {
		int duration = 0;
		Iterator<EffectTemplate> itr = getEffects().getEffects().iterator();
		while(itr.hasNext() && duration == 0) {
			EffectTemplate et = itr.next();
			int effectDuration = et.getDuration2() + et.getDuration1() * skillLevel;
			if (et.getRandomTime() > 0)
				effectDuration -= Rnd.get(et.getRandomTime());
			duration = duration > effectDuration ? duration : effectDuration;
		}
		
		return duration;
	}

	public ChainCondition getChainCondition() {
		if (startconditions != null) {
			for (Condition cond : startconditions.getConditions()) {
				if (cond instanceof ChainCondition)
					return (ChainCondition)cond;
			}
		}
		
		return null;
	}

	public HashMap<Integer, Integer> getEffectIds() {
		return this.effectIds;
	}

	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (this.getEffects() != null && this.getEffects().getEffects() != null) {
			for (EffectTemplate et : this.getEffects().getEffects()) {
				if (et.getEffectid() != 0) {
					if (effectIds == null)
						effectIds = new HashMap<Integer, Integer>();

					effectIds.put(et.getEffectid(), et.getBasicLvl());
				}
			}
		}
	}

	/**
	 * @return
	 */
	public HpCondition getHpCondition() {
		if (startconditions != null) {
		    for (Condition cond : startconditions.getConditions()) {
			    if (cond instanceof HpCondition)
				    return (HpCondition) cond;
			}
		}
		return null;
	}
	
	public int getUseBattery() {
		return useBattery;
	}
}