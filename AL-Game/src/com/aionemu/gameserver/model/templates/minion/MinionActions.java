package com.aionemu.gameserver.model.templates.minion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * 
 * @author Ranastic
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MinionActions")
public class MinionActions {

	@XmlElement(name = "skill")
	protected ArrayList<MinionSkill> skillActions;
	
	public Collection<MinionSkill> getSkillsCollections() {
		return skillActions != null ? skillActions : Collections.<MinionSkill> emptyList();
    }
}
