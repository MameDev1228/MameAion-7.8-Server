package com.aionemu.gameserver.model.templates.item;

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ItemEnchantBouns")
public class ItemEnchantBonus
{
	@XmlElement(name = "modifiers", required = false)
	private ModifiersTemplate modifiers;
	
	@XmlAttribute(name = "level")
	private int level;
	
	public List<StatFunction> getModifiers() {
		return this.modifiers == null ? null : this.modifiers.getModifiers();
	}
	
	public int getLevel() {
		return this.level;
	}
}