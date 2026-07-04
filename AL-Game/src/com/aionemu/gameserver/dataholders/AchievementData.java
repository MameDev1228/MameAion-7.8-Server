package com.aionemu.gameserver.dataholders;


import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.model.templates.achievement.AchievementTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastList;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "achievement_templates")
public class AchievementData {

    @XmlElement(name = "achievement_template", required = true)
    protected List<AchievementTemplate> achievements;

    List<AchievementTemplate> daily = new FastList<AchievementTemplate>();
    List<AchievementTemplate> weekly = new FastList<AchievementTemplate>();

    @XmlTransient
    private TIntObjectHashMap<AchievementTemplate> custom = new TIntObjectHashMap<AchievementTemplate>();

    public AchievementTemplate getAchievementId(int id) {
        return custom.get(id);
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (AchievementTemplate it : achievements) {
            getCustomMap().put(it.getId(), it);
            if(it.getType() == AchievementType.DAILY) {
                daily.add(it);
            } else {
                weekly.add(it);
            }
        }
    }

    public List<AchievementTemplate> getDaily() {
        return daily;
    }

    public List<AchievementTemplate> getWeekly() {
        return weekly;
    }

    private TIntObjectHashMap<AchievementTemplate> getCustomMap() {
        return custom;
    }

    public int size() {
        return custom.size();
    }
}