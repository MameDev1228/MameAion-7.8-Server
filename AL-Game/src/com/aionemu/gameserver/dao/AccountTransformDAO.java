package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountTransfo;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import java.util.Map;

public abstract class AccountTransformDAO implements DAO
{
    @Override
    public String getClassName() {
        return AccountTransformDAO.class.getName();
    }


    public abstract Map<Integer, AccountTransfo> loadAccountTransfo(Account account);
    public abstract boolean addTransfo(final Account account, AccountTransfo trans);
    public abstract boolean updateTransfo(final Account account, AccountTransfo transfo);
    public abstract void deleteTransfo(final Account account, int id);
}