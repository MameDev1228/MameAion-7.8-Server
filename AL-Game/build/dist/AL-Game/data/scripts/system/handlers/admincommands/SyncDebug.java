/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package admincommands;

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.player.PlayerSyncService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Prints runtime state useful for diagnosing 7.8 stat/visibility/air desyncs.
 */
public class SyncDebug extends AdminCommand {

	public SyncDebug() {
		super("syncdebug");
	}

	@Override
	public void execute(Player admin, String... params) {
		Player target = resolveTarget(admin, params);
		if (target == null) {
			PacketSendUtility.sendMessage(admin, "syntax //syncdebug [self|target|characterName] [resync|ground]");
			return;
		}

		if (params != null && params.length > 1) {
			if ("ground".equalsIgnoreCase(params[1])) {
				PlayerSyncService.prepareForGroundReviveTeleport(target);
				PacketSendUtility.sendMessage(admin, "Ground/air transient state cleared for " + target.getName() + ".");
			}
			else if ("resync".equalsIgnoreCase(params[1])) {
				PlayerSyncService.resendVisibilityAndStats(target, true);
				PacketSendUtility.sendMessage(admin, "Visibility/stat resync sent for " + target.getName() + ".");
			}
		}

		List<String> lines = PlayerSyncService.buildSyncDebugLines(target);
		for (String line : lines) {
			PacketSendUtility.sendMessage(admin, line);
		}
	}

	private Player resolveTarget(Player admin, String... params) {
		if (params == null || params.length == 0 || "self".equalsIgnoreCase(params[0])) {
			return admin;
		}
		if ("target".equalsIgnoreCase(params[0])) {
			VisibleObject visibleTarget = admin.getTarget();
			return visibleTarget instanceof Player ? (Player) visibleTarget : null;
		}
		return World.getInstance().findPlayer(Util.convertName(params[0]));
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //syncdebug [self|target|characterName] [resync|ground]");
	}
}
