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
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/****/
/** Author Wnkrz (Encom)
/****/

public class CM_COMBAT_SUPPORT extends AionClientPacket
{
    int active;
    int time;
    int exp;
    int kinah;
    int item;
    int ap;
    int gp;
    int unk1;
    int unk2;
	
    public CM_COMBAT_SUPPORT(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        this.active = readC();
        this.time = readD();
        this.exp = readD();
        this.kinah = readD();
        this.unk1 = readD();
        this.unk2 = readD();
        this.item = readD();
        this.ap = readD();
        this.gp = readD();
    }
	
    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        if (this.active == 1) {
            player.setCombatSupport(true);
			PlayerGroupService.removePlayer(player);
			PlayerAllianceService.removePlayer(player);
        } else {
            player.setCombatSupport(false);
        }
    }
}