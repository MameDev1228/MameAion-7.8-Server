package playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.player.MameFfaService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class cmd_ffa extends PlayerCommand {

    public cmd_ffa() {
        super("ffa");
    }

    @Override
    public void execute(Player player, String... params) {
        if (params.length == 0 || "status".equalsIgnoreCase(params[0])) {
            PacketSendUtility.sendMessage(player, "FFA: " + (MameFfaService.isInFfa(player) ? "参加中" : "未参加") + " / 参加者 " + MameFfaService.getActiveCount() + "人");
            PacketSendUtility.sendMessage(player, "使い方: .ffa join / .ffa leave / .ffa status");
            return;
        }
        if ("join".equalsIgnoreCase(params[0])) {
            MameFfaService.join(player);
            return;
        }
        if ("leave".equalsIgnoreCase(params[0]) || "exit".equalsIgnoreCase(params[0])) {
            MameFfaService.leave(player);
            return;
        }
        PacketSendUtility.sendMessage(player, "使い方: .ffa join / .ffa leave / .ffa status");
    }

    @Override
    public void onFail(Player player, String message) {
    }
}
