package com.aionemu.gameserver.services.ranking;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.dao.SeasonRankingDAO;
import com.aionemu.gameserver.model.ranking.SeasonRankingEnum;
import com.aionemu.gameserver.model.ranking.SeasonRankingResult;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SEASON_RANKING;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Wnkrz on 24/07/2017.
 */

public class SeasonRankingUpdateService
{
    private static final Logger log = LoggerFactory.getLogger(SeasonRankingService.class);
    private int lastUpdate;
    private final FastMap<Integer, List<SM_SEASON_RANKING>> players = new FastMap<Integer, List<SM_SEASON_RANKING>>();
	
    public void onStart() {
        initUpdateSeasonRanking();
		renewPlayerRanking(SeasonRankingEnum.DAMAGE.getId());
		renewPlayerRanking(SeasonRankingEnum.INFINITY.getId());
        renewPlayerRanking(SeasonRankingEnum.ARENA_6VS6.getId());
		renewPlayerRanking(SeasonRankingEnum.GLORY_POINT.getId());
    }
	
    private void renewPlayerRanking(int tableId) {
        List<SM_SEASON_RANKING> newlyCalculated;
        newlyCalculated = loadRankPacket(tableId);
        players.remove(tableId);
        players.put(tableId, newlyCalculated);
        log.info("Season Ranking Updated");
    }
	
    private List<SM_SEASON_RANKING> loadRankPacket(int tableid) {
        ArrayList<SeasonRankingResult> list = DAOManager.getDAO(SeasonRankingDAO.class).getCompetitionRankingPlayers(tableid);
        List<SM_SEASON_RANKING> playerPackets = new ArrayList<SM_SEASON_RANKING>();
        for (int i = 0; i < list.size(); i += 94) {
            if (list.size() > i + 94) {
                playerPackets.add(new SM_SEASON_RANKING(tableid, 0, list.subList(i, i + 94), lastUpdate));
                playerPackets.add(new SM_SEASON_RANKING(tableid, 1, list.subList(i, i + 94), lastUpdate));
            } else {
                playerPackets.add(new SM_SEASON_RANKING(tableid, 0, list.subList(i, list.size()), lastUpdate));
                playerPackets.add(new SM_SEASON_RANKING(tableid, 1, list.subList(i, list.size()), lastUpdate));
            }
        }
        return playerPackets;
    }

    public void initUpdateSeasonRanking() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        String seasonRanking = "0 0 12 ? * * *";
        CronService.getInstance().schedule(new Runnable() {
            public void run() {
                renewPlayerRanking(SeasonRankingEnum.DAMAGE.getId());
                renewPlayerRanking(SeasonRankingEnum.INFINITY.getId());
                renewPlayerRanking(SeasonRankingEnum.ARENA_6VS6.getId());
                renewPlayerRanking(SeasonRankingEnum.GLORY_POINT.getId());
                log.info("<Season Ranking Daily Update>");
            }
        }, seasonRanking);
    }
	
    public List<SM_SEASON_RANKING> getPlayers(int tableId) {
        return players.get(tableId);
    }
	
    public static final SeasonRankingUpdateService getInstance() {
        return SingletonHolder.INSTANCE;
    }
	
    private static class SingletonHolder {
        protected static final SeasonRankingUpdateService INSTANCE = new SeasonRankingUpdateService();
    }
}