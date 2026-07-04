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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.upgrade.ItemUpgradeTemplate;
import com.aionemu.gameserver.model.templates.item.upgrade.SubMaterialItem;
import com.aionemu.gameserver.model.templates.item.upgrade.UpgradeResultItem;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemUpgradeService
{
	private static final Logger log = LoggerFactory.getLogger(ItemUpgradeService.class);
	
	public static boolean checkItemUpgrade(Player player, Item baseItem, int choice) {
		ItemUpgradeTemplate itemUpgardeTemplate = DataManager.ITEM_UPGRADE_DATA.getItemUpgradeTemplate(baseItem.getItemId());
		UpgradeResultItem resultItem = itemUpgardeTemplate.getUpgrade_result_item().get(choice);
		if (itemUpgardeTemplate == null) {
			return false;
		} if (resultItem.getNeed_abyss_point() != null) {
			if (player.getAbyssRank().getAp() < resultItem.getNeed_abyss_point().getCount()) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REGISTER_ITEM_MSG_UPGRADE_CANNOT_NEED_AP);
				return false;
			}
		} if (resultItem.getNeed_kinah() != null) {
			if (player.getInventory().getKinah() < resultItem.getNeed_kinah().getCount()) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REGISTER_ITEM_MSG_UPGRADE_CANNOT_NEED_QINA);
				return false;
			}
		}
		return true;
	}
	
	public static boolean decreaseMaterial(Player player, Item baseItem, int choice) {
		ItemUpgradeTemplate itemUpgardeTemplate = DataManager.ITEM_UPGRADE_DATA.getItemUpgradeTemplate(baseItem.getItemId());
		UpgradeResultItem resultItem = itemUpgardeTemplate.getUpgrade_result_item().get(choice);
		//If items purifier has none "Upgrade Materials" and required only "AP or Kinah"
		if (resultItem.getUpgrade_materials() == null) {
			if (resultItem.getNeed_abyss_point() != null && resultItem.getUpgrade_materials() == null) {
				AbyssPointsService.setAp(player, -resultItem.getNeed_abyss_point().getCount());
			} if (resultItem.getNeed_kinah() != null && resultItem.getUpgrade_materials() == null) {
				player.getInventory().decreaseKinah(resultItem.getNeed_kinah().getCount());
			}
		}
		//If items purifier has "Upgrade Materials" and required or no somes "AP or Kinah"
		if (resultItem.getUpgrade_materials() != null) {
			for (SubMaterialItem item : resultItem.getUpgrade_materials().getSubMaterialItem()) {
				if (!player.getInventory().decreaseByItemId(item.getId(), item.getCount())) {
					return false;
				}
			}
		} if (resultItem.getNeed_abyss_point() != null && resultItem.getUpgrade_materials() != null) {
			AbyssPointsService.setAp(player, -resultItem.getNeed_abyss_point().getCount());
		} if (resultItem.getNeed_kinah() != null && resultItem.getUpgrade_materials() != null) {
			player.getInventory().decreaseKinah(resultItem.getNeed_kinah().getCount());
		}
		player.getInventory().decreaseByObjectId(baseItem.getObjectId(), 1);
		return true;
	}
}