package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TIME_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AFTER_TIME_CHECK;

public class CM_TIME_CHECK extends AionClientPacket
{
	private int nanoTime;
	
	public CM_TIME_CHECK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		nanoTime = readD();
	}
	
	@Override
	protected void runImpl() {
		AionConnection client = getConnection();
		int timeNow = (int) (System.nanoTime() / 1000000);
		@SuppressWarnings("unused")
		int diff = timeNow - nanoTime;
		client.sendPacket(new SM_AFTER_TIME_CHECK(1));
		client.sendPacket(new SM_TIME_CHECK(nanoTime));
	}
}