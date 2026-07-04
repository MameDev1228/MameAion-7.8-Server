package com.aionemu.gameserver.model.templates.monster_core;


import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "QualityCoreType")
@XmlEnum
public enum  MonsterCoreType
{
    GOLD,
    SILVER,
    BRONZE,
	PLATINUM;
	
    private MonsterCoreType() {
    }
}