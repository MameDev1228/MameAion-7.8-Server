package com.aionemu.gameserver.model.templates.achievement;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AchievementRewards")
public class AchievementRewards {
    @XmlAttribute(name = "gold")
    protected Integer gold;

    @XmlAttribute(name = "exp")
    protected Integer exp;

    @XmlAttribute(name = "ap")
    protected Integer ap;

    @XmlAttribute(name = "gp")
    protected Integer gp;

    @XmlElement(name = "reward_item")
    protected List<AchievementItems> achievementItems;

    public Integer getGold() {
        return gold;
    }

    public Integer getAp() {
        return ap;
    }

    public Integer getExp() {
        return exp;
    }

    public Integer getGp() {
        return gp;
    }

    public List<AchievementItems> getAchievementItems() {
        return achievementItems;
    }
}
