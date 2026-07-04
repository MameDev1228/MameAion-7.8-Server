package com.aionemu.gameserver.model.templates.item.bonuses;

import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "real_random_template")
public class RandomBonus
{
	@XmlElement(required = true)
	protected List<RandomAttr> random_attr;

	@XmlAttribute(required = true)
	protected int id;

	@XmlAttribute(name="option_num", required = true)
	private int option_num;

	public List<RandomAttr> getRandomAttr() {
		return this.random_attr;
	}

	/**
	 * Gets the value of the id property.
	 */
	public int getId() {
		return id;
	}

	public int getOptionNum() {
		return option_num;
	}
}