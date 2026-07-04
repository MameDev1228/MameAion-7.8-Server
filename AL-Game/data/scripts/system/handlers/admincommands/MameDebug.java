package admincommands;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MameClientCompatDebug;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class MameDebug extends AdminCommand {

    public MameDebug() {
        super("mamedebug");
    }

    @Override
    public void execute(Player admin, String... params) {
        if (params.length == 0) {
            help(admin);
            return;
        }
        String mode = params[0].toLowerCase();
        if ("on".equals(mode)) {
            Player target = resolveTarget(admin);
            MameClientCompatDebug.enableFor(target);
            PacketSendUtility.sendMessage(admin, "Mame CC2 debug ON: " + MameClientCompatDebug.getTargetText());
            PacketSendUtility.sendMessage(admin, MameClientCompatDebug.statsSummary(target));
        } else if ("all".equals(mode)) {
            MameClientCompatDebug.enableAllPlayers();
            PacketSendUtility.sendMessage(admin, "Mame CC2 debug ON: ALL players");
        } else if ("off".equals(mode)) {
            MameClientCompatDebug.disable();
            PacketSendUtility.sendMessage(admin, "Mame CC2 debug OFF");
        } else if ("stats".equals(mode)) {
            Player target = resolveTarget(admin);
            MameClientCompatDebug.logStats(target, "manual");
            PacketSendUtility.sendMessage(admin, MameClientCompatDebug.statsSummary(target));
        } else if ("audit".equals(mode) || "equipment".equals(mode) || "weapon".equals(mode)) {
            Player target = resolveTarget(admin);
            MameClientCompatDebug.logAudit(target, "manual");
            PacketSendUtility.sendMessage(admin, MameClientCompatDebug.statsSummary(target));
            PacketSendUtility.sendMessage(admin, MameClientCompatDebug.equipmentSummary(target));
        } else if ("status".equals(mode)) {
            PacketSendUtility.sendMessage(admin, "Mame CC2 debug: " + MameClientCompatDebug.getTargetText() + " statsMode=" + MameClientCompatDebug.getStatsInfoModeName() + " dmgMode=" + MameClientCompatDebug.getPhysicalSkillDamageModeName());
        } else if ("statsmode".equals(mode)) {
            if (params.length < 2) {
                PacketSendUtility.sendMessage(admin, "Usage: //mamedebug statsmode 77|base-current|cc2-clean");
                PacketSendUtility.sendMessage(admin, "Current statsMode=" + MameClientCompatDebug.getStatsInfoModeName());
                return;
            }
            if (!MameClientCompatDebug.setStatsInfoMode(params[1])) {
                PacketSendUtility.sendMessage(admin, "Unknown statsMode: " + params[1] + " use 77, base-current, or cc2-clean");
                return;
            }
            Player target = resolveTarget(admin);
            PacketSendUtility.sendMessage(admin, "Mame CC2 statsMode=" + MameClientCompatDebug.getStatsInfoModeName());
            PacketSendUtility.sendPacket(target, new SM_STATS_INFO(target));
        } else if ("dmgmode".equals(mode) || "damagemode".equals(mode)) {
            if (params.length < 2) {
                PacketSendUtility.sendMessage(admin, "Usage: //mamedebug dmgmode legacy  (locked to legacy-initial-source)");
                PacketSendUtility.sendMessage(admin, "Current dmgMode=" + MameClientCompatDebug.getPhysicalSkillDamageModeName());
                return;
            }
            if (!MameClientCompatDebug.setPhysicalSkillDamageMode(params[1])) {
                PacketSendUtility.sendMessage(admin, "Unknown dmgMode: " + params[1] + " use legacy");
                return;
            }
            PacketSendUtility.sendMessage(admin, "Mame CC2 dmgMode=" + MameClientCompatDebug.getPhysicalSkillDamageModeName());
        } else if ("refreshstats".equals(mode) || "sendstats".equals(mode)) {
            Player target = resolveTarget(admin);
            MameClientCompatDebug.logStats(target, "manual-refresh");
            PacketSendUtility.sendPacket(target, new SM_STATS_INFO(target));
            PacketSendUtility.sendMessage(admin, "SM_STATS_INFO sent to " + target.getName() + " statsMode=" + MameClientCompatDebug.getStatsInfoModeName());
        } else {
            help(admin);
        }
    }

    private Player resolveTarget(Player admin) {
        VisibleObject target = admin.getTarget();
        if (target instanceof Player) {
            return (Player) target;
        }
        return admin;
    }

    private void help(Player admin) {
        PacketSendUtility.sendMessage(admin, "//mamedebug on  - enable for selected player, or yourself if no player target");
        PacketSendUtility.sendMessage(admin, "//mamedebug stats - print selected/self server-side stats");
        PacketSendUtility.sendMessage(admin, "//mamedebug audit - print stats + equipped weapon template values");
        PacketSendUtility.sendMessage(admin, "//mamedebug off - disable");
        PacketSendUtility.sendMessage(admin, "//mamedebug statsmode 77|base-current|cc2-clean - switch SM_STATS_INFO mode");
        PacketSendUtility.sendMessage(admin, "//mamedebug dmgmode legacy - damage formula is locked to legacy-initial-source");
        PacketSendUtility.sendMessage(admin, "//mamedebug refreshstats - resend SM_STATS_INFO to selected/self");
    }
}
