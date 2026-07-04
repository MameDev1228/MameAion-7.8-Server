package com.aionemu.gameserver.model.templates.transform_book;

import com.aionemu.gameserver.model.stats.container.StatEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="CollectionAttr")
public class CollectionAttr {

    @XmlAttribute(name="name", required = true)
    protected StatEnum name;

    @XmlAttribute(name="value",required = true)
    protected int value;

    public StatEnum getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
