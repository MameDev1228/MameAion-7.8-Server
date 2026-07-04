package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.ranking.*;
import com.aionemu.gameserver.model.ranking.SeasonRankingResult;

import java.util.*;

/**
 * Created by Wnkrz on 24/07/2017.
 */

public abstract class SeasonRankingDAO implements DAO
{
    @Override
    public final String getClassName() {
        return SeasonRankingDAO.class.getName();
    }
	
    public abstract ArrayList<SeasonRankingResult> getCompetitionRankingPlayers(int tableId);
	
    public abstract GloryPointRank loadGloryRank(int playerId, int tableId);
    public abstract InfinityRank loadInfinityRank(int playerId, int tableId);
    public abstract Arena6VS6Ranking loadArena6VS6Rank(int playerId, int tableId);
	public abstract DamageRank loadDamageRank(int playerId, int tableId);
	
    public abstract boolean storeGloryRank(Player player);
    public abstract boolean storeInfinityRank(Player player);
    public abstract boolean store6VS6Rank(Player player);
	public abstract boolean storeDamageRank(Player player);
}
