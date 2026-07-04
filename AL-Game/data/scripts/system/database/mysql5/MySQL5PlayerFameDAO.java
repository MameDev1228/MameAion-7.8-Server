package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerAchievementActionDAO;
import com.aionemu.gameserver.dao.PlayerFameDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerLunaShop;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementState;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.model.gameobjects.player.achievement.PlayerAchievement;
import com.aionemu.gameserver.model.gameobjects.player.fame.PlayerFame;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MySQL5PlayerFameDAO extends PlayerFameDAO {

    private Logger log = LoggerFactory.getLogger(MySQL5PlayerAchievementDAO.class);

    public static final String INSERT_ACHIEVEMENT = "INSERT INTO player_fame (player_id, fame_id, level, exp) VALUES (?, ?, ?, ?)";
    public static final String LOAD_QUERY = "SELECT * FROM `player_fame` WHERE `player_id`=?";
    public static final String UPDATE_QUERY = "UPDATE player_fame set level=?, exp=?, exp_loss=? WHERE `player_id`=? AND `fame_id`=?";
    public static final String REDUCE_QUERY = "UPDATE player_fame set exp=?, level=? WHERE `player_id`=? AND `fame_id`=?";
    public static final String LOAD_WEEKLY = "SELECT * FROM `player_fame`";


    @Override
    public Map<Integer, PlayerFame> loadPlayerFame(final Player player) {
        final Map<Integer, PlayerFame> playerfames = new FastMap<Integer, PlayerFame>();
        DB.select(LOAD_QUERY, new ParamReadStH() {
            @Override
            public void setParams(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, player.getObjectId());
            }
            @Override
            public void handleRead(ResultSet rset) throws SQLException {
                while (rset.next()) {
                    PlayerFame fame = new PlayerFame(rset.getInt("fame_id"), rset.getInt("level"), rset.getLong("exp"), rset.getLong("exp_loss"), player.getObjectId());
                    playerfames.put(fame.getId(), fame);
                }
            }
        });
        return playerfames;
    }

    @Override
    public boolean addPlayerFame(Player player, PlayerFame fame) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(INSERT_ACHIEVEMENT);
            stmt.setInt(1, player.getObjectId());
            stmt.setInt(2, fame.getId());
            stmt.setInt(3, fame.getLevel());
            stmt.setLong(4, fame.getExp());
            stmt.execute();
            stmt.close();
            return true;
        } catch (SQLException e) {
            log.error("addFame error", e);

            return false;
        } finally {
            DatabaseFactory.close(con);
        }
    }

    @Override
    public boolean updatePlayerFame(Player player, PlayerFame fame) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
            stmt.setInt(1, fame.getLevel());
            stmt.setLong(2, fame.getExp());
            stmt.setLong(3, fame.getExpLoss());
            stmt.setInt(4, player.getObjectId());
            stmt.setInt(5, fame.getId());
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Could not update PlayerFame data for Player " + player.getName() + " from DB: " + e.getMessage(), e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
        return true;
    }

    @Override
    public List<PlayerFame> weeklyFame() {
        final List<PlayerFame> playerfames = new FastList<PlayerFame>();
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(LOAD_WEEKLY);
            ResultSet rset = stmt.executeQuery();
            if (rset.next()) {
                PlayerFame fame = new PlayerFame(rset.getInt("fame_id"), rset.getInt("level"), rset.getLong("exp"), rset.getLong("exp_loss"), rset.getInt("player_id"));
                playerfames.add(fame);
            }
            rset.close();
            stmt.close();
        }
        catch (Exception e) {
        }
        finally {
            DatabaseFactory.close(con);
        }
        return playerfames;
    }

    @Override
    public boolean reduceWeekly(PlayerFame fame) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(REDUCE_QUERY);
            stmt.setLong(1, fame.getExp());
            stmt.setInt(2, fame.getLevel());
            stmt.setInt(3, fame.getPlayerId());
            stmt.setInt(4, fame.getId());
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
        return true;
    }


    @Override
    public boolean supports(String s, int i, int i1) {
        return MySQL5DAOUtils.supports(s, i, i1);
    }
}
