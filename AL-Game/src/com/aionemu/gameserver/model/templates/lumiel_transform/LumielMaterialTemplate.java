package com.aionemu.gameserver.model.templates.lumiel_transform;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LumielMaterialTemplate")
public class LumielMaterialTemplate {

    @XmlAttribute(name = "id")
    protected int id;

    @XmlAttribute(name = "name")
    protected String name;

    @XmlAttribute(name = "lumiel_id")
    protected int lumielId;

    @XmlAttribute(name = "item_id")
    protected int itemId;

    @XmlAttribute(name = "point")
    protected int point;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLumielId() {
        return lumielId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getPoint() {
        return point;
    }
}
