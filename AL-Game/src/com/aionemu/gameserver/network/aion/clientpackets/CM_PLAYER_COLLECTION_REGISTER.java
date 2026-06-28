/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.player.PlayerCollectionService;

public class CM_PLAYER_COLLECTION_REGISTER extends AionClientPacket {

	private int id;
	private int index;
	private int objectId;
	private int count;

	public CM_PLAYER_COLLECTION_REGISTER(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		id = readD();
		index = readC();
		objectId = readD();
		count = readD();
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
		PlayerCollectionService.getInstance().registerCollection(player, id, index, objectId, count);
	}
}
