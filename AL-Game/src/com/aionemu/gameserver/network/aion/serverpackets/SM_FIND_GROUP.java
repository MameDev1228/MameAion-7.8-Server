package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.gameobjects.FindGroup;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.Collection;

public class SM_FIND_GROUP extends AionServerPacket
{
	private int action;
	private int lastUpdate;
	private Collection<FindGroup> findGroups;
	private int groupSize;
	private int instanceId;
	
	public SM_FIND_GROUP(int action, int lastUpdate, Collection<FindGroup> findGroups) {
		this.lastUpdate = lastUpdate;
		this.action = action;
		this.findGroups = findGroups;
		this.groupSize = findGroups.size();
	}
	
	public SM_FIND_GROUP(int action, int lastUpdate, int unk) {
		this.action = action;
		this.lastUpdate = lastUpdate;
	}
	
	public SM_FIND_GROUP(int action, int instanceId) {
		this.action = action;
		this.instanceId = instanceId;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(action);
		switch (action) {
			case 0x00:
				writeH(groupSize);
				writeH(groupSize);
				writeD(lastUpdate);
				for (FindGroup findGroup : findGroups) {
					writeD(findGroup.getObjectId());
					writeC(NetworkConfig.GAMESERVER_ID);
					writeH(0);
					writeC(16);
					writeC(findGroup.getGroupType());
					writeS(findGroup.getMessage());
					writeS(findGroup.getName());
					writeC(findGroup.getSize());
					writeC(findGroup.getMinLevel());
					writeC(findGroup.getMaxLevel());
					writeD(findGroup.getLastUpdate());
				}
			break;
			case 0x01:
				writeD(lastUpdate);
				writeH(NetworkConfig.GAMESERVER_ID);
				writeC(0);
				writeC(16);
			break;
			case 0x04:
				writeH(groupSize);
				writeH(groupSize);
				writeD(lastUpdate);
				for (FindGroup findGroup : findGroups) {
					writeD(findGroup.getObjectId());
					writeC(findGroup.getGroupType());
					writeS(findGroup.getMessage());
					writeS(findGroup.getName());
					writeC(findGroup.getClassId());
					writeC(findGroup.getMinLevel());
					writeD(findGroup.getLastUpdate());
				}
			break;
			case 0x05:
				writeD(lastUpdate);
			break;
			case 0x0A:
				writeH(groupSize);
				writeH(groupSize);
				writeD(lastUpdate);
				for (FindGroup findGroup : findGroups) {
					writeD(0);
					writeD(findGroup.getInstanceId());
					writeD(0);
					writeC(findGroup.getSize());
					writeC(findGroup.getMinMembers());
					writeH(0);
					writeD(findGroup.getObjectId());
					writeD(1);
					writeD(0);
					writeC(findGroup.getMinLevel());
					writeC(findGroup.getMaxLevel());
					writeH(0);
					writeD(findGroup.getLastUpdate());
					writeD(0);
					writeS(findGroup.getName());
					writeS(findGroup.getMessage());
				}
			break;
			case 0x0E:
				writeC(1);
				for (FindGroup findGroup : findGroups) {
					writeD(0);
					writeD(findGroup.getInstanceId());
					writeD(1);
					writeC(findGroup.getSize());
					writeC(findGroup.getMinMembers());
					writeH(0);
					writeH(50);
					writeD(findGroup.getObjectId());
					writeC(1);
					writeC(findGroup.getMinLevel());
					writeC(findGroup.getMaxLevel());
					writeC(0);
					writeD(0);
					writeD(findGroup.getLastUpdate());
					writeD(0);
					writeD(0);
					writeD(0);
					writeS(findGroup.getName());
					writeS(findGroup.getMessage());
				}
			break;
			case 0x10:
				writeH(groupSize);
				writeH(groupSize);
				writeD(lastUpdate);
				for (FindGroup findGroup : findGroups) {
					writeD(0);
					writeD(findGroup.getInstanceId());
					writeD(findGroup.getObjectId());
					writeD(findGroup.getMinLevel());
					writeD(1);
					writeH(1);
					writeC(findGroup.getGroupType());
					writeC(findGroup.getClassId());
					writeS(findGroup.getName());
				}
			break;
			case 0x12:
			case 0x16:
				writeD(0);
				writeD(0);
			break;
			case 0x17:
				writeD(0);
				writeD(0);
				writeC(3);
			break;
			case 0x18:
				writeD(0);
				writeD(0);
				writeC(0);
				for (FindGroup findGroup : findGroups) {
					writeD(0);
					writeD(findGroup.getInstanceId());
					writeD(findGroup.getObjectId());
					writeD(findGroup.getMinLevel());
					writeD(findGroup.getClassId());
					writeH(50);
					writeC(0);
					writeC(1);
					writeH(438);
					writeH(1);
					writeD(0);
					writeS(findGroup.getName());
					writeC(0);
				}
			break;
			case 0x1A:
				writeH(1);
				writeD(instanceId);
			break;
		}
	}
}