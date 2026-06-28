/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.combat.CombatSupportService;

/**
 * 7.x Combat Support packet.
 *
 * Variable-length 7.8 payload parser. The actual behavior is implemented in
 * CombatSupportService so this packet no longer silently discards the client
 * intent.
 */
public class CM_COMBAT_SUPPORT extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_COMBAT_SUPPORT.class);

	private int action;
	private final List<Integer> payload = new ArrayList<Integer>();

	public CM_COMBAT_SUPPORT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		payload.clear();
		action = getRemainingBytes() > 0 ? readC() : 0;
		while (getRemainingBytes() >= 4) {
			payload.add(readD());
		}
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		CombatSupportService.getInstance().handlePacket(player, action, payload);
	}
}
