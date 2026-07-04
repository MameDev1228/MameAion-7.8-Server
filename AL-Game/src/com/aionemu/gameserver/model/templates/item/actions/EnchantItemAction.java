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

import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.EnchantType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.enchant.*;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.util.Iterator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnchantItemAction")
public class EnchantItemAction extends AbstractItemAction
{
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (parentItem == null || targetItem == null) {
			//The item cannot be found.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		} if (parentItem.getItemTemplate().isEnchantmentStone() &&
		    player.getInventory().getKinah() < EnchantService.EnchantKinah(targetItem)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return false;
		} if (targetItem.getEnchantPvPvELevel() >= 20 && !parentItem.getItemTemplate().isManaStone()) {
			//You cannot enchant %0 any further.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_IT_CAN_NOT_BE_ENCHANTED_MORE_TIME(targetItem.getNameId()));
			return false;
		}
		int msID = parentItem.getItemTemplate().getTemplateId() / 1000000;
		int tID = targetItem.getItemTemplate().getTemplateId() / 1000000;
		int wID = targetItem.getItemTemplate().getTemplateId() / 1000000;
		int oID = targetItem.getItemTemplate().getTemplateId() / 1000000;
		int rID = targetItem.getItemTemplate().getTemplateId() / 1000000;
		if (msID != 166 && msID != 167 || msID != 166 && tID >= 120) {
			if (targetItem.getItemTemplate().isPlume() &&
			    targetItem.getItemTemplate().isBracelet() &&
				//targetItem.getItemTemplate().isGlyph() &&
				targetItem.getItemTemplate().isAccessory()) {
				return true;
			}
		} else if (msID != 166) {
			if (tID >= 120 && wID != 187) {
				return false;
			}
		} if (targetItem.getItemTemplate().getArmorType() == ArmorType.WING) {
			return true;
		}
		//Achievement Service 7.x
		AchievementService.getInstance().onUpdateAchievementAction(player, parentItem.getItemId(), 1, AchievementActionType.ITEM_PLAY);
		return true;
	}
	
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		if (player.getInventory().getKinah() < EnchantService.EnchantKinah(targetItem)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return;
		}
		int enchantCast = 0;
		if (player.getGameStats().getStat(StatEnum.ENCHANT_BOOST, 0).getCurrent() != 0) {
			enchantCast = EnchantsConfig.ENCHANT_SPEED / 2 - (EnchantsConfig.ENCHANT_SPEED * player.getGameStats().getStat(StatEnum.ENCHANT_BOOST, 0).getCurrent() / 100);
		} else {
			enchantCast = EnchantsConfig.ENCHANT_SPEED;
		}
		final boolean isSuccess = isSuccess(player, parentItem, targetItem, 0);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), enchantCast, 0, 0));
		final ItemUseObserver moveObserver = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), targetItem.getObjectId().intValue(), targetItem.getItemTemplate().getTemplateId(), 0, 3, 0));
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				//You have cancelled the enchanting of %0.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_CANCELED(targetItem.getItemTemplate().getNameId()));
			}
		};
		player.getObserveController().attach(moveObserver);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(moveObserver);
				ItemTemplate itemTemplate = parentItem.getItemTemplate();
				EnchantService.enchantItemAct(player, parentItem, targetItem, targetItem.getEnchantPvPvELevel(), isSuccess);
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, isSuccess ? 1 : 2, 384));
				if (itemTemplate.isEnchantmentStone()) {
					Iterator<Player> iter = World.getInstance().getPlayersIterator();
					while (iter.hasNext()) {
						Player player2 = iter.next();
						if (targetItem.getEnchantPvPvELevel() >= 1 && targetItem.getEnchantPvPvELevel() <= 20 && isSuccess) {
							if (player2.getRace() == player.getRace()) {
								///%0 has succeeded in enchanting %1 to level %2.
								PacketSendUtility.sendPacket(player2, SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEEDED_COMMON(player.getName(), targetItem.getItemTemplate().getNameId(), targetItem.getEnchantPvPvELevel()));
							}
						}
					}
				}
			}
		}, enchantCast));
	}
	
    private boolean isSuccess(final Player player, final Item parentItem, final Item targetItem, final int targetWeapon) {
        if (parentItem.getItemTemplate() != null) {
            return EnchantService.enchantItem(player, parentItem, targetItem);
        }
        return false;
    }
}