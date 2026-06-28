/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.player;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANNEL_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Centralized 7.8 runtime sync helpers used while stabilizing revive, teleport,
 * air-move and stat packets on the JDK25 branch.
 */
public final class PlayerSyncService {

	private PlayerSyncService() {
	}

	/**
	 * Location based revive/teleport must not keep transient air movement states
	 * from the death location. Keeping WINDSTREAM/FLIGHT_TELEPORT/FLYING while
	 * moving to a kisk or bind point is a common cause of invisible objects until
	 * the player changes region again.
	 */
	public static void prepareForGroundReviveTeleport(Player player) {
		if (player == null) {
			return;
		}
		player.unsetPlayerMode(PlayerMode.WINDSTREAM);
		player.getMoveController().abortMove();
		// DEAD/CHAIR/FLIGHT_TELEPORT share bit flags with FLYING in this core,
		// so do not rely on FlyController.endFly() during revive. Force a clean
		// active ground state before any location based respawn/teleport.
		player.setState(CreatureState.ACTIVE.getId());
		player.setFlyState(0);
		player.setIsFlyingBeforeDeath(false);
		player.getLifeStats().triggerFpRestore();
		player.getGameStats().updateStatsAndSpeedVisually();
	}

	/**
	 * Re-send the minimal packets that repair the most common visibility/stat
	 * desyncs without changing the player's position.
	 */
	public static void resendVisibilityAndStats(Player player, boolean clearKnownList) {
		if (player == null) {
			return;
		}
		if (clearKnownList) {
			player.clearKnownlist();
		}
		if (player.getPosition() != null) {
			PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		}
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		player.getEffectController().updatePlayerEffectIcons();
		player.updateKnownlist();
	}

	public static List<String> buildSyncDebugLines(Player player) {
		List<String> lines = new ArrayList<String>();
		if (player == null) {
			lines.add("player=null");
			return lines;
		}
		PlayerGameStats gs = player.getGameStats();
		PlayerLifeStats ls = player.getLifeStats();
		lines.add("== SyncDebug: " + player.getName() + " ==");
		lines.add("obj=" + player.getObjectId() + " account=" + (player.getPlayerAccount() != null ? player.getPlayerAccount().getName() : "-") + " level=" + player.getLevel() + " class=" + player.getPlayerClass());
		lines.add("world=" + player.getWorldId() + " instance=" + player.getInstanceId() + " x=" + f(player.getX()) + " y=" + f(player.getY()) + " z=" + f(player.getZ()) + " h=" + player.getHeading() + " spawned=" + player.isSpawned());
		lines.add("state=" + player.getState() + " flyState=" + player.getFlyState() + " flying=" + player.isInState(CreatureState.FLYING) + " gliding=" + player.isInState(CreatureState.GLIDING) + " flightTeleport=" + player.isInState(CreatureState.FLIGHT_TELEPORT) + " windstream=" + player.isInPlayerMode(PlayerMode.WINDSTREAM));
		lines.add("hp=" + ls.getCurrentHp() + "/" + gs.getMaxHp().getCurrent() + " mp=" + ls.getCurrentMp() + "/" + gs.getMaxMp().getCurrent() + " fp=" + ls.getCurrentFp() + "/" + gs.getFlyTime().getCurrent() + " dp=" + player.getCommonData().getDp());
		lines.add("known objects=" + player.getKnownList().getKnownObjects().size() + " players=" + player.getKnownList().getKnownPlayers().size() + " visibleObjects=" + player.getKnownList().getVisibleObjects().size() + " visiblePlayers=" + player.getKnownList().getVisiblePlayers().size());
		lines.add("atk=" + gs.getMainHandPAttack().getCurrent() + " matk=" + gs.getMAttack().getCurrent() + " pdef=" + gs.getPDef().getCurrent() + " mdef=" + gs.getMDef().getCurrent() + " mres=" + gs.getMResist().getCurrent());
		lines.add("pvpAtk=" + gs.getPVPAttack().getCurrent() + " pvpDef=" + gs.getPVPDefense().getCurrent() + " pveAtk=" + gs.getPVEAttack().getCurrent() + " pveDef=" + gs.getPVEDefense().getCurrent());
		lines.add("critDmg phys=" + gs.getStrikeFort().getCurrent() + " magic=" + gs.getSpellFort().getCurrent() + " stunRes=" + stat(gs, StatEnum.STUN_RESISTANCE) + " stunPen=" + stat(gs, StatEnum.STUN_RESISTANCE_PENETRATION));
		return lines;
	}

	private static String f(float value) {
		return String.format(java.util.Locale.US, "%.3f", value);
	}

	private static int stat(PlayerGameStats gs, StatEnum stat) {
		return gs.getStat(stat, 0).getCurrent();
	}
}
