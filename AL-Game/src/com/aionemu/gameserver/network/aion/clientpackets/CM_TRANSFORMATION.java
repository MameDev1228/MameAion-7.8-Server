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
import com.aionemu.gameserver.services.TransformationService;

/**
 * @author Falke_34, FrozenKiller
 */
public class CM_TRANSFORMATION extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_TRANSFORMATION.class);
	private int itemObjId;
	private int actionId;
	private int cardId;
	private ArrayList<Integer> materials = new ArrayList<Integer>();

	public CM_TRANSFORMATION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		materials.clear();
		actionId = getRemainingBytes() >= 2 ? readH() : -1;
		switch (actionId) {
			case 0:
				itemObjId = readDIfPresent();
				readCIfPresent();
				break;
			case 1:
				cardId = readDIfPresent();
				itemObjId = readDIfPresent();
				break;
			case 2:
				for (int i = 0; i < 6 && getRemainingBytes() >= 4; i++) {
					materials.add(readD());
				}
				break;
			default:
				log.debug("Unknown transformation action {} remaining {}", actionId, getRemainingBytes());
				if (getRemainingBytes() > 0) {
					readB(getRemainingBytes());
				}
				break;
		}
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	private int readDIfPresent() {
		return getRemainingBytes() >= 4 ? readD() : 0;
	}

	private int readCIfPresent() {
		return getRemainingBytes() >= 1 ? readC() : 0;
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		switch (actionId) {
			case 0:
				TransformationService.getInstance().makeTransform(player, itemObjId);
				break;
			case 1:
				TransformationService.getInstance().onPlayerTransform(player, itemObjId, cardId);
				break;
			case 2:
				TransformationService.getInstance().onCombineTransformation(player, materials);
				break;
			default:
				break;
		}
	}
}
