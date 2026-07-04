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
package com.aionemu.gameserver.services;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.ShutdownHook;
import com.aionemu.gameserver.dao.*;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.model.gameobjects.player.fame.PlayerFame;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.player.PlayerFameService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;

public class RestartService
{
    private Logger log = LoggerFactory.getLogger(RestartService.class);
	
    public void onStart() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        String daily1 = "0 0 9 ? * * *";
		String daily2 = "0 0 0/12 ? * * *";
		CronService.getInstance().schedule(new Runnable() {
            public void run() {
                LoginEventService.getInstance().onReset();
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                    DAOManager.getDAO(PlayerAchievementDAO.class).deleteAchievements(AchievementType.WEEKLY);
                    DAOManager.getDAO(PlayerAchievementActionDAO.class).deleteAchievementsActions(AchievementType.WEEKLY);
                    DAOManager.getDAO(PlayerAchievementDAO.class).deleteAchievements(AchievementType.DAILY);
                    DAOManager.getDAO(PlayerAchievementActionDAO.class).deleteAchievementsActions(AchievementType.DAILY);
                    DAOManager.getDAO(PlayerLunaShopDAO.class).delete();
					DAOManager.getDAO(PlayerShugoSweepDAO.class).delete();
                } else {
                    DAOManager.getDAO(PlayerAchievementDAO.class).deleteAchievements(AchievementType.DAILY);
                    DAOManager.getDAO(PlayerAchievementActionDAO.class).deleteAchievementsActions(AchievementType.DAILY);
                    DAOManager.getDAO(PlayerLunaShopDAO.class).delete();
					DAOManager.getDAO(PlayerShugoSweepDAO.class).delete();
                } if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    PlayerFameService.getInstance().onResetWeekly();
                }
                int delay1 = 300;
				log.info("Restart Achievement + Luna Shop + Shugo Sweep");
                ShutdownHook.getInstance().doShutdown(delay1, 20, ShutdownHook.ShutdownMode.RESTART);
            }
        }, daily1);
		CronService.getInstance().schedule(new Runnable() {
            public void run() {
                int delay2 = 300;
				log.info("Restart Server Service");
                ShutdownHook.getInstance().doShutdown(delay2, 20, ShutdownHook.ShutdownMode.RESTART);
            }
        }, daily2);
    }
	
    public static RestartService getInstance() {
        return SingletonHolder.instance;
    }
	
	@SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final RestartService instance = new RestartService();
    }
}