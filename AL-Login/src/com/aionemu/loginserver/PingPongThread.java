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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_PING;
import com.aionemu.loginserver.configs.SvStatsConfig;
import com.aionemu.loginserver.dao.SvStatsDAO;
import com.aionemu.commons.database.dao.DAOManager;

/**
 * @author KID
 */
public class PingPongThread implements Runnable {

    private final Logger log = LoggerFactory.getLogger(PingPongThread.class);
    private GsConnection connection;
    public volatile boolean uptime = true;
    private SM_PING ping;
    private byte requests = 0;
    private int serverPID = -1;
    private boolean killProcess = false;

    public PingPongThread(GsConnection connection) {
        this.uptime = true;
        this.connection = connection;
        this.ping = new SM_PING();
    }

    @Override
    public void run() {
        GameServerInfo info = getInfo();
        if (info == null) {
            uptime = false;
            log.warn("PingPong start skipped because GameServerInfo is null. Connection was probably rejected before authentication: " + connection);
            return;
        }

        log.info("PingPong for gameserver #" + (info.getId() & 0xFF) + " has started.");
        while (uptime) {
            try {
                Thread.sleep(Config.PINGPONG_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!uptime || validateResponse()) {
                return;
            }

            try {
                info = getInfo();
                if (info == null) {
                    uptime = false;
                    log.warn("PingPong stopped because GameServerInfo became null. Connection=" + connection);
                    return;
                }

                connection.sendPacket(ping);
                requests++;
                if (SvStatsConfig.SVSTATS_ENABLE) {
                    int currentID = info.getId();
                    int currentPlayer = info.getCurrentPlayers();
                    int currentMax = info.getMaxPlayers();
                    DAOManager.getDAO(SvStatsDAO.class).update_SvStats_Online(currentID, 1, currentPlayer, currentMax);
                }
            } catch (Exception ex) {
                log.error("PingThread#" + getServerIdForLog(), ex);
            }
        }
    }

    public void onResponse(int pid) {
        requests--;
        this.serverPID = pid;
    }

    public boolean validateResponse() {
        if (requests >= 2) {
            uptime = false;
            GameServerInfo info = getInfo();
            log.info("Gameserver #" + getServerIdForLog() + " [PID=" + this.serverPID + "] died, closing.");

            if (SvStatsConfig.SVSTATS_ENABLE && info != null) {
                int currentID = info.getId();
                DAOManager.getDAO(SvStatsDAO.class).update_SvStats_Offline(currentID, 0, 0);
            }
            connection.close(false);
            if (killProcess && serverPID != -1) {
                if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
                    try {
                        Runtime.getRuntime().exec("taskkill /pid " + serverPID + " /f");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void closeMe() {
        uptime = false;

        GameServerInfo info = getInfo();
        if (info == null) {
            log.debug("PingPong close skipped SvStats offline update because GameServerInfo is null. Connection=" + connection);
            return;
        }

        if (SvStatsConfig.SVSTATS_ENABLE) {
            int currentID = info.getId();
            DAOManager.getDAO(SvStatsDAO.class).update_SvStats_Offline(currentID, 0, 0);
        }
    }

    private GameServerInfo getInfo() {
        return connection == null ? null : connection.getGameServerInfo();
    }

    private String getServerIdForLog() {
        GameServerInfo info = getInfo();
        return info == null ? "unauthenticated" : String.valueOf(info.getId() & 0xFF);
    }
}
