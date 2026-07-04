package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SkillEnhance")
public class SkillEnhance {

    @XmlAttribute(name = "group_name")
    protected String skillGroupeName;

    @XmlAttribute(name = "prob")
    public int prob;


    public String getSkillGroupeName() {
        return skillGroupeName;
    }

    public int getProb() {
        return prob;
    }
}
