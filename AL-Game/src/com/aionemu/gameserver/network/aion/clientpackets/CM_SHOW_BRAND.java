package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHOW_BRAND;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_SHOW_BRAND extends AionClientPacket
{
	private int brandId;
	private int targetObjectId;
	
	public CM_SHOW_BRAND(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		brandId = readD();
		targetObjectId = readD();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player.isInGroup2() && player.isInAlliance2()) {
			PlayerGroupService.showBrand(player, targetObjectId, brandId);
		}
		PacketSendUtility.sendPacket(player, new SM_SHOW_BRAND(brandId, targetObjectId));
	}
}