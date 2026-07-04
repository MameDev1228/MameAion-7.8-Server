package com.aionemu.gameserver.model.templates.achievement;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionRewards")
public class ActionRewards {

    @XmlAttribute(name = "gold")
    protected Integer gold;

    @XmlAttribute(name = "exp")
    protected Integer exp;

    @XmlAttribute(name = "ap")
    protected Integer ap;

    @XmlAttribute(name = "gp")
    protected Integer gp;

    @XmlElement(name = "reward_item")
    protected List<ActionsItems> achievementItems;

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

    public List<ActionsItems> getAchievementItems() {
        return achievementItems;
    }
}
