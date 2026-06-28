/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.ranking;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerRankingDAO;
import com.aionemu.gameserver.model.ranking.PlayerRankingEnum;
import com.aionemu.gameserver.model.ranking.PlayerRankingResult;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RANK_LIST;

import javolution.util.FastMap;

public class PlayerRankingUpdateService {

	private static final Logger log = LoggerFactory.getLogger(PlayerRankingUpdateService.class);
	private int lastUpdate;
	private final FastMap<Integer, List<SM_RANK_LIST>> players = new FastMap<Integer, List<SM_RANK_LIST>>().shared();

	public void onStart() {
		renewPlayerRanking(PlayerRankingEnum.ARENA_OF_DISCIPLINE.getId());
		renewPlayerRanking(PlayerRankingEnum.ARENA_OF_COOPERATION.getId());
		log.info("[PlayerRankingUpdateService] Player Ranking Loaded");
	}

	public synchronized void renewPlayerRanking(int tableId) {
		lastUpdate = (int) (System.currentTimeMillis() / 1000L);
		List<SM_RANK_LIST> newlyCalculated = loadRankPacket(tableId);
		players.remove(tableId);
		players.put(tableId, newlyCalculated);
		log.info("[PlayerRankingUpdateService] Player Ranking Updated tableId=" + tableId + " packets=" + newlyCalculated.size());
	}

	private List<SM_RANK_LIST> loadRankPacket(int tableid) {
		ArrayList<PlayerRankingResult> list = DAOManager.getDAO(PlayerRankingDAO.class).getCompetitionRankingPlayers(tableid);
		List<SM_RANK_LIST> playerPackets = new ArrayList<SM_RANK_LIST>();
		if (list == null || list.isEmpty()) {
			playerPackets.add(new SM_RANK_LIST(tableid, 0, new ArrayList<PlayerRankingResult>(), lastUpdate));
			playerPackets.add(new SM_RANK_LIST(tableid, 1, new ArrayList<PlayerRankingResult>(), lastUpdate));
			return playerPackets;
		}
		for (int i = 0; i < list.size(); i += 94) {
			List<PlayerRankingResult> page = list.subList(i, Math.min(i + 94, list.size()));
			playerPackets.add(new SM_RANK_LIST(tableid, 0, page, lastUpdate));
			playerPackets.add(new SM_RANK_LIST(tableid, 1, page, lastUpdate));
		}
		return playerPackets;
	}

	public List<SM_RANK_LIST> getPlayers(int tableId) {
		List<SM_RANK_LIST> result = players.get(tableId);
		if (result == null) {
			renewPlayerRanking(tableId);
			result = players.get(tableId);
		}
		return result;
	}

	public static final PlayerRankingUpdateService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		protected static final PlayerRankingUpdateService INSTANCE = new PlayerRankingUpdateService();
	}
}
