/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RANK_LIST;
import com.aionemu.gameserver.services.ranking.PlayerRankingUpdateService;

/**
 * @author Falke_34
 */
public class CM_RANK_LIST extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_RANK_LIST.class);
	private int tableId;
	private int serverSwitch;

	public CM_RANK_LIST(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		tableId = readD();
		serverSwitch = getRemainingBytes() > 0 ? readC() : -1;
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	@Override
	protected void runImpl() {
		List<SM_RANK_LIST> results = PlayerRankingUpdateService.getInstance().getPlayers(tableId);
		if (results == null || results.isEmpty()) {
			log.warn("Rank list requested but no packet could be generated. tableId={} serverSwitch={}", tableId, serverSwitch);
			return;
		}
		for (SM_RANK_LIST packet : results) {
			sendPacket(packet);
		}
	}
}
