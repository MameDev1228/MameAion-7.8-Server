package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.monsterCore.MonsterCore;

import java.util.Map;

public abstract class AccountMonsterCoreDAO implements DAO
{
    public abstract Map<Integer, MonsterCore> load(Account player);
    public abstract boolean update(Account player, MonsterCore core);
    public abstract boolean store(Account player, MonsterCore core);
	
    @Override
    public String getClassName() {
        return AccountMonsterCoreDAO.class.getName();
    }
}