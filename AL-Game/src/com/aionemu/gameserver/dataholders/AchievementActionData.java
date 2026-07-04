package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.achievement.AchievementActionTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "achievement_action_templates")
public class AchievementActionData {

    @XmlElement(name = "achievement_action_template", required = true)
    protected List<AchievementActionTemplate> achievements;

    @XmlTransient
    private TIntObjectHashMap<AchievementActionTemplate> custom = new TIntObjectHashMap<AchievementActionTemplate>();

    public AchievementActionTemplate getAchievementActionId(int id) {
        return custom.get(id);
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (AchievementActionTemplate it : achievements) {
            getCustomMap().put(it.getId(), it);
        }
    }

    private TIntObjectHashMap<AchievementActionTemplate> getCustomMap() {
        return custom;
    }

    public int size() {
        return custom.size();
    }
}
