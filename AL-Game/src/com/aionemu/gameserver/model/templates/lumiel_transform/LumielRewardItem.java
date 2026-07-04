package com.aionemu.gameserver.model.templates.lumiel_transform;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "LumielRewardItem")
public class LumielRewardItem {

    @XmlAttribute(name = "item_id")
    private int itemId;

    @XmlAttribute(name = "count")
    private int count;

    public int getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }
}
