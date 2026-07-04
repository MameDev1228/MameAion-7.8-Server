package mysql5;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.SeasonRankingDAO;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.ranking.*;
import com.aionemu.gameserver.model.ranking.SeasonRankingEnum;
import com.aionemu.gameserver.model.ranking.SeasonRankingResult;

import org.slf4j.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Wnkrz on 24/07/2017.
 */

public class MySQL5SeasonRankingDAO extends SeasonRankingDAO
{
    private static final Logger log = LoggerFactory.getLogger(MySQL5SeasonRankingDAO.class);
	
    public static final String SELECT_PLAYERS_RANKING = "SELECT competition_ranking.rank, competition_ranking.last_rank, competition_ranking.points, competition_ranking.player_id, players.name, players.id, players.player_class, players.race FROM competition_ranking INNER JOIN players ON competition_ranking.player_id = players.id WHERE competition_ranking.table_id = ? AND competition_ranking.points > 0 ORDER BY competition_ranking.points DESC LIMIT 0, 300";
    public static final String SELECT_MY_HISTORY = "SELECT * FROM competition_ranking  WHERE player_id = ? AND table_id = ?";
    public static final String INSERT_QUERY = "INSERT INTO competition_ranking (player_id, table_id, rank, last_rank, points, last_points, high_points, low_points, position_match) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String UPDATE_QUERY = "UPDATE competition_ranking SET  rank = ?, last_rank = ?, points = ?, last_points = ?, high_points = ?, low_points = ?, position_match = ? WHERE player_id = ? AND table_id = ?";
	
