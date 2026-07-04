package com.aionemu.gameserver.services.ranking;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.SeasonRankingDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.ranking.*;
import com.aionemu.gameserver.model.ranking.SeasonRankingEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MY_HISTORY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SEASON_RANKING;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Wnkrz on 24/07/2017.
 */

public class SeasonRankingService
{
    private static final Logger log = LoggerFactory.getLogger(SeasonRankingService.class);
    
    public void onPlayerLogin(Player player){
        player.setDamageRank(getDao().loadDamageRank(player.getObjectId(), SeasonRankingEnum.DAMAGE.getId()));
        player.setGloryPointRank(getDao().loadGloryRank(player.getObjectId(), SeasonRankingEnum.GLORY_POINT.getId()));
    }

    public void loadPacketPlayer(Player player, int tableid) {
        switch (tableid) {
			case 541:
				loadArena6VS6Score(player);
			break;
			case 102:
				loadInfinityScore(player);
			break;
			case 1000:
				loadGloryScore(player);
			break;
			case 461:
				loadDamageScore(player);
			break;
		}
    }
	
    public void loadArena6VS6Score(Player player) {
		Arena6VS6Ranking rank = DAOManager.getDAO(SeasonRankingDAO.class).loadArena6VS6Rank(player.getObjectId(), SeasonRankingEnum.ARENA_6VS6.getId());
        player.set6VS6Rank(rank);
        PacketSendUtility.sendPacket(player, new SM_MY_HISTORY(SeasonRankingEnum.ARENA_6VS6.getId(), player.get6VS6Rank()));
    }
	
    public void loadInfinityScore(Player player) {
		InfinityRank rank = DAOManager.getDAO(SeasonRankingDAO.class).loadInfinityRank(player.getObjectId(), SeasonRankingEnum.INFINITY.getId());
        player.setInfinityRank(rank);
        PacketSendUtility.sendPacket(player, new SM_MY_HISTORY(SeasonRankingEnum.INFINITY.getId(), player.getInfinityRank()));
    }
	
    public void loadGloryScore(Player player) {
		GloryPointRank rank = getDao().loadGloryRank(player.getObjectId(), SeasonRankingEnum.GLORY_POINT.getId());
        PacketSendUtility.sendPacket(player, new SM_MY_HISTORY(SeasonRankingEnum.GLORY_POINT.getId(), rank));
    }

	public void loadDamageScore(Player player) {
        DamageRank rank = getDao().loadDamageRank(player.getObjectId(), SeasonRankingEnum.DAMAGE.getId());
        PacketSendUtility.sendPacket(player, new SM_MY_HISTORY(SeasonRankingEnum.DAMAGE.getId(), rank));
    }

    public void addDamagePlayerScore(Player player) {
        DamageRank rank = player.getDamageRank();
        PersistentState persistentState = PersistentState.NEW;
        if (rank.getLastScore() != 0) {
            persistentState = PersistentState.UPDATE_REQUIRED;
        }
        //set time
        rank.setLastScore(rank.getCurrentScore());
        rank.setCurrentScore(player.getInstanceScore());
        if (player.getInstanceScore() > rank.getLastScore()) {
            rank.setBestScore(player.getInstanceScore());
        } else {
            rank.setBestRank(rank.getLastScore());
        }
        //set all rank
        rank.setBestRank(1);
        rank.setLowRank(1);
        rank.setRank(1);
        rank.setPersistentState(persistentState);
        DAOManager.getDAO(SeasonRankingDAO.class).storeDamageRank(player);
    }

    public void addGloryPlayerScore(Player player, int points) {
        GloryPointRank rank = player.getGloryPointRank();
        PersistentState persistentState = PersistentState.UPDATE_REQUIRED;
        rank.setPoints(rank.getPoints() + points);
        rank.setBestRank(1);
        rank.setRank(1);
        rank.setPersistentState(persistentState);
        DAOManager.getDAO(SeasonRankingDAO.class).storeGloryRank(player);
    }

    public SeasonRankingDAO getDao() {
        return DAOManager.getDAO(SeasonRankingDAO.class);
    }

    public static final SeasonRankingService getInstance() {
        return SingletonHolder.INSTANCE;
    }
	
    private static class SingletonHolder {
        protected static final SeasonRankingService INSTANCE = new SeasonRankingService();
    }
}