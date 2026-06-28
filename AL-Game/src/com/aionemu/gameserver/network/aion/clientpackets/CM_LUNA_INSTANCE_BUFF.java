/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.player.LunaShopService;

public class CM_LUNA_INSTANCE_BUFF extends AionClientPacket {

	private int buffId;

	public CM_LUNA_INSTANCE_BUFF(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		buffId = getRemainingBytes() >= 4 ? readD() : 0;
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null || !player.isSpawned() || player.getController().isInShutdownProgress()) {
			return;
		}
		LunaShopService.getInstance().buyLunaBuff(player, buffId);
	}
}
