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

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.ShutdownHook;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dao.PlayerAchievementActionDAO;
import com.aionemu.gameserver.dao.PlayerAchievementDAO;
import com.aionemu.gameserver.dao.PlayerLunaShopDAO;
import com.aionemu.gameserver.dao.PlayerShugoSweepDAO;
import com.aionemu.gameserver.model.gameobjects.player.achievement.AchievementType;
import com.aionemu.gameserver.services.player.PlayerFameService;

public class RestartService {
	private final Logger log = LoggerFactory.getLogger(RestartService.class);

	public void onStart() {
		if (!GSConfig.RESTART_SERVICE_ENABLE) {
			log.info("RestartService disabled by config. No daily reset/restart cron will be registered.");
			return;
		}

		if (GSConfig.RESTART_DAILY_RESET_ENABLE) {
			final String dailyResetSchedule = normalizeSchedule(GSConfig.RESTART_DAILY_RESET_SCHEDULE, "0 0 9 ? * * *");
			CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					runDailyReset();
					if (GSConfig.RESTART_DAILY_RESET_RESTART_AFTER_RESET) {
						log.info("Daily reset completed. Restart is enabled by config, scheduling server restart.");
						restartServer("Daily reset restart");
					} else {
						log.info("Daily reset completed. Restart skipped by config.");
					}
				}
			}, dailyResetSchedule);
			log.info("RestartService daily reset scheduled: schedule=" + dailyResetSchedule + " restartAfterReset=" + GSConfig.RESTART_DAILY_RESET_RESTART_AFTER_RESET);
		} else {
			log.info("RestartService daily reset cron disabled by config.");
		}

		if (GSConfig.RESTART_SERVER_REBOOT_ENABLE) {
			final String rebootSchedule = normalizeSchedule(GSConfig.RESTART_SERVER_REBOOT_SCHEDULE, "0 0 0/12 ? * * *");
			CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					restartServer("Scheduled server reboot");
				}
			}, rebootSchedule);
			log.info("RestartService scheduled reboot enabled: schedule=" + rebootSchedule);
		} else {
			log.info("RestartService scheduled reboot disabled by config. The old 00:00/12:00 restart will not run.");
		}
	}

	private void runDailyReset() {
		Calendar calendar = Calendar.getInstance();
		LoginEventService.getInstance().onReset();

		boolean weeklyAchievementReset = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY;
		if (weeklyAchievementReset) {
			DAOManager.getDAO(PlayerAchievementDAO.class).deleteAchievements(AchievementType.WEEKLY);
			DAOManager.getDAO(PlayerAchievementActionDAO.class).deleteAchievementsActions(AchievementType.WEEKLY);
		}

		DAOManager.getDAO(PlayerAchievementDAO.class).deleteAchievements(AchievementType.DAILY);
		DAOManager.getDAO(PlayerAchievementActionDAO.class).deleteAchievementsActions(AchievementType.DAILY);
		DAOManager.getDAO(PlayerLunaShopDAO.class).delete();
		DAOManager.getDAO(PlayerShugoSweepDAO.class).delete();

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
			PlayerFameService.getInstance().onResetWeekly();
		}

		log.info("Daily reset: loginEvent, achievements, luna shop, shugo sweep" + (weeklyAchievementReset ? ", weekly achievements" : "") + " reset.");
	}

	private void restartServer(String reason) {
		int delay = Math.max(0, GSConfig.RESTART_SHUTDOWN_DELAY);
		int announceInterval = Math.max(1, GSConfig.RESTART_SHUTDOWN_ANNOUNCE_INTERVAL);
		log.info(reason + ": restart in " + delay + " seconds, announceInterval=" + announceInterval + ".");
		ShutdownHook.getInstance().doShutdown(delay, announceInterval, ShutdownHook.ShutdownMode.RESTART);
	}

	private String normalizeSchedule(String schedule, String fallback) {
		return schedule == null || schedule.trim().isEmpty() ? fallback : schedule.trim();
	}

	public static RestartService getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final RestartService instance = new RestartService();
	}
}
