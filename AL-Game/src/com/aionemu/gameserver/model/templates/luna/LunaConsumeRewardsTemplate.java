package com.aionemu.gameserver.model.templates.luna;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "luna_consume_reward")
@XmlAccessorType(XmlAccessType.NONE)
public class LunaConsumeRewardsTemplate
{
	@XmlAttribute
	protected int id;
	
	@XmlAttribute
	protected String name;
	
	@XmlAttribute
	protected int luna_sum_count;
	
	@XmlAttribute
	protected int gacha_cost;
	
	@XmlAttribute
	protected int create_1;
	
	@XmlAttribute
	protected int create_2;
	
	@XmlAttribute
	protected int num_1;
	
	@XmlAttribute
	protected int num_2;
	
	public int getId() {
        return this.id;
    }
	
	public String getName() {
		return name;
	}
	
	public int getSumCount() {
		return luna_sum_count;
	}
	
	public int getGachaCost() {
		return gacha_cost;
	}
	
	public int getCreateItemId1() {
		return create_1;
	}
	
	public int getCreateItemId2() {
		return create_2;
	}
	
	public int getCreateItemCount1() {
		return num_1;
	}
	
	public int getCreateItemCount2() {
		return num_2;
	}
}