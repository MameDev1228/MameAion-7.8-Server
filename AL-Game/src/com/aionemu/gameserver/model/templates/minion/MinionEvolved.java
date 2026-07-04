package com.aionemu.gameserver.model.templates.minion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Ranastic
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MinionEvolved")
public class MinionEvolved {

	@XmlAttribute(name = "itemId")
	private int itemId;
	
	@XmlAttribute(name = "evolvedNum")
	private int evolvedNum;
	
	@XmlAttribute(name = "evolvedCost")
	private int evolvedCost;
	
	public int getItemId() {
		return itemId;
	}
	
	public int getEvolvedNum() {
		return evolvedNum;
	}
	
	public int getEvolvedCost() {
		return evolvedCost;
	}
}
