/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RANK_LIST;
import com.aionemu.gameserver.services.ranking.PlayerRankingUpdateService;
import java.util.List;

/**
 * 7.x alternate Rank List request. Phase9 wires the audited payload to the
 * regular ranking service so the UI receives data instead of a debug-only stub.
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
		if (listId <= 0) {
			log.debug("CM_UNK_1EA rank-list audit player=" + (player != null ? player.getName() : "-") + " invalidListId=" + listId + " action=" + action);
			return;
		}
		List<SM_RANK_LIST> packets = PlayerRankingUpdateService.getInstance().getPlayers(listId);
		if (packets == null || packets.isEmpty()) {
			log.warn("CM_UNK_1EA no rank packets listId=" + listId + " action=" + action + " player=" + (player != null ? player.getName() : "-"));
			return;
		}
		for (SM_RANK_LIST packet : packets) {
			sendPacket(packet);
		}
	}
}
