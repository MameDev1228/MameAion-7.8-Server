package com.aionemu.gameserver.model.templates.lumiel_transform;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LumielTransformTemplate")
public class LumielTransformTemplate {

    @XmlElement(name = "reward")
    private List<LumielTransformReward> lumielTransformRewards;

    @XmlAttribute(name = "id")
    protected int id;

    @XmlAttribute(name = "name")
    protected String name;

    @XmlAttribute(name = "need_point")
    protected int needPoints;

    @XmlAttribute(name = "activate")
    protected boolean activate;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNeedPoints() {
        return needPoints;
    }

    public boolean isActivate() {
        return activate;
    }

    public List<LumielTransformReward> getLumielTransformRewards() {
        return lumielTransformRewards;
    }
}
