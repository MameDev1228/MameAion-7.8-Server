/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ReadStH;
import com.aionemu.gameserver.dao.AccountDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.aionemu.gameserver.model.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Krz on décembre 2018
 */
public class MySQL5AccountDAO extends AccountDAO {

    private Logger log = LoggerFactory.getLogger(MySQL5AccountDAO.class);

    @Override
    public int getJumpingCountOnAccount(int accountId) {
        Connection con = null;
        int cnt = 0;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM `account_data` WHERE `id` = ?");
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            cnt = rs.getInt("jumping");
            rs.close();
            stmt.close();
        }
        catch (Exception e) {
            return 0;
        }
        finally {
            DatabaseFactory.close(con);
        }

        return cnt;
    }

    @Override
    public void SaveJumpingCountOnAccount(Account account) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE account_data set jumping=? WHERE `id`=?");
            stmt.setInt(1, account.getJumping());
            stmt.setInt(2, account.getId());
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            log.error("Could not update Jumping data for Player " + account.getName() + " from DB: " + e.getMessage(), e);
        } finally {
            DatabaseFactory.close(con);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(String s, int i, int i1) {
        return MySQL5DAOUtils.supports(s, i, i1);
    }

}
