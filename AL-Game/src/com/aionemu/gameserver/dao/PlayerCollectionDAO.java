package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionEntry;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionInfos;
import com.aionemu.gameserver.model.templates.collection.CollectionType;

import java.util.Map;

public abstract class PlayerCollectionDAO implements DAO {

    public abstract Map<CollectionType, PlayerCollectionInfos> loadPlayerCollection(Player player);
    public abstract boolean insertPlayerCollection(Player player, PlayerCollectionInfos infos);
    public abstract boolean updatePlayerCollection(Player player, PlayerCollectionInfos infos);

    public abstract void loadCollectionEntry(Player player);
    public abstract boolean insertCollection(Player player, PlayerCollectionEntry entry);
    public abstract boolean updateCollection(Player player, PlayerCollectionEntry entry);

    public final String getClassName() {
        return PlayerCollectionDAO.class.getName();
    }
}
