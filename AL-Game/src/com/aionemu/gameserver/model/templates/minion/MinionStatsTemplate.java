package com.aionemu.gameserver.model.templates.minion;

import com.aionemu.gameserver.model.templates.stats.StatsTemplate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Ranastic
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MinionStatsTemplate")
public class MinionStatsTemplate extends StatsTemplate
{
	@XmlAttribute(name = "height")
	private float height;
	
	@XmlAttribute(name = "altitude")
	private float altitude;
	
	public float getHeight() {
		return height;
	}
	
	public float getAltitude() {
		return altitude;
	}
}