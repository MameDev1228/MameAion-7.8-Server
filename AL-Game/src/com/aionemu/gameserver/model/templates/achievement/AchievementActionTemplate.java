package com.aionemu.gameserver.model.templates.achievement;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "achievement_action_template")
public class AchievementActionTemplate {

    @XmlElement(name = "required")
    protected AchievementRequired required;
    @XmlElement(name = "rewards")
    protected ActionRewards rewards;

    @XmlAttribute(name = "id", required = true)
    protected int id;

    @XmlAttribute(name = "name")
    protected String name;

    @XmlAttribute(name = "title")
    protected Integer title;

    @XmlAttribute(name = "type")
    protected AchievementActionType type;

    @XmlAttribute(name = "maxvalue")
    protected Integer maxvalue;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getTitle() {
        return title;
    }

    public ActionRewards getRewards() {
        return rewards;
    }

    public AchievementRequired getRequired() {
        return required;
    }

    public AchievementActionType getType() {
        return type;
    }

    public Integer getMaxvalue() {
        return maxvalue;
    }

}
