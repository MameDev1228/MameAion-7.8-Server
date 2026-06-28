/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.enchant.CombineDaevanionBook;

/**
 * @author Falke_34
 */
public class CM_DAEVANION_SKILL_FUSION extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_DAEVANION_SKILL_FUSION.class);

	private ArrayList<Integer> sacrificeBook = new ArrayList<Integer>();
	private int count;

	public CM_DAEVANION_SKILL_FUSION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		if (getRemainingBytes() >= 4) {
			readD();
		}
		count = getRemainingBytes() >= 2 ? readH() : 0;
		int safeCount = Math.min(count, 24);
		for (int i = 0; i < safeCount && getRemainingBytes() >= 4; ++i) {
			sacrificeBook.add(readD());
		}
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
		if (sacrificeBook.size() < 2) {
			log.debug("Invalid daevanion fusion packet player=" + player.getName() + " declaredCount=" + count + " actualCount=" + sacrificeBook.size());
			return;
		}
		CombineDaevanionBook.combineDaevanionBook(player, sacrificeBook);
	}
}
