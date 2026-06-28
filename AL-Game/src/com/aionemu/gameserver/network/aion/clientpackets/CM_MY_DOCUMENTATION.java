/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.ranking.PlayerRankingService;

/**
 * @author Falke_34
 */
public class CM_MY_DOCUMENTATION extends AionClientPacket {

	private int tableId;

	public CM_MY_DOCUMENTATION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		tableId = readD();
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		PlayerRankingService.getInstance().loadPacketPlayer(player, tableId);
	}
}
