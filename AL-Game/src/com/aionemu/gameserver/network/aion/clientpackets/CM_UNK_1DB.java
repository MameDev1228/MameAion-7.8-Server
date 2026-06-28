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
 * 7.x unknown/audit packet. Kept as a non-mutating parser so opcode mapping can
 * be verified on the real 7.8 client.
 */
public class CM_UNK_1DB extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_UNK_1DB.class);
	private int objectId;
	private int action;
	private int[] payload;

	public CM_UNK_1DB(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		objectId = getRemainingBytes() >= 4 ? readD() : 0;
		action = getRemainingBytes() >= 1 ? readC() : -1;
		int count = getRemainingBytes() / 4;
		payload = new int[count];
		for (int i = 0; i < count; ++i) {
			payload[i] = readD();
		}
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		log.debug("CM_UNK_1DB audit player=" + (player != null ? player.getName() : "-") + " objectId=" + objectId + " action=" + action + " payload=" + java.util.Arrays.toString(payload));
	}
}
