/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.instance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.NeviwindCanyonReward;
import com.aionemu.gameserver.model.instance.playerreward.NeviwindCanyonPlayerReward;
import com.aionemu.gameserver.model.templates.npc.NpcRank;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NEVIWIND_CANYON;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapInstanceFactory;

/**
 * Working server-side flow for the 7.x Neviwind Canyon client opcode.
 *
 * Phase8 adds exit/reward handling on top of the Phase6 register/enter pass.
 * Retail matchmaking/scoring can still be layered later, but players can now
 * enter, receive score packets, leave, and get a deterministic reward instead
 * of being trapped in an empty instance.
 */
public class NeviwindCanyonService {

	private static final Logger log = LoggerFactory.getLogger(NeviwindCanyonService.class);
	public static final int MAP_ID = 302350000;
	private static final float ENTER_X = 1108.7395f;
	private static final float ENTER_Y = 746.5648f;
	private static final float ENTER_Z = 336.33923f;
	private static final byte ENTER_H = 60;
	private static final int NORMAL_REWARD_CHEST = 188057336;
	private static final int SPECIAL_REWARD_CHEST = 188057337;
	private static final int PVP_KILL_POINTS = 250;
	private static final int PVP_VICTIM_POINTS_LOSS = -50;
	private static final int OBJECTIVE_NORMAL_POINTS = 120;
	private static final int OBJECTIVE_NAMED_POINTS = 350;
	private static final int OBJECTIVE_BOSS_POINTS = 700;

	private final Map<Integer, Long> registeredPlayers = new ConcurrentHashMap<Integer, Long>();
	private final Map<Integer, Session> activeSessionsByPlayer = new ConcurrentHashMap<Integer, Session>();
	private final Map<Integer, NeviwindCanyonReward> rewardsByInstance = new ConcurrentHashMap<Integer, NeviwindCanyonReward>();

	public void handle(Player player, int action, int[] payload) {
		if (player == null) {
			return;
		}
		switch (action) {
			case 0: // status/open panel
			case 3:
				sendStatus(player, action);
				break;
			case 4: // send offer / register quick
			case 5: // apply group / register
				register(player, action);
				break;
			case 6: // accept entry
			case 9:
			case 10:
				enter(player);
				break;
			case 8: // cancel queue
				cancel(player);
				break;
			case 11: // claim reward / leave variants observed on 7.x UI
			case 12:
			case 13:
			case 14:
				rewardAndExit(player, action);
				break;
			default:
				log.info("Neviwind unverified action player=" + player.getName() + " action=" + action + " payload=" + payloadToString(payload));
				sendStatus(player, action);
				break;
		}
	}

	private void sendStatus(Player player, int action) {
		PacketSendUtility.sendPacket(player, new SM_NEVIWIND_CANYON(action));
		if (activeSessionsByPlayer.containsKey(player.getObjectId())) {
			PacketSendUtility.sendMessage(player, "Neviwind Canyon: in progress");
		}
		else {
			PacketSendUtility.sendMessage(player, registeredPlayers.containsKey(player.getObjectId()) ? "Neviwind Canyon: registered" : "Neviwind Canyon: available");
		}
	}

	private void register(Player player, int action) {
		if (player.isInInstance()) {
			PacketSendUtility.sendMessage(player, "Neviwind Canyon: you are already in an instance.");
			return;
		}
		registeredPlayers.put(player.getObjectId(), System.currentTimeMillis());
		PacketSendUtility.sendPacket(player, new SM_NEVIWIND_CANYON(action));
		PacketSendUtility.sendMessage(player, "Neviwind Canyon: registered. Press enter in the UI to join.");
		log.info("Neviwind registered player={} action={}", player.getName(), action);
	}

	private void cancel(Player player) {
		registeredPlayers.remove(player.getObjectId());
		PacketSendUtility.sendPacket(player, new SM_NEVIWIND_CANYON(8));
		PacketSendUtility.sendMessage(player, "Neviwind Canyon: registration cancelled.");
	}

