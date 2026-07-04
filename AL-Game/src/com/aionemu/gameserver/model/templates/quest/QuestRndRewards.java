package com.aionemu.gameserver.model.templates.quest;


import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "quest_ramdom_reward")
public class QuestRndRewards {

    @XmlElement(name = "rnd_reward")
    protected List<RndRewards> rndRewards;

    @XmlAttribute(name = "id", required = true)
    protected int id;

    public int getId() {
        return id;
    }

    public List<RndRewards> getRndRewards() {
        return rndRewards;
    }
}
