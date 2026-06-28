/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.enchant.EnchantDaevanionBook;

/**
 * @author Falke_34
 */
public class CM_DAEVANION_SKILL_ENCHANT extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_DAEVANION_SKILL_ENCHANT.class);

	private int skillId;
	private int bookObjId;
	private int materialObjId;

	public CM_DAEVANION_SKILL_ENCHANT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		skillId = getRemainingBytes() >= 2 ? readH() : 0;
		bookObjId = getRemainingBytes() >= 4 ? readD() : 0;
		materialObjId = getRemainingBytes() >= 4 ? readD() : 0;
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
		if (skillId <= 0 || bookObjId <= 0) {
			log.debug("Invalid daevanion enchant packet player=" + player.getName() + " skill=" + skillId + " book=" + bookObjId + " material=" + materialObjId);
			return;
		}
		EnchantDaevanionBook.enchantDaevanionSkill(player, skillId, bookObjId, materialObjId);
	}
}
