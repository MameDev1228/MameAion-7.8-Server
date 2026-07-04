package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.LoginEventDAO;
import com.aionemu.gameserver.model.account.AccountLoginEvent;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MySQL5LoginEventDAO extends LoginEventDAO {

    private static final Logger log = LoggerFactory.getLogger(LoginEventDAO.class);

    public static final String ADD_QUERY = "INSERT INTO `login_event` (`account_id`, `stamp`, `available`, `event_id`) VALUES (?,?,?,?)";
    public static final String SELECT_QUERY = "SELECT * FROM `login_event` WHERE `account_id`=?";
    public static final String DAILY_UPDATE = "UPDATE login_event SET available = 1 WHERE available = 0";
    public static final String WEEKLY_UPDATE = "UPDATE login_event SET stamp = 1, available = 1 WHERE event_id = 10";
    public static final String UPDATE_QUERY = "UPDATE login_event set `available`=?, `collected`=?, stamp=? WHERE `account_id`=?";


    @Override
    public void load(final Player player) {
    Connection con = null;
        try {
        con = DatabaseFactory.getConnection();
        PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
        stmt.setInt(1, player.getPlayerAccount().getId());
        ResultSet rset = stmt.executeQuery();
        if (rset.next()) {
            int id = rset.getInt("event_id");
            int stamp = rset.getInt("stamp");
            int collected = rset.getInt("collected");
            boolean available = rset.getBoolean("available");
            AccountLoginEvent event = new AccountLoginEvent(id, stamp, available, collected);
            player.setLoginEvent(event);
        }
        rset.close();
        stmt.close();
    }
        catch (Exception e) {
        log.error("Could not restore PlayerLoginEvent data for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
    }
        finally {
        DatabaseFactory.close(con);
    }
}

    @Override
    public boolean add(final Player player) {
        return DB.insertUpdate(ADD_QUERY, new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
                ps.setInt(1, player.getPlayerAccount().getId());
                ps.setInt(2, player.getLoginEvent().getStamp());
                ps.setBoolean(3, player.getLoginEvent().isAvailable());
                ps.setInt(4, player.getLoginEvent().getId());
                ps.execute();
                ps.close();
            }
        });
    }

    @Override
    public boolean update(Player player) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
            stmt.setBoolean(1, player.getLoginEvent().isAvailable());
            stmt.setInt(2, player.getLoginEvent().getCollected());
            stmt.setInt(3, player.getLoginEvent().getStamp());
            stmt.setInt(4, player.getPlayerAccount().getId());
            stmt.execute();
            stmt.close();
        }
        catch (Exception e) {
            return false;
        }
        finally {
            DatabaseFactory.close(con);
        }
        return true;
    }

    @Override
    public boolean dailyUpdate() {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(DAILY_UPDATE);
            stmt.execute();
            stmt.close();
        }
        catch (Exception e) {
            return false;
        }
        finally {
            DatabaseFactory.close(con);
        }
        return true;
    }

    @Override
    public boolean weeklyUpdate() {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(WEEKLY_UPDATE);
            stmt.execute();
            stmt.close();
        }
        catch (Exception e) {
            return false;
        }
        finally {
            DatabaseFactory.close(con);
        }
        return true;
    }

    @Override
    public boolean supports(String databaseName, int majorVersion, int minorVersion) {
        return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
    }
}
