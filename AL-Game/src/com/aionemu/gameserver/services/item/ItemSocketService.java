package com.aionemu.gameserver.services.item;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ItemSocketService
{
	private static final Logger log = LoggerFactory.getLogger(ItemSocketService.class);
	
	public static ManaStone addManaStone(Item item, int itemId) {
		if (item == null) {
			return null;
		}
		Set<ManaStone> manaStones = item.getItemStones();
		if (manaStones.size() >= item.getSockets(false)) {
			return null;
		}
		int nextSlot = 0;
		boolean slotFound = false;
		for (ManaStone ms : manaStones) {
			if (nextSlot != ms.getSlot()) {
				slotFound = true;
				break;
			}
			nextSlot++;
		} if (!slotFound) {
			nextSlot = manaStones.size();
		}
		ManaStone stone = new ManaStone(item.getObjectId(), itemId, nextSlot, PersistentState.NEW);
		manaStones.add(stone);
		return stone;
	}

	public static ManaStone addManaStone(Item item, int itemId, int slotId) {
        if (item == null) {
            return null;
        }
        Set<ManaStone> manaStones = item.getItemStones();
        if (manaStones.size() >= Item.MAX_BASIC_STONES) {
            return null;
        }
        ManaStone stone = new ManaStone(item.getObjectId(), itemId, slotId, PersistentState.NEW);
        manaStones.add(stone);
        return stone;
    }

	public static void copyManaStones(Item source, Item target) {
        if (source.hasManaStones()) {
            for (ManaStone manaStone: source.getItemStones()) {
                target.getItemStones().add(new ManaStone(target.getObjectId(), manaStone.getItemId(), manaStone.getSlot(), PersistentState.NEW));
            } for (ManaStone manaStone: source.getFusionStones()) {
                target.getFusionStones().add(new ManaStone(target.getObjectId(), manaStone.getItemId(), manaStone.getSlot(), PersistentState.NEW));
            }
        }
    }

	public static void copyFusionStones(Item source, Item target) {
		if (source.hasManaStones()) {
			for (ManaStone manaStone : source.getItemStones()) {
				target.getFusionStones().add(new ManaStone(target.getObjectId(), manaStone.getItemId(), manaStone.getSlot(), PersistentState.NEW));
			}
		}
	}

	public static ManaStone addFusionStone(Item item, int itemId) {
		if (item == null) {
			return null;
		}
		Set<ManaStone> manaStones = item.getFusionStones();
		if (manaStones.size() >= item.getSockets(true)) {
			return null;
		}
		int nextSlot = 0;
		boolean slotFound = false;
		for (ManaStone ms : manaStones) {
			if (nextSlot != ms.getSlot()) {
				slotFound = true;
				break;
			}
			nextSlot++;
		} if (!slotFound) {
			nextSlot = manaStones.size();
		}
		ManaStone stone = new ManaStone(item.getObjectId(), itemId, nextSlot, PersistentState.NEW);
		manaStones.add(stone);
		return stone;
	}

	public static ManaStone addFusionStone(Item item, int itemId, int slotId) {
        if (item == null) {
            return null;
        }
        Set<ManaStone> fusionStones = item.getFusionStones();
        if (fusionStones.size() > item.getSockets(true)) {
            return null;
        }
        ManaStone stone = new ManaStone(item.getObjectId(), itemId, slotId, PersistentState.NEW);
        fusionStones.add(stone);
        return stone;
    }

	public static void removeManastone(Player player, int itemObjId, int slotNum) {
		Storage inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjId);
		if (item == null) {
			log.warn("Item not found during manastone remove");
			return;
		} if (!item.hasManaStones()) {
			log.warn("Item stone list is empty");
			return;
		}
		Set<ManaStone> itemStones = item.getItemStones();
		if (itemStones.size() <= slotNum) {
			return;
		}
		int counter = 0;
		for (ManaStone ms: itemStones) {
			if (counter == slotNum) {
				ms.setPersistentState(PersistentState.DELETED);
				DAOManager.getDAO(ItemStoneListDAO.class).storeManaStones(Collections.singleton(ms));
				itemStones.remove(ms);
				break;
			}
			counter++;
		}
		ItemPacketService.updateItemAfterInfoChange(player, item);
	}

	public static void removeFusionstone(Player player, int itemObjId, int slotNum) {
		Storage inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjId);
		if (item == null) {
			log.warn("Item not found during manastone remove");
			return;
		} if (!item.hasFusionStones()) {
			log.warn("Item stone list is empty");
			return;
		}
		Set<ManaStone> itemStones = item.getFusionStones();
		if (itemStones.size() <= slotNum) {
			return;
		}
		int counter = 0;
		for (ManaStone ms : itemStones) {
			if (counter == slotNum) {
				ms.setPersistentState(PersistentState.DELETED);
				DAOManager.getDAO(ItemStoneListDAO.class).storeFusionStones(Collections.singleton(ms));
				itemStones.remove(ms);
				break;
			}
			counter++;
		}
		ItemPacketService.updateItemAfterInfoChange(player, item);
	}

	public static void removeAllManastone(Player player, Item item) {
        if (item == null) {
            log.warn("Item not found during manastone remove");
            return;
        } if (!item.hasManaStones()) {
            return;
        }
        Set<ManaStone> itemStones = item.getItemStones();
        for (ManaStone ms : itemStones) {
            ms.setPersistentState(PersistentState.DELETED);
        }
        DAOManager.getDAO(ItemStoneListDAO.class).storeManaStones(itemStones);
        itemStones.clear();
        ItemPacketService.updateItemAfterInfoChange(player, item);
    }

	public static void removeAllFusionStone(Player player, Item item) {
        if (item == null) {
            log.warn("Item not found during manastone remove");
            return;
        } if (!item.hasFusionStones()) {
            return;
        }
        Set<ManaStone> fusionStones = item.getFusionStones();
        for (ManaStone ms : fusionStones) {
            ms.setPersistentState(PersistentState.DELETED);
        }
        DAOManager.getDAO(ItemStoneListDAO.class).storeFusionStones(fusionStones);
        fusionStones.clear();
        ItemPacketService.updateItemAfterInfoChange(player, item);
    }
	
	private static int getPriceByQuality(Item item) {
		int price = 0;
		switch (item.getItemTemplate().getItemQuality()) {
			case RARE:
			    price = 842;
			break;
			case LEGEND:
			    price = 2542;
			break;
			case UNIQUE:
			    price = 7627;
			break;
			case EPIC:
			    price = 22882;
			break;
			default:
			break;
		}
		return price;
	}
}