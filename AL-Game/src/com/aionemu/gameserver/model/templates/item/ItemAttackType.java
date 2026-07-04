package com.aionemu.gameserver.model.templates.item;

import com.aionemu.gameserver.model.SkillElement;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum ItemAttackType
{
	PHYSICAL(false, SkillElement.NONE),
	MAGICAL_EARTH(true, SkillElement.EARTH),
	MAGICAL_WATER(true, SkillElement.WATER),
	MAGICAL_WIND(true, SkillElement.WIND),
	MAGICAL_FIRE(true, SkillElement.FIRE);
	
	private boolean magic;
	private SkillElement elem;
	
	private ItemAttackType(boolean magic, SkillElement elem) {
		this.magic = magic;
		this.elem = elem;
	}
	
	public boolean isMagical() {
		return magic;
	}
	
	public SkillElement getMagicalElement() {
		return elem;
	}
}