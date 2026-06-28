/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.enchant;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DAEVANION_SKILL_ENCHANT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class EnchantDaevanionBook {

	private static final long ENCHANT_KINAH_COST = 100000L;
	private static final int MAX_DAEVANION_ENCHANT = 15;

	public static boolean isDaevanionSkillBook(Item item) {
		if (item == null || item.getItemTemplate() == null) {
			return false;
		}
		int itemId = item.getItemId();
		return itemId >= 169501000 && itemId <= 169502999;
	}

	private static boolean isDaevanionEnchantMaterial(Item item) {
		if (item == null || item.getItemTemplate() == null) {
			return false;
		}
		int itemId = item.getItemId();
		// 169501xxx are skill books. The 1695/1696 safety range keeps 7.x book
		// variants valid while blocking random inventory items from being consumed.
		return (itemId >= 169501000 && itemId <= 169502999) || (itemId >= 169600000 && itemId <= 169699999);
	}

	public static void enchantDaevanionSkill(final Player player, final int skillId, final int bookObjId, final int materials) {
		if (player == null || skillId <= 0 || bookObjId <= 0) {
			return;
		}
		final Item parentItem = player.getInventory().getItemByObjId(bookObjId);
		final PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
		if (parentItem == null || parentItem.getItemTemplate() == null || skill == null) {
			PacketSendUtility.sendMessage(player, "Daevanion skill enchant failed: invalid skill or book.");
			return;
		}
		if (!isDaevanionSkillBook(parentItem)) {
			PacketSendUtility.sendMessage(player, "Daevanion skill enchant failed: the selected book is not a Daevanion skill book.");
			return;
		}
		if (materials != 0) {
			Item materialItem = player.getInventory().getItemByObjId(materials);
			if (materialItem == null || !isDaevanionEnchantMaterial(materialItem)) {
				PacketSendUtility.sendMessage(player, "Daevanion skill enchant failed: invalid material category.");
				return;
			}
		}
		if (player.getInventory().getKinah() < ENCHANT_KINAH_COST) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return;
		}
		final ItemTemplate template = parentItem.getItemTemplate();
		final int nameId = template.getNameId();
		final int currentEnchant = Math.max(1, skill.getSkillLevel());
		if (currentEnchant >= MAX_DAEVANION_ENCHANT) {
			PacketSendUtility.sendMessage(player, "Daevanion skill is already at maximum enchant level.");
			return;
		}
		final ItemUseObserver moveObserver = new ItemUseObserver() {

			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(nameId)));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 2, 0), true);
			}
		};
		player.getObserveController().attach(moveObserver);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(moveObserver);
				PlayerSkillEntry liveSkill = player.getSkillList().getSkillEntry(skillId);
				Item liveBook = player.getInventory().getItemByObjId(bookObjId);
				if (liveSkill == null || liveBook == null || !isDaevanionSkillBook(liveBook)) {
					return;
				}
				if (materials != 0) {
					Item liveMaterial = player.getInventory().getItemByObjId(materials);
					if (liveMaterial == null || !isDaevanionEnchantMaterial(liveMaterial)) {
						return;
					}
				}
				if (player.getInventory().getKinah() < ENCHANT_KINAH_COST) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
					return;
				}
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 1), true);
				if (!player.getInventory().decreaseByObjectId(bookObjId, 1L)) {
					return;
				}
				player.getInventory().decreaseKinah(ENCHANT_KINAH_COST);
				if (materials != 0) {
					player.getInventory().decreaseByObjectId(materials, 1L);
				}
				boolean success = Rnd.chance(75);
				int before = Math.max(1, liveSkill.getSkillLevel());
				int enchantLevel = success ? Math.min(MAX_DAEVANION_ENCHANT, before + 1) : Math.max(1, before - 1);
				liveSkill.setSkillLvl(enchantLevel);
				player.getSkillList().addSkill(player, liveSkill.getSkillId(), enchantLevel);
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
				PacketSendUtility.sendPacket(player, new SM_DAEVANION_SKILL_ENCHANT(skillId, before, enchantLevel));
				PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
				if (enchantLevel >= MAX_DAEVANION_ENCHANT) {
					SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), player.getLevel(), player.getRace());
					PlayerSkillList playerSkillList = player.getSkillList();
					if (skillTemplates != null) {
						for (SkillLearnTemplate template : skillTemplates) {
							if (template.getRequiredSkill() != skillId) {
								continue;
							}
							playerSkillList.addSkill(player, template.getSkillId(), 1);
						}
					}
				}
			}
		}, 1000));
	}
}
