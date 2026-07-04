package com.aionemu.gameserver.model.templates.monster_core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.stats.container.StatEnum;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MonsterCoreList")
public class MonsterCoreList {

    @XmlAttribute(name = "id")
    protected int id;
    @XmlAttribute(required = true)
    protected StatEnum stat;
    @XmlAttribute(name = "level")
    protected int level;
    @XmlAttribute(name = "value")
    protected int value;

    public int getId() {
        return this.id;
    }

    public StatEnum getStat() {
        return stat;
    }

    public int getLevel() {
        return this.level;
    }

    public int getValue() {
        return this.value;
    }
}
