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
package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import java.util.Iterator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnchantStigmaAction")
public class EnchantStigmaAction extends AbstractItemAction
{
    @XmlAttribute(name = "count")
    private int count;
	
    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem) {
        if (parentItem == null || targetItem == null) {
            ///The item cannot be found.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
            return false;
        } if (targetItem.getEnchantLevel() >= 15) {
            ///You cannot enchant %0 any further.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_IT_CAN_NOT_BE_ENCHANTED_MORE_TIME(targetItem.getNameId()));
            return false;
        } if (targetItem.isEquipped()) {
            PacketSendUtility.sendBrightYellowMessageOnCenter(player, "You can not enchant a stigma stone equipped.");
            return false;
        } if (player.getInventory().getKinah() < getStigmaByQuality(parentItem)) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
            return false;
        }
        return true;
    }
	
    @Override
    public void act(final Player player, final Item parentItem, final Item targetItem) {
        if (!canAct(player, parentItem, targetItem)) {
            return;
        }
		int enchantCast = 0;
		if (player.getGameStats().getStat(StatEnum.ENCHANT_BOOST, 0).getCurrent() != 0) {
			enchantCast = EnchantsConfig.ENCHANT_SPEED / 2 - (EnchantsConfig.ENCHANT_SPEED * player.getGameStats().getStat(StatEnum.ENCHANT_BOOST, 0).getCurrent() / 100);
		} else {
			enchantCast = EnchantsConfig.ENCHANT_SPEED;
		}
		final boolean isStigmaSuccess = isGuaranteedStigmaEnchantStone(parentItem) || Rnd.chance(65);
        final int parentItemId = parentItem.getItemId();
        final int parentObjectId = parentItem.getObjectId();
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItemId, enchantCast, 0, 0), true);
        final ItemUseObserver moveObserver = new ItemUseObserver() {
            @Override
            public void abort() {
                player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentObjectId, parentItemId, 0, 2, 0), true);
                ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				///Stigma enchantment of %0 has been cancelled.
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_ENCHANT_CANCEL(targetItem.getNameId()));
            }
        };
        player.getObserveController().attach(moveObserver);
        player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (isStigmaSuccess) {
					player.getController().cancelTask(TaskId.ITEM_USE);
					player.getObserveController().removeObserver(moveObserver);
                    PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentObjectId, parentItemId, 0, 1, 1), true);
                    ///You have successfully charged %0 and the Stigma's charge level has increased by 1.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_ENCHANT_SUCCESS(new DescriptionId(targetItem.getNameId())));
                    player.getInventory().decreaseKinah(getStigmaByQuality(parentItem));
					player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
					targetItem.setEnchantLevel(targetItem.getEnchantLevel() + 1);
                    targetItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
					PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, targetItem));
					player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
                } else {
					player.getObserveController().removeObserver(moveObserver);
					if (targetItem.getEnchantLevel() == 0) {
						targetItem.setEnchantLevel(0);
						///You have failed to enchant %0 but the enchantment level remains the same.
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_MATTER_ENCHANT_FAIL2(new DescriptionId(targetItem.getNameId())));
					} else if (targetItem.getEnchantLevel() >= 1 && targetItem.getEnchantLevel() <= 9) {
						targetItem.setEnchantLevel(targetItem.getEnchantLevel() - 1);
						///You have failed to enchant %0 and the enchantment level has decreased by 1.
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_MATTER_ENCHANT_FAIL(new DescriptionId(targetItem.getNameId())));
					} else if (targetItem.getEnchantLevel() >= 10 && targetItem.getEnchantLevel() <= 15) {
						targetItem.setEnchantLevel(10);
						///You have failed to enchant %0.
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_FAILED(new DescriptionId(targetItem.getNameId())));
					}
					player.getInventory().decreaseKinah(getStigmaByQuality(parentItem));
					player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
					targetItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
					PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, targetItem));
                }
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, isStigmaSuccess ? 1 : 2, 384));
				Iterator<Player> iter = World.getInstance().getPlayersIterator();
				while (iter.hasNext()) {
					Player player2 = iter.next();
					if (targetItem.getEnchantLevel() >= 1 && targetItem.getEnchantLevel() <= 15 && isStigmaSuccess) {
						if (player2.getRace() == player.getRace()) {
							///%0 has succeeded in enchanting %1 to level %2.
							PacketSendUtility.sendPacket(player2, SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEEDED_COMMON(player.getName(), targetItem.getItemTemplate().getNameId(), targetItem.getEnchantLevel()));
						}
					}
				}
            }
        }, enchantCast));
    }
	
	private boolean isGuaranteedStigmaEnchantStone(Item item) {
		if (item == null || item.getItemTemplate() == null) {
			return false;
		}
		int itemId = item.getItemId();
		if (itemId == 166075000 || itemId == 166075001) {
			return true;
		}
		String name = item.getItemTemplate().getName();
		return name != null && name.toLowerCase().contains("100_stigma");
	}

    public static int getStigmaByQuality(Item item) {
        int price = 0;
        switch (item.getItemTemplate().getItemQuality()) {
            case RARE:
                price = 25425;
            break;
			case LEGEND:
                price = 76275;
            break;
			case UNIQUE:
                price = 228825;
            break;
            default:
            break;
        }
        return price;
    }
}