package com.aionemu.gameserver.model.templates.monster_core;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MonsterCoreTemplate")
public class MonsterCoreTemplate {

    protected List<MonsterCoreList> core_list;
    @XmlAttribute(name = "id", required = true)
    private int id;
    @XmlAttribute(name = "category")
    private int category;
    @XmlAttribute(name = "maxRank")
    private int maxRank;
    @XmlAttribute(name = "quality")
    private MonsterCoreType quality;
    @XmlAttribute(name = "itemId")
    private int itemId;

    public List<MonsterCoreList> getStatLists() {
        if (this.core_list == null) {
            this.core_list = new ArrayList<MonsterCoreList>();
        }
        return this.core_list;
    }

    public int getId() {
        return this.id;
    }

    public int getCategory() {
        return this.category;
    }

    public int getMaxRank() {
        return this.maxRank;
    }

    public MonsterCoreType getQuality() {
        return this.quality;
    }

    public int getItemId() {
        return this.itemId;
    }

}