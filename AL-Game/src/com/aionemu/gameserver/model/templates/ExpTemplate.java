package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "exp")
public class ExpTemplate
{
    @XmlAttribute(name = "level", required = true)
    private int level;
	
    @XmlAttribute(name = "exp", required = true)
    private long exp;
	
    public int getLevel() {
        return level;
    }
	
    public long getExp() {
        return exp;
    }
}