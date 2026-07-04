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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.fame.PlayerFame;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.services.player.PlayerFameService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FameAddExpAction")
public class FameAddExpAction extends AbstractItemAction
{
	@XmlAttribute(name = "value")
    protected Integer value;
	
	public FameAddExpAction(Integer value) {
        this.value = value;
    }
	
    public Integer getRate() {
        return value;
    }
	
    public void setRate(Integer value) {
        this.value = value;
    }
	
    public FameAddExpAction() {
    }
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (parentItem == null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		} for (PlayerFame playerFame: player.getPlayerFame().values()) {
		    if (player.getWorldId() == playerFame.getFameEnum().getWorldId()) {
				if (playerFame.getLevel() == 9) {
					//Renown level is already at maximum.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_POPUP_ADDFEXP_USE_ITEM_FULL);
					return false;
				}
			}
		}
		return true;
	}
	
    @Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
        if (player.getInventory().decreaseByObjectId(parentItem.getObjectId().intValue(), 1)) {
			PlayerFameService.getInstance().addFameExp(player, value);
			player.getObserveController().notifyItemuseObservers(parentItem);
			//You gained %num1 %2 Renown from %0.
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GET_FEXP(value));
			ItemTemplate itemTemplate = parentItem.getItemTemplate();
			PacketSendUtility.sendPacket(player, new SM_PLAYER_FAME(player));
            PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), parentItem.getObjectId().intValue(), itemTemplate.getTemplateId()), true);
        }
    }
}