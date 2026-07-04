package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.dao.AccountDAO;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.network.aion.serverpackets.unk_60.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_CHARACTER_LIST extends AionClientPacket
{
	private static Logger log = LoggerFactory.getLogger(CM_CHARACTER_LIST.class);
	
	private int accountId;
	
	public CM_CHARACTER_LIST(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		accountId = readD();
	}
	
	@Override
	protected void runImpl() {
		boolean isGM = (getConnection()).getAccount().getAccessLevel() >= AdminConfig.GM_PANEL;
		sendPacket(new SM_ACCOUNT_PROPERTIES(isGM));
		sendPacket(new SM_CHARACTER_LIST(0, accountId));
		sendPacket(new SM_CHARACTER_LIST(2, accountId));
		if (EventsConfig.ENABLE_CASH_BUFF_PACKET) {
			sendPacket(new SM_CASH_BUFF(2));
		}
		if (EventsConfig.ENABLE_ACCOUNT_BENEFIT_PACKETS) {
			sendPacket(new SM_ACCOUNT_PROPERTIES(isGM));
			log.info("Jumping Count: " + DAOManager.getDAO(AccountDAO.class).getJumpingCountOnAccount(getConnection().getAccount().getId()));
			sendPacket(new SM_ACCOUNT_TYPE(getConnection().getAccount()));
		}
	}
}