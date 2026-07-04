package com.aionemu.gameserver.model.templates.event;

import javax.xml.bind.annotation.XmlEnum;

/**
 * @author Ranastic
 */

@XmlEnum
public enum AttendType
{
	PC_BASIC(0),
	BASIC(1),
	ANNIVERSARY(2);
	
	private int id;
	
	private AttendType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}