    @Override
    public ArrayList<SeasonRankingResult> getCompetitionRankingPlayers(int tableId) {
        Connection con = null;
        final ArrayList<SeasonRankingResult> results = new ArrayList<SeasonRankingResult>();
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_PLAYERS_RANKING);
            stmt.setInt(1, tableId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("players.name");
                int rank = resultSet.getInt("competition_ranking.rank");
                int last_rank = resultSet.getInt("competition_ranking.last_rank");
                int pc = resultSet.getInt("competition_ranking.points");
                int playerId = resultSet.getInt("players.id");
                String playerClassStr = resultSet.getString("players.player_class");
                PlayerClass playerClass = PlayerClass.getPlayerClassByString(playerClassStr);
                String race = resultSet.getString("players.race");
                if (playerClass == null) {
                    continue;
				}
                SeasonRankingResult rsl = new SeasonRankingResult(name, last_rank, rank, pc, playerClass, Race.getRaceByString(race.toString()).getRaceId(), playerId);
                results.add(rsl);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            log.error("getCompetitionRankingPlayers",e);
        } finally {
            DatabaseFactory.close(con);
        }
        return results;
    }
	
    @Override
    public GloryPointRank loadGloryRank(int playerId, int tableId) {
        GloryPointRank arenaRank = null;
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_MY_HISTORY);
            stmt.setInt(1, playerId);
            stmt.setInt(2, tableId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int rank = resultSet.getInt("rank");
                int best_rank = resultSet.getInt("last_rank");
                int point = resultSet.getInt("points");
                int last_point = resultSet.getInt("last_points");
                int high_point = resultSet.getInt("high_points");
                int low_point = resultSet.getInt("low_points");
                arenaRank = new GloryPointRank(rank, best_rank, point, last_point, high_point, low_point, 0);
                arenaRank.setPersistentState(PersistentState.UPDATED);
            } else {
                arenaRank = new GloryPointRank(0, 0, 0, 0, 0, 0, 0);
                arenaRank.setPersistentState(PersistentState.NEW);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            log.error("loadGoldArenaRank", e);
        } finally {
            DatabaseFactory.close(con);
        }
        return arenaRank;
    }
	
    @Override
    public boolean storeGloryRank(Player player) {
        GloryPointRank rank = player.getGloryPointRank();
        boolean result = false;
        switch (rank.getPersistentState()) {
            case NEW:
                result = addGloryRank(player.getObjectId(), rank);
            break;
            case UPDATE_REQUIRED:
                result = updateGloryRank(player.getObjectId(), rank);
            break;
        }
        rank.setPersistentState(PersistentState.UPDATED);
        return result;
    }
	
    private boolean addGloryRank(final int objectId, final GloryPointRank rank) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
            stmt.setInt(1, objectId);
            stmt.setInt(2, SeasonRankingEnum.GLORY_POINT.getId());
            stmt.setInt(3, rank.getRank());
            stmt.setInt(4, rank.getBestRank());
            stmt.setInt(5, rank.getPoints());
            stmt.setInt(6, rank.getLastPoints());
            stmt.setInt(7, rank.getHighPoints());
            stmt.setInt(8, rank.getLowPoints());
            stmt.setInt(9, rank.getPossitionMatch());
            stmt.execute();
            stmt.close();
            return true;
        } catch (SQLException e) {
            log.error("addGoldRank", e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
    }
	
    private boolean updateGloryRank(final int objectId, GloryPointRank rank) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
            stmt.setInt(1, rank.getRank());
            stmt.setInt(2, rank.getBestRank());
            stmt.setInt(3, rank.getPoints());
            stmt.setInt(4, rank.getLastPoints());
            stmt.setInt(5, rank.getHighPoints());
            stmt.setInt(6, rank.getLowPoints());
            stmt.setInt(7, rank.getPossitionMatch());
            stmt.setInt(8, objectId);
            stmt.setInt(9, SeasonRankingEnum.GLORY_POINT.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (SQLException e) {
            log.error("updateGoldRank", e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
    }
	
    @Override
    public boolean storeInfinityRank(Player player) {
        InfinityRank rank = player.getInfinityRank();
        boolean result = false;
        switch (rank.getPersistentState()) {
            case NEW:
                result = addInfinityRank(player.getObjectId(), rank);
            break;
            case UPDATE_REQUIRED:
                result = updateInfinityRank(player.getObjectId(), rank);
            break;
        }
        rank.setPersistentState(PersistentState.UPDATED);
        return result;
    }
	
    private boolean addInfinityRank(final int objectId, final InfinityRank rank) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
            stmt.setInt(1, objectId);
            stmt.setInt(2, SeasonRankingEnum.INFINITY.getId());
            stmt.setInt(3, rank.getRank());
            stmt.setInt(4, rank.getBestRank());
            stmt.setInt(5, rank.getCurrentTime());
            stmt.setInt(6, rank.getLastTime());
            stmt.setInt(7, rank.getBestTime());
            stmt.setInt(8, rank.getLowRank());
            stmt.setInt(9, 0);
            stmt.execute();
            stmt.close();
            return true;
        } catch (SQLException e) {
            log.error("addInfinityRank", e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
    }
	
    private boolean updateInfinityRank(final int objectId, InfinityRank rank) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
            stmt.setInt(1, rank.getRank());
            stmt.setInt(2, rank.getBestRank());
            stmt.setInt(3, rank.getCurrentTime());
            stmt.setInt(4, rank.getLastTime());
            stmt.setInt(5, rank.getBestTime());
            stmt.setInt(6, rank.getLowRank());
            stmt.setInt(7, 0);
            stmt.setInt(8, objectId);
            stmt.setInt(9, SeasonRankingEnum.INFINITY.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (SQLException e) {
            log.error("updateInfinityRank", e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public boolean store6VS6Rank(Player player) {
        Arena6VS6Ranking rank = player.get6VS6Rank();
        boolean result = false;
        switch (rank.getPersistentState()) {
            case NEW:
                result = add6VS6Rank(player.getObjectId(), rank);
            break;
            case UPDATE_REQUIRED:
                result = update6VS6Rank(player.getObjectId(), rank);
            break;
        }
        rank.setPersistentState(PersistentState.UPDATED);
        return result;
    }
	
    private boolean add6VS6Rank(final int objectId, final Arena6VS6Ranking rank) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
            stmt.setInt(1, objectId);
            stmt.setInt(2, SeasonRankingEnum.ARENA_6VS6.getId());
            stmt.setInt(3, rank.getRank());
            stmt.setInt(4, rank.getBestRank());
            stmt.setInt(5, rank.getPoints());
            stmt.setInt(6, rank.getLastPoints());
            stmt.setInt(7, rank.getHighPoints());
            stmt.setInt(8, rank.getLowPoints());
            stmt.setInt(9, rank.getPossitionMatch());
            stmt.execute();
            stmt.close();
            return true;
        } catch (SQLException e) {
            log.error("add6VS6Rank", e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
    }
	
    private boolean update6VS6Rank(final int objectId, Arena6VS6Ranking rank) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
            stmt.setInt(1, rank.getRank());
            stmt.setInt(2, rank.getBestRank());
            stmt.setInt(3, rank.getPoints());
            stmt.setInt(4, rank.getLastPoints());
            stmt.setInt(5, rank.getHighPoints());
            stmt.setInt(6, rank.getLowPoints());
            stmt.setInt(7, rank.getPossitionMatch());
            stmt.setInt(8, objectId);
            stmt.setInt(9, SeasonRankingEnum.ARENA_6VS6.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (SQLException e) {
            log.error("update6VS6Rank", e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
    }
	
	@Override
    public boolean storeDamageRank(Player player) {
        DamageRank rank = player.getDamageRank();
        boolean result = false;
        switch (rank.getPersistentState()) {
            case NEW:
                result = addDamageRank(player.getObjectId(), rank);
            break;
            case UPDATE_REQUIRED:
                result = updateDamageRank(player.getObjectId(), rank);
            break;
        }
        rank.setPersistentState(PersistentState.UPDATED);
        return result;
    }
	
    private boolean addDamageRank(final int objectId, final DamageRank rank) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
            stmt.setInt(1, objectId);
            stmt.setInt(2, SeasonRankingEnum.DAMAGE.getId());
            stmt.setInt(3, rank.getRank());
            stmt.setInt(4, rank.getBestRank());
            stmt.setInt(5, rank.getCurrentScore());
            stmt.setInt(6, rank.getLastScore());
            stmt.setInt(7, rank.getBestScore());
            stmt.setInt(8, rank.getLowRank());
            stmt.setInt(9, 0);
            stmt.execute();
            stmt.close();
            return true;
        } catch (SQLException e) {
            log.error("addDamageRank", e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
    }
	
    private boolean updateDamageRank(final int objectId, DamageRank rank) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
            stmt.setInt(1, rank.getRank());
            stmt.setInt(2, rank.getBestRank());
            stmt.setInt(3, rank.getCurrentScore());
            stmt.setInt(4, rank.getLastScore());
            stmt.setInt(5, rank.getBestScore());
            stmt.setInt(6, rank.getLowRank());
            stmt.setInt(7, 0);
            stmt.setInt(8, objectId);
            stmt.setInt(9, SeasonRankingEnum.DAMAGE.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (SQLException e) {
            log.error("updateDamageRank", e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public InfinityRank loadInfinityRank(int playerId, int tableId) {
        InfinityRank ranking = null;
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_MY_HISTORY);
            stmt.setInt(1, playerId);
            stmt.setInt(2, tableId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int rank = resultSet.getInt("rank");
                int best_rank = resultSet.getInt("last_rank");
                int point = resultSet.getInt("points");
                int last_point = resultSet.getInt("last_points");
                int high_point = resultSet.getInt("high_points");
                int low_point = resultSet.getInt("low_points");
                ranking = new InfinityRank(rank, best_rank, point, last_point, high_point, low_point);
                ranking.setPersistentState(PersistentState.UPDATED);
            } else {
                ranking = new InfinityRank(0, 0, 0, 0, 0, 0);
                ranking.setPersistentState(PersistentState.NEW);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            log.error("loadInfinityRank", e);
        } finally {
            DatabaseFactory.close(con);
        }
        return ranking;
    }
	
    @Override
    public Arena6VS6Ranking loadArena6VS6Rank(int playerId, int tableId) {
        Arena6VS6Ranking ranking = null;
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_MY_HISTORY);
            stmt.setInt(1, playerId);
            stmt.setInt(2, tableId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int rank = resultSet.getInt("rank");
                int best_rank = resultSet.getInt("last_rank");
                int point = resultSet.getInt("points");
                int last_point = resultSet.getInt("last_points");
                int high_point = resultSet.getInt("high_points");
                int low_point = resultSet.getInt("low_points");
                int position_match = resultSet.getInt("position_match");
                ranking = new Arena6VS6Ranking(rank, best_rank, point, last_point, high_point, low_point, position_match);
                ranking.setPersistentState(PersistentState.UPDATED);
            } else {
                ranking = new Arena6VS6Ranking(0, 0, 0, 0, 0, 0, 0);
                ranking.setPersistentState(PersistentState.NEW);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            log.error("loadArena6VS6Rank", e);
        } finally {
            DatabaseFactory.close(con);
        }
        return ranking;
    }
	
	@Override
    public DamageRank loadDamageRank(int playerId, int tableId) {
        DamageRank ranking = null;
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_MY_HISTORY);
            stmt.setInt(1, playerId);
            stmt.setInt(2, tableId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int rank = resultSet.getInt("rank");
                int best_rank = resultSet.getInt("last_rank");
                int point = resultSet.getInt("points");
                int last_point = resultSet.getInt("last_points");
                int high_point = resultSet.getInt("high_points");
                int low_point = resultSet.getInt("low_points");
                ranking = new DamageRank(rank, best_rank, point, last_point, high_point, low_point);
                ranking.setPersistentState(PersistentState.UPDATED);
            } else {
                ranking = new DamageRank(0, 0, 0, 0, 0, 0);
                ranking.setPersistentState(PersistentState.NEW);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            log.error("loadDamageRank", e);
        } finally {
            DatabaseFactory.close(con);
        }
        return ranking;
    }
	
    @Override
    public boolean supports(String databaseName, int majorVersion, int minorVersion) {
        return com.aionemu.gameserver.dao.MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
    }
}