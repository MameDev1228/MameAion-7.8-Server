package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.model.templates.achievement.AchievementEventTemplate;
import com.aionemu.gameserver.model.templates.achievement.AchievementTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastList;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "achievement_event_templates")
public class AchievementEventData {

    @XmlElement(name = "achievement_event_template", required = true)
    protected List<AchievementEventTemplate> achievements;

    @XmlTransient
    private TIntObjectHashMap<AchievementEventTemplate> custom = new TIntObjectHashMap<AchievementEventTemplate>();

    public AchievementEventTemplate getAchievementId(int id) {
        return custom.get(id);
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (AchievementEventTemplate it : achievements) {
            getCustomMap().put(it.getId(), it);
        }
    }

    private TIntObjectHashMap<AchievementEventTemplate> getCustomMap() {
        return custom;
    }

    public int size() {
        return custom.size();
    }

    public List<AchievementEventTemplate> getAchievements() {
        return achievements;
    }
}
