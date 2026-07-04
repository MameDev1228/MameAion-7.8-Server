package com.aionemu.gameserver.model.templates.loginevents;


import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "atreian_passport_template")
public class LoginEventTemplate {

    @XmlElement(name = "stamp_reward")
    protected List<StampReward> stampRewards;

    @XmlAttribute(name = "id", required = true)
    protected int id;

    public int getId() {
        return id;
    }

    public List<StampReward> getStampRewards() {
        return stampRewards;
    }
}
