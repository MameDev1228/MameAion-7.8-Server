/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.services.player.PlayerSyncService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * 7.x misc client request. In the 7.8 branch this packet is used as a safe
 * runtime sync/detail request until the remaining UI payload variants are fully
 * named from retail captures.
 */
public class CM_UNK_1DB extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_UNK_1DB.class);
	private int objectId;
	private int action;
	private int[] payload;

	public CM_UNK_1DB(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		objectId = getRemainingBytes() >= 4 ? readD() : 0;
		action = getRemainingBytes() >= 1 ? readC() : -1;
		int count = getRemainingBytes() / 4;
		payload = new int[count];
		for (int i = 0; i < count; ++i) {
			payload[i] = readD();
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
		Player target = objectId > 0 ? World.getInstance().findPlayer(objectId) : player;
		switch (action) {
			case 0:
				PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
				break;
			case 1:
				PlayerSyncService.resendVisibilityAndStats(target != null ? target : player, false);
				break;
			case 2:
				PlayerSyncService.resendVisibilityAndStats(target != null ? target : player, true);
				break;
			default:
				log.debug("CM_UNK_1DB audit player=" + player.getName() + " objectId=" + objectId + " action=" + action + " payload=" + java.util.Arrays.toString(payload));
				break;
		}
	}
}
