package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_EVENT_WINDOW extends AionServerPacket
{
	private int activeEventCount;
	private int active;
	
	public SM_EVENT_WINDOW(int activeEventCount, int active) {
		this.activeEventCount = activeEventCount;
		this.active = active;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(activeEventCount);
		writeC(active);
	}
}