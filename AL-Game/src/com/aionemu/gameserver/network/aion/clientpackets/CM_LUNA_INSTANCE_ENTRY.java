package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.InstanceEntryCostEnum;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.InstanceEntryService;

public class CM_LUNA_INSTANCE_ENTRY extends AionClientPacket {

	private int syncId;
	private InstanceEntryCostEnum type;

	public CM_LUNA_INSTANCE_ENTRY(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		syncId = getRemainingBytes() >= 4 ? readD() : 0;
		if (getRemainingBytes() >= 4) {
			readD();
		}
		type = getRemainingBytes() >= 1 ? InstanceEntryCostEnum.getCotstId(readC()) : null;
		if (getRemainingBytes() > 0) {
			readB(getRemainingBytes());
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null || syncId <= 0 || type == null) {
			return;
		}
		int worldId = DataManager.INSTANCE_COOLTIME_DATA.getSyncId(syncId);
		if (worldId <= 0) {
			return;
		}
		InstanceEntryService.getInstance().onResetInstanceEntry(player, worldId, type);
	}
}
