package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

public class CM_GG extends AionClientPacket
{
	private int size;
	byte[] data;
	
	public CM_GG(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		size = readD();
		data = readB(size);
	}
	
	@Override
	protected void runImpl() {
	}
}