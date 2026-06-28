/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * 7.5/7.8 client-side misc packet.
 *
 * Kept registered so the client is not disconnected, but no longer completely
 * silent: if a 7.8 client starts sending payload here, we get an actionable log
 * without read failures.
 */
public class CM_UNK_E3 extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_UNK_E3.class);
	private final List<Integer> payload = new ArrayList<Integer>();

	public CM_UNK_E3(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		payload.clear();
		while (getRemainingBytes() >= 4) {
			payload.add(readD());
		}
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	@Override
	protected void runImpl() {
		if (!payload.isEmpty()) {
			Player player = getConnection().getActivePlayer();
			log.debug("CM_UNK_E3 payload player=" + (player != null ? player.getName() : "-") + " payload=" + payload);
		}
	}
}
