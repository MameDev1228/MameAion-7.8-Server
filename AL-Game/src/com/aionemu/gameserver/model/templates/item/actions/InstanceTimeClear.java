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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class InstanceTimeClear extends AbstractItemAction
{
    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem) {
        return true;
    }
	
    @Override
    public void act(final Player player, final Item parentItem, Item targetItem) {
    }
	
    public void act(final Player player, final Item parentItem, final int SelectedSyncId) {
        int mapid = DataManager.INSTANCE_COOLTIME_DATA.getSynchId(SelectedSyncId);
        if (player.getPortalCooldownList().getPortalCooldown(mapid) == 0) {
            player.getController().cancelTask(TaskId.ITEM_USE);
            player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_COOL_TIME_INIT);
            return;
        } if (parentItem.getActivationCount() > 1) {
            parentItem.setActivationCount(parentItem.getActivationCount() - 1);
        } else {
            player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
        }
        player.getPortalCooldownList().reduceEntry(mapid);
    }
}