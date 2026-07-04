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
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Rinzler (Encom)
 */

public class CM_INSTANCE_INFO extends AionClientPacket
{
	private static Logger log = LoggerFactory.getLogger(CM_INSTANCE_INFO.class);
	
    @SuppressWarnings("unused")
    private int unk1;
	private boolean isInTeam;
	
    public CM_INSTANCE_INFO(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        unk1 = readD();
        isInTeam = readC() == 0 ? false : true;
    }
	
    @Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (isInTeam) {
			if (player.isInAlliance2()) {
				boolean answer = true;
				for (Player players: player.getPlayerAlliance2().getMembers()) {
					if (answer) {
						PacketSendUtility.sendPacket(players, new SM_INSTANCE_INFO(players, true, players.getCurrentTeam()));
						answer = false;
					} else {
						PacketSendUtility.sendPacket(players, new SM_INSTANCE_INFO(players, false, players.getCurrentTeam()));
					}
				}
			} else if (player.isInGroup2()) {
				boolean answer = true;
				for (Player players: player.getPlayerGroup2().getMembers()) {
					if (answer) {
						PacketSendUtility.sendPacket(players, new SM_INSTANCE_INFO(players, true, players.getCurrentTeam()));
						answer = false;
					} else {
						PacketSendUtility.sendPacket(players, new SM_INSTANCE_INFO(players, false, players.getCurrentTeam()));
					}
				}
			}
		} else {
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_INFO(player, true, player.getCurrentTeam()));
		}
	}
}