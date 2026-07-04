package com.aionemu.gameserver.model.templates.achievement;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "achievement_template")
public class AchievementTemplate {

    @XmlElement(name = "rewards")
    protected AchievementRewards rewards;
    @XmlElement(name = "actions")
    protected AchievementAction actions;

    @XmlAttribute(name = "id", required = true)
    protected int id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "title")
    protected Integer title;
    @XmlAttribute(name = "type")
    protected AchievementType type;
    @XmlAttribute(name = "repeat")
    protected AchievementRepeat repeat;
    @XmlAttribute(name = "race")
    protected Race race;
    @XmlAttribute(name = "minlevel")
    protected Integer minlevel;
    @XmlAttribute(name = "maxlevel")
    protected Integer maxlevel;
    @XmlAttribute(name = "completecount")
    protected Integer completecount;

    public AchievementRewards getRewards() {
        return rewards;
    }

    public AchievementAction getActions() {
        return actions;
    }

    public AchievementRepeat getRepeat() {
        return repeat;
    }

    public AchievementType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public Integer getCompletecount() {
        return completecount;
    }

    public Integer getMaxlevel() {
        return maxlevel;
    }

    public Integer getMinlevel() {
        return minlevel;
    }

    public Integer getTitle() {
        return title;
    }

    public Race getRace() {
        return race;
    }

    public String getName() {
        return name;
    }
}
