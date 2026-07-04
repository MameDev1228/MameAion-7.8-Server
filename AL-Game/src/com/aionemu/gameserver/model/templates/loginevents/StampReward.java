package com.aionemu.gameserver.model.templates.loginevents;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StampReward")
public class StampReward {

    @XmlAttribute(name = "item_id")
    protected Integer itemId;
    @XmlAttribute(name = "count")
    protected Integer count;
    @XmlAttribute(name = "stamp")
    protected Integer stamp;

    public Integer getItemId() {
        return itemId;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getStamp() {
        return stamp;
    }
}
