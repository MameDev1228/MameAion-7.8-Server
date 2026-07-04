package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DecomposeStuffResult")
public class DecomposeStuffResult {

    @XmlAttribute(name = "item_id")
    private int itemId;


    public int getItemId() {
        return itemId;
    }
}
