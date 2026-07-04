package com.aionemu.gameserver.model.templates.achievement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AchievementEventRewards")
public class AchievementEventRewards {

    @XmlAttribute(name = "item_id")
    protected Integer itemId;
    @XmlAttribute(name = "count")
    protected Integer count;
    @XmlAttribute(name = "stage")
    protected Integer stage;


    public Integer getItemId() {
        return itemId;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getStage() {
        return stage;
    }
}
