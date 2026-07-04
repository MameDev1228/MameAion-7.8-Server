/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.player;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.LunaConfig;
import com.aionemu.gameserver.dao.PlayerLunaShopDAO;
import com.aionemu.gameserver.dao.PlayerWardrobeDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.LunaBuffBonus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerLunaShop;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.luna.LunaConsumeRewardsTemplate;
import com.aionemu.gameserver.model.templates.luna.LunaDicesReward;
import com.aionemu.gameserver.model.templates.recipe.LunaComponent;
import com.aionemu.gameserver.model.templates.recipe.LunaComponentElement;
import com.aionemu.gameserver.model.templates.recipe.LunaTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUNA_INSTANCE_BUFF;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SHOP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SHOP_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Ranastic
 */

public class LunaShopService
{
	private Logger log = LoggerFactory.getLogger(LunaShopService.class);
	PlayerWardrobeDAO wDAO = DAOManager.getDAO(PlayerWardrobeDAO.class);
	private boolean freeBoxCraft = true;
	private boolean dailyGenerated = true;
	private boolean specialGenerated = true;
	private List<Integer> DailyCraft = new ArrayList<Integer>();
	private List<Integer> SpecialElyosCraft = new ArrayList<Integer>();
	private List<Integer> SpecialAsmoCraft = new ArrayList<Integer>();
	private List<Integer> armors = new ArrayList<Integer>();
	private List<Integer> pants = new ArrayList<Integer>();
	private List<Integer> shoes = new ArrayList<Integer>();
	private List<Integer> gloves = new ArrayList<Integer>();
	private List<Integer> shoulders = new ArrayList<Integer>();
	private List<Integer> weapons = new ArrayList<Integer>();
	
	public void init() {
		log.info("Luna Reset");
		String daily = "0 0 9 ? * * *";
		String weekly = "0 0 9 ? * WED *";
		if (DailyCraft.size() == 0) {
			generateDailyCraft();
		}

		generateSpecialCraft();

		CronService.getInstance().schedule(new Runnable() {
			public void run() {
				dailyGenerated = false;
				generateDailyCraft();
			}
		}, daily);
		CronService.getInstance().schedule(new Runnable() {
			public void run() {
				specialGenerated = false;
				generateSpecialCraft();
			}
		}, weekly);
	}
	
	public void generateSpecialCraft() {
		SpecialElyosCraft.clear();
		SpecialAsmoCraft.clear();

		FastList<LunaTemplate> elyos = DataManager.LUNA_DATA.getLunaTemplatesElyos();
		FastList<LunaTemplate> asmo = DataManager.LUNA_DATA.getLunaTemplatesAsmo();
		Random rand = new Random();
		for (int i = 0; i < 6; i++) {
			int randomIndex = rand.nextInt(elyos.size());
			LunaTemplate randomElement = elyos.get(randomIndex);
			SpecialElyosCraft.add(randomElement.getId());
		}
		for (int i = 0; i < 6; i++) {
			int randomIndex = rand.nextInt(asmo.size());
			LunaTemplate randomElement = asmo.get(randomIndex);
			SpecialElyosCraft.add(randomElement.getId());
		}
		 if (!specialGenerated) {
			updateSpecialCraft();
		}
	}
	
	public void resetFreeLuna() {
		DAOManager.getDAO(PlayerLunaShopDAO.class).delete();
		updateFreeLuna();
	}
	
