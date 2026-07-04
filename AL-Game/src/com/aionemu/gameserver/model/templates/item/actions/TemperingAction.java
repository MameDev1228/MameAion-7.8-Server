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
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/****/
/** Author Ranastic (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name= "TemperingAction")
public class TemperingAction extends AbstractItemAction
{
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (targetItem.getItemTemplate().getMaxAuthorize() == 0) {
			return false;
		} if (targetItem.getItemTemplate().isAccessory() && targetItem.getAuthorizeLevel() >= 15) {
			//%0 cannot be tempered anymore.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_CANT_MORE_AUTHORIZE(new DescriptionId(targetItem.getNameId())));
            return false;
        } if (targetItem.getItemTemplate().isWeapon() && targetItem.getAuthorizeLevel() >= 15) {
			//%0 cannot be tempered anymore.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_CANT_MORE_AUTHORIZE(new DescriptionId(targetItem.getNameId())));
		    return false;
		} if (targetItem.getItemTemplate().isArmor() && targetItem.getAuthorizeLevel() >= 15) {
			//%0 cannot be tempered anymore.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_CANT_MORE_AUTHORIZE(new DescriptionId(targetItem.getNameId())));
		    return false;
		} if (targetItem.getItemTemplate().isPlume() && targetItem.getAuthorizeLevel() >= 15) {
			//%0 cannot be tempered anymore.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_CANT_MORE_AUTHORIZE(new DescriptionId(targetItem.getNameId())));
		    return false;
		} if (targetItem.getItemTemplate().isBracelet() && targetItem.getAuthorizeLevel() >= 15) {
			//%0 cannot be tempered anymore.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_CANT_MORE_AUTHORIZE(new DescriptionId(targetItem.getNameId())));
			return false;
		}
		return targetItem.getAuthorizeLevel() < targetItem.getItemTemplate().getMaxAuthorize();
	}
	
	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		if (player.isAuthorizeBoost()) {
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 1500, 0, 0));
		} else {
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 3000, 0, 0));
		}
		final ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), parentItem.getObjectId().intValue(), parentItem.getItemTemplate().getTemplateId(), 0, 3, 0));
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				//You have canceled the tempering of %0.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_CANCEL(targetItem.getNameId()));
			}
		};
		player.getObserveController().attach(observer);
		final boolean isTemperingSuccess = isTemperingSuccess(player);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (player.getInventory().decreaseByItemId(parentItem.getItemId(), 1)) {
					if (!isTemperingSuccess) {
						PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), player.getObjectId().intValue(), parentItem.getObjectId().intValue(), parentItem.getItemId(), 0, 2, 0));
						//Bracelet.
						if (targetItem.getItemTemplate().isBracelet() && targetItem.getAuthorizeLevel() == 0) {
							targetItem.setAuthorizeLevel(0);
							checkTempering(player, targetItem);
						} else if (targetItem.getItemTemplate().isBracelet() && targetItem.getAuthorizeLevel() > 0) {
							checkTempering(player, targetItem);
							targetItem.setAuthorizeLevel(targetItem.getAuthorizeLevel() - 1);
						}
						//Accessory.
						if (targetItem.getItemTemplate().isAccessory() && targetItem.getAuthorizeLevel() == 0) {
							targetItem.setAuthorizeLevel(0);
						} else if (targetItem.getItemTemplate().isAccessory() && targetItem.getAuthorizeLevel() > 0) {
							targetItem.setAuthorizeLevel(targetItem.getAuthorizeLevel() - 1);
						}
						//Plume.
						if (targetItem.getItemTemplate().isPlume() && targetItem.getAuthorizeLevel() == 0) {
							targetItem.setAuthorizeLevel(0);
						} else if (targetItem.getItemTemplate().isPlume() && targetItem.getAuthorizeLevel() > 0) {
							targetItem.setAuthorizeLevel(targetItem.getAuthorizeLevel() - 1);
						}
						//Weapon.
						if (targetItem.getItemTemplate().isWeapon() && targetItem.getAuthorizeLevel() == 0) {
							targetItem.setAuthorizeLevel(0);
						} else if (targetItem.getItemTemplate().isWeapon() && targetItem.getAuthorizeLevel() > 0) {
							targetItem.setAuthorizeLevel(targetItem.getAuthorizeLevel() - 1);
						}
						//Armor.
						if (targetItem.getItemTemplate().isArmor() && targetItem.getAuthorizeLevel() == 0) {
							targetItem.setAuthorizeLevel(0);
						} else if (targetItem.getItemTemplate().isArmor() && targetItem.getAuthorizeLevel() > 0) {
							targetItem.setAuthorizeLevel(targetItem.getAuthorizeLevel() - 1);
						}
						//You failed to temper %0 & loose -1 level.
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_FAILED_NO_PENALTY(targetItem.getNameId()));
					} else {
						PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), player.getObjectId().intValue(), parentItem.getObjectId().intValue(), parentItem.getItemId(), 0, 1, 0));
						targetItem.setAuthorizeLevel(targetItem.getAuthorizeLevel() + 1);
						if (targetItem.getItemTemplate().isBracelet()) {
							checkTempering(player, targetItem);
						}
						//You have successfully tempered %0. +%num1 temperance level achieved.
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_SUCCEEDED(targetItem.getNameId(), targetItem.getAuthorizeLevel()));
					}
					PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, targetItem));
					player.getObserveController().removeObserver(observer);
					if (targetItem.isEquipped()) {
						player.getGameStats().updateStatsVisually();
					}
					ItemPacketService.updateItemAfterInfoChange(player, targetItem);
					if (targetItem.isEquipped()) {
						player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
					} else {
						player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
					}
				}
			}
		}, 3000));
	}
	
	public void checkTempering(Player player, Item item) {
		if (item.getAuthorizeLevel() >= 5 && item.getAuthorizeLevel() <= 6) {
			item.setOptionalSocket(1);
		} else if (item.getAuthorizeLevel() == 7) {
			item.setOptionalSocket(3);
		} else if (item.getAuthorizeLevel() == 8) {
			item.setOptionalSocket(3);
		} else if (item.getAuthorizeLevel() == 9) {
			item.setOptionalSocket(4);
		} else if (item.getAuthorizeLevel() == 10) {
			item.setOptionalSocket(6);
		} else {
			if (item.hasManaStones()) {
				ItemSocketService.removeAllManastone(player, item);
			}
			item.setOptionalSocket(0);
		}
	}
	
	public boolean isTemperingSuccess(Player player) {
        int chance = Rnd.get(1, 1000);
        if (chance < 800) {
            return true;
        } else {
            return false;
        }
    }


}