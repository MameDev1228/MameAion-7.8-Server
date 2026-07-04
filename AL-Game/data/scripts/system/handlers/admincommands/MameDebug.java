package admincommands;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MameClientCompatDebug;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;

public class MameDebug extends AdminCommand {

    private static final Logger log = LoggerFactory.getLogger(MameDebug.class);

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
        } else if ("skill".equals(mode)) {
            printSkill(admin, params);
        } else if ("skilldelay".equals(mode) || "cooldown".equals(mode)) {
            printSkillDelayGroup(admin, params);
        } else if ("skillgroup".equals(mode) || "skillcd".equals(mode)) {
            printSkillDelayGroupBySkill(admin, params);
        } else if ("skillpenalty".equals(mode)) {
            printSkillPenalty(admin, params);
        } else if ("skilleffects".equals(mode) || "skillsignet".equals(mode)) {
            printSkillEffects(admin, params);
        } else if ("skillcooldowns".equals(mode) || "cooldowns".equals(mode)) {
            printPlayerCooldowns(admin, params);
        } else if ("skillaudit".equals(mode)) {
            auditSkillTemplates(admin, params);
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


    private void printSkill(Player admin, String... params) {
        if (params.length < 2) {
            PacketSendUtility.sendMessage(admin, "Usage: //mamedebug skill <skillId>");
            return;
        }
        Integer skillId = parseInt(params[1]);
        if (skillId == null) {
            PacketSendUtility.sendMessage(admin, "skillId must be integer: " + params[1]);
            return;
        }
        SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
        if (template == null) {
            PacketSendUtility.sendMessage(admin, "Skill not found: " + skillId);
            return;
        }
        String line1 = skillLine(template);
        String line2 = skillExtraLine(template);
        PacketSendUtility.sendMessage(admin, line1);
        PacketSendUtility.sendMessage(admin, line2);
        log.info("[MAME-SKILL-AUDIT][SKILL] " + line1 + " | " + line2);
    }

    private void printSkillDelayGroup(Player admin, String... params) {
        if (params.length < 2) {
            PacketSendUtility.sendMessage(admin, "Usage: //mamedebug skilldelay <delayId|cooldownId>");
            return;
        }
        Integer delayId = parseInt(params[1]);
        if (delayId == null) {
            PacketSendUtility.sendMessage(admin, "delayId must be integer: " + params[1]);
            return;
        }
        printDelayGroup(admin, delayId);
    }

    private void printDelayGroup(Player admin, int delayId) {
        ArrayList<Integer> skills = DataManager.SKILL_DATA.getSkillsForDelayId(delayId);
        if (skills == null || skills.isEmpty()) {
            PacketSendUtility.sendMessage(admin, "No skills for delayId/cooldownId=" + delayId);
            return;
        }
        Collections.sort(skills);
        PacketSendUtility.sendMessage(admin, "delayId=" + delayId + " skillCount=" + skills.size() + " first=" + previewSkills(skills, 18));
        int printed = 0;
        for (Integer skillId : skills) {
            SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
            if (template == null) {
                continue;
            }
            if (printed < 10) {
                PacketSendUtility.sendMessage(admin, skillLine(template));
            }
            log.info("[MAME-SKILL-AUDIT][DELAY_GROUP] delayId=" + delayId + " " + skillLine(template) + " | " + skillExtraLine(template));
            printed++;
        }
        if (printed > 10) {
            PacketSendUtility.sendMessage(admin, "... " + (printed - 10) + " more lines written to console.log");
        }
    }


    private void printSkillDelayGroupBySkill(Player admin, String... params) {
        if (params.length < 2) {
            PacketSendUtility.sendMessage(admin, "Usage: //mamedebug skillgroup <skillId>");
            return;
        }
        Integer skillId = parseInt(params[1]);
        if (skillId == null) {
            PacketSendUtility.sendMessage(admin, "skillId must be integer: " + params[1]);
            return;
        }
        SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
        if (template == null) {
            PacketSendUtility.sendMessage(admin, "Skill not found: " + skillId);
            return;
        }
        PacketSendUtility.sendMessage(admin, "Selected: " + skillLine(template));
        printDelayGroup(admin, template.getDelayId());
    }

    private void printSkillPenalty(Player admin, String... params) {
        if (params.length < 2) {
            PacketSendUtility.sendMessage(admin, "Usage: //mamedebug skillpenalty <skillId>");
            return;
        }
        Integer skillId = parseInt(params[1]);
        if (skillId == null) {
            PacketSendUtility.sendMessage(admin, "skillId must be integer: " + params[1]);
            return;
        }
        SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
        if (template == null) {
            PacketSendUtility.sendMessage(admin, "Skill not found: " + skillId);
            return;
        }
        PacketSendUtility.sendMessage(admin, "Selected: " + skillLine(template));
        int penaltyId = template.getPenaltySkillId();
        if (penaltyId <= 0) {
            PacketSendUtility.sendMessage(admin, "No penalty_skill_id on skill=" + skillId);
            return;
        }
        SkillTemplate penalty = DataManager.SKILL_DATA.getSkillTemplate(penaltyId);
        if (penalty == null) {
            PacketSendUtility.sendMessage(admin, "penalty_skill_id=" + penaltyId + " is not loaded");
            return;
        }
        PacketSendUtility.sendMessage(admin, "Penalty: " + skillLine(penalty));
        PacketSendUtility.sendMessage(admin, skillExtraLine(penalty));
        log.info("[MAME-SKILL-AUDIT][PENALTY] source=" + skillLine(template) + " | penalty=" + skillLine(penalty) + " | " + skillExtraLine(penalty));
    }

    private void printSkillEffects(Player admin, String... params) {
        if (params.length < 2) {
            PacketSendUtility.sendMessage(admin, "Usage: //mamedebug skilleffects <skillId>");
            return;
        }
        Integer skillId = parseInt(params[1]);
        if (skillId == null) {
            PacketSendUtility.sendMessage(admin, "skillId must be integer: " + params[1]);
            return;
        }
        SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
        if (template == null) {
            PacketSendUtility.sendMessage(admin, "Skill not found: " + skillId);
            return;
        }
        PacketSendUtility.sendMessage(admin, "Selected: " + skillLine(template));
        if (template.getEffects() == null || template.getEffects().getEffects() == null || template.getEffects().getEffects().isEmpty()) {
            PacketSendUtility.sendMessage(admin, "No effects on skill=" + skillId);
            return;
        }
        int idx = 0;
        for (EffectTemplate effect : template.getEffects().getEffects()) {
            idx++;
            String line = "effect#" + idx + " " + effect.getClass().getSimpleName()
                    + fieldIfPresent(effect, "signet")
                    + fieldIfPresent(effect, "signetid")
                    + fieldIfPresent(effect, "signetlvlstart")
                    + fieldIfPresent(effect, "signetlvl")
                    + fieldIfPresent(effect, "prob")
                    + fieldIfPresent(effect, "removeCd")
                    + fieldIfPresent(effect, "value")
                    + fieldIfPresent(effect, "delta")
                    + fieldIfPresent(effect, "duration2");
            PacketSendUtility.sendMessage(admin, line);
            log.info("[MAME-SKILL-AUDIT][EFFECT] skill=" + skillId + " " + line);
        }
    }

    private void printPlayerCooldowns(Player admin, String... params) {
        Player target = resolveTarget(admin);
        Integer filterDelayId = params.length >= 2 ? parseInt(params[1]) : null;
        Map<Integer, Long> cooldowns = target.getSkillCoolDowns();
        if (cooldowns == null || cooldowns.isEmpty()) {
            PacketSendUtility.sendMessage(admin, "No active skill cooldowns for " + target.getName());
            return;
        }
        long now = System.currentTimeMillis();
        ArrayList<Integer> delayIds = new ArrayList<Integer>(cooldowns.keySet());
        Collections.sort(delayIds);
        int printed = 0;
        for (Integer delayId : delayIds) {
            if (filterDelayId != null && !filterDelayId.equals(delayId)) {
                continue;
            }
            Long end = cooldowns.get(delayId);
            long leftMs = end == null ? 0 : Math.max(0L, end - now);
            ArrayList<Integer> group = DataManager.SKILL_DATA.getSkillsForDelayId(delayId);
            String line = "cooldown delayId=" + delayId + " leftMs=" + leftMs + " leftSec=" + (leftMs / 1000L)
                    + " groupCount=" + (group == null ? 0 : group.size())
                    + " skills=" + (group == null ? "" : previewSkills(group, 12));
            PacketSendUtility.sendMessage(admin, line);
            log.info("[MAME-COOLDOWN][AUDIT] player=" + target.getName() + " " + line);
            printed++;
        }
        if (printed == 0) {
            PacketSendUtility.sendMessage(admin, "No active cooldown for delayId=" + filterDelayId + " on " + target.getName());
        }
    }

    private void auditSkillTemplates(Player admin, String... params) {
        int limit = 50;
        if (params.length >= 2) {
            Integer parsedLimit = parseInt(params[1]);
            if (parsedLimit != null && parsedLimit > 0) {
                limit = parsedLimit;
            }
        }
        List<SkillTemplate> risky = new ArrayList<SkillTemplate>();
        int total = 0;
        int noDelayWithCooldown = 0;
        int noCooldownButDuration = 0;
        int negativeLike = 0;
        int duplicateDelayLarge = 0;
        for (SkillTemplate template : DataManager.SKILL_DATA.getSkillTemplates()) {
            if (template == null) {
                continue;
            }
            total++;
            boolean risk = false;
            if (template.getCooldown() > 0 && template.getDelayId() <= 0) {
                noDelayWithCooldown++;
                risk = true;
            }
            if (template.getCooldown() <= 0 && template.getDuration() > 0 && template.isActive()) {
                noCooldownButDuration++;
            }
            if (template.getCooldown() < 0 || template.getDuration() < 0 || template.getPvpDamage() < 0 || template.getPvpDuration() < 0) {
                negativeLike++;
                risk = true;
            }
            ArrayList<Integer> group = DataManager.SKILL_DATA.getSkillsForDelayId(template.getDelayId());
            if (template.getDelayId() > 0 && group != null && group.size() > 60) {
                duplicateDelayLarge++;
                risk = true;
            }
            if (risk) {
                risky.add(template);
            }
        }
        Collections.sort(risky, new Comparator<SkillTemplate>() {
            @Override
            public int compare(SkillTemplate a, SkillTemplate b) {
                return Integer.compare(a.getSkillId(), b.getSkillId());
            }
        });
        String summary = "SkillAudit total=" + total + " risky=" + risky.size() + " noDelayWithCooldown=" + noDelayWithCooldown
                + " noCooldownButDuration=" + noCooldownButDuration + " negative=" + negativeLike + " largeDelayGroups=" + duplicateDelayLarge;
        PacketSendUtility.sendMessage(admin, summary);
        log.info("[MAME-SKILL-AUDIT][SUMMARY] " + summary);
        int printed = 0;
        for (SkillTemplate template : risky) {
            if (printed < limit) {
                PacketSendUtility.sendMessage(admin, skillLine(template));
            }
            log.warn("[MAME-SKILL-AUDIT][RISK] " + skillLine(template) + " | " + skillExtraLine(template));
            printed++;
        }
        if (printed > limit) {
            PacketSendUtility.sendMessage(admin, "... " + (printed - limit) + " more risky skills written to console.log");
        }
    }

    private String skillLine(SkillTemplate template) {
        return "skill=" + template.getSkillId() + " lvl=" + template.getLvl() + " name=\"" + safe(template.getName()) + "\" cd="
                + template.getCooldown() + " delay=" + template.getDelayId() + " cooldownId=" + template.getCooldownId()
                + " delta=" + template.getCooldownDeltaLv() + " dur=" + template.getDuration()
                + " pvpDmg=" + template.getPvpDamage() + " pvpDur=" + template.getPvpDuration();
    }

    private String skillExtraLine(SkillTemplate template) {
        int effectCount = template.getEffects() != null && template.getEffects().getEffects() != null ? template.getEffects().getEffects().size() : 0;
        return "type=" + template.getType() + "/" + template.getSubType() + " activation=" + template.getActivationAttribute()
                + " group=" + safe(template.getGroup()) + " stack=" + safe(template.getStack()) + " tslot=" + template.getTargetSlot()
                + " chainProb=" + template.getChainSkillProb() + " ammoSpeed=" + template.getAmmoSpeed()
                + " conflict=" + template.getConflictId() + " noremoveAtDie=" + template.isNoRemoveAtDie()
                + " noSaveOnLogout=" + template.isNoSaveOnLogout() + " effects=" + effectCount;
    }

    private String previewSkills(ArrayList<Integer> skills, int limit) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < skills.size() && i < limit; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(skills.get(i));
        }
        if (skills.size() > limit) {
            sb.append("...");
        }
        return sb.toString();
    }

    private String fieldIfPresent(Object obj, String name) {
        Field field = findField(obj.getClass(), name);
        if (field == null) {
            return "";
        }
        try {
            field.setAccessible(true);
            Object value = field.get(obj);
            if (value == null) {
                return " " + name + "=null";
            }
            return " " + name + "=" + String.valueOf(value);
        } catch (Exception e) {
            return " " + name + "=<err>";
        }
    }

    private Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
        PacketSendUtility.sendMessage(admin, "//mamedebug skill <skillId> - print current skill template values");
        PacketSendUtility.sendMessage(admin, "//mamedebug skilldelay <delayId> - print cooldown/delay group");
        PacketSendUtility.sendMessage(admin, "//mamedebug skillgroup <skillId> - print selected skill and its cooldown/delay group");
        PacketSendUtility.sendMessage(admin, "//mamedebug skillpenalty <skillId> - print penalty_skill_id target, if any");
        PacketSendUtility.sendMessage(admin, "//mamedebug skilleffects <skillId> - print effect internals such as signetlvl/remove_cd");
        PacketSendUtility.sendMessage(admin, "//mamedebug skillcooldowns [delayId] - print selected/self active cooldown map");
        PacketSendUtility.sendMessage(admin, "//mamedebug skillaudit [limit] - scan risky cooldown/template rows");
    }
}
