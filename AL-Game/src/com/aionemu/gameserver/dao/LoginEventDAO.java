package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.account.AccountLoginEvent;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import java.util.Map;

public abstract class LoginEventDAO implements DAO {

    @Override
    public String getClassName() {
        return LoginEventDAO.class.getName();
    }

    public abstract void load(Player player);
    public abstract boolean add(final Player player);
    public abstract boolean update(final Player player);
    public abstract boolean dailyUpdate();
    public abstract boolean weeklyUpdate();

}
