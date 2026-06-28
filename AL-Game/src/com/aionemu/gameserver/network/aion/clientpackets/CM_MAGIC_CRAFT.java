/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.craft.MagicCraftService;

/**
 * @author Falke_34, FrozenKiller
 */
public class CM_MAGIC_CRAFT extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_MAGIC_CRAFT.class);

	private int action;
	private int recipeId;
	private int craftType;

	public CM_MAGIC_CRAFT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		action = readCIfPresent();
		switch (action) {
			case 0: // Cancel MagicCraft
			case 1: // Start MagicCraft
				readDIfPresent(); // targetTemplateId
				recipeId = readDIfPresent();
				readDIfPresent(); // targetObjId
				int materialsCount = readHIfPresent();
				craftType = readCIfPresent();
				for (int i = 0; i < materialsCount && getRemainingBytes() >= 12; i++) {
					readD(); // materialId
					readQ(); // materialCount
				}
				if (getRemainingBytes() > 0) {
					readB(getRemainingBytes());
				}
				break;
			default:
				if (getRemainingBytes() > 0) {
					readB(getRemainingBytes());
				}
				break;
		}
	}

	private int readCIfPresent() {
		return getRemainingBytes() >= 1 ? readC() : 0;
	}

	private int readHIfPresent() {
		return getRemainingBytes() >= 2 ? readH() : 0;
	}

	private int readDIfPresent() {
		return getRemainingBytes() >= 4 ? readD() : 0;
	}

	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
		if (player == null || !player.isSpawned() || player.getController().isInShutdownProgress()) {
			return;
		}
		switch (action) {
			case 0:
				MagicCraftService.sendCancelMagicCraft(player);
				break;
			case 1:
				MagicCraftService.startMagicCraft(player, recipeId, craftType);
				break;
			default:
				log.debug("Unhandled magic craft action {} from {}", action, player.getName());
				break;
		}
	}
}
