package com.aionemu.gameserver.model.templates.lumiel_transform;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(name = "LumielTransformReward")
public class LumielTransformReward {

    @XmlElement(name = "reward_item")
    private List<LumielRewardItem> lumielTransformRewards;

    @XmlAttribute(name = "grade")
    private int grade;

    public int getGrade() {
        return grade;
    }

    public List<LumielRewardItem> getLumielTransformRewards() {
        return lumielTransformRewards;
    }
}
