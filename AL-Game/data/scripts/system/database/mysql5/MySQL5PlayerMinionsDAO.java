package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerMinionsDAO;
import com.aionemu.gameserver.model.gameobjects.player.MinionCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ranastic
 *
 */
public class MySQL5PlayerMinionsDAO extends PlayerMinionsDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerMinionsDAO.class);

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

	@Override
	public void insertPlayerMinion(MinionCommonData minionCommonData) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("INSERT INTO player_minions(player_id, object_id, minion_id, name, grade, level, birthday, growth_points, is_lock, despawn_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			stmt.setInt(1, minionCommonData.getMasterObjectId());
			stmt.setInt(2, minionCommonData.getObjectId());
			stmt.setInt(3, minionCommonData.getMinionId());
			stmt.setString(4, minionCommonData.getName());
			stmt.setString(5, minionCommonData.getMinionGrade());
			stmt.setInt(6, minionCommonData.getMinionLevel());
			stmt.setTimestamp(7, minionCommonData.getBirthdayTimestamp());
			stmt.setInt(8, minionCommonData.getGrowthPoints());
			stmt.setBoolean(9, minionCommonData.isLocked());
			stmt.setTimestamp(10, minionCommonData.getDespawnTime());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			//log.error("Error inserting new minion #" + minionCommonData.getMinionId() + "[" + minionCommonData.getName() + "]", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void removePlayerMinion(Player player, int minionObjectId) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("DELETE FROM player_minions WHERE player_id = ? AND object_id = ?");
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, minionObjectId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error removing minion #" + minionObjectId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public List<MinionCommonData> getPlayerMinions(Player player) {
		List<MinionCommonData> minions = new ArrayList<MinionCommonData>();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM player_minions WHERE player_id = ?");
			stmt.setInt(1, player.getObjectId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				MinionCommonData minionCommonData = new MinionCommonData(rs.getInt("minion_id"), player.getObjectId(), rs.getString("name"), rs.getString("grade"), rs.getInt("level"), rs.getInt("growth_points"), rs.getBoolean("is_lock"));
				minionCommonData.setObjectId(rs.getInt("object_id"));
				minionCommonData.setBirthday(rs.getTimestamp("birthday"));
				Timestamp ts = null;
				try {
					ts = rs.getTimestamp("despawn_time");
				}
				catch (Exception e) {
				}
				if (ts == null)
					ts = new Timestamp(System.currentTimeMillis());
				minionCommonData.setDespawnTime(ts);
				minions.add(minionCommonData);
			}
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error getting minions for " + player.getObjectId(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		return minions;
	}
	
	@Override
	public void updateName(MinionCommonData minionCommonData) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE player_minions SET name = ? WHERE player_id = ? AND object_id = ?");
			stmt.setString(1, minionCommonData.getName());
			stmt.setInt(2, minionCommonData.getMasterObjectId());
			stmt.setInt(3, minionCommonData.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error update minion #" + minionCommonData.getMinionId(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void updateMinionGrowth(MinionCommonData minionCommonData) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE player_minions SET growth_points = ? WHERE player_id = ? AND object_id = ?");
			stmt.setInt(1, minionCommonData.getGrowthPoints());
			stmt.setInt(2, minionCommonData.getMasterObjectId());
			stmt.setInt(3, minionCommonData.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error update minion #" + minionCommonData.getMinionId(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void updateMinionLock(MinionCommonData minionCommonData) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE player_minions SET is_lock = ? WHERE player_id = ? AND object_id = ?");
			stmt.setBoolean(1, minionCommonData.isLocked());
			stmt.setInt(2, minionCommonData.getMasterObjectId());
			stmt.setInt(3, minionCommonData.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error update minion #" + minionCommonData.getMinionId(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public boolean isNameUsed(int playerId, final String name) {
		PreparedStatement s = DB.prepareStatement("SELECT 1 FROM player_minions WHERE player_id = ? AND name = ? LIMIT 1");
		try {
			s.setInt(1, playerId);
			s.setString(2, name);
			ResultSet rs = s.executeQuery();
			return rs.next();
		}
		catch (SQLException e) {
			log.error("Can't check if name " + name + ", is used, returning possitive result", e);
			return true;
		}
		finally {
			DB.close(s);
		}
	}

	@Override
	public void setTime(Player player, int petId, long time) {
		// TODO Auto-generated method stub
		
	}
}
