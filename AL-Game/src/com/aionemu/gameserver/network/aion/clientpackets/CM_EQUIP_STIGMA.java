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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_EQUIP_STIGMA extends AionClientPacket
{
    private int action;
    private long slotRead;
    private int itemUniqueId;
	
    public CM_EQUIP_STIGMA(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        action = readC();
        slotRead = readQ();
        itemUniqueId = readD();
    }
	
    @Override
    protected void runImpl() {
        Player activePlayer = getConnection().getActivePlayer();
		activePlayer.getController().cancelUseItem();
        Equipment equipment = activePlayer.getEquipment();
        Item equipStigma = activePlayer.getInventory().getItemByObjId(itemUniqueId);
        Item unEquipStigma = activePlayer.getEquipment().getEquippedItemByObjId(itemUniqueId);
		if (!RestrictionsManager.canChangeEquip(activePlayer)) {
			return;
		} if (activePlayer.getEffectController().isAbnormalState(AbnormalState.CANT_ATTACK_STATE)) {
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_ACT_WHILE_IN_ABNORMAL_STATE);
			return;
		} switch (action) {
            case 0:
				equipment.equipItem(equipStigma.getObjectId(), slotRead);
            break;
            case 1:
				equipment.unEquipItem(unEquipStigma.getObjectId(), slotRead);
            break;
        }
    }
}