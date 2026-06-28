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
import com.aionemu.gameserver.services.packet.PacketAuditService;
import com.aionemu.gameserver.services.player.LunaShopService;
import com.aionemu.gameserver.services.player.PlayerSyncService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * 7.8 UI/panel refresh request.
 * <p>
 * This replaces the old CM_UNK_E3 name. Known variants refresh stats, visibility
 * and Luna panel state. Other action variants stay audited until live client
 * logs provide a reliable name.
 */
public class CM_UI_PANEL_REFRESH_REQUEST extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_UI_PANEL_REFRESH_REQUEST.class);
	private final List<Integer> payload = new ArrayList<Integer>();

	public CM_UI_PANEL_REFRESH_REQUEST(int opcode, State state, State... restStates) {
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
				PacketAuditService.getInstance().logAction(getPacketName(), getOpcode(), player, action, payload, "stats_info_refresh");
				break;
			case 1:
				PlayerSyncService.resendVisibilityAndStats(player, false);
				PacketAuditService.getInstance().logAction(getPacketName(), getOpcode(), player, action, payload, "self_visibility_stats_refresh");
				break;
			case 2:
				LunaShopService.getInstance().onLogin(player);
				PacketAuditService.getInstance().logAction(getPacketName(), getOpcode(), player, action, payload, "luna_panel_refresh");
				break;
			default:
				PacketAuditService.getInstance().logAction(getPacketName(), getOpcode(), player, action, payload, "unmapped_ui_panel_refresh_action");
				log.debug("CM_UI_PANEL_REFRESH_REQUEST payload player={} payload={}", player.getName(), payload);
				break;
		}
	}
}
