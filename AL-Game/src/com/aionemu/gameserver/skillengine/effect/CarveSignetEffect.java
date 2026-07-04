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
	
	@XmlAttribute(required = true)
	protected final float prob = 100;
	
	private int nextSignetLevel = 1;
	
	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect);
		if (Rnd.get(0, 100) > prob) {
			return;
		} if (signetlvl == 0) {
			signetlvl = 1;
		}
		Effect placedSignet = effect.getEffected().getEffectController().getAnormalEffect(signet);
		if (placedSignet != null) {
			placedSignet.endEffect();
		}
		nextSignetLevel = 1;
		if (placedSignet != null) {
			nextSignetLevel = placedSignet.getSkillId() - 8302 + 1;
			if (nextSignetLevel > signetlvl || nextSignetLevel > 5) {
				nextSignetLevel--;
			}
		} if (nextSignetLevel < signetlvlstart) {
			nextSignetLevel = signetlvlstart + 0;
		}
		effect.setCarvedSignet(nextSignetLevel);
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(8302 + nextSignetLevel);
		Effect newEffect = new Effect(effect.getEffector(), effect.getEffected(), template, effect.getCarvedSignet(), 0);
		newEffect.initialize();
		newEffect.applyEffect();
	}
	
	@Override
	public void calculate(Effect effect) {
		if (!super.calculate(effect, DamageType.PHYSICAL)) {
			return;
		}
	}
}