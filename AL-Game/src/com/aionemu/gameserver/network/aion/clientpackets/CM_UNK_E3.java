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
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.services.player.LunaShopService;
import com.aionemu.gameserver.services.player.PlayerSyncService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * 7.5/7.8 client-side misc packet. Phase10 turns the known harmless variants
 * into real sync/status actions and keeps all other payloads audited.
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
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		int action = payload.isEmpty() ? 0 : payload.get(0).intValue();
		switch (action) {
			case 0:
				PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
				break;
			case 1:
				PlayerSyncService.resendVisibilityAndStats(player, false);
				break;
			case 2:
				LunaShopService.getInstance().onLogin(player);
				break;
			default:
				log.debug("CM_UNK_E3 payload player={} payload={}", player.getName(), payload);
				break;
		}
	}
}
