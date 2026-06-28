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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * 7.x Combat Support packet.
 *
 * This packet was previously read and silently discarded. Keeping the payload
 * fields lets us audit the 7.8 client behaviour without breaking the session.
 * The actual auto-combat implementation should be added after opcodes are
 * confirmed against the production 7.8 client.
 *
 * @author Falke_34
 */
public class CM_COMBAT_SUPPORT extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_COMBAT_SUPPORT.class);

	private int action;
	private int value1;
	private int value2;
	private int value3;
	private int value4;
	private int value5;
	private int value6;
	private int value7;
	private int value8;
	private int value9;

	public CM_COMBAT_SUPPORT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		action = readC();
		value1 = readD();
		value2 = readD();
		value3 = readD();
		value4 = readD();
		value5 = readD();
		value6 = readD();
		value7 = readD();
		value8 = readD();
		value9 = readD();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		log.debug(String.format("CombatSupport packet player=%s action=%d payload=[%d,%d,%d,%d,%d,%d,%d,%d,%d]", player.getName(), action, value1, value2, value3, value4, value5, value6, value7, value8, value9));
	}
}
