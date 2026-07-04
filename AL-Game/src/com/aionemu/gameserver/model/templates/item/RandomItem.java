package com.aionemu.gameserver.model.templates.item;

import com.aionemu.commons.utils.Rnd;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "RandomItem")
public class RandomItem
{
	@XmlAttribute(name = "count")
	protected int count;
	
	@XmlAttribute(name = "rnd_min")
	public int rndMin;
	
	@XmlAttribute(name = "rnd_max")
	public int rndMax;
	
	public int getCount() {
		return count;
	}
	
	public int getRndMin() {
		return rndMin;
	}
	
	public int getRndMax() {
		return rndMax;
	}
	
	public final int getResultCount() {
		if ((count == 0) && (rndMin == 0) && (rndMax == 0)) {
			return 1;
		} if ((rndMin > 0) || (rndMax > 0)) {
			if (rndMax < rndMin) {
				LoggerFactory.getLogger(RandomItem.class).warn("Wrong rnd result item definition {} {}", rndMin, rndMax);
				return 1;
			}
			return Rnd.get(rndMin, rndMax);
		}
		return count;
	}
}