package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CarveSignetEffect")
public class CarveSignetEffect extends DamageEffect
{
	@XmlAttribute(required = true)
	protected int signetlvlstart;
	
	@XmlAttribute(required = true)
	protected int signetlvl;
	
	@XmlAttribute(required = true)
	protected int signetid;
	
	@XmlAttribute(required = true)
	protected String signet;
	
	@XmlAttribute(required = false)
	protected float prob = 100;
	
	private int nextSignetLevel = 1;
	
	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect);
		if (Rnd.get(0, 100) > prob) {
			return;
		}
		Effect placedSignet = effect.getEffected().getEffectController().getAnormalEffect(signet);
		if (placedSignet != null) {
			placedSignet.endEffect();
		}
		int skillId = resolveSignetSkillId(nextSignetLevel);
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (template == null) {
			log.warn("[MAME-SIGNET][MISSING_TEMPLATE] sourceSkill=" + effect.getSkillId() + " signet=" + signet + " signetid=" + signetid
					+ " nextLevel=" + nextSignetLevel + " resolvedSkill=" + skillId + " maxLevel=" + getMaxSignetLevel());
			return;
		}
		effect.setCarvedSignet(nextSignetLevel);
		Effect newEffect = new Effect(effect.getEffector(), effect.getEffected(), template, nextSignetLevel, 0);
		newEffect.initialize();
		newEffect.applyEffect();
	}
	
	@Override
	public void calculate(Effect effect) {
		if (!super.calculate(effect, DamageType.PHYSICAL)) {
			return;
		}
		int maxLevel = getMaxSignetLevel();
		int addLevel = signetlvlstart > 0 ? signetlvlstart : 1;
		Effect placedSignet = effect.getEffected().getEffectController().getAnormalEffect(signet);
		nextSignetLevel = addLevel;
		if (placedSignet != null) {
			int currentLevel = placedSignet.getSkillLevel();
			if (currentLevel <= 0) {
				currentLevel = resolveLevelFromSignetSkillId(placedSignet.getSkillId(), maxLevel);
			}
			if (currentLevel >= maxLevel) {
				nextSignetLevel = maxLevel;
			} else {
				nextSignetLevel = Math.min(maxLevel, currentLevel + addLevel);
			}
		}
		nextSignetLevel = Math.max(1, Math.min(maxLevel, nextSignetLevel));
		effect.setCarvedSignet(nextSignetLevel);
	}

	private int getMaxSignetLevel() {
		return signetlvl > 0 ? Math.min(signetlvl, 5) : 1;
	}

	private int resolveSignetSkillId(int level) {
		int maxLevel = getMaxSignetLevel();
		int clampedLevel = Math.max(1, Math.min(maxLevel, level));
		// ArchSoft data stores signetid as the first signet template id, e.g. 8303.
		if (isStartStyleSignetId(maxLevel)) {
			return signetid + clampedLevel - 1;
		}
		// Current 7.7 data often stores signetid as the max template id, e.g. 8307 for a 5-stage signet.
		int maxStyle = signetid - maxLevel + clampedLevel;
		if (DataManager.SKILL_DATA.getSkillTemplate(maxStyle) != null) {
			return maxStyle;
		}
		// Legacy Encom fallback for SYSTEM_SKILL_SIGNET1: 8303..8307.
		int legacyStyle = 8302 + clampedLevel;
		if (DataManager.SKILL_DATA.getSkillTemplate(legacyStyle) != null) {
			return legacyStyle;
		}
		return signetid;
	}

	private int resolveLevelFromSignetSkillId(int skillId, int maxLevel) {
		if (isStartStyleSignetId(maxLevel) && skillId >= signetid && skillId < signetid + maxLevel) {
			return skillId - signetid + 1;
		}
		int maxStyleStart = signetid - maxLevel + 1;
		if (skillId >= maxStyleStart && skillId <= signetid) {
			return skillId - maxStyleStart + 1;
		}
		if (skillId >= 8303 && skillId <= 8307) {
			return skillId - 8302;
		}
		return 1;
	}

	private boolean isStartStyleSignetId(int maxLevel) {
		return DataManager.SKILL_DATA.getSkillTemplate(signetid + maxLevel - 1) != null;
	}
}
