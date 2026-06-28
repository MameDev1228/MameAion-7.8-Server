/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package admincommands;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.player.PlayerSyncService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Re-sends the minimal player visibility/stat packets used to diagnose kisk
 * revive, teleport and air-move desyncs.
 */
public class Resync extends AdminCommand {

	public Resync() {
		super("resync");
	}

	@Override
	public void execute(Player admin, String... params) {
		Player target = resolveTarget(admin, params);
		if (target == null) {
			PacketSendUtility.sendMessage(admin, "syntax //resync [self|target|characterName]");
			return;
		}
		resync(target);
		PacketSendUtility.sendMessage(admin, "Resync sent for " + target.getName() + ".");
		if (target != admin) {
			PacketSendUtility.sendMessage(target, "Your visibility/stat state was resynchronized by " + admin.getName() + ".");
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

	private void resync(Player player) {
		PlayerSyncService.resendVisibilityAndStats(player, true);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //resync [self|target|characterName]");
	}
}
