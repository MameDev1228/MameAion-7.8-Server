package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class GmList extends AdminCommand {

    public GmList() {
        super("gmlist");
    }

    @Override
    public void execute(Player admin, String... params) {
        PacketSendUtility.sendMessage(admin, "===== MameAion GM Commands =====");
        PacketSendUtility.sendMessage(admin, "//tp <player> - GMがプレイヤー位置へ移動");
        PacketSendUtility.sendMessage(admin, "//tpme <player> - プレイヤーをGM位置へ召喚");
        PacketSendUtility.sendMessage(admin, "//tpmap <mapname|mapid> - 主要マップへ移動 / 候補表示");
        PacketSendUtility.sendMessage(admin, "//list - 現在接続中のプレイヤー一覧");
        PacketSendUtility.sendMessage(admin, "//gps - 現在座標表示");
        PacketSendUtility.sendMessage(admin, "//moveto <worldId> <x> <y> <z> - 座標移動");
        PacketSendUtility.sendMessage(admin, "//goto <location> - 既存移動ショートカット");
        PacketSendUtility.sendMessage(admin, "//mameitem <itemId|link> [count] [enchant] - 強化済み支給");
        PacketSendUtility.sendMessage(admin, "//sys shutdown 60 10 - 安全シャットダウン");
        PacketSendUtility.sendMessage(admin, "既存コマンド一覧はパッチ内 tools/GM_COMMAND_LIST.txt も参照。");
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "syntax //gmlist");
    }
}
