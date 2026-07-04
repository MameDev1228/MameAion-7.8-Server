package com.aionemu.gameserver.model.templates.luna;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="LunaDicesTemplate")
public class LunaDicesTemplate
{
    protected List<LunaDicesReward> dicereward;
    @XmlAttribute
	
    protected Integer id;
	
    @XmlAttribute(name="name")
    protected String name;
	
    @XmlAttribute(name="set_name")
    protected String setName;
	
    @XmlAttribute(name="is_flag")
    protected boolean isflag;
	
    @XmlAttribute(name="group_name")
    protected String groupName;
	
    @XmlAttribute(name="race")
    protected String race;
	
    @XmlAttribute(name="reward_total")
    protected int rewardTotal;
	
    public LunaDicesTemplate() {}
	
    public List<LunaDicesReward> getReward() {
        if (dicereward == null) {
            dicereward = new ArrayList();
        }
        return dicereward;
    }
	
    public Integer getId() {
        return id;
    }
	
    public String getName() {
        return name;
    }
	
    public String getSetName() {
        return setName;
    }
	
    public boolean isFlag() {
        return isflag;
    }
	
    public String getGroupName() {
        return groupName;
    }
	
    public String getRace() {
        return race;
    }
	
    public int getRewardTotal() {
        return rewardTotal;
    }
}