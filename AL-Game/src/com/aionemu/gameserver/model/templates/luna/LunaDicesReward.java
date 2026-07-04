package com.aionemu.gameserver.model.templates.luna;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="LunaDicesReward")
public class LunaDicesReward
{
    @XmlAttribute(name="item_id")
    protected int itemid;
	
    @XmlAttribute(name="quantity")
    protected int quantity;
	
    @XmlAttribute(name="name")
    protected String name;
	
    public LunaDicesReward() {}

    public Integer getItemid() {
        return itemid;
    }
	
    public Integer getQuantity() {
        return quantity;
    }
	
    public String getName() {
        return name;
    }
}