	private void enter(final Player player) {
		if (player.isInInstance()) {
			PacketSendUtility.sendMessage(player, "Neviwind Canyon: you are already in an instance.");
			return;
		}
		registeredPlayers.remove(player.getObjectId());
		WorldMapInstance instance = createInstance();
		if (instance == null) {
			PacketSendUtility.sendMessage(player, "Neviwind Canyon: map is not available.");
			return;
		}
		NeviwindCanyonReward reward = new NeviwindCanyonReward(MAP_ID, instance.getInstanceId(), instance);
		reward.setInstanceStartTime();
		reward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
		reward.regPlayerReward(player);
		NeviwindCanyonPlayerReward playerReward = reward.getPlayerReward(player.getObjectId());
		if (playerReward != null) {
			playerReward.setRewardAp(reward.AbyssReward(false, false));
			playerReward.setRewardGp(reward.GloryReward(false, false));
			playerReward.setRewardExp(reward.ExpReward(false, false));
			playerReward.setRewardCount(1f);
			playerReward.setBrokenSpinel(NORMAL_REWARD_CHEST);
		}
		rewardsByInstance.put(instance.getInstanceId(), reward);
		activeSessionsByPlayer.put(player.getObjectId(), new Session(instance.getInstanceId(), System.currentTimeMillis()));
		PacketSendUtility.sendPacket(player, new SM_NEVIWIND_CANYON(6));
		reward.portToPosition(player);
		PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(2, reward.getTime(), reward, player.getObjectId()));
		log.info("Neviwind enter player={} instance={}", player.getName(), instance.getInstanceId());
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				Session session = activeSessionsByPlayer.get(player.getObjectId());
				if (session != null && player.isOnline() && player.getWorldId() == MAP_ID) {
					rewardAndExit(player, 99);
				}
			}
		}, 30 * 60 * 1000);
	}

	private void rewardAndExit(Player player, int action) {
		Session session = activeSessionsByPlayer.remove(player.getObjectId());
		if (session == null) {
			PacketSendUtility.sendPacket(player, new SM_NEVIWIND_CANYON(action));
			if (player.getWorldId() == MAP_ID) {
				TeleportService2.moveToBindLocation(player, true);
			}
			return;
		}
		NeviwindCanyonReward reward = rewardsByInstance.get(session.instanceId);
		int ap = 1031;
		int gp = 75;
		int exp = 5000;
		int chest = NORMAL_REWARD_CHEST;
		if (reward != null) {
			NeviwindCanyonPlayerReward playerReward = reward.getPlayerReward(player.getObjectId());
			if (playerReward != null) {
				long elapsed = System.currentTimeMillis() - session.startTime;
				boolean fullParticipation = elapsed >= 10 * 60 * 1000;
				boolean isWin = reward.getWinnerRaceByScore() == player.getRace();
				boolean objectiveCompleted = reward.getObjectiveKillsByRace(player.getRace()).intValue() > 0;
				ap = fullParticipation ? reward.AbyssReward(isWin, objectiveCompleted) : Math.max(500, playerReward.getPoints() / 2);
				gp = fullParticipation ? reward.GloryReward(isWin, objectiveCompleted) : 25;
				exp = fullParticipation ? reward.ExpReward(isWin, objectiveCompleted) : 2500;
				chest = isWin && fullParticipation ? SPECIAL_REWARD_CHEST : NORMAL_REWARD_CHEST;
				playerReward.setRewardAp(ap);
				playerReward.setRewardGp(gp);
				playerReward.setRewardExp(exp);
				playerReward.setRewardCount(1f);
				playerReward.setBrokenSpinel(chest);
				reward.setWinnerRace(reward.getWinnerRaceByScore());
				reward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(5, reward.getTime(), reward, player.getObjectId()));
			}
		}
		AbyssPointsService.addAp(player, ap);
		AbyssPointsService.addGp(player, gp);
		player.getCommonData().addExp(RewardType.QUEST.calcReward(player, exp), RewardType.QUEST);
		if (com.aionemu.gameserver.dataholders.DataManager.ITEM_DATA.getItemTemplate(chest) != null) {
			ItemService.addItem(player, chest, 1L);
		}
		PacketSendUtility.sendPacket(player, new SM_NEVIWIND_CANYON(action));
		PacketSendUtility.sendMessage(player, "Neviwind Canyon reward: AP " + ap + ", GP " + gp + ".");
		if (player.getWorldId() == MAP_ID) {
			TeleportService2.moveToBindLocation(player, true);
		}
	}


	/**
	 * Called from PvpService after the normal AP/quest reward flow. Keeps the
	 * Neviwind scoreboard moving instead of only showing a static start/end UI.
	 */
	public void onPlayerKill(Player winner, Player victim) {
		if (winner == null || victim == null || winner == victim) {
			return;
		}
		if (winner.getWorldId() != MAP_ID || victim.getWorldId() != MAP_ID) {
			return;
		}
		Session winnerSession = activeSessionsByPlayer.get(winner.getObjectId());
		Session victimSession = activeSessionsByPlayer.get(victim.getObjectId());
		if (winnerSession == null || victimSession == null || winnerSession.instanceId != victimSession.instanceId) {
			return;
		}
		NeviwindCanyonReward reward = rewardsByInstance.get(winnerSession.instanceId);
		if (reward == null) {
			return;
		}
		NeviwindCanyonPlayerReward winnerReward = reward.getPlayerReward(winner.getObjectId());
		NeviwindCanyonPlayerReward victimReward = reward.getPlayerReward(victim.getObjectId());
		if (winnerReward == null) {
			reward.regPlayerReward(winner);
			winnerReward = reward.getPlayerReward(winner.getObjectId());
		}
		if (victimReward == null) {
			reward.regPlayerReward(victim);
			victimReward = reward.getPlayerReward(victim.getObjectId());
		}
		if (winnerReward != null) {
			winnerReward.addPvPKillToPlayer();
			winnerReward.addPoints(PVP_KILL_POINTS);
		}
		if (victimReward != null) {
			victimReward.addPoints(PVP_VICTIM_POINTS_LOSS);
		}
		reward.addPvpKillsByRace(winner.getRace(), 1);
		reward.addPointsByRace(winner.getRace(), PVP_KILL_POINTS);
		reward.addPointsByRace(victim.getRace(), PVP_VICTIM_POINTS_LOSS);
		reward.setWinnerRace(reward.getWinnerRaceByScore());
		sendScoreUpdate(reward, winner.getObjectId());
	}

	/**
	 * Called from NpcController for Neviwind map NPC deaths. Retail objectives are
	 * data-driven, but until the full client table is mapped we score robustly by
	 * NPC rank and named/boss-like template data.
	 */
	public void onObjectiveKill(Player killer, Npc npc) {
		if (killer == null || npc == null || killer.getWorldId() != MAP_ID || npc.getWorldId() != MAP_ID) {
			return;
		}
		Session session = activeSessionsByPlayer.get(killer.getObjectId());
		if (session == null) {
			return;
		}
		NeviwindCanyonReward reward = rewardsByInstance.get(session.instanceId);
		if (reward == null) {
			return;
		}
		NeviwindCanyonPlayerReward playerReward = reward.getPlayerReward(killer.getObjectId());
		if (playerReward == null) {
			reward.regPlayerReward(killer);
			playerReward = reward.getPlayerReward(killer.getObjectId());
		}
		int points = getObjectivePoints(npc);
		if (playerReward != null) {
			playerReward.addMonsterKillToPlayer();
			playerReward.addPoints(points);
		}
		reward.addObjectiveKillsByRace(killer.getRace(), 1);
		reward.addPointsByRace(killer.getRace(), points);
		reward.setWinnerRace(reward.getWinnerRaceByScore());
		sendScoreUpdate(reward, killer.getObjectId());
		if (reward.hasCapPoints()) {
			reward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
		}
	}

	public void onNpcKilledByCreature(Creature attacker, Npc npc) {
		Player killer = attacker instanceof Player ? (Player) attacker : attacker != null && attacker.getMaster() instanceof Player ? (Player) attacker.getMaster() : null;
		onObjectiveKill(killer, npc);
	}

	private int getObjectivePoints(Npc npc) {
		NpcRank rank = npc.getObjectTemplate() != null ? npc.getObjectTemplate().getRank() : null;
		if (rank == NpcRank.MASTER || rank == NpcRank.VETERAN) {
			return OBJECTIVE_BOSS_POINTS;
		}
		if (rank == NpcRank.EXPERT) {
			return OBJECTIVE_NAMED_POINTS;
		}
		return OBJECTIVE_NORMAL_POINTS;
	}

	private void sendScoreUpdate(NeviwindCanyonReward reward, Integer objectId) {
		if (reward == null) {
			return;
		}
		reward.sendPacket(10, objectId);
		reward.sendPacket(6, objectId);
		reward.sendPacket(7, objectId);
	}

	private WorldMapInstance createInstance() {
		WorldMap map = World.getInstance().getWorldMap(MAP_ID);
		if (map == null) {
			log.warn("Neviwind Canyon map {} is not loaded", MAP_ID);
			return null;
		}
		int nextInstanceId = map.getNextInstanceId();
		WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId);
		map.addInstance(nextInstanceId, worldMapInstance);
		SpawnEngine.spawnInstance(MAP_ID, worldMapInstance.getInstanceId(), (byte) 0);
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);
		return worldMapInstance;
	}

	private String payloadToString(int[] payload) {
		if (payload == null || payload.length == 0) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < payload.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(payload[i]);
		}
		return sb.append(']').toString();
	}

	public static NeviwindCanyonService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class Session {
		private final int instanceId;
		private final long startTime;

		private Session(int instanceId, long startTime) {
			this.instanceId = instanceId;
			this.startTime = startTime;
		}
	}

	private static class SingletonHolder {
		private static final NeviwindCanyonService INSTANCE = new NeviwindCanyonService();
	}
}
