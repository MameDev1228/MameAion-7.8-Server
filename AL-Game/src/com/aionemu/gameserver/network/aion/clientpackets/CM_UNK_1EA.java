/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * TODO - 7.x Rank List. Audit-only until the 7.8 payload is verified.
 */
public class CM_UNK_1EA extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_UNK_1EA.class);
	private int listId;
	private int action;

	public CM_UNK_1EA(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		listId = readD();
		action = readC();
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		log.debug("CM_UNK_1EA rank-list audit player=" + (player != null ? player.getName() : "-") + " listId=" + listId + " action=" + action);
	}
}
