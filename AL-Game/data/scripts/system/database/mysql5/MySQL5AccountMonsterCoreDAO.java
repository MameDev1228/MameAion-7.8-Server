package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.AccountMonsterCoreDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.monsterCore.MonsterCore;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MySQL5AccountMonsterCoreDAO extends AccountMonsterCoreDAO
{
    private static final Logger log = LoggerFactory.getLogger(MySQL5AccountMonsterCoreDAO.class);
    public static final String INSERT_QUERY = "INSERT INTO `player_monster_core` (`account_id`, `mc_id`, `collect`, `level`) VALUES (?,?,?,?)";
    public static final String UPDATE_QUERY = "UPDATE `player_monster_core` set collect=?, level=? WHERE `account_id`=? AND `mc_id`=?";
    public static final String LOAD_QUERY = "SELECT * FROM `player_monster_core` WHERE `account_id`=?";
	
    @Override
    public Map<Integer, MonsterCore> load(final Account account) {
        final Map<Integer, MonsterCore> cores = new FastMap<Integer, MonsterCore>();
        DB.select(LOAD_QUERY, new ParamReadStH() {
            @Override
            public void setParams(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, account.getId());
            }
            @Override
            public void handleRead(ResultSet rset) throws SQLException {
                while (rset.next()) {
                    MonsterCore core = new MonsterCore(rset.getInt("mc_id"));
                    core.setLevel(rset.getInt("level"));
                    core.setCollect(rset.getInt("collect"));
                    cores.put(rset.getInt("mc_id"), core);
                }
            }
        });
        return cores;
    }
	
    @Override
    public boolean update(Account account, MonsterCore core) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
            stmt.setInt(1, core.getCollect());
            stmt.setInt(2, core.getLevel());
            stmt.setInt(3, account.getId());
            stmt.setInt(4, core.getId());
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            //log.error("Could not update Monster Core data for Player " + account.getName() + " from DB: " + e.getMessage(), e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
        return true;
    }
	
    @Override
    public boolean store(Account account, MonsterCore core) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
            stmt.setInt(1, account.getId());
            stmt.setInt(2, core.getId());
            stmt.setInt(3, core.getCollect());
            stmt.setInt(4, core.getLevel());
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            //log.error("Could not store Monster core for player " + account.getName() + " from DB: " + e.getMessage(), e);
            return false;
        } finally {
            DatabaseFactory.close(con);
        }
        return true;
    }
	
    @Override
    public boolean supports(String databaseName, int majorVersion, int minorVersion) {
        return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
    }
}