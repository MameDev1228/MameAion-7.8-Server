package com.aionemu.gameserver.model.templates.achievement;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.utils.gametime.DateTimeUtil;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "achievement_event_template")
public class AchievementEventTemplate {

    @XmlElement(name = "event_item")
    protected AchievementEventRewards rewards;

    @XmlAttribute(name = "id", required = true)
    protected int id;

    @XmlAttribute(name = "name")
    protected String name;

    @XmlAttribute(name = "event_section")
    protected int section;

    @XmlAttribute(name = "type")
    protected AchievementType type;

    @XmlAttribute(name = "repeat")
    protected AchievementRepeat repeat;

    @XmlAttribute(name = "race")
    protected Race race;

    @XmlAttribute(name = "minlevel")
    protected Integer minlevel;

    @XmlAttribute(name = "maxlevel")
    protected Integer maxlevel;

    @XmlAttribute(name = "active")
    protected Boolean active;

    @XmlAttribute(name = "complete_point")
    protected int completePoint;

    @XmlAttribute(name = "action_id")
    protected int actionId;

    @XmlAttribute(name = "start", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;

    @XmlAttribute(name = "end", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;

    public int getId() {
        return id;
    }

    public AchievementEventRewards getRewards() {
        return rewards;
    }

    public AchievementRepeat getRepeat() {
        return repeat;
    }

    public AchievementType getType() {
        return type;
    }

    public Boolean getActive() {
        return active;
    }

    public int getActionId() {
        return actionId;
    }

    public int getCompletePoint() {
        return completePoint;
    }

    public int getSection() {
        return section;
    }

    public Integer getMaxlevel() {
        return maxlevel;
    }

    public Integer getMinlevel() {
        return minlevel;
    }

    public Race getRace() {
        return race;
    }

    public String getName() {
        return name;
    }

    public DateTime getStartDate() {
        return DateTimeUtil.getDateTime(startDate.toGregorianCalendar());
    }

    public DateTime getEndDate() {
        return DateTimeUtil.getDateTime(endDate.toGregorianCalendar());
    }
}
