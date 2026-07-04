package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.model.autogroup.EntryRequestType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.instance.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_AUTO_GROUP extends AionClientPacket
{
	private Logger log = LoggerFactory.getLogger(CM_AUTO_GROUP.class);
	private int instanceMaskId;
	private byte windowId;
	private byte entryRequestId;
	
	public CM_AUTO_GROUP(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		instanceMaskId = readD();
		windowId = (byte) readC();
		entryRequestId = (byte) readC();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		PacketSendUtility.sendMessage(player, "Auto Group windows id : " + this.windowId + " requestId : " + this.entryRequestId);
		if (!AutoGroupConfig.AUTO_GROUP_ENABLED) {
			PacketSendUtility.sendMessage(player, "Auto Group is disabled");
			return;
		} switch (windowId) {
			case 100:
				EntryRequestType ert = EntryRequestType.getTypeById(entryRequestId);
				if (ert == null) {
					return;
				}
				AutoGroupService.getInstance().startLooking(player, instanceMaskId, ert);
			break;
			case 101:
				AutoGroupService.getInstance().unregisterLooking(player, instanceMaskId);
			break;
			case 102:
				AutoGroupService.getInstance().pressEnter(player, instanceMaskId);
			break;
			case 103:
				AutoGroupService.getInstance().cancelEnter(player, instanceMaskId);
			break;
			case 104:
				IdgelDomeService.getInstance().showWindow(player, instanceMaskId);
				AshunatalService.getInstance().showWindow(player, instanceMaskId);
				FireTempleService.getInstance().showWindow(player, instanceMaskId);
				KamarBattlefieldService.getInstance().showWindow(player, instanceMaskId);
				IllumielBrawlService.getInstance().showWindow(player, instanceMaskId);
				PvPSoloArenaService.getInstance().showWindow(player, instanceMaskId);
			break;
			case 105:
			break;
		}
	}
}