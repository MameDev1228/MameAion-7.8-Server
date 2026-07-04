package com.aionemu.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.network.NioServer;
import com.aionemu.commons.network.ServerCfg;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.AEInfos;
import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.RankingConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.configs.main.ThreadConfig;
import com.aionemu.gameserver.configs.main.WeddingsConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.house.MaintenanceTask;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.network.BannedMacManager;
import com.aionemu.gameserver.network.aion.GameConnectionFactoryImpl;
import com.aionemu.gameserver.network.chatserver.ChatServer;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.abyss.AbyssRankCleaningService;
import com.aionemu.gameserver.services.abyss.AbyssRankUpdateService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.events.*;
import com.aionemu.gameserver.services.instance.*;
import com.aionemu.gameserver.services.player.*;
import com.aionemu.gameserver.services.reward.RewardService;
import com.aionemu.gameserver.services.teleport.HotspotTeleportService;
import com.aionemu.gameserver.services.ranking.SeasonRankingUpdateService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.TemporarySpawnEngine;
import com.aionemu.gameserver.taskmanager.TaskManagerFromDB;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.utils.chathandlers.ChatProcessor;
import com.aionemu.gameserver.utils.cron.ThreadPoolManagerRunnableRunner;
import com.aionemu.gameserver.utils.gametime.DateTimeUtil;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.javaagent.JavaAgentUtils;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldEngine;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.zone.ZoneService;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class GameServer
{
	private static final Logger log = LoggerFactory.getLogger(GameServer.class);
	private static int ELYOS_COUNT = 0;
	private static int ASMOS_COUNT = 0;
	private static double ELYOS_RATIO = 0.0;
	private static double ASMOS_RATIO = 0.0;
	
	private static final ReentrantLock lock = new ReentrantLock();
	public static HashSet<String> npcs_count = new HashSet<String>();
	private static Set<StartupHook> startUpHooks = new HashSet<StartupHook>();
	
	private static void initalizeLoggger() {
		new File("./log/backup/").mkdirs();
		File[] files = new File("log").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		});
		if (files != null && files.length > 0) {
			byte[] buf = new byte[1024];
			try {
				String outFilename = "./log/backup/" + new SimpleDateFormat("yyyy-MM-dd HHmmss").format(new Date()) + ".zip";
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
				out.setMethod(ZipOutputStream.DEFLATED);
				out.setLevel(Deflater.BEST_COMPRESSION);
				for (File logFile : files) {
					FileInputStream in = new FileInputStream(logFile);
					out.putNextEntry(new ZipEntry(logFile.getName()));
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.closeEntry();
					in.close();
					logFile.delete();
				}
				out.close();
			}
			catch (IOException e) {
			}
		}
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure("config/slf4j-logback.xml");
		} catch (JoranException je) {
			throw new RuntimeException("Failed to configure loggers, shutting down...", je);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();
		final GameEngine[] parallelEngines = { QuestEngine.getInstance(), InstanceEngine.getInstance(), AI2Engine.getInstance(), ChatProcessor.getInstance() };
		final GameEngine[] worldEngines = {WorldEngine.getInstance()};
		final CountDownLatch progressLatch = new CountDownLatch(parallelEngines.length);
		final CountDownLatch progressLatch2 = new CountDownLatch(worldEngines.length);
		initalizeLoggger();
		initUtilityServicesAndConfig();
		DataManager.getInstance();
		Util.printSection("IDFactory");
		IDFactory.getInstance();
		for (int i = 0; i < worldEngines.length; i++) {
			final int index = i;
			ThreadPoolManager.getInstance().execute(new Runnable() {
				public void run() {
					worldEngines[index].load(progressLatch2);
				}
			});
		}
		try {
			progressLatch2.await();
		}
		catch (InterruptedException e1) {
		}
		Util.printSection("Zone");
		ZoneService.getInstance().load(null);
		Util.printSection("World");
		World.getInstance();
		Util.printSection("Luna Shop initialization");
		LunaShopService.getInstance().init();
		Util.printSection("Shugo Sweep initialization");
		ShugoSweepService.getInstance().init();
		Util.printSection("Drops");
		DropRegistrationService.getInstance();
		GameServer gs = new GameServer();
		DAOManager.getDAO(PlayerDAO.class).setPlayersOffline(false);		
		BannedMacManager.getInstance();
		Util.printSection("Cleaning");
 		DatabaseCleaningService.getInstance();
		AbyssRankCleaningService.getInstance();
		Util.printSection("Geo Data");
		GeoService.getInstance().initializeGeo();
		System.gc();
		for (int i = 0; i < parallelEngines.length; i++) {
			final int index = i;
			ThreadPoolManager.getInstance().execute(new Runnable() {
				public void run() {
					parallelEngines[index].load(progressLatch);
				}
			});
		}
		try {
			progressLatch.await();
		}
		catch (InterruptedException e1) {
		}
		//Siege
		Util.printSection("Siege Location Data");
		SiegeService.getInstance().initSiegeLocations();
		//Base
		Util.printSection("Base Location Data");
		BaseService.getInstance().initBaseLocations();
		//Outpost
		Util.printSection("Outpost Location Data");
		OutpostService.getInstance().initOutpostLocations();
		//Dynamic Rift
		Util.printSection("Dynamic Rift Location Data");
		DynamicRiftService.getInstance().initDynamicRift();
		DynamicRiftService.getInstance().initDynamicRiftLocations();
		//Rift
		Util.printSection("Rift Location Data");
		RiftService.getInstance().initRiftLocations();
		//Conquest/Offering
		Util.printSection("Conquest/Offering Location Data");
		ConquestService.getInstance().initOffering();
		ConquestService.getInstance().initConquestLocations();
		Util.printSection("Spawns");
		SpawnEngine.spawnAll();
		RiftService.getInstance().initRifts();
		TemporarySpawnEngine.spawnAll();
		Util.printSection("Limits");
		LimitedItemTradeService.getInstance().start();
		if (CustomConfig.LIMITS_ENABLED) {
			PlayerLimitService.getInstance().scheduleUpdate();
		}
		GameTimeManager.startClock();
		Util.printSection("Siege Schedule initialization");
		SiegeService.getInstance().initSieges();
		Util.printSection("TaskManagers");
		SeasonRankingUpdateService.getInstance().onStart();
		PacketBroadcaster.getInstance();
		GameTimeService.getInstance();
		AnnouncementService.getInstance();
		DebugService.getInstance();
		WeatherService.getInstance();
		BrokerService.getInstance();
		Influence.getInstance();
		ExchangeService.getInstance();
		PeriodicSaveService.getInstance();
		PetitionService.getInstance();
		if (AIConfig.SHOUTS_ENABLE) {
			NpcShoutsService.getInstance();
		}
		InstanceService.load();
		FlyRingService.getInstance();
		CuringZoneService.getInstance();
		HTMLCache.getInstance();
		if (RankingConfig.TOP_RANKING_UPDATE_SETTING) {
			AbyssRankUpdateService.getInstance().scheduleUpdateHour();
		} else {
			AbyssRankUpdateService.getInstance().scheduleUpdateMinute();
		}
		//Reward Weekly Manager.
		AbyssRankUpdateService.getInstance().initDailyGpReduceManager();
		TaskManagerFromDB.getInstance();
		if (SiegeConfig.SIEGE_SHIELD_ENABLED) {
			ShieldService.getInstance().spawnAll();
		}
		//Ashunatal Dredgion.
		if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("[Ashunatal] Dredgion");
			AshunatalService.getInstance().initAshunatal();
		}
		//Battlefield.
		if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("Idgel Dome");
			IdgelDomeService.getInstance().initIdgelDome();
		} if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("Illumiel Battlefield");
			IllumielBrawlService.getInstance().initIllumielBrawl();
		} if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			Util.printSection("Kamar Battlefield");
			KamarBattlefieldService.getInstance().initKamarBattlefield();
		}
		//Custom
		if (CustomConfig.ENABLE_REWARD_SERVICE) {
			RewardService.getInstance();
		}
		if (EventsConfig.ENABLE_EVENT_SERVICE) {
			EventService.getInstance().start();
		} if (WeddingsConfig.WEDDINGS_ENABLE) {
			WeddingService.getInstance();
		}
		if (EventsConfig.ENABLE_BOOST_EVENT) {
			BoostEventService.getInstance().onStart();
		}
		EventWindowService.getInstance().initialize();
		AdminService.getInstance();
		MaintenanceTask.getInstance();
		HousingBidService.getInstance().start();
		TownService.getInstance();
		ChallengeTaskService.getInstance();
		HotspotTeleportService.getInstance();
		Util.printSection("Protector/Conqueror initialization");
		ProtectorConquerorService.getInstance().initSystem();
		Util.printSection("Dispute Land initialization");
		DisputeLandService.getInstance().initDisputeLand();
		BaseService.getInstance().initBases();
		OutpostService.getInstance().initOutposts();
		MinionService.getInstance().init();
		Util.printSection("Auto Group initialization");
		FireTempleService.getInstance().initFireTemple();
		PvPSoloArenaService.getInstance().initPvPSoloArena();
		Util.printSection("Lugbug Mission initialization");
		AchievementService.getInstance().init();
		Util.printSection("Renown initialization");
		PlayerFameService.getInstance().init();
		Util.printSection("Lumiel Transmutation initialization");
		LumielTransformService.getInstance().init();
		Util.printSection("Item Collection initialization");
		PlayerCollectionService.getInstance().init();
		Util.printSection("System");
		AEVersions.printFullVersionInfo();
		WebshopService.getInstance();
		RestartService.getInstance().onStart();
		QuestUpdateService.getInstance().onStart();
		WorldPlayTimeService.getInstance().onStart();
		System.gc();
		AEInfos.printAllInfos();
		Util.printSection("GameServer Log");
		log.info("Encom Emu Started In " + (System.currentTimeMillis() - start) / 1000 + " Seconds.");
		gs.startServers();
		Runtime.getRuntime().addShutdownHook(ShutdownHook.getInstance());
		if (GSConfig.ENABLE_RATIO_LIMITATION) {
			addStartupHook(new StartupHook() {
				@Override
				public void onStartup() {
					lock.lock();
					try {
						ASMOS_COUNT = DAOManager.getDAO(PlayerDAO.class).getCharacterCountForRace(Race.ASMODIANS);
						ELYOS_COUNT = DAOManager.getDAO(PlayerDAO.class).getCharacterCountForRace(Race.ELYOS);
						computeRatios();
					}
					catch (Exception e) {
					}
					finally {
						lock.unlock();
					}
					displayRatios(false);
				}
			});
		}
		onStartup();
	}
	
	private void startServers() {
		Util.printSection("Starting Network");
		NioServer nioServer = new NioServer(NetworkConfig.NIO_READ_WRITE_THREADS, new ServerCfg(NetworkConfig.GAME_BIND_ADDRESS, NetworkConfig.GAME_PORT, "Game Connections", new GameConnectionFactoryImpl()));
		LoginServer ls = LoginServer.getInstance();
		ChatServer cs = ChatServer.getInstance();
		ls.setNioServer(nioServer);
		cs.setNioServer(nioServer);
		nioServer.connect();
		ls.connect();
		if (GSConfig.ENABLE_CHAT_SERVER)
			cs.connect();
	}
	
	private static void initUtilityServicesAndConfig() {
		Thread.setDefaultUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
		if (JavaAgentUtils.isConfigured()) {
			log.info("JavaAgent [Callback Support] is configured.");
		}
		CronService.initSingleton(ThreadPoolManagerRunnableRunner.class);
		Config.load();
		DateTimeUtil.init();
		Util.printSection("DataBase");
		DatabaseFactory.init();
		DAOManager.init();
		Util.printSection("Threads");
		ThreadConfig.load();
		ThreadPoolManager.getInstance();
	}
	
	public static synchronized void addStartupHook(StartupHook hook) {
		if (startUpHooks != null)
			startUpHooks.add(hook);
		else
			hook.onStartup();
	}
	
	private synchronized static void onStartup() {
		final Set<StartupHook> startupHooks = startUpHooks;
		startUpHooks = null;
		for (StartupHook hook : startupHooks)
			hook.onStartup();
	}
	
	public static void updateRatio(Race race, int i) {
		lock.lock();
		try {
			switch (race) {
				case ASMODIANS:
					ASMOS_COUNT += i;
				break;
				case ELYOS:
					ELYOS_COUNT += i;
				break;
				default:
					break;
			}
			computeRatios();
		} catch (Exception e) {
		} finally {
			lock.unlock();
		}
		displayRatios(true);
	}
	
	private static void computeRatios() {
		if (ASMOS_COUNT <= GSConfig.RATIO_MIN_CHARACTERS_COUNT && ELYOS_COUNT <= GSConfig.RATIO_MIN_CHARACTERS_COUNT) {
			ASMOS_RATIO = GameServer.ELYOS_RATIO = 50.0;
		} else {
			ASMOS_RATIO = ASMOS_COUNT * 100.0 / (ASMOS_COUNT + ELYOS_COUNT);
			ELYOS_RATIO = ELYOS_COUNT * 100.0 / (ASMOS_COUNT + ELYOS_COUNT);
		}
	}
	
	private static void displayRatios(boolean updated) {
		log.info("FACTIONS RATIO " + (updated ? "UPDATED " : "") + ": E " + String.format("%.1f", GameServer.ELYOS_RATIO) + " % / A " + String.format("%.1f", GameServer.ASMOS_RATIO) + " %");
	}
	
	public static double getRatiosFor(Race race) {
		switch (race) {
		case ASMODIANS:
			return ASMOS_RATIO;
		case ELYOS:
			return ELYOS_RATIO;
		default:
			return 0.0;
		}
	}
	
	public static int getCountFor(Race race) {
		switch (race) {
		case ASMODIANS:
			return ASMOS_COUNT;
		case ELYOS:
			return ELYOS_COUNT;
		default:
			return 0;
		}
	}
	
	public static abstract interface StartupHook {
		public abstract void onStartup();
	}
}