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
package com.aionemu.gameserver.services.item;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.item.ItemRndBonus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.bonuses.RandomAttr;
import com.aionemu.gameserver.model.templates.item.bonuses.RandomBonus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Krz on décembre 2018
 */

public class ItemTuningService
{
    private Logger log = LoggerFactory.getLogger(ItemTuningService.class);
	
    public void onTuneItem(final Player player, final Item item) {
        if (player.getInventory().getKinah() < getReTuningByQuality(item) && item.getOptionalSocket() != -1) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return;
        }
        final ItemTemplate template = item.getItemTemplate();
        final int nameId = template.getNameId();
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 2000, 0, 0), true);
        final ItemUseObserver moveObserver = new ItemUseObserver() {
            @Override
            public void abort() {
                player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				ItemPacketService.updateItemAfterInfoChange(player, item);
                player.removeItemCoolDown(template.getUseLimits().getDelayId());
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(nameId)));
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 2, 0), true);
            }
        };
        player.getObserveController().attach(moveObserver);
        player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(moveObserver);
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 1, 1), true);
				if(item.getOptionalSocket() != -1) {
                    player.getInventory().decreaseKinah(getReTuningByQuality(item));
                }
                if (item.getItemTemplate().getRandomBonusId() != 0) {
                    if (item.isEquipped()) {
                        onItemUnEquip(player, item);
                    }
                    if(item.getRndBonus().size() !=0){
                        item.getRndBonus().clear();
                        DAOManager.getDAO(InventoryDAO.class).deleteItemRndBonus(item);
                    }
                    RandomBonus bonus = DataManager.ITEM_RANDOM_BONUSES.getRndBonusById(item.getItemTemplate().getRandomBonusId());
                    int rndCount = bonus.getOptionNum();
                    for (int i=0; i< rndCount; i++) {
                        RandomAttr attr = bonus.getRandomAttr().get(Rnd.get(0, bonus.getRandomAttr().size() - 1));
                        int value = Rnd.get(attr.getMinValue(), attr.getMaxValue());
                        ItemRndBonus rndBonus = new ItemRndBonus(attr.getStatName().getItemStoneMask(), value);
                        item.getRndBonus().put(rndBonus.getBonus(), rndBonus);
                        DAOManager.getDAO(InventoryDAO.class).saveItemRndBonus(item, rndBonus);
                    }
                    item.setBonusNumber(bonus.getId());
                    item.setOptionalSocket(0);
                    item.setGrindSocket(0);
                    if (item.isEquipped()) {
                        onItemEquip(player, item);
                        player.getLifeStats().updateCurrentStats();
                        player.getGameStats().updateStatsAndSpeedVisually();
                    }
                }

				item.setPersistentState(PersistentState.UPDATE_REQUIRED);
				player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401626, new DescriptionId(nameId)));
            }
        }, 2000));
    }
	
    public void onTuneItemLuna(final Player player, final Item item, final int statId) {
        if (player.getLunaAccount() < getReTuningLuna(item)) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LUNA_NOT_ENOUGH_LUNA);
            return;
        }
        final ItemTemplate template = item.getItemTemplate();
        final int nameId = template.getNameId();
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 2000, 0, 0), true);
        final ItemUseObserver moveObserver = new ItemUseObserver() {
            @Override
            public void abort() {
                player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				ItemPacketService.updateItemAfterInfoChange(player, item);
                player.removeItemCoolDown(template.getUseLimits().getDelayId());
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(nameId)));
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 2, 0), true);
            }
        };
        player.getObserveController().attach(moveObserver);
        player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(moveObserver);
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 1, 1), true);
                if (item.getItemTemplate().getRandomBonusId() != 0) {
                    RandomBonus bonus = DataManager.ITEM_RANDOM_BONUSES.getRndBonusById(item.getItemTemplate().getRandomBonusId());
                    ItemRndBonus rndBonus = item.getRndBonus().get(statId);
                    for (RandomAttr rnd : bonus.getRandomAttr()) {
                        if (rnd.getStatName().getItemStoneMask() == statId) {
                            rndBonus.setValue(Rnd.get(rnd.getMinValue(), rnd.getMaxValue()));
                        }
                    }
                    DAOManager.getDAO(InventoryDAO.class).updateItemRndBonus(item, rndBonus);
                    if (item.isEquipped()) {
                        onItemEquip(player, item);
                        player.getLifeStats().updateCurrentStats();
                        player.getGameStats().updateStatsAndSpeedVisually();
                    }
                    player.setLunaAccount(player.getLunaAccount() - getReTuningLuna(item));
                    player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
                    PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
                    PacketSendUtility.sendPacket(player, new SM_LUNA_SHOP_LIST(0, player.getLunaAccount()));
                }
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, false ? 1 : 2));
            }
        }, 2000));
    }
	
	public static int getReTuningByQuality(Item item) {
        int price = 0;
        switch (item.getItemTemplate().getItemQuality()) {
            case ANCIENT:
                price = 10000;
            break;
			case RELIC:
                price = 50000;
            break;
			case FINALITY:
                price = 150000;
            break;
            default:
            break;
        }
        return price;
    }
	
	public static int getReTuningLuna(Item item) {
        int luna = 0;
        switch (item.getItemTemplate().getItemQuality()) {
            case ANCIENT:
                luna = 40;
            break;
			case RELIC:
                luna = 80;
            break;
			case FINALITY:
                luna = 160;
            break;
            default:
            break;
        }
        return luna;
    }
	
    public static void onItemEquip(Player player, Item item) {
        item.getRndBonusEffect().applyEffect(player);
    }
	
    public static void onItemUnEquip(Player player, Item item) {
        item.getRndBonusEffect().endEffect(player);
    }
	
    public static ItemTuningService getInstance() {
        return SingletonHolder.instance;
    }
	
    private static class SingletonHolder {
        protected static final ItemTuningService instance = new ItemTuningService();
    }
}