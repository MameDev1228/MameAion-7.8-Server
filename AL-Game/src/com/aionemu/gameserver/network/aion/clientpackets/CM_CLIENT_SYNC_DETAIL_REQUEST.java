/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.services.packet.PacketAuditService;
import com.aionemu.gameserver.services.player.PlayerSyncService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * 7.8 client sync/detail request.
 * <p>
 * This replaces the old CM_UNK_1DB name. Retail captures show this opcode is
 * used by client UI/status panels as a harmless refresh/detail request. Unknown
 * action variants remain audited so new payloads can be promoted to named
 * actions without losing runtime data.
 */
public class CM_CLIENT_SYNC_DETAIL_REQUEST extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_CLIENT_SYNC_DETAIL_REQUEST.class);
	private int objectId;
	private int action;
	private int[] payload = new int[0];

	public CM_CLIENT_SYNC_DETAIL_REQUEST(int opcode, State state, State... restStates) {
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
		if (target == null) {
			target = player;
		}

		switch (action) {
			case 0:
				PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
				PacketAuditService.getInstance().logAction(getPacketName(), getOpcode(), player, action, objectId, payload, "stats_info_refresh");
				break;
			case 1:
				PlayerSyncService.resendVisibilityAndStats(target, false);
				PacketAuditService.getInstance().logAction(getPacketName(), getOpcode(), player, action, objectId, payload, "visibility_stats_refresh");
				break;
			case 2:
				PlayerSyncService.resendVisibilityAndStats(target, true);
				PacketAuditService.getInstance().logAction(getPacketName(), getOpcode(), player, action, objectId, payload, "knownlist_rebuild_refresh");
				break;
			default:
				PacketAuditService.getInstance().logAction(getPacketName(), getOpcode(), player, action, objectId, payload, "unmapped_client_sync_detail_action");
				log.debug("CM_CLIENT_SYNC_DETAIL_REQUEST audit player=" + player.getName() + " objectId=" + objectId + " action=" + action + " payload=" + Arrays.toString(payload));
				break;
		}
	}
}
