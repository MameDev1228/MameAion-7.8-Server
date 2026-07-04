package com.aionemu.gameserver.model.templates.transform_book;

import com.aionemu.gameserver.model.templates.minion.MinionAttr;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "transform_collection_template")
@XmlAccessorType(XmlAccessType.NONE)
public class TransformCollectionTemplate {

    @XmlElement(name="physical_bonus")
    protected CollectionAttr physicalAttr;

    @XmlElement(name="magical_bonus")
    protected CollectionAttr magicalAttr;

    @XmlElement(name="required")
    protected CollectionRequired required;

    @XmlAttribute(name = "id", required = true)
    private int id;

    @XmlAttribute(name = "need_count")
    private int needCount;

    @XmlAttribute(name = "reward_skill")
    private int rewarSkill;

    public int getId() {
        return id;
    }

    public CollectionAttr getMagicalAttr() {
        return magicalAttr;
    }

    public CollectionAttr getPhysicalAttr() {
        return physicalAttr;
    }

    public CollectionRequired getRequired() {
        return required;
    }

    public int getNeedCount() {
        return needCount;
    }

    public int getRewarSkill() {
        return rewarSkill;
    }
}
