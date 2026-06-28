/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.instance.NeviwindCanyonService;
import com.aionemu.gameserver.services.packet.PacketAuditService;

/**
 * 7.x Neviwind Canyon UI packet.
 *
 * The previous implementation parsed a fixed legacy payload and did nothing in
 * runImpl. 7.8 clients may send small payload variations, so this parser keeps
 * the action verified and stores the rest as integers for service-side handling.
 */
public class CM_NEVIWIND_CANYON extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_NEVIWIND_CANYON.class);
	private int action;
	private int[] payload = new int[0];

	public CM_NEVIWIND_CANYON(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		action = getRemainingBytes() >= 2 ? readH() : 0;
		int count = getRemainingBytes() / 4;
		payload = new int[count];
		for (int i = 0; i < count; i++) {
			payload[i] = readD();
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
		if (log.isDebugEnabled()) {
			log.debug("Neviwind packet player=" + player.getName() + " action=" + action + " payloadSize=" + payload.length);
		}
		PacketAuditService.getInstance().logAction(getPacketName(), getOpcode(), player, action, 0, payload, "neviwind_client_request");
		NeviwindCanyonService.getInstance().handle(player, action, payload);
	}
}
