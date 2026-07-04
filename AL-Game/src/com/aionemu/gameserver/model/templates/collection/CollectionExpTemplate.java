package com.aionemu.gameserver.model.templates.collection;

import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollectionExpTemplate")
public class CollectionExpTemplate {

    @XmlElement(name = "modifiers", required = false)
    private ModifiersTemplate modifiers;

    @XmlAttribute(name = "id")
    protected int id;

    @XmlAttribute(name = "level")
    protected int level;

    @XmlAttribute(name = "exp")
    protected int exp;

    @XmlAttribute(name = "grade")
    protected CollectionType grade;

    public ModifiersTemplate getModifiers() {
        return modifiers;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public CollectionType getGrade() {
        return grade;
    }
}
