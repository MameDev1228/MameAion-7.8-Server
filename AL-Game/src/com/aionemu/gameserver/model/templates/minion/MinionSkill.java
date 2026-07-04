package com.aionemu.gameserver.model.templates.minion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MinionSkill")
public class MinionSkill {
	
	@XmlAttribute(name = "skill_id")
	public int skill_id;
	
	@XmlAttribute(name = "energyCost")
	public int energyCost;
	
	public int getSkillId() {
		return skill_id;
	}

	public int getEnergyCost() {
		return energyCost;
	}
}
