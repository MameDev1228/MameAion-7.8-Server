package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.AccountDAO;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_ACCOUNT_TYPE extends AionServerPacket
{

    Account account;
	public SM_ACCOUNT_TYPE(Account account) {
	    this.account = account;
	}
	
    @Override
    protected void writeImpl(AionConnection con) {
	    writeC(DAOManager.getDAO(AccountDAO.class).getJumpingCountOnAccount(account.getId()) == 0 ? 2 : 3);
    }
}