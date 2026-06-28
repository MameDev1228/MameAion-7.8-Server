/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dao.PlayerLunaShopDAO;
import com.aionemu.gameserver.dao.PlayerWardrobeDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.LunaBuffBonus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerLunaShop;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.luna.LunaConsumeRewardsTemplate;
import com.aionemu.gameserver.model.templates.recipe.LunaComponent;
import com.aionemu.gameserver.model.templates.recipe.LunaComponentElement;
import com.aionemu.gameserver.model.templates.recipe.LunaTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_INSTANCE_BUFF;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SYSTEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SYSTEM_INFO;
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

public class LunaShopService {

	private Logger log = LoggerFactory.getLogger(LunaShopService.class);
	PlayerWardrobeDAO wDAO = DAOManager.getDAO(PlayerWardrobeDAO.class);
	private boolean dailyGenerated = true;
//	private boolean specialGenerated = true;
	private boolean reciveBonus = false;
	private List<Integer> DailyCraft = new ArrayList<Integer>();
	private List<Integer> SpecialCraft = new ArrayList<Integer>();
//	private List<Integer> armors = new ArrayList<Integer>();
//	private List<Integer> pants = new ArrayList<Integer>();
//	private List<Integer> shoes = new ArrayList<Integer>();
//	private List<Integer> gloves = new ArrayList<Integer>();
//	private List<Integer> shoulders = new ArrayList<Integer>();
//	private List<Integer> weapons = new ArrayList<Integer>();

	public void init() {
		log.info("[LunaSystem] Luna Reset");
		String daily = "0 0 9 1/1 * ? *";
//		String weekly = "0 0 9 ? * WED *";
		if (DailyCraft.size() == 0) {
			generateDailyCraft();
		}
//		if (SpecialCraft.size() == 0) {
//			generateSpecialCraft();
//		}

		CronService.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				dailyGenerated = false;
				generateDailyCraft();
				resetFreeLuna();
			}
		}, daily);

//		CronService.getInstance().schedule(new Runnable() {
//
//			@Override
//			public void run() {
//				specialGenerated = false;
//				generateSpecialCraft();
//			}
//		}, weekly);
	}

//	public void generateSpecialCraft() {
//		if (SpecialCraft.size() > 0) {
//			SpecialCraft.clear();
//		}
//		armors.add(10029);
//		armors.add(10031);
//		armors.add(10033);
//		armors.add(10035);
//		pants.add(10037);
//		pants.add(10039);
//		pants.add(10041);
//		pants.add(10043);
//		shoes.add(10061);
//		shoes.add(10063);
//		shoes.add(10065);
//		shoes.add(10067);
//		gloves.add(10053);
//		gloves.add(10055);
//		gloves.add(10057);
//		gloves.add(10059);
//		shoulders.add(10045);
//		shoulders.add(10047);
//		shoulders.add(10049);
//		shoulders.add(10051);
//		weapons.add(10021);
//		weapons.add(10017);
//		weapons.add(10025);
//		weapons.add(10005);
//		weapons.add(10011);
//		weapons.add(10023);
//		weapons.add(10003);
//		weapons.add(10007);
//		weapons.add(10019);
//		weapons.add(10013);
//		weapons.add(10027);
//		weapons.add(10009);
//		weapons.add(10015);
//		weapons.add(10001);
//		int rnd = Rnd.get(1, 6);
//		switch (rnd) {
//			case 1:
//				SpecialCraft.addAll(weapons);
//				break;
//			case 2:
//				SpecialCraft.addAll(armors);
//				break;
//			case 3:
//				SpecialCraft.addAll(pants);
//				break;
//			case 4:
//				SpecialCraft.addAll(shoes);
//				break;
//			case 5:
//				SpecialCraft.addAll(gloves);
//				break;
//			case 6:
//				SpecialCraft.addAll(shoulders);
//				break;
//		}
//		if (!specialGenerated) {
//			updateSpecialCraft();
//		}
//	}

	public void resetFreeLuna() {
		DAOManager.getDAO(PlayerLunaShopDAO.class).delete();
		updateFreeLuna();
	}

	public void sendSpecialCraft(Player player) {
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(2, 0, SpecialCraft));
	}

//	private void updateSpecialCraft() {
//		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
//
//			@Override
//			public void visit(Player player) {
//				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(2, 0, SpecialCraft));
//			}
//		});
//	}

	private void updateFreeLuna() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PlayerLunaShop pls = new PlayerLunaShop(true, true, true);
				pls.setPersistentState(PersistentState.UPDATE_REQUIRED);
				player.setPlayerLunaShop(pls);
				DAOManager.getDAO(PlayerLunaShopDAO.class).store(player);
			}
		});
	}

	public void generateDailyCraft() {
		if (DailyCraft.size() > 0) {
			dailyGenerated = false;
			DailyCraft.clear();
		}
		FastList<LunaTemplate> templates = DataManager.LUNA_DATA != null ? DataManager.LUNA_DATA.getLunaTemplatesAny() : null;
		if (templates == null || templates.isEmpty()) {
			log.warn("[LunaSystem] No Luna craft templates loaded; daily craft list is empty.");
			return;
		}
		Random rand = new Random();
		int count = Math.min(5, templates.size());
		for (int i = 0; i < count; i++) {
			LunaTemplate randomElement = templates.get(rand.nextInt(templates.size()));
			if (randomElement != null) {
				DailyCraft.add(randomElement.getId());
			}
		}
		if (!dailyGenerated) {
			updateDailyCraft();
		}
	}


	public void sendDailyCraft(Player player) {
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(2, 1, DailyCraft));
	}

	private void updateDailyCraft() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(2, 1, DailyCraft));
				dailyGenerated = true;
			}
		});
	}

	public void lunaPointController(Player player, int point) {
		player.setLunaAccount(point);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0, player.getLunaAccount()));
	}

	public void muniKeysController(Player player, int keys) {
		player.setMuniKeys(keys);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(4));
	}

	public void onLogin(Player player) {
		if (player == null) {
			return;
		}
		ensureLunaShop(player);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(7));
		sendSpecialCraft(player);
		sendDailyCraft(player);
		for (int i = 0; i < 9; i++) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(8, i, 0));
		}
		updateLunaInfo(player);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(4, player.getMuniKeys()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(9, 0));
		if (!player.getPlayerLunaShop().isFreeUnderpath()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 45));
		}
		if (!player.getPlayerLunaShop().isFreeFactory()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 47));
		}
		if (!player.getPlayerLunaShop().isFreeChest()) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 1));
		}
	}


	public void specialDesign(Player player, int recipeId) {
		if (player == null || recipeId <= 0) {
			return;
		}
		LunaTemplate recipe = DataManager.LUNA_DATA != null ? DataManager.LUNA_DATA.getLunaTemplateById(recipeId) : null;
		if (recipe == null) {
			log.warn("[LunaSystem] Missing Luna recipe {} requested by {}", recipeId, player.getName());
			return;
		}
		int productId = recipe.getProductid();
		int quantity = recipe.getQuantity();
		ItemTemplate item = DataManager.ITEM_DATA.getItemTemplate(productId);
		if (item == null || quantity <= 0) {
			log.warn("[LunaSystem] Invalid Luna recipe " + recipeId + " product=" + productId + " qty=" + quantity);
			return;
		}
		if (!hasRecipeMaterials(player, recipe)) {
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(2, item, 1));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(3, productId, quantity, false));
			return;
		}
		boolean isSuccess = isSuccess(player, recipeId);
		consumeRecipeMaterials(player, recipe);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(2, item, isSuccess ? 0 : 1));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(3, productId, quantity, isSuccess));
		if (isSuccess) {
			ItemService.addItem(player, productId, quantity);
		}
	}


	public void craftBox(Player player) {
		if (player == null) {
			return;
		}
		ensureLunaShop(player);
		int itemId = 188055460;
		if (player.getPlayerLunaShop().isFreeChest()) {
			player.getPlayerLunaShop().setFreeChest(false);
		}
		else if (!spendLuna(player, 2)) {
			return;
		}
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(3, itemId, 1, true));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 1));
		ItemService.addItem(player, itemId, 1);
	}


	private boolean isSuccess(Player player, int recipeId) {
		LunaTemplate recipe = DataManager.LUNA_DATA != null ? DataManager.LUNA_DATA.getLunaTemplateById(recipeId) : null;
		if (recipe == null) {
			return false;
		}
		float rate = recipe.getRate();
		return rate >= 100 || (rate > 0 && Rnd.get(1, 100) <= rate);
	}


	public void buyMaterials(Player player, int itemId, long count) {
		if (player == null || itemId <= 0 || count <= 0 || count > 1000000L) {
			return;
		}
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (itemTemplate == null || itemTemplate.getLunaPrice() <= 0) {
			return;
		}
		long price = count * itemTemplate.getLunaPrice();
		if (price <= 0 || !spendLuna(player, price)) {
			return;
		}
		ItemService.addItem(player, itemId, count);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(4, player.getMuniKeys()));
	}


	public void dorinerkWardrobeLoad(Player player) {
		int size = DAOManager.getDAO(PlayerWardrobeDAO.class).getItemSize(player.getObjectId());
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(8, player.getWardrobeSlot(), size));
	}

	public void dorinerkWardrobeAct(Player player, int applySlot, int itemObjId) {
		if (player == null || applySlot < 0 || applySlot >= player.getWardrobeSlot()) {
			return;
		}
		Item sourceItem = player.getInventory().getItemByObjId(itemObjId);
		if (sourceItem == null) {
			return;
		}
		int itemId = sourceItem.getItemId();
		int itemOnDB = DAOManager.getDAO(PlayerWardrobeDAO.class).getWardrobeItemBySlot(player.getObjectId(), applySlot);
		if (itemOnDB != 0) {
			if (!spendLuna(player, 10)) {
				return;
			}
			DAOManager.getDAO(PlayerWardrobeDAO.class).delete(player.getObjectId(), itemOnDB);
		}
		player.getWardrobe().addItem(player, itemId, applySlot, 0);
		player.getInventory().decreaseByObjectId(itemObjId, 1);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(10, 0x00, applySlot, itemId, 1));
	}


	public void dorinerkWardrobeModifyAppearance(Player player, int applySlot, int itemObjId) {
		if (player == null || applySlot < 0 || applySlot >= player.getWardrobeSlot()) {
			return;
		}
		int itemId = DAOManager.getDAO(PlayerWardrobeDAO.class).getWardrobeItemBySlot(player.getObjectId(), applySlot);
		if (itemId == 0) {
			return;
		}
		int reskinCount = DAOManager.getDAO(PlayerWardrobeDAO.class).getReskinCountBySlot(player.getObjectId(), applySlot);
		ItemTemplate it = DataManager.ITEM_DATA.getItemTemplate(itemId);
		Storage inventory = player.getInventory();
		Item keepItem = inventory.getItemByObjId(itemObjId);
		if (it == null || keepItem == null) {
			return;
		}
		if (reskinCount != 0 && !spendLuna(player, 15)) {
			return;
		}
		DAOManager.getDAO(PlayerWardrobeDAO.class).setReskinCountBySlot(player.getObjectId(), applySlot, reskinCount + 1);
		keepItem.setItemSkinTemplate(it);
		if (!keepItem.getItemTemplate().isItemDyePermitted()) {
			keepItem.setItemColor(0);
		}
		keepItem.setLunaReskin(true);
		ItemPacketService.updateItemAfterInfoChange(player, keepItem, ItemUpdateType.STATS_CHANGE);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_SUCCEED(new DescriptionId(keepItem.getItemTemplate().getNameId())));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(11, applySlot));
	}


	public void dorinerkWardrobeExtendSlots(Player player) {
		if (player == null) {
			return;
		}
		int currentSlot = player.getWardrobeSlot();
		int nextSlot = currentSlot + 1;
		int price = wardrobePrice(nextSlot);
		if (price <= 0 || !spendLuna(player, price)) {
			return;
		}
		int size = DAOManager.getDAO(PlayerWardrobeDAO.class).getItemSize(player.getObjectId());
		player.setWardrobeSlot(nextSlot);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(9, player.getWardrobeSlot(), size));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5, player.getLunaAccount()));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(4, player.getMuniKeys()));
	}


	public void takiAdventure(Player player, int indun_id) {
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(14, indun_id));
	}

	public void takiAdventureTeleport(Player player, int indun_unk, int indun_id) {
		if (player == null) {
			return;
		}
		ensureLunaShop(player);
		if (indun_id == 1) {
			boolean free = player.getPlayerLunaShop().isFreeUnderpath();
			if (!free && !spendLuna(player, 89)) {
				return;
			}
			WorldMapInstance contaminatedUnderpath = InstanceService.getNextAvailableInstance(301630000);
			if (contaminatedUnderpath == null) {
				return;
			}
			InstanceService.registerPlayerWithInstance(contaminatedUnderpath, player);
			TeleportService2.teleportTo(player, 301630000, contaminatedUnderpath.getInstanceId(), 230f, 169f, 164f, (byte) 60);
			if (free) {
				player.getPlayerLunaShop().setLunaShopByObjId(player.getObjectId());
				player.getPlayerLunaShop().setFreeUnderpath(false);
			}
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 45));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(0, 0));
		}
		else if (indun_id == 2) {
			boolean free = player.getPlayerLunaShop().isFreeFactory();
			if (!free && !spendLuna(player, 59)) {
				return;
			}
			WorldMapInstance secretMunitionsFactory = InstanceService.getNextAvailableInstance(301640000);
			if (secretMunitionsFactory == null) {
				return;
			}
			InstanceService.registerPlayerWithInstance(secretMunitionsFactory, player);
			TeleportService2.teleportTo(player, 301640000, secretMunitionsFactory.getInstanceId(), 400.3279f, 290.5061f, 198.64015f, (byte) 60);
			if (free) {
				player.getPlayerLunaShop().setLunaShopByObjId(player.getObjectId());
				player.getPlayerLunaShop().setFreeFactory(false);
			}
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 47));
			PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(0, 0));
		}
	}


	public void teleport(Player player, int action, int teleportId) {
		switch (action) {
			case 6:
				PacketSendUtility.sendMessage(player, "teleportId : " + teleportId);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(6));
				break;
			case 7:
				PacketSendUtility.sendMessage(player, "teleportId : " + teleportId);
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(7));
				break;
		}
	}

	public void munirunerksTreasureChamber(final Player player) {
		if (player == null) {
			return;
		}
		HashMap<Integer, Long> hm = new HashMap<Integer, Long>();
		hm.put(188054633, 1L);
		hm.put(188054634, 1L);
		hm.put(166030013, 1L);
		hm.put(166020003, 1L);
		hm.put(188054122, 1L);
		hm.put(188055183, 1L);
		hm.put(188054287, 1L);
		hm.put(188054462, 1L);
		hm.put(188052639, 1L);
		hm.put(169405339, 10L);
		hm.put(164000076, 10L);
		hm.put(164000134, 10L);
		hm.put(166000196, 3L);
		hm.put(186000242, 2L);
		hm.put(186000051, 2L);
		hm.put(188055168, 10L);
		hm.put(188054283, 30L);
		hm.put(188054463, 1L);
		hm.put(188053002, 1L);
		hm.put(188100335, 2000L);
		hm.put(164000073, 10L);
		hm.put(160002497, 1L);
		hm.put(160002499, 1L);
		if (player.getMuniKeys() > 0) {
			player.setMuniKeys(player.getMuniKeys() - 1);
		}
		else {
			if (!spendLuna(player, 19)) {
				return;
			}
			player.setLunaConsumePoint(player.getLunaConsumePoint() + 25);
			applyLunaConsumeReward(player);
		}
		final HashMap<Integer, Long> mt = new HashMap<Integer, Long>();
		Object[] keys = hm.keySet().toArray();
		Random rnd = new Random();
		while (mt.size() < 3 && mt.size() < hm.size()) {
			Integer key = (Integer) keys[rnd.nextInt(keys.length)];
			mt.put(key, hm.get(key));
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				for (Map.Entry<Integer, Long> e : mt.entrySet()) {
					ItemTemplate t = DataManager.ITEM_DATA.getItemTemplate(e.getKey());
					if (t == null) {
						continue;
					}
					ItemService.addItem(player, e.getKey(), e.getValue());
					if (e.getValue() == 1) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_REWARD_GOTCHA_ITEM(t.getNameId()));
					}
					else if (e.getValue() > 1) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_REWARD_GOTCHA_ITEM_MULTI(e.getValue(), t.getNameId()));
					}
				}
				PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(mt));
			}
		}, 1);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(4, player.getMuniKeys()));
		updateLunaInfo(player);
	}


	public void onLogout(Player player) {
		PlayerLunaShop pls = player.getPlayerLunaShop();
		pls.setPersistentState(PersistentState.UPDATE_REQUIRED);
		DAOManager.getDAO(PlayerLunaShopDAO.class).store(player);
	}

	private int wardrobePrice(int WardrobeSlot) { // Done
		switch (WardrobeSlot) {
			case 1:
			case 2:
			case 3:
			case 4:
				return 69;
			case 5:
			case 6:
			case 7:
			case 8:
				return 99;
		}
		return 0;
	}
	
	public void diceGame(Player player) {
		if (player == null) {
			return;
		}
		int diceTry = player.getLunaDiceGameTry();
		int price = lunaDicePrice(diceTry);
		if (!spendLuna(player, price)) {
			return;
		}
		int random = Rnd.get(1, 1000);
		if (random >= 100 && random <= 400) {
			player.setLunaDiceGame(1, false);
		}
		else if (random >= 450 && random <= 749) {
			player.setLunaDiceGame(2, false);
		}
		else if (random >= 750 && random <= 849) {
			player.setLunaDiceGame(3, false);
		}
		else if (random >= 850 && random <= 900) {
			player.setLunaDiceGame(4, false);
		}
		else {
			player.setLunaDiceGame(5, false);
		}
		player.setLunaDiceGameTry(player.getLunaDiceGameTry() + 1);
		player.setLunaConsumePoint(player.getLunaConsumePoint() + price);
		applyLunaConsumeReward(player);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, diceTry < 1 ? 78 : 79));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(15));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(5));
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(4, player.getMuniKeys()));
	}

	
	public void diceGameReward(Player player) {
		if (player == null || player.getLunaDiceGame() <= 0) {
			return;
		}
		int itemId = 162001014;
		long count = Math.max(1, player.getLunaDiceGame());
		ItemService.addItem(player, itemId, count);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM(16, itemId, count));
		player.setLunaDiceGame(0, true);
		player.setLunaDiceGameTry(0);
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(1, 1, 78));
	}

	
	public int lunaDicePrice(int diceTry) { // Done
		switch (diceTry) {
			case 0:
				return 20;
			case 1:
				return 22;
			case 2:
				return 24;
			case 3:
				return 26;
			case 4:
			case 5:
				return 30;
			case 6:
				return 32;
			case 7:
				return 36;
			case 8:
			case 9:
				return 38;
			case 10:
				return 40;
			default:
				return 40;
		}
	}

    public void sendLunaInstanceBuff(Player player, int buffId) {
        PacketSendUtility.sendPacket(player, new SM_LUNA_INSTANCE_BUFF(buffId, false));
    }

    public void buyLunaBuff(Player player, int buffId) {
        if (player == null || buffId <= 0) {
            return;
        }
        if (DataManager.LUNA_BUFF_DATA == null || DataManager.LUNA_BUFF_DATA.getLunaBuffId(buffId) == null) {
            return;
        }
        if (!spendLuna(player, 20)) {
            return;
        }
        player.setLunaBuffBonus(new LunaBuffBonus(buffId));
        player.getLunaBuffBonus().applyEffect(player);
        PacketSendUtility.sendPacket(player, new SM_LUNA_INSTANCE_BUFF(buffId, true));
        PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_QUNABUFF_SUCCEEDED, 3000);
    }


	private void applyLunaConsumeReward(Player player) {
		int consumePoint = player.getLunaConsumePoint();
		int count = 0;
		switch (consumePoint) {
			case 25:
				count = 1;
				break;
			case 50:
				count = 2;
				break;
			case 100:
				count = 3;
				muniKeysController(player, player.getMuniKeys() + 1);
				break;
			case 150:
				count = 4;
				muniKeysController(player, player.getMuniKeys() + 1);
				break;
			case 300:
				count = 5;
				muniKeysController(player, player.getMuniKeys() + 2);
				break;
			case 500:
				count = 6;
				muniKeysController(player, player.getMuniKeys() + 2);
				break;
			case 1000:
				count = 7;
				muniKeysController(player, player.getMuniKeys() + 3);
				break;
			default:
				return;
		}
		player.setLunaConsumeCount(count);
		LunaConsumeRewardsTemplate reward = DataManager.LUNA_CONSUME_REWARDS_DATA != null ? DataManager.LUNA_CONSUME_REWARDS_DATA.getLunaConsumeRewardsId(count) : null;
		if (reward != null && DataManager.ITEM_DATA.getItemTemplate(reward.getCreateItemId()) != null) {
			ItemService.addItem(player, reward.getCreateItemId(), reward.getCreateItemCount());
		}
	}

	private boolean hasRecipeMaterials(Player player, LunaTemplate recipe) {
		if (recipe.getLunaComponent() == null) {
			return true;
		}
		for (LunaComponent lc : recipe.getLunaComponent()) {
			if (lc == null || lc.getComponents() == null) {
				continue;
			}
			for (LunaComponentElement component : lc.getComponents()) {
				if (component == null || player.getInventory().getItemCountByItemId(component.getItemid()) < component.getQuantity()) {
					return false;
				}
			}
		}
		return true;
	}

	private void consumeRecipeMaterials(Player player, LunaTemplate recipe) {
		if (recipe.getLunaComponent() == null) {
			return;
		}
		for (LunaComponent lc : recipe.getLunaComponent()) {
			if (lc == null || lc.getComponents() == null) {
				continue;
			}
			for (LunaComponentElement component : lc.getComponents()) {
				if (component != null) {
					player.getInventory().decreaseByItemId(component.getItemid(), component.getQuantity());
				}
			}
		}
	}

	private void ensureLunaShop(Player player) {
		if (player.getPlayerLunaShop() == null) {
			PlayerLunaShop pls = new PlayerLunaShop(true, true, true);
			pls.setPersistentState(PersistentState.UPDATE_REQUIRED);
			player.setPlayerLunaShop(pls);
			DAOManager.getDAO(PlayerLunaShopDAO.class).add(player.getObjectId(), pls.isFreeUnderpath(), pls.isFreeFactory(), pls.isFreeChest());
		}
	}

	private boolean spendLuna(Player player, long price) {
		if (price <= 0) {
			return true;
		}
		if (player.getLunaAccount() < price) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
			updateLunaInfo(player);
			return false;
		}
		player.setLunaAccount((int) (player.getLunaAccount() - price));
		updateLunaInfo(player);
		return true;
	}

	private void updateLunaInfo(Player player) {
		PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0, player.getLunaAccount()));
	}

	public static LunaShopService getInstance() {
		return NewSingletonHolder.INSTANCE;
	}

	private static class NewSingletonHolder {

		private static final LunaShopService INSTANCE = new LunaShopService();
	}
}