	public void sendSpecialCraft(Player player) {
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(SpecialElyosCraft, SpecialAsmoCraft));
	}
	
	private void updateSpecialCraft() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(SpecialElyosCraft, SpecialAsmoCraft));
			}
		});
	}
	
	private void updateFreeLuna() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PlayerLunaShop pls = new PlayerLunaShop(true, true, true);
				pls.setPersistentState(PersistentState.UPDATE_REQUIRED);
				player.setPlayerLunaShop(pls);
				DAOManager.getDAO(PlayerLunaShopDAO.class).add(player.getObjectId(), pls.isFreeUnderpath(), pls.isFreeFactory(), pls.isFreeChest());
			}
		});
	}
	
	public void generateDailyCraft() {
		if (DailyCraft.size() > 0) {
			dailyGenerated = false;
			DailyCraft.clear();
		}

		FastList<LunaTemplate> test = DataManager.LUNA_DATA.getLunaTemplatesDaly();
		Random rand = new Random();
		for (int i = 0; i < 5; i++) {
			int randomIndex = rand.nextInt(test.size());
			LunaTemplate randomElement = test.get(randomIndex);
			DailyCraft.add(randomElement.getId());
		}

		if (!dailyGenerated) {
			updateDailyCraft();
		}
	}
	
	public void sendDailyCraft(Player player) {
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(DailyCraft));
	}
	
	private void updateDailyCraft() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(DailyCraft));
				dailyGenerated = true;
			}
		});
	}
	
	public void lunaPointController(Player player, int point) {
		player.setLunaAccount(point);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
	}
	
	public void muniKeysController(Player player, int keys) {
		player.setMuniKeys(keys);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(4));
	}

	public void sendLunaPrice(Player player) {
		if (!player.getPlayerLunaShop().isFreeUnderpath()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 4, 45));
		} if (!player.getPlayerLunaShop().isFreeFactory()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 4, 45));
		} if(!player.getPlayerLunaShop().isFreeChest()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 1, 1));
		}

		if(player.isLunaGoldenDice()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 7, 74));
		} else {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(1, 7, 72));
		}
	}
	
	public void onLogin(Player player) {
		if (player.getPlayerLunaShop() == null) {
			PlayerLunaShop pls = new PlayerLunaShop(true, true, true);
			pls.setPersistentState(PersistentState.UPDATE_REQUIRED);
			player.setPlayerLunaShop(pls);
			DAOManager.getDAO(PlayerLunaShopDAO.class).add(player.getObjectId(), pls.isFreeUnderpath(), pls.isFreeFactory(), pls.isFreeChest());
		}
		sendLunaPrice(player);
		sendSpecialCraft(player);
		sendDailyCraft(player);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(4, player.getMuniKeys()));

		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(8, 0, LunaConfig.LUNA_DICE, "Luna Dice"));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(8, 1, LunaConfig.LUNA_DICE_1, "Luna Dice"));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(8, 2, LunaConfig.LUNA_DICE_2, "Luna Dice"));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(8, 3, LunaConfig.LUNA_DICE_3, "Luna Dice"));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(8, 4, LunaConfig.LUNA_DICE_4, "Luna Dice"));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(8, 5, LunaConfig.LUNA_DICE_5, "Luna Dice"));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(8, 6, LunaConfig.LUNA_DICE_6, "Luna Dice"));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(8, 7, LunaConfig.LUNA_DICE_7, "Luna Dice"));

		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(9));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(6));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(7));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(3));
	}
	
	public void specialDesign(Player player, int recipeId) {
		LunaTemplate recipe = DataManager.LUNA_DATA.getLunaTemplateById(recipeId);
		int product_id = recipe.getProductid();
		int quantity = recipe.getQuantity();
		boolean isSuccess = isSuccess(player, recipeId);
		if (isSuccess) {
			for (LunaComponent lc : recipe.getLunaComponent()) {
				for (LunaComponentElement a : lc.getComponents()) {
					if (!player.getInventory().decreaseByItemId(a.getItemid(), a.getQuantity())) {
						PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(product_id, quantity, true));
						PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(2, product_id, 0));
						return;
					}
				}
			}
			ItemService.addItem(player, product_id, quantity);
		} else {
			for (LunaComponent lc : recipe.getLunaComponent()) {
				for (LunaComponentElement a : lc.getComponents()) {
					if (!player.getInventory().decreaseByItemId(a.getItemid(), a.getQuantity())) {
						PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(product_id, quantity, false));
						PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(2, product_id, 1));
						return;
					}
				}
			}
		}
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(product_id, quantity, isSuccess));
	}
	
	public void craftBox(Player player) {
		int itemId = 188055460;
		ItemTemplate item = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (player.getPlayerLunaShop().isFreeChest()) {
			player.getPlayerLunaShop().setFreeChest(false);
		} else {
			player.setLunaAccount(player.getLunaAccount() - 16);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0));
		}
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(itemId, 1, true));
		sendLunaPrice(player);
		ItemService.addItem(player, itemId, 1);
	}
	
	private boolean isSuccess(Player player, int recipeId) {
		LunaTemplate recipe = DataManager.LUNA_DATA.getLunaTemplateById(recipeId);
		boolean result = false;
		float random = Rnd.get(1, 100);
		if (recipe.getRate() == 100) {
			result = true;
		} else if (recipe.getRate() < 100) {
			if (random <= recipe.getRate()) {
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}
	
	public void buyMaterials(Player player, int itemId, long count) {
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		int lunaPrice = itemTemplate.getLunaPrice();
		long price = count * lunaPrice;
		ItemService.addItem(player, itemId, count);
		player.setLunaAccount(player.getLunaAccount() - price);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(4, player.getMuniKeys()));
	}
	
	public void dorinerkWardrobeLoad(Player player) {
		int size = DAOManager.getDAO(PlayerWardrobeDAO.class).getItemSize(player.getObjectId());
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(8, player.getWardrobeSlot(), size));
	}
	
	public void dorinerkWardrobeAct(Player player, int applySlot, int itemObjId) {
		int itemId = player.getInventory().getItemByObjId(itemObjId).getItemId();
		int itemOnDB = DAOManager.getDAO(PlayerWardrobeDAO.class).getWardrobeItemBySlot(player.getObjectId(), applySlot);
		if (itemOnDB != 0) {
			DAOManager.getDAO(PlayerWardrobeDAO.class).delete(player.getObjectId(), itemOnDB);
			player.setLunaAccount(player.getLunaAccount() - 80);
			player.getWardrobe().addItem(player, itemId, applySlot, 0);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		} else {
			player.getWardrobe().addItem(player, itemId, applySlot, 0);
		}
		player.getInventory().decreaseByObjectId(itemObjId, 1);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(10, 0x00, applySlot, itemId, 1));
	}
	
	public void dorinerkWardrobeModifyAppearance(Player player, int applySlot, int itemObjId) {
		int itemId = DAOManager.getDAO(PlayerWardrobeDAO.class).getWardrobeItemBySlot(player.getObjectId(), applySlot);
		int reskinCount = DAOManager.getDAO(PlayerWardrobeDAO.class).getReskinCountBySlot(player.getObjectId(), applySlot);
		ItemTemplate it = DataManager.ITEM_DATA.getItemTemplate(itemId);
		Storage inventory = player.getInventory();
		Item keepItem = inventory.getItemByObjId(itemObjId);
		if (reskinCount != 0) {
			DAOManager.getDAO(PlayerWardrobeDAO.class).setReskinCountBySlot(player.getObjectId(), applySlot, reskinCount+1);
			player.setLunaAccount(player.getLunaAccount() - 96);
			keepItem.setItemSkinTemplate(it);
			if (!keepItem.getItemTemplate().isItemDyePermitted()) {
				keepItem.setItemColor(0);
			}
			keepItem.setLunaReskin(true);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		} else {
			DAOManager.getDAO(PlayerWardrobeDAO.class).setReskinCountBySlot(player.getObjectId(), applySlot, reskinCount+1);
			keepItem.setItemSkinTemplate(it);
			if (!keepItem.getItemTemplate().isItemDyePermitted()) {
				keepItem.setItemColor(0);
			}
			keepItem.setLunaReskin(true);
		}
		ItemPacketService.updateItemAfterInfoChange(player, keepItem, ItemUpdateType.STATS_CHANGE);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_SUCCEED(new DescriptionId(keepItem.getItemTemplate().getNameId())));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(11, applySlot));
	}
	
	public void dorinerkWardrobeExtendSlots(Player player) {
		int currentSlot = player.getWardrobeSlot();
		int size = DAOManager.getDAO(PlayerWardrobeDAO.class).getItemSize(player.getObjectId());
		player.setWardrobeSlot(currentSlot + 1);
		player.setLunaAccount(player.getLunaAccount() - 80);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(8, player.getWardrobeSlot(), size));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
	}
	
	public void takiAdventure(Player player, int indun_id) {
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(14, indun_id));
	}
	
	public void takiAdventureTeleport(Player player, int indun_unk, int indun_id) {
        if (player.getLevel() < 10) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
            return;
        } if (indun_id == 1) {
			if (player.getPlayerLunaShop().isFreeUnderpath()) {
				WorldMapInstance contaminatedUnderpath = InstanceService.getNextAvailableInstance(301630000);
				InstanceService.registerPlayerWithInstance(contaminatedUnderpath, player);
				TeleportService2.teleportTo(player, 301630000, contaminatedUnderpath.getInstanceId(), 230f, 169f, 164f, (byte) 60);
				player.getPlayerLunaShop().setFreeUnderpath(false);
				player.getPlayerLunaShop().setLunaShopByObjId(player.getObjectId());
			} else {
				if (player.getLunaAccount() < 89) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
					return;
				} else {
					WorldMapInstance contaminatedUnderpath = InstanceService.getNextAvailableInstance(301630000);
					InstanceService.registerPlayerWithInstance(contaminatedUnderpath, player);
					TeleportService2.teleportTo(player, 301630000, contaminatedUnderpath.getInstanceId(), 230f, 169f, 164f, (byte) 60);
					player.setLunaAccount(player.getLunaAccount() - 89);
				}
			}
		} if (indun_id == 2) {
			if (player.getPlayerLunaShop().isFreeFactory()) {
				WorldMapInstance secretMunitionsFactory = InstanceService.getNextAvailableInstance(301640000);
				InstanceService.registerPlayerWithInstance(secretMunitionsFactory, player);
				TeleportService2.teleportTo(player, 301640000, secretMunitionsFactory.getInstanceId(), 400.3279f, 290.5061f, 198.64015f, (byte) 60);
				player.getPlayerLunaShop().setFreeFactory(false);
				player.getPlayerLunaShop().setLunaShopByObjId(player.getObjectId());
			} else {
				if (player.getLunaAccount() < 59) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
					return;
				} else {
					WorldMapInstance secretMunitionsFactory = InstanceService.getNextAvailableInstance(301640000);
					InstanceService.registerPlayerWithInstance(secretMunitionsFactory, player);
					TeleportService2.teleportTo(player, 301640000, secretMunitionsFactory.getInstanceId(), 400.3279f, 290.5061f, 198.64015f, (byte) 60);
					player.setLunaAccount(player.getLunaAccount() - 59);
				}
			}
		}
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		sendLunaPrice(player);
	}
	
	public void teleport(Player player, int action, int teleportId) {
		switch (action) {
			case 6:
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(6));
			break;
			case 7:
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(7));
			break;
		}
	}
	
	public void siegeTeleport(Player player, int action, int siegeId) {
		switch (siegeId) {
			case 1011: //Divine Fortress.
				if (player.getLunaAccount() < 80) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
					return;
				}
				player.setLunaAccount(player.getLunaAccount() - 80);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
				TeleportService2.teleportTo(player, 400070000, 1524.0000f, 1559.0000f, 2309.0000f, (byte) 0);
			break;
			case 1512: //Fern Overlook.
				if (player.getLunaAccount() < 40) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
					return;
				}
				player.setLunaAccount(player.getLunaAccount() - 40);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
				TeleportService2.teleportTo(player, 800050000, 1050.0000f, 551.0000f, 304.0000f, (byte) 90);
			break;
			case 1513: //Breath Of Memory.
				if (player.getLunaAccount() < 40) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
					return;
				}
				player.setLunaAccount(player.getLunaAccount() - 40);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
				TeleportService2.teleportTo(player, 800050000, 740.0000f, 814.0000f, 332.0000f, (byte) 86);
			break;
			case 1515: //East Bank.
				if (player.getLunaAccount() < 40) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
					return;
				}
				player.setLunaAccount(player.getLunaAccount() - 40);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
				TeleportService2.teleportTo(player, 800050000, 1084.0000f, 2763.0000f, 231.0000f, (byte) 81);
			break;
			case 1516: //Bark Giant.
				if (player.getLunaAccount() < 40) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
					return;
				}
				player.setLunaAccount(player.getLunaAccount() - 40);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
				TeleportService2.teleportTo(player, 800050000, 545.0000f, 2186.0000f, 284.0000f, (byte) 28);
			break;
			case 1517: //Shipwreck Cut.
				if (player.getLunaAccount() < 40) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
					return;
				}
				player.setLunaAccount(player.getLunaAccount() - 40);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
				TeleportService2.teleportTo(player, 800050000, 2779.0000f, 1461.0000f, 197.0000f, (byte) 59);
			break;
			case 1518: //Temple Walk.
				if (player.getLunaAccount() < 40) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
					return;
				}
				player.setLunaAccount(player.getLunaAccount() - 40);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
				TeleportService2.teleportTo(player, 800050000, 1930.0000f, 1492.0000f, 215.0000f, (byte) 73);
			break;
			case 1521: //Lakrum.
				if (player.getLunaAccount() < 40) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
					return;
				}
				player.setLunaAccount(player.getLunaAccount() - 40);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action));
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
				TeleportService2.teleportTo(player, 800050000, 2045.0000f, 2009.0000f, 1503.0000f, (byte) 28);
			break;
		}
	}
	
	
	public void lunaRevive(Player player) {
		if (player.getLunaAccount() < 4) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
            return;
		}
		player.setLunaAccount(player.getLunaAccount() - 4);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		PlayerReviveService.lunaRevive(player);
	}
	
	public void buyLunaBuff(Player player, int buffId) {
		if (player.getLunaAccount() < 20) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
            return;
		}
		player.setLunaAccount(player.getLunaAccount() - 20);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
		player.setLunaBuffBonus(new LunaBuffBonus(buffId));
		player.getLunaBuffBonus().applyEffect(player);
		PacketSendUtility.sendPacket(player, new SM_QUNA_INSTANCE_BUFF(buffId, true));
		//You've purchased Lunamin's Cheer buff.
		PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_QUNABUFF_SUCCEEDED, 3000);
	}
	
	public void sendLunaInstanceBuff(Player player, int buffId) {
		PacketSendUtility.sendPacket(player, new SM_QUNA_INSTANCE_BUFF(buffId, false));
	}
	
	public void munirunerksTreasureChamber(final Player player) {
		HashMap<Integer, Long> hm = new HashMap<Integer, Long>();
		hm.put(169020003, (long) 250); //Shard.
		hm.put(166401000, (long) 150); //Manastone Fastener.
		hm.put(188070569, (long) 1); //Level 40 Manastone Bundle.
		hm.put(188070570, (long) 1); //Level 60 Manastone Bundle.
		hm.put(166023100, (long) 2); //Ancient PvE Enchantment Stone.
		hm.put(166023101, (long) 2); //Legendary PvE Enchantment Stone.
		hm.put(166023102, (long) 2); //Ultimate PvE Enchantment Stone.
		hm.put(166033100, (long) 2); //Ancient PvP Enchantment Stone.
		hm.put(166033101, (long) 2); //Legendary PvP Enchantment Stone.
		hm.put(166033102, (long) 2); //Ultimate PvP Enchantment Stone.
		hm.put(188071302, (long) 1); //Bronze Cubicle Bundle.
		hm.put(188071303, (long) 1); //Silver Cubicle Bundle.
		hm.put(188071304, (long) 1); //Gold Cubicle Bundle.
		hm.put(188071305, (long) 1); //Platinum Cubicle Bundle.
		hm.put(190099000, (long) 3); //Transformation Scroll.
		hm.put(162001062, (long) 5); //Berdin's Lucky Star.
		hm.put(162001069, (long) 5); //Abyssal Star.
		hm.put(162005004, (long) 5); //Lesser Recovery Serum.
		hm.put(162005005, (long) 5); //Greater Recovery Serum.
		hm.put(162005006, (long) 5); //Major Recovery Serum.
		hm.put(162005007, (long) 5); //Superior Recovery Serum.
		hm.put(162005008, (long) 5); //Superior Wind Serum.
		hm.put(164010028, (long) 5); //Stellium.
		if (player.getMuniKeys() > 0) {
			player.setMuniKeys(player.getMuniKeys() - 1);
		} else {
			player.setLunaAccount(player.getLunaAccount() - 40);
			player.setLunaConsumePoint(player.getLunaConsumePoint() + 40);
		} if (player.getLunaConsumePoint() == 200) {
			player.setLunaConsumeCount(1);
			LunaConsumeRewardsTemplate lt = DataManager.LUNA_CONSUME_REWARDS_DATA.getLunaConsumeRewardsId(player.getLunaConsumeCount());
			ItemService.addItem(player, lt.getCreateItemId1(), lt.getCreateItemCount1());
		} else if (player.getLunaConsumePoint() == 400) {
			player.setLunaConsumeCount(2);
			LunaConsumeRewardsTemplate lt = DataManager.LUNA_CONSUME_REWARDS_DATA.getLunaConsumeRewardsId(player.getLunaConsumeCount());
			ItemService.addItem(player, lt.getCreateItemId1(), lt.getCreateItemCount1());
		} else if (player.getLunaConsumePoint() == 800) {
			player.setLunaConsumeCount(3);
			LunaConsumeRewardsTemplate lt = DataManager.LUNA_CONSUME_REWARDS_DATA.getLunaConsumeRewardsId(player.getLunaConsumeCount());
			player.setMuniKeys(player.getMuniKeys() + 1);
		} else if (player.getLunaConsumePoint() == 1200) {
			player.setLunaConsumeCount(4);
			LunaConsumeRewardsTemplate lt = DataManager.LUNA_CONSUME_REWARDS_DATA.getLunaConsumeRewardsId(player.getLunaConsumeCount());
			player.setMuniKeys(player.getMuniKeys() + 1);
		} else if (player.getLunaConsumePoint() == 2400) {
			player.setLunaConsumeCount(5);
			LunaConsumeRewardsTemplate lt = DataManager.LUNA_CONSUME_REWARDS_DATA.getLunaConsumeRewardsId(player.getLunaConsumeCount());
			player.setMuniKeys(player.getMuniKeys() + 1);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(4, player.getMuniKeys()));
		} else if (player.getLunaConsumePoint() == 4000) {
			player.setLunaConsumeCount(6);
			LunaConsumeRewardsTemplate lt = DataManager.LUNA_CONSUME_REWARDS_DATA.getLunaConsumeRewardsId(player.getLunaConsumeCount());
			ItemService.addItem(player, lt.getCreateItemId1(), lt.getCreateItemCount1());
			player.setMuniKeys(player.getMuniKeys() + 1);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(4, player.getMuniKeys()));
		} else if (player.getLunaConsumePoint() == 8000) {
			player.setLunaConsumeCount(7);
			LunaConsumeRewardsTemplate lt = DataManager.LUNA_CONSUME_REWARDS_DATA.getLunaConsumeRewardsId(player.getLunaConsumeCount());
			ItemService.addItem(player, lt.getCreateItemId1(), lt.getCreateItemCount1());
			player.setMuniKeys(player.getMuniKeys() + 1);
			PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(4, player.getMuniKeys()));
		}
		final HashMap<Integer, Long> mt = new HashMap<Integer, Long>();
		for (int i=0;i<3;i++) {
			Object[] crunchifyKeys = hm.keySet().toArray();
			Object key = crunchifyKeys[new Random().nextInt(crunchifyKeys.length)];
			mt.put((int)key, (long)hm.get(key));
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				for (Map.Entry<Integer, Long> e : mt.entrySet()) {
					ItemService.addItem(player, (int)e.getKey(), (long)e.getValue());
					ItemTemplate t = DataManager.ITEM_DATA.getItemTemplate((int)e.getKey());
					if (e.getValue() == 1) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_REWARD_GOTCHA_ITEM(t.getNameId()));
					} else if (e.getValue() > 1) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_REWARD_GOTCHA_ITEM_MULTI(e.getValue(), t.getNameId()));
					}
				}
				PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(mt));
			}
		}, 1);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(4, player.getMuniKeys()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
	}
	
	public void lunaDices(Player player, int action) {
		int rndSuccess = Rnd.get(1, 100);
		int price = 0;
		if (player.isLunaGoldenDice()) {
			price = 200;
		} else {
			price = 20;
		}
		if (!LunaConfig.ENABLE_LUNA_DICE) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404432, new Object[0]));
			return;
		} if ((player.getLunaAccount() < price)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1403481, new Object[0]));
			return;
		}
		player.getPlayerAccount().setLuna(player.getLunaAccount() - price);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0));
		int dice = player.getLunaDiceCount();
		boolean isDiceFinish = false;
		int diceReward = 0;
		switch (player.getLunaDiceCount()) {
			case 0:
				if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_1) {
					player.setLunaDiceCount(dice + 1);
					diceReward = LunaConfig.LUNA_DICE_1;
				} else {
					diceReward = LunaConfig.LUNA_DICE;
				}
			break;
			case 1:
				if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_2) {
					player.setLunaDiceCount(dice + 1);
					diceReward = LunaConfig.LUNA_DICE_2;
				} else {
					diceReward = LunaConfig.LUNA_DICE_1;
				}
			break;
			case 2:
				if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_3) {
					player.setLunaDiceCount(dice + 1);
					diceReward = LunaConfig.LUNA_DICE_3;
				} else {
					diceReward = LunaConfig.LUNA_DICE_2;
				}
			break;
			case 3:
				if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_4) {
					player.setLunaDiceCount(dice + 1);
					diceReward = LunaConfig.LUNA_DICE_4;
				} else {
					diceReward = LunaConfig.LUNA_DICE_3;
				}
			break;
			case 4:
				if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_5) {
					player.setLunaDiceCount(dice + 1);
					diceReward = LunaConfig.LUNA_DICE_5;
					player.setLunaGoldenDice(true);
				} else {
					diceReward = LunaConfig.LUNA_DICE_4;
				}
			break;
			case 5:
				if (rndSuccess <= LunaConfig.LUNA_DICE_RATE_6) {
					int rndGolden = Rnd.get(1, 10);
					if (rndGolden > 2) {
						player.setLunaDiceCount(dice + 1);
						diceReward = LunaConfig.LUNA_DICE_6;
						isDiceFinish = true;
					} else {
						player.setLunaDiceCount(dice + 2);
						diceReward = LunaConfig.LUNA_DICE_7;
						isDiceFinish = true;
					}
				} else {
					diceReward = LunaConfig.LUNA_DICE_5_FAIL;
					isDiceFinish = true;
				}
			break;
		}
		player.setLunaDiceReward(diceReward);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action, player, player.isLunaGoldenDice(), isDiceFinish));
		sendLunaPrice(player);
	}

	public void lunaDiceReward(Player player, int action) {
		int diceItemId1 = 0;
		int diceItemCount1 = 0;
		if (DataManager.LUNA_DICE_DATA.getId(player.getLunaDiceReward()).getReward() == null) {
			return;
		}
		int randomIndex = Rnd.get(1, DataManager.LUNA_DICE_DATA.getId(player.getLunaDiceReward()).getReward().size() - 1);
		LunaDicesReward reward = DataManager.LUNA_DICE_DATA.getId(player.getLunaDiceReward()).getReward().get(randomIndex);
		diceItemId1 = reward.getItemid();
		diceItemCount1 = reward.getQuantity();

		player.setLunaDiceCount(0);
		player.setLunaDiceReward(0);
		player.setLunaGoldenDice(false);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action, player, player.isLunaGoldenDice(), true));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP(action, 1, diceItemId1, diceItemCount1));
		ItemService.addItem(player, diceItemId1, diceItemCount1);
		sendLunaPrice(player);
	}
	
	public static LunaShopService getInstance() {
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder {
		protected static final LunaShopService instance = new LunaShopService();
	}
}