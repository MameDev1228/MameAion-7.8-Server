package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.fame.PlayerFame;

import java.util.List;
import java.util.Map;

public abstract class PlayerFameDAO implements DAO {

    public abstract Map<Integer, PlayerFame> loadPlayerFame(Player player);
    public abstract boolean addPlayerFame(Player player, PlayerFame fame);
    public abstract boolean updatePlayerFame(Player player, PlayerFame fame);
    public abstract List<PlayerFame> weeklyFame();
    public abstract boolean reduceWeekly(PlayerFame fame);
    public final String getClassName() {
        return PlayerFameDAO.class.getName();
    }
}
