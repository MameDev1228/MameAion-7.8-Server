package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.LumielTransform;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import java.util.Map;

public abstract class PlayerLumielDAO implements DAO {

    public abstract Map<Integer, LumielTransform> loadPlayerLumiel(Player player);
    public abstract boolean addPlayerLumiel(Player player, LumielTransform lumielTransform);
    public abstract boolean updateLumielTransform(Player player, LumielTransform lumielTransform);

    public final String getClassName() {
        return PlayerLumielDAO.class.getName();
    }
}
