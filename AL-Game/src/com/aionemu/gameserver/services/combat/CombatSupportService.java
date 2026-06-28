/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * Server-side implementation for the 7.x Combat Support client opcode.
 *
 * Retail 7.x sends this opcode from the combat-support/auto-combat panel. The
 * exact payload still differs between client builds, so the client packet keeps
 * the variable payload, while this service provides the first safe working
 * feature set:
 *
 *  - stores the requested profile payload for audit/debugging
 *  - supports enable / disable / toggle / status actions
 *  - when enabled, repeatedly performs the normal server-side attack against the
 *    player's current selected target, using the same restrictions as CM_ATTACK
 *  - automatically stops when the player logs out, dies, despawns, or no longer
 *    exists in World
 *
 * This intentionally does not move the player or auto-select targets. That keeps
 * the first implementation PvP/PvE safe and avoids introducing bot-like pathing
 * logic before the 7.8 payload is fully verified.
 */
public class CombatSupportService {

	private static final Logger log = LoggerFactory.getLogger(CombatSupportService.class);
	private final Map<Integer, CombatSupportSession> sessions = new ConcurrentHashMap<Integer, CombatSupportSession>();

	public void handlePacket(Player player, int action, List<Integer> payload) {
		if (player == null) {
			return;
		}
		List<Integer> safePayload = payload == null ? Collections.<Integer>emptyList() : new ArrayList<Integer>(payload);
		switch (action) {
			case 0: // open/status request
				sendStatus(player);
				break;
			case 1: // enable/start
				start(player, safePayload);
				break;
			case 2: // disable/stop
			case 5:
				stop(player, true);
				break;
			case 3: // toggle
				if (isEnabled(player)) {
					stop(player, true);
				}
				else {
					start(player, safePayload);
				}
				break;
			case 4: // settings/profile update; do not force-start unless already enabled
				updateProfile(player, safePayload);
				sendStatus(player);
				break;
			default:
				updateProfile(player, safePayload);
				log.info("CombatSupport unverified action player=" + player.getName() + " action=" + action + " payload=" + safePayload);
				sendStatus(player);
				break;
		}
	}

	public boolean isEnabled(Player player) {
		return player != null && sessions.containsKey(player.getObjectId()) && sessions.get(player.getObjectId()).isEnabled();
	}

	public void onLogout(Player player) {
		stop(player, false);
	}

	private void start(Player player, List<Integer> payload) {
		stop(player, false);
		CombatSupportSession session = new CombatSupportSession(player.getObjectId(), payload);
		sessions.put(player.getObjectId(), session);
		session.task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				tick(session.playerObjectId);
			}
		}, 500, 1100);
		PacketSendUtility.sendMessage(player, "Combat Support: ON");
		log.info("CombatSupport enabled player={} payload={}", player.getName(), payload);
	}

	private void stop(Player player, boolean notify) {
		if (player == null) {
			return;
		}
		CombatSupportSession session = sessions.remove(player.getObjectId());
		if (session != null) {
			session.cancel();
			log.info("CombatSupport disabled player={}", player.getName());
		}
		if (notify) {
			PacketSendUtility.sendMessage(player, "Combat Support: OFF");
		}
	}

	private void updateProfile(Player player, List<Integer> payload) {
		CombatSupportSession session = sessions.get(player.getObjectId());
		if (session != null) {
			session.payload = new ArrayList<Integer>(payload);
			return;
		}
		// Store a disabled profile so status/debug has the last client payload.
		session = new CombatSupportSession(player.getObjectId(), payload);
		session.enabled = false;
		sessions.put(player.getObjectId(), session);
	}

	private void sendStatus(Player player) {
		CombatSupportSession session = sessions.get(player.getObjectId());
		boolean enabled = session != null && session.enabled;
		PacketSendUtility.sendMessage(player, "Combat Support: " + (enabled ? "ON" : "OFF"));
		if (session != null && !session.payload.isEmpty()) {
			log.debug("CombatSupport status player=" + player.getName() + " enabled=" + enabled + " payload=" + session.payload);
		}
	}

	private void tick(int playerObjectId) {
		CombatSupportSession session = sessions.get(playerObjectId);
		if (session == null || !session.enabled) {
			return;
		}
		Player player = World.getInstance().findPlayer(playerObjectId);
		if (player == null || !player.isSpawned() || player.getLifeStats().isAlreadyDead()) {
			if (session != null) {
				session.cancel();
			}
			sessions.remove(playerObjectId);
			return;
		}
		VisibleObject target = player.getTarget();
		if (!(target instanceof Creature)) {
			return;
		}
		Creature creature = (Creature) target;
		if (creature.getLifeStats().isAlreadyDead() || !creature.isSpawned()) {
			return;
		}
		try {
			player.getController().attackTarget(creature, session.nextAttackNo() & 0xFF, 0, 0);
		}
		catch (Exception e) {
			log.warn("CombatSupport tick failed player=" + player.getName() + " target=" + creature.getObjectId(), e);
		}
	}

	private static class CombatSupportSession {
		final int playerObjectId;
		volatile boolean enabled = true;
		volatile List<Integer> payload;
		volatile Future<?> task;
		private int attackNo;

		CombatSupportSession(int playerObjectId, List<Integer> payload) {
			this.playerObjectId = playerObjectId;
			this.payload = new ArrayList<Integer>(payload);
		}

		boolean isEnabled() {
			return enabled;
		}

		int nextAttackNo() {
			attackNo = (attackNo + 1) & 0xFF;
			return attackNo;
		}

		void cancel() {
			enabled = false;
			Future<?> currentTask = task;
			if (currentTask != null) {
				currentTask.cancel(false);
			}
		}
	}

	public static CombatSupportService getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		private static final CombatSupportService INSTANCE = new CombatSupportService();
	}
}
