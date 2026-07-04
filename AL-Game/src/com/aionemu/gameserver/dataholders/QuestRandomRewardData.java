package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.quest.QuestRndRewards;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "quest_rnd_rewards")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuestRandomRewardData {

    @XmlElement(name = "quest_rnd_reward")
    private List<QuestRndRewards> rndRewards;

    private TIntObjectHashMap<QuestRndRewards> rndRewardsData = new TIntObjectHashMap<QuestRndRewards>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (QuestRndRewards npc : rndRewards) {
            rndRewardsData.put(npc.getId(), npc);
        }
        rndRewardsData.clear();
        rndRewardsData = null;
    }

    public int size() {
        return rndRewardsData.size();
    }

    public QuestRndRewards getRamdomReward(int id) {
        return rndRewardsData.get(id);
    }


    /**
     * @return the npcData
     */
    public TIntObjectHashMap<QuestRndRewards> getRandomRewards() {
        return rndRewardsData;
    }

}
