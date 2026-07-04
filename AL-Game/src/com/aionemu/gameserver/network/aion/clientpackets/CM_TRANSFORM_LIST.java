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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.account.TransformService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CM_TRANSFORM_LIST  extends AionClientPacket
{
    private int itemObjId;
    private int multy;
    private int actionId;
    private int cardId;
    private int collectionId;
    private int activeCollection;
    private ArrayList<Integer> materials = new ArrayList<Integer>();
    private static final Logger log = LoggerFactory.getLogger(TransformService.class);
	
    public CM_TRANSFORM_LIST(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        actionId = readH();
        switch (actionId) {
            case 0: //contract
			     itemObjId = readD();
			     multy =  readC();
            break;
            case 1: //transform
                cardId = readD();
                itemObjId = readD();
            break;
            case 2: //create
                materials.clear();
                materials.add(readD());
                materials.add(readD());
                materials.add(readD());
                materials.add(readD());
                materials.add(readD());
                materials.add(readD());
            break;
            default:
                log.info("unknow function Id : " + actionId + " read : " +  getRemainingBytes());
            break;
        }
    }
	
    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        switch (actionId) {
            case 0:
                TransformService.getInstance().makeTransform(player, itemObjId);
            break;
            case 1:
                TransformService.getInstance().onPlayerTransform(player, itemObjId, cardId);
            break;
            case 2:
				TransformService.getInstance().onCombineTransformation(player, materials);
            break;
        }
    }
}