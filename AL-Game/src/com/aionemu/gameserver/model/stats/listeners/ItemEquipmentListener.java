/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.stats.listeners;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.IdianStone;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.container.CreatureGameStats;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.WeaponStats;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.model.templates.itemset.FullBonus;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.model.templates.itemset.PartBonus;
import com.aionemu.gameserver.services.enchant.EnchantService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.item.ItemTuningService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ItemEquipmentListener
{
	static Logger log = LoggerFactory.getLogger(ItemEquipmentListener.class);
	
	public static void onItemEquipment(Item item, Player owner) {
		owner.getController().cancelUseItem();
		ItemTemplate itemTemplate = item.getItemTemplate();
		onItemEquipment(item, owner.getGameStats(), owner);
		if (itemTemplate.isItemSet()) {
			recalculateItemSet(itemTemplate.getItemSet(), owner, item.getItemTemplate().isWeapon());
		} if (item.hasManaStones()) {
			addStonesStats(item, item.getItemStones(), owner.getGameStats());
		} if (item.hasFusionStones()) {
			addStonesStats(item, item.getFusionStones(), owner.getGameStats());
		}
		IdianStone idianStone = item.getIdianStone();
		if (idianStone != null) {
			idianStone.onEquip(owner);
		}
		addGodstoneEffect(owner, item);
		if (item.getConditioningInfo() != null) {
			owner.getObserveController().addObserver(item.getConditioningInfo());
			item.getConditioningInfo().setPlayer(owner);
		} if (item.getAmplificationSkill() > 0) {
			owner.getSkillList().addSkill(owner, item.getAmplificationSkill(), 1);
		} if (item.getItemSkinSkill() > 0) {
			owner.getSkillList().addSkill(owner, item.getItemSkinSkill(), 1);
		} if (owner.isProtectionActive()) {
			owner.getController().stopProtectionActiveTask();
		}
		EnchantService.GloryShieldSkill(owner);
		EnchantService.onItemEquip(owner, item);
		ItemTuningService.onItemEquip(owner, item);
	}
	
	public static void onItemUnequipment(Item item, Player owner) {
		owner.getController().cancelUseItem();
		ItemTemplate itemTemplate = item.getItemTemplate();
		if (itemTemplate.isItemSet()) {
			recalculateItemSet(itemTemplate.getItemSet(), owner, item.getItemTemplate().isWeapon());
		}
		owner.getGameStats().endEffect(item);
		if (item.hasManaStones()) {
			removeStoneStats(item.getItemStones(), owner.getGameStats());
		} if (item.hasFusionStones()) {
			removeStoneStats(item.getFusionStones(), owner.getGameStats());
		} if (item.getConditioningInfo() != null) {
			owner.getObserveController().removeObserver(item.getConditioningInfo());
			item.getConditioningInfo().setPlayer(null);
		}
		IdianStone idianStone = item.getIdianStone();
		if (idianStone != null) {
			idianStone.onUnEquip(owner);
		}
		removeGodstoneEffect(owner, item);
		if (item.getAmplificationSkill() > 0) {
			if (owner.getSkillList().isSkillPresent(item.getAmplificationSkill())) {
				SkillLearnService.removeSkill(owner, item.getAmplificationSkill());
			}
		} if (item.getItemSkinSkill() > 0) {
			if (owner.getSkillList().isSkillPresent(item.getItemSkinSkill())) {
				SkillLearnService.removeSkill(owner, item.getItemSkinSkill());
			}
		} if (owner.isProtectionActive()) {
			owner.getController().stopProtectionActiveTask();
		}
		EnchantService.GloryShieldSkill(owner);
		ItemTuningService.onItemUnEquip(owner, item);
	}

	/**
	 * Rebuilds only the stat effects owned by this equipped item after enchant/authorize/retune
	 * values changed. Full onItemEquipment() is intentionally not reused here because it also
	 * re-adds manastones, idian/godstone hooks and temporary skills, which can duplicate effects
	 * while the item is already equipped.
	 */
	public static void refreshEquippedItemStats(Item item, Player owner) {
		if (item == null || owner == null || !item.isEquipped()) {
			return;
		}
		owner.getGameStats().endEffect(item);
		onItemEquipment(item, owner.getGameStats(), owner);
		EnchantService.onItemEquip(owner, item);
		ItemTuningService.onItemEquip(owner, item);
		owner.getLifeStats().updateCurrentStats();
		owner.getGameStats().updateStatsAndSpeedVisually();
	}
	
	private static void onItemEquipment(Item item, CreatureGameStats<?> cgs, Player player) {
		ItemTemplate itemTemplate = item.getItemTemplate();
		long slot = item.getEquipmentSlot();
		List<StatFunction> modifiers = itemTemplate.getModifiers();
		List<StatFunction> allModifiers = new ArrayList<StatFunction>();

		// ArchSoft StatsCore phase: never abort the whole item when the primary
		// <modifiers> block is absent. Some 7.x items still need fusion/enchant/tuning
		// paths and the audit currentModifiers must represent exactly what this item
		// contributes through the template path.
		if ((slot & ItemSlot.MAIN_OR_SUB.getSlotIdMask()) != 0) {
			if (modifiers != null) {
				allModifiers.addAll(wrapModifiers(item, modifiers));
			}
			if (item.hasFusionedItem()) {
				// add all bonus modifiers according to ArchSoft fusion rules
				ItemTemplate fusionedItemTemplate = item.getFusionedItemTemplate();
				WeaponType weaponType = fusionedItemTemplate.getWeaponType();
				List<StatFunction> fusionedItemModifiers = fusionedItemTemplate.getModifiers();
				if (fusionedItemModifiers != null) {
					allModifiers.addAll(wrapModifiers(item, fusionedItemModifiers));
				}
				// add 10% of magic boost and weapon attack from fused weapon
				WeaponStats weaponStats = fusionedItemTemplate.getWeaponStats();
				if (weaponStats != null) {
					int boostMagicalSkill = Math.round(0.1f * weaponStats.getBoostMagicalSkill());
					int attack = Math.round(0.1f * weaponStats.getMeanDamage());
					if (weaponType == WeaponType.ORB_2H ||
						weaponType == WeaponType.BOOK_2H ||
						weaponType == WeaponType.GUN_1H ||
						weaponType == WeaponType.CANNON_2H ||
						weaponType == WeaponType.HARP_2H ||
						weaponType == WeaponType.KEYBLADE_2H) {
						allModifiers.add(new StatAddFunction(StatEnum.MAGICAL_POWER_BOOST, attack, false));
						allModifiers.add(new StatAddFunction(StatEnum.BOOST_MAGICAL_SKILL, boostMagicalSkill, false));
					} else {
						allModifiers.add(new StatAddFunction(StatEnum.MAIN_HAND_POWER, attack, false));
					}
				}
			}
		} else if (modifiers != null) {
			// Accessories/armour: apply every template modifier as-is.
			// This is the path that carries accuracy/magic accuracy/evasion/resist from accessories.
			allModifiers.addAll(modifiers);
		}

		item.setCurrentModifiers(allModifiers);
		if (!allModifiers.isEmpty()) {
			cgs.addEffect(item, allModifiers);
		}
	}
	
	private static List<StatFunction> wrapModifiers(Item item, List<StatFunction> modifiers) {
		List<StatFunction> allModifiers = new ArrayList<StatFunction>();
		if (modifiers == null) {
			return allModifiers;
		}
		for (StatFunction modifier : modifiers) {
			if (modifier == null) {
				continue;
			}
			switch (modifier.getName()) {
				case ATTACK_SPEED:
				case BOOST_CASTING_TIME:
					continue;
				default:
					allModifiers.add(modifier);
			}
		}
		return allModifiers;
	}
	
	private static void recalculateItemSet(ItemSetTemplate itemSetTemplate, Player player, boolean isWeapon) {
		if (itemSetTemplate == null)
			return;

		// TODO quite
		player.getGameStats().endEffect(itemSetTemplate);
		// 1.- Check equipment for items already equip with this itemSetTemplate id
		int itemSetPartsEquipped = player.getEquipment().itemSetPartsEquipped(itemSetTemplate.getId());

		// If main hand and off hand is same , no bonus
		int mainHandItemId = 0;
		int offHandItemId = 0;
		if (player.getEquipment().getMainHandWeapon() != null)
			mainHandItemId = player.getEquipment().getMainHandWeapon().getItemId();
		if (player.getEquipment().getOffHandWeapon() != null)
			offHandItemId = player.getEquipment().getOffHandWeapon().getItemId();
		boolean mainAndOffNotSame = mainHandItemId != offHandItemId;

		// 2.- Check Item Set Parts and add effects one by one if not done already
		for (PartBonus itempartbonus : itemSetTemplate.getPartbonus()) {
			if (mainAndOffNotSame && isWeapon) {
				// If the partbonus was not applied before, do it now
				if (itempartbonus.getCount() <= itemSetPartsEquipped) {
           			if (itempartbonus.getModifiers() != null) {
           				player.getGameStats().addEffect(itemSetTemplate, itempartbonus.getModifiers());
           			}
                }
			}
			else if (!isWeapon) {
				// If the partbonus was not applied before, do it now
				if (itempartbonus.getCount() <= itemSetPartsEquipped) {
					player.getGameStats().addEffect(itemSetTemplate, itempartbonus.getModifiers());
				}
			}
		}

		// 3.- Finally check if all items are applied and set the full bonus if not already applied
		FullBonus fullbonus = itemSetTemplate.getFullbonus();
		if (fullbonus != null && itemSetPartsEquipped == fullbonus.getCount()) {
			// Add the full bonus with index = total parts + 1 to avoid confusion with part bonus equal to number of
			// objects
			player.getGameStats().addEffect(itemSetTemplate, fullbonus.getModifiers());
		}
	}

	/**
	 * All modifiers of stones will be applied to character
	 * 
	 * @param item
	 * @param cgs
	 */
	private static void addStonesStats(Item item, Set<? extends ManaStone> itemStones, CreatureGameStats<?> cgs) {
		if (itemStones == null || itemStones.size() == 0)
			return;

		for (ManaStone stone : itemStones) {
			addStoneStats(item, stone, cgs);
		}
	}

	/**
	 * Used when socketing of equipped item
	 * 
	 * @param item
	 * @param stone
	 * @param cgs
	 */
	public static void addStoneStats(Item item, ManaStone stone, CreatureGameStats<?> cgs) {
		List<StatFunction> modifiers = stone.getModifiers();
		if (modifiers == null) {
			return;
		}
		cgs.addEffect(stone, modifiers);
	}

	/**
	 * All modifiers of stones will be removed
	 * 
	 * @param itemStones
	 * @param cgs
	 */
	public static void removeStoneStats(Set<? extends ManaStone> itemStones, CreatureGameStats<?> cgs) {
		if (itemStones == null || itemStones.size() == 0)
			return;

		for (ManaStone stone : itemStones) {
			List<StatFunction> modifiers = stone.getModifiers();
			if (modifiers != null) {
				cgs.endEffect(stone);
			}
		}
	}

	/**
	 * @param item
	 */
	private static void addGodstoneEffect(Player player, Item item) {
		if (item.getGodStone() != null) {
			item.getGodStone().onEquip(player);
		}
	}

	/**
	 * @param item
	 */
	private static void removeGodstoneEffect(Player player, Item item) {
		if (item.getGodStone() != null) {
			item.getGodStone().onUnEquip(player);
		}
	}

	public static void addIdianBonusStats(Item item, List<StatFunction> modifiers, CreatureGameStats<?> cgs) {
		cgs.addEffect(item, modifiers);
	}

	public static void removeIdianBonusStats(Item item, CreatureGameStats<?> cgs) {
		cgs.endEffect(item);
	}
}
