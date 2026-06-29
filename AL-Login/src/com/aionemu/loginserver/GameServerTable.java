/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */


package com.aionemu.loginserver;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.network.IPRange;
import com.aionemu.commons.utils.NetworkUtils;
import com.aionemu.loginserver.dao.GameServersDAO;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsAuthResponse;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_REQUEST_KICK_ACCOUNT;

/**
 * GameServerTable contains list of GameServers registered on this LoginServer.
 * GameServer may by online or down.
 *
 * @author -Nemesiss-
 */
public class GameServerTable {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(GameServerTable.class);
    /**
     * Map<Id,GameServer>
     */
    private static Map<Byte, GameServerInfo> gameservers;

    /**
     * Return collection contains all registered [up/down] GameServers.
     *
     * @return collection of GameServers.
     */
    public static Collection<GameServerInfo> getGameServers() {
        return Collections.unmodifiableCollection(gameservers.values());
    }

    /**
     * Load GameServers from database.
     */
    public static void load() {
        gameservers = getDAO().getAllGameServers();
        log.info("GameServerTable loaded " + gameservers.size() + " registered GameServers.");
        for (GameServerInfo gsi : gameservers.values()) {
            log.info("[GS-AUTH-CONFIG] dbId=" + (gsi.getId() & 0xFF) + " mask=" + gsi.getIp() + " passwordLength=" + safeLength(gsi.getPassword()));
        }
    }

    /**
     * Register GameServer if its possible.
     *
     * @param gsConnection Connection object
     * @param requestedId id of server that was requested
     * @param defaultAddress default network address from server, usually
     * internet address
     * @param ipRanges mapping of various ip ranges, usually used for local area
     * networks
     * @param port port that is used by server
     * @param maxPlayers maximum amount of players
     * @param password server password that is specified configs, used to check
     * if gs can auth on ls
     * @return GsAuthResponse
     */
    public static GsAuthResponse registerGameServer(GsConnection gsConnection, byte requestedId, byte[] defaultAddress, List<IPRange> ipRanges, int port, int maxPlayers, String password) {
        GameServerInfo gsi = gameservers.get(requestedId);
        int requestedIdUnsigned = requestedId & 0xFF;
        String remoteIp = gsConnection.getIP();

        log.info("[GS-AUTH] request id=" + requestedIdUnsigned + " remoteIp=" + remoteIp + " port=" + port + " maxPlayers=" + maxPlayers
            + " defaultAddress=" + formatAddress(defaultAddress) + " ipRangeCount=" + (ipRanges == null ? 0 : ipRanges.size())
            + " passwordLength=" + safeLength(password));

        /**
         * This id is not Registered at LoginServer.
         */
        if (gsi == null) {
            log.warn("[GS-AUTH] rejected: requested id=" + requestedIdUnsigned + " is not registered in LoginServer DB. Registered ids=" + getRegisteredIdsForLog());
            return GsAuthResponse.NOT_AUTHED;
        }

        /**
         * Check if this GameServer is not already registered.
         */
        if (gsi.getConnection() != null) {
            log.warn("[GS-AUTH] rejected: id=" + requestedIdUnsigned + " is already registered by " + gsi.getConnection());
            return GsAuthResponse.ALREADY_REGISTERED;
        }

        /**
         * Check if password and ip are ok.
         */
        boolean passwordMatch = safeEquals(gsi.getPassword(), password);
        boolean ipMatch = NetworkUtils.checkIPMatching(gsi.getIp(), remoteIp);

        log.info("[GS-AUTH] compare id=" + requestedIdUnsigned + " dbMask=" + gsi.getIp() + " remoteIp=" + remoteIp + " ipMatch=" + ipMatch
            + " passwordMatch=" + passwordMatch + " dbPasswordLength=" + safeLength(gsi.getPassword()) + " receivedPasswordLength=" + safeLength(password));

        if (!passwordMatch || !ipMatch) {
            log.warn("[GS-AUTH] rejected: id=" + requestedIdUnsigned + " remoteIp=" + remoteIp + " dbMask=" + gsi.getIp()
                + " ipMatch=" + ipMatch + " passwordMatch=" + passwordMatch + ". Check AL-Game config gameserver.network.login.gsid/password and LS DB gameservers.mask/password.");
            return GsAuthResponse.NOT_AUTHED;
        }

        gsi.setDefaultAddress(defaultAddress);
        gsi.setIpRanges(ipRanges);
        gsi.setPort(port);
        gsi.setMaxPlayers(maxPlayers);
        gsi.setConnection(gsConnection);

        gsConnection.setGameServerInfo(gsi);
        log.info("[GS-AUTH] accepted: id=" + requestedIdUnsigned + " remoteIp=" + remoteIp + " mask=" + gsi.getIp() + " port=" + port + " maxPlayers=" + maxPlayers);
        return GsAuthResponse.AUTHED;
    }

    /**
     * Returns GameSererInfo object for given gameserverId.
     *
     * @param gameServerId
     * @return GameSererInfo object for given gameserverId.
     */
    public static GameServerInfo getGameServerInfo(byte gameServerId) {
        return gameservers.get(gameServerId);
    }

    /**
     * Check if account is already in use on any GameServer. If so - kick
     * account from GameServer.
     *
     * @param acc account to check
     * @return true is account is logged in on one of GameServers
     */
    public static boolean isAccountOnAnyGameServer(Account acc) {
        for (GameServerInfo gsi : getGameServers()) {
            if (gsi.isAccountOnGameServer(acc.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method, used to kick account from any gameServer if it's logged in
     *
     * @param account account to kick
     */
    public static void kickAccountFromGameServer(Account account) {
        for (GameServerInfo gsi : getGameServers()) {
            if (gsi.isAccountOnGameServer(account.getId())) {
                gsi.getConnection().sendPacket(new SM_REQUEST_KICK_ACCOUNT(account.getId()));
                break;
            }
        }
    }

    /**
     * Retuns {@link com.aionemu.loginserver.dao.GameServersDAO} , just a
     * shortcut
     *
     * @return {@link com.aionemu.loginserver.dao.GameServersDAO}
     */
    private static GameServersDAO getDAO() {
        return DAOManager.getDAO(GameServersDAO.class);
    }

    public static void pong(byte serverId, int pid) {
        for (GameServerInfo gsi : getGameServers()) {
            if (gsi.getId() == serverId) {
                if (gsi.getConnection() != null) {
                    gsi.getConnection().pong(pid);
                }
                break;
            }
        }
    }

    private static boolean safeEquals(String expected, String actual) {
        return expected == null ? actual == null : expected.equals(actual);
    }

    private static int safeLength(String value) {
        return value == null ? -1 : value.length();
    }

    private static String getRegisteredIdsForLog() {
        if (gameservers == null || gameservers.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (GameServerInfo gsi : gameservers.values()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(gsi.getId() & 0xFF);
            first = false;
        }
        sb.append(']');
        return sb.toString();
    }

    private static String formatAddress(byte[] address) {
        if (address == null || address.length == 0) {
            return "empty";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < address.length; i++) {
            if (i > 0) {
                sb.append('.');
            }
            sb.append(address[i] & 0xFF);
        }
        return sb.toString();
    }
}
