/**
 * This file is part of aion-emu <aion-emu.com>.
 * <p>
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.CharacterBanInfo;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.PlayerInfo;
import com.aionemu.gameserver.services.BrokerService;
import com.aionemu.gameserver.services.player.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class SM_CHARACTER_LIST extends PlayerInfo {
    private static Logger log = LoggerFactory.getLogger(SM_CHARACTER_LIST.class);
    private final int playOk2;
    private final int unkValue;

    public SM_CHARACTER_LIST(int unkValue, int playOk2) {
        this.playOk2 = playOk2;
        this.unkValue = unkValue;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(unkValue);
        if (unkValue == 0) {
            writeD(playOk2);
            writeC(0);
        } else if (unkValue == 2) {
            writeD(playOk2);
            Account account = con.getAccount();
            removeDeletedCharacters(account);
            writeC(account.size()); // characters count
            for (PlayerAccountData playerData : account.getSortedAccountsList()) {
                PlayerCommonData pcd = playerData.getPlayerCommonData();
                CharacterBanInfo cbi = playerData.getCharBanInfo();
                Player player = PlayerService.getPlayer(pcd.getPlayerObjId(), account);
                writePlayerInfo(playerData);
                writeB(new byte[40]); //spacer

                if (cbi != null && cbi.getEnd() > System.currentTimeMillis() / 1000) {
                    writeD((int) cbi.getStart()); // startPunishDate
                    writeD((int) cbi.getEnd()); // endPunishDate
                    writeS(cbi.getReason());
                } else {
                    writeB(new byte[52]); // Ban Info
                }

                writeD(playerData.getDeletionTimeInSeconds());
                writeD(0); // unk
                writeD(0); // unk
                writeD(0); // unk
                writeH(player.getPlayerSettings().getDisplay());// display helmet 0 show, 5 dont show
                writeH(0); // unk 129
                writeD(DAOManager.getDAO(MailDAO.class).mailCount(pcd.getPlayerObjId())); // All Mail Count
                writeD(DAOManager.getDAO(MailDAO.class).unreadedMails(pcd.getPlayerObjId())); // Unread Mail Count
                writeQ(BrokerService.getInstance().getCollectedMoney(pcd)); // collected money from broker
                writeB(new byte[158]); // 7.5

            }
        }
    }

    public void removeDeletedCharacters(Account account) {
        Iterator<PlayerAccountData> it = account.iterator();
        while (it.hasNext()) {
            PlayerAccountData pad = it.next();
            Race race = pad.getPlayerCommonData().getRace();
            long deletionTime = (long) pad.getDeletionTimeInSeconds() * (long) 1000;
            if (deletionTime != 0 && deletionTime <= System.currentTimeMillis()) {
                it.remove();
                account.decrementCountOf(race);
                PlayerService.deletePlayerFromDB(pad.getPlayerCommonData().getPlayerObjId());
            }
        }
        if (account.isEmpty()) {
            removeAccountWH(account.getId());
            account.getAccountWarehouse().clear();
        }
    }

    private static void removeAccountWH(int accountId) {
        DAOManager.getDAO(InventoryDAO.class).deleteAccountWH(accountId);
    }
}