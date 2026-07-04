package com.aionemu.gameserver.model.templates.item.grind;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.templates.item.SkillEnhance;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="GrindCombine")
public class GrindCombine {

    @XmlElement(name = "reward_grind")
    protected List<GrindReward> rewards;

    @XmlAttribute(name="id")
    protected int id;

    @XmlAttribute(name="target_class")
    protected PlayerClass playerClass;

    @XmlAttribute(name="price")
    protected int price;

    @XmlAttribute(name="materiel_color_1")
    protected int color1;

    @XmlAttribute(name="materiel_color_2")
    protected int color2;

    public int getId() {
        return id;
    }

    public int getColor1() {
        return color1;
    }

    public int getColor2() {
        return color2;
    }

    public int getPrice() {
        return price;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public List<GrindReward> getRewards() {
        return rewards;
    }
}
