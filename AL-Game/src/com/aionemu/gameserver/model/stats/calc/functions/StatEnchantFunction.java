package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ArmorType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatEnchantFunction extends StatAddFunction
{
	private static final Logger log = LoggerFactory.getLogger(StatEnchantFunction.class);
	
	private Item item;
	private int point;
	
	public StatEnchantFunction(Item owner, StatEnum stat, int point) {
		this.stat = stat;
		this.item = owner;
		this.point = point;
	}
	
	@Override
	public final int getPriority() {
		return 30;
	}
	
	@Override
    public void apply(Stat2 stat) {
        if (!item.isEquipped()) {
            return;
        }
        int enchantLvl = this.item.getEnchantLevel();
		int authorizeLvl = this.item.getAuthorizeLevel();
		if (enchantLvl == 0) {
            return;
        } if (authorizeLvl == 0) {
            return;
        } if ((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_HAND.getSlotIdMask()) != 0 || (item.getEquipmentSlot() & ItemSlot.SUB_OFF_HAND.getSlotIdMask()) != 0) {
            return;
        }
        stat.addToBase(point);
    }
}