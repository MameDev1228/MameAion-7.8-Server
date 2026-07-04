package com.aionemu.gameserver.model.templates.transform_book;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "transform_book_template")
@XmlAccessorType(XmlAccessType.NONE)
public class TransformBookTemplate
{
    @XmlAttribute(name = "id", required = true)
    private int id;
	
    @XmlAttribute(name = "name")
    private String name = "";
	
    @XmlAttribute(name = "grade", required = true)
    private int grade;
	
    @XmlAttribute(name = "skill_id", required = true)
    private int skillId;
	
    @XmlAttribute(name = "compose_value")
    private int composeValue;
	
    public int getId() {
        return id;
    }
	
    public String getName() {
        return name;
    }
	
    public int getGrade() {
        return grade;
    }
	
    public int getSkillId() {
        return skillId;
    }
	
    public int getComposeValue() {
        return composeValue;
    }
}