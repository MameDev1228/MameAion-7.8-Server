package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FIND_GROUP;
import com.aionemu.gameserver.services.FindGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_FIND_GROUP extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_FIND_GROUP.class);
	private int action;
	private int playerObjId;
	private String message;
	private int groupType;
	@SuppressWarnings("unused")
	private int classId;
	@SuppressWarnings("unused")
	private int level;
	private int unk;
	private int instanceId;
	private int minMembers;
	@SuppressWarnings("unused")
	private int groupId;
	
	public CM_FIND_GROUP(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		action = readC();
		switch (action) {
			case 0x00:
			break;
			case 0x01:
				playerObjId = readD();
				readH();
				readC();
				readC();
			break;
			case 0x02:
				playerObjId = readD();
				message = readS();
				groupType = readC();
			break;
			case 0x03:
				playerObjId = readD();
				readH();
				readC();
				readC();
				message = readS();
				groupType = readC();
			break;
			case 0x04:
			break;
			case 0x05:
				playerObjId = readD();
			break;
			case 0x06:
				playerObjId = readD();
				message = readS();
				groupType = readC();
				classId = readC();
				level = readC();
			break;
			case 0x07:
				playerObjId = readD();
				message = readS();
				groupType = readC();
				classId = readC();
				level = readC();
			break;
			case 0x08:
				instanceId = readD();
				unk = readC(); 
				message = readS();
				minMembers = readC();
				unk = readD();
				unk = readD();
			break;
			case 0x09:
				unk = readD();
				instanceId = readD();
			break;
			case 0x14:
				groupId = readD();
				instanceId = readD();
				unk = readC();
			break;
			case 0x0A:
			break;
			case 0x0D:
			break;
			default:
				//log.error("Unknown find group packet? 0x" + Integer.toHexString(action).toUpperCase());
			break;
		}
	}
	
	@Override
	protected void runImpl() {
		final Player player = this.getConnection().getActivePlayer();
		switch (action) {
			case 0x00:
			case 0x04:
				FindGroupService.getInstance().sendFindGroups(player, action);
			break;
			case 0x01:
			case 0x05:
				FindGroupService.getInstance().removeFindGroup(player.getRace(), action - 1, playerObjId);
			break;
			case 0x02:
			case 0x06:
				FindGroupService.getInstance().addFindGroupList(player, action, message, groupType);
			break;
			case 0x03:
			case 0x07:
				FindGroupService.getInstance().updateFindGroupList(player, message, action, groupType, playerObjId);
			break;
			case 0x08:
				FindGroupService.getInstance().registerInstanceGroup(player, 0x0E, instanceId, message, minMembers, groupType);
			break;
			case 0x14:
			break;
			case 0x0A:
				FindGroupService.getInstance().sendFindGroups(player, action);
			break;
			case 0x0D:
				FindGroupService.getInstance().sendFindGroups(player, action);
			break;
			default:
				PacketSendUtility.sendPacket(player, new SM_FIND_GROUP(action, playerObjId, unk));
			break;
		}
	}
}