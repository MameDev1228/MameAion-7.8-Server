/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.craft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.recipe.Component;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemService.ItemUpdatePredicate;
import com.aionemu.gameserver.skillengine.task.MagicCraftTask;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Falke_34, FrozenKiller
 */
public class MagicCraftService {

	private static final Logger log = LoggerFactory.getLogger("MAGIC_CRAFT_LOG");

	public static void startMagicCraft(Player player, int recipeId, int craftType) {
		if (player == null) {
			return;
		}
		RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		if (recipeTemplate == null) {
			sendCancelMagicCraft(player);
			return;
		}
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());
		if (!checkMagicCraft(player, recipeTemplate, itemTemplate)) {
			sendCancelMagicCraft(player);
			return;
		}
		player.setCraftingTask(new MagicCraftTask(player, (StaticObject) null, recipeTemplate));
		player.getCraftingTask().start();
	}

	private static boolean checkMagicCraft(Player player, RecipeTemplate recipeTemplate, ItemTemplate itemTemplate) {
		if (recipeTemplate == null || itemTemplate == null) {
			return false;
		}
		if (player.getCraftingTask() != null && player.getCraftingTask().isInProgress()) {
			return false;
		}
		if (player.getInventory().getFreeSlots() == 0) {
			return false;
		}
		if (recipeTemplate.getComponent() != null) {
			for (Component items : recipeTemplate.getComponent()) {
				if (items == null || items.getItemid() <= 0 || items.getQuantity() <= 0) {
					return false;
				}
				if (DataManager.ITEM_DATA.getItemTemplate(items.getItemid()) == null) {
					return false;
				}
				if (player.getInventory().getItemCountByItemId(items.getItemid()) < items.getQuantity()) {
					return false;
				}
			}
		}
		return true;
	}

	public static void finishMagicCrafting(final Player player, RecipeTemplate recipetemplate, int critCount, int bonus) {
		if (player == null || recipetemplate == null) {
			return;
		}
		int xpReward = ((2 * (recipetemplate.getSkillpoint() + 100) * (recipetemplate.getSkillpoint() + 100) + 60));
		xpReward = xpReward + (xpReward * bonus / 100);

		if (player.getInventory().getFreeSlots() == 0) {
			sendCancelMagicCraft(player);
			return;
		}

		if (recipetemplate.getComponent() != null) {
			for (Component items : recipetemplate.getComponent()) {
				if (player.getInventory().getItemCountByItemId(items.getItemid()) < items.getQuantity()) {
					sendCancelMagicCraft(player);
					return;
				}
			}
			for (Component items : recipetemplate.getComponent()) {
				player.getInventory().decreaseByItemId(items.getItemid(), items.getQuantity());
			}
		}

		int critVal = Rnd.get(10000);
		int productItemId = recipetemplate.getProductid();
		Integer comboProduct = recipetemplate.getComboProductSize() > 0 && critVal > 9800 ? recipetemplate.getComboProduct(1) : null;
		if (comboProduct != null && DataManager.ITEM_DATA.getItemTemplate(comboProduct) != null) {
			productItemId = comboProduct;
		}
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(productItemId);
		if (itemTemplate == null) {
			sendCancelMagicCraft(player);
			return;
		}

		ItemService.addItem(player, productItemId, recipetemplate.getQuantity(), new ItemUpdatePredicate() {
			@Override
			public boolean changeItem(Item item) {
				if (item.getItemTemplate().isWeapon() || item.getItemTemplate().isArmor()) {
					item.setItemCreator(player.getName());
				}
				return true;
			}
		});

		if (LoggingConfig.LOG_CRAFT) {
			log.info(((comboProduct != null && critVal > 9800) ? "[MAGIC_CRAFT][Critical] ID/Count" : "[MAGIC_CRAFT][Normal] Added ID/Count") + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "/Item Name - " + productItemId + "/" + recipetemplate.getQuantity() + "/" + itemTemplate.getName() : " - " + productItemId + "/" + recipetemplate.getQuantity()) + " to player: " + player.getName());
		}

		int gainedCraftExp = (int) RewardType.CRAFTING.calcReward(player, xpReward);
		if (player.getSkillList().addSkillXp(player, recipetemplate.getSkillid(), gainedCraftExp, recipetemplate.getSkillpoint())) {
			player.getCommonData().addExp(xpReward, RewardType.CRAFTING);
		}
		else if (DataManager.SKILL_DATA.getSkillTemplate(recipetemplate.getSkillid()) != null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(recipetemplate.getSkillid()).getNameId())));
		}
	}

	public static void sendCancelMagicCraft(Player player) {
		if (player != null && player.getCraftingTask() != null && player.getCraftingTask().isInProgress()) {
			player.getCraftingTask().abort();
		}
	}
}
