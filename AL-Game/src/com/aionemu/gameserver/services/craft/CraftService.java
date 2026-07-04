/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.services.craft;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.recipe.Component;
import com.aionemu.gameserver.model.templates.recipe.ComponentElement;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemService.ItemUpdatePredicate;
import com.aionemu.gameserver.skillengine.task.CraftingTask;
import com.aionemu.gameserver.skillengine.task.MorphingTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class CraftService
{
	private static final Logger log = LoggerFactory.getLogger("CRAFT_LOG");
	
	public static void finishCrafting(final Player player, RecipeTemplate recipetemplate, int critCount, int bonus) {
		if (recipetemplate.getMaxProductionCount() != null) {
			player.getRecipeList().deleteRecipe(player, recipetemplate.getId());
			if (critCount == 0) {
				QuestEngine.getInstance().onFailCraft(new QuestEnv(null, player, 0, 0), recipetemplate.getComboProduct(1) == null? 0 : recipetemplate.getComboProduct(1));
			}
		}
		int xpReward = (int) ((0.008 * (recipetemplate.getSkillpoint() + 100) * (recipetemplate.getSkillpoint() + 100) + 60));
		xpReward = xpReward + (xpReward * bonus / 100);
		int productItemId = critCount > 0 ? recipetemplate.getComboProduct(critCount) : recipetemplate.getProductid();
		ItemService.addItem(player, productItemId, recipetemplate.getQuantity(), new ItemUpdatePredicate() {
			@Override
			public boolean changeItem(Item item) {
				if (item.getItemTemplate().isWeapon() || item.getItemTemplate().isArmor()) {
					item.setItemCreator(player.getName());
				}
				return true;
			}
		});
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(productItemId);
		int gainedCraftExp = (int) RewardType.CRAFTING.calcReward(player, xpReward);
		int skillId = recipetemplate.getSkillid();
		if ((skillId == 40001) ||
			(skillId == 40002) ||
			(skillId == 40003) ||
			(skillId == 40004) ||
			(skillId == 40007) ||
			(skillId == 40008) ||
			(skillId == 40010)) {
			if ((player.getSkillList().getSkillLevel(skillId) >= 500) && (recipetemplate.getSkillpoint() < 500)) {
				//Such basic crafting doesn't affect your skill level, Master.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_COMBINE_EXP_GRAND_MASTER);
			} else if ((player.getSkillList().getSkillLevel(skillId) >= 400) && (recipetemplate.getSkillpoint() < 400)) {
				//Your skill level does not increase with low level crafting as you are an Expert.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_COMBINE_EXP);
			} else {
				if (player.getSkillList().addSkillXp(player, recipetemplate.getSkillid(), gainedCraftExp, recipetemplate.getSkillpoint())) {
					player.getCommonData().addExp(xpReward, RewardType.CRAFTING);
				} else {
					//The skill level for the %0 skill does not increase as the difficulty is too low.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(recipetemplate.getSkillid()).getNameId())));
				}
			}
		} if (recipetemplate.getCraftDelayId() != null) {
			player.getCraftCooldownList().addCraftCooldown(recipetemplate.getCraftDelayId(), recipetemplate.getCraftDelayTime());
		}
	}
	
	public static void startCrafting(Player player, int recipeId, int targetObjId, int craftType) {
		RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		int skillId = recipeTemplate.getSkillid();
		VisibleObject target = player.getKnownList().getObject(targetObjId);
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());
		if (recipeTemplate.getDp() != null) {
			player.getCommonData().addDp(-recipeTemplate.getDp());
		} if (skillId == 40009) {
			player.setCraftingTask(new MorphingTask(player, (StaticObject) target, recipeTemplate));
		} else {
			int skillLvlDiff = player.getSkillList().getSkillLevel(skillId) - recipeTemplate.getSkillpoint();
			player.setCraftingTask(new CraftingTask(player, (StaticObject) target, recipeTemplate, skillLvlDiff, craftType == 1 ? 15 : 0));
		}
		player.getCraftingTask().start();
	}
	
	public static void stopAetherforging(final Player player, int recipeId) {
		final RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		final ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
		        player.getObserveController().removeObserver(this);
		        PacketSendUtility.broadcastPacket(player, new SM_AETHERFORGING_ANIMATION(player, recipeTemplate.getId(), 0, 1), true);
			}
		};
	}
	
	public static void startAetherforging(final Player player, int recipeId, int craftType) {
		final RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		final ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());
		PacketSendUtility.broadcastPacket(player, new SM_AETHERFORGING_ANIMATION(player, recipeTemplate.getId(), 3000, 0), true);
		final ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.broadcastPacket(player, new SM_AETHERFORGING_ANIMATION(player, recipeTemplate.getId(), 0, 1), true);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				int criticLvl = (Rnd.get(10000));
				int xpReward = recipeTemplate.getSkillpoint() + 1000;
				xpReward = xpReward + (xpReward * 50 / 100);
				int productItemId = (recipeTemplate.getComboProductSize() > 0 && criticLvl > 9800) ? recipeTemplate.getComboProduct(1) : recipeTemplate.getProductid();
				ItemService.addItem(player, productItemId, recipeTemplate.getQuantity(), new ItemUpdatePredicate(ItemAddType.AETHERFORGING, ItemUpdateType.INC_ITEM_COLLECT));
				//Aetherforging < 300Pts.
				if (player.getSkillList().getSkillLevel(40011) != 300) {
					player.getObserveController().removeObserver(observer);
					player.getCommonData().addExp(xpReward, RewardType.CRAFTING);
					player.getSkillList().addSkill(player, 40011, player.getSkillList().getSkillLevel(40011) + 1);
				}
				//Aetherforging >= 300Pts.
				if (player.getSkillList().getSkillLevel(40011) >= 300) {
					player.getObserveController().removeObserver(observer);
					player.getCommonData().addExp(xpReward, RewardType.CRAFTING);
				}
				player.getObserveController().removeObserver(observer);
				//Send for: "Berdin's Favor & Abyss Favor"
		        PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFT_SUCCESS_GETEXP);
				PacketSendUtility.sendPacket(player, new SM_AETHERFORGING_ANIMATION(player, recipeTemplate.getId(), 0, 2));
			}
		}, 3000));
	}
	
	@SuppressWarnings("rawtypes")
	public static void checkComponents(Player player, int recipeId, int itemId, int materialsCount) {
		RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		if (recipeTemplate.getComponent() != null) {
			HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
			for (Component a : recipeTemplate.getComponent()) {
				for (ComponentElement b : a.getComponents()) {
					if (b.getItemid().equals(itemId)) {
						hm.put(itemId, b.getQuantity());
					}
				}
			}
			Set<Entry<Integer, Integer>> set = hm.entrySet();
			Iterator<Entry<Integer, Integer>> i = set.iterator();
			while(i.hasNext()) {
				Map.Entry me = (Map.Entry)i.next();
				if (!player.getInventory().decreaseByItemId((Integer)me.getKey(),(Integer)me.getValue())) {
					return;
				}
			}
		}
	}
}