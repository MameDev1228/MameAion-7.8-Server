package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_VERSION_CHECK extends AionClientPacket
{
	private int version;

	private static final Logger log = LoggerFactory.getLogger(SM_VERSION_CHECK.class);
	
	@SuppressWarnings("unused")
	private int subversion;
	
	@SuppressWarnings("unused")
	private int windowsEncoding;
	
	@SuppressWarnings("unused")
	private int windowsVersion;
	
	@SuppressWarnings("unused")
	private int windowsSubVersion;
	
	private int unk;
	
	public CM_VERSION_CHECK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		version = readH();
		subversion = readH();
		windowsEncoding = readD();
		windowsVersion = readD();
		windowsSubVersion = readD();
		unk = readC();
	}
	
	@Override
	protected void runImpl() {
		log.info("CM VERSION CHECK : Version : " + this.version);
		sendPacket(new SM_VERSION_CHECK(version));
		sendPacket(new SM_VERSION());
	}
}