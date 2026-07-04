package com.aionemu.gameserver.model.templates.collection;

import com.aionemu.gameserver.model.templates.lumiel_transform.LumielTransformReward;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollectionTemplate")
public class CollectionTemplate {

    @XmlElement(name = "modifiers", required = false)
    private ModifiersTemplate modifiers;

    @XmlElement(name = "material")
    private List<MaterialCollectionTemplate> materials;

    @XmlElement(name = "reward")
    private List<RewardCollectionTemplate> rewards;

    @XmlAttribute(name = "id")
    protected int id;

    @XmlAttribute(name = "active")
    protected boolean active;

    @XmlAttribute(name = "grade")
    protected CollectionType grade;

    public ModifiersTemplate getModifiers() {
        return modifiers;
    }

    public List<MaterialCollectionTemplate> getMaterials() {
        return materials;
    }

    public List<RewardCollectionTemplate> getRewards() {
        return rewards;
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public CollectionType getGrade() {
        return grade;
    }
}
