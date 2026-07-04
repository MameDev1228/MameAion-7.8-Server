package com.aionemu.gameserver.model.templates.item.grind;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "GrindReward")
public class GrindReward {

    @XmlAttribute(name = "item_id")
    public int itemId;

    public int getItemId() {
        return itemId;
    }
}
