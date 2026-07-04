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
        // MAME: Enchant/authorize bonus functions are created only for the current positive
        // enchant level in EnchantService.onItemEquip(). The old guard required both
        // enchantLevel and authorizeLevel to be non-zero, which blocked normal PvE/PvP
        // equipment bonuses because 7.x equipment stores these values separately
        // (enchantPvPvELevel / authorizeLevel).
        if ((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_HAND.getSlotIdMask()) != 0 || (item.getEquipmentSlot() & ItemSlot.SUB_OFF_HAND.getSlotIdMask()) != 0) {
            return;
        }
        stat.addToBase(point);
    }
}