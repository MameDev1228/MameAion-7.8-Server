package com.aionemu.gameserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.WeaponStats;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * Temporary one-character compatibility logger for MameAion75 CC2/KR client packet checks.
 *
 * Enable from game with: //mamedebug on
 * Disable with:          //mamedebug off
 * Print stats with:      //mamedebug stats
 */
public final class MameClientCompatDebug {

    private static final Logger log = LoggerFactory.getLogger(MameClientCompatDebug.class);

    // v45 locks damage to the initial legacy source calculation path.
    // Probe damage modes are disabled so combat cannot accidentally run the temporary
    // soft/boost formulas from v39-v41.
    private static final float POWER_NET_DIVISOR = 100000.0f;
    private static final float CONTEXT_NET_DIVISOR = 10000.0f;

    private static volatile boolean enabled;
    private static volatile int targetObjectId;
    private static volatile String targetName;
    private static volatile boolean logAllPlayers;
    private static volatile long lastStatsLog;

    /**
     * SM_STATS_INFO compatibility mode.
     * 0 = original 7.7 layout/values.
     * 1 = probe only: keep 7.7 packet length/order, but mirror some base values to current values.
     * 2 = CC2 clean display mode: removes fake 1..24 special-state values and swaps the
     *     known 7.5/7.7 base-stat mismatch slots used by the CC2/KR 7.7 profile tooltip.
     */
    private static volatile int statsInfoMode = 2;

    /**
     * Damage formula mode.
     * v44 is locked to the original source formula.
     * The old v39-v41 soft/boost probes are no longer applied from runtime commands.
     */
    private static volatile int physicalSkillDamageMode = 0;


    private MameClientCompatDebug() {
    }

    public static void enableFor(Player player) {
        enabled = true;
        logAllPlayers = false;
        targetObjectId = player != null ? player.getObjectId() : 0;
        targetName = player != null ? player.getName() : null;
        log.info("[MAME-CC2][DEBUG] enabled target=" + describe(player));
    }

    public static void enableAllPlayers() {
        enabled = true;
        logAllPlayers = true;
        targetObjectId = 0;
        targetName = null;
        log.info("[MAME-CC2][DEBUG] enabled for all players");
    }

    public static void disable() {
        log.info("[MAME-CC2][DEBUG] disabled target=" + targetName + "#" + targetObjectId);
        enabled = false;
        logAllPlayers = false;
        targetObjectId = 0;
        targetName = null;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static String getTargetText() {
        if (!enabled) {
            return "OFF";
        }
        if (logAllPlayers) {
            return "ALL";
        }
        return (targetName == null ? "unknown" : targetName) + "#" + targetObjectId;
    }

    public static boolean isTarget(Player player) {
        if (!enabled || player == null) {
            return false;
        }
        if (logAllPlayers) {
            return true;
        }
        if (targetObjectId != 0 && player.getObjectId() == targetObjectId) {
            return true;
        }
        return targetName != null && targetName.equalsIgnoreCase(player.getName());
    }


    public static int getStatsInfoMode() {
        return statsInfoMode;
    }

    public static String getStatsInfoModeName() {
        switch (statsInfoMode) {
            case 1:
                return "base-current";
            case 2:
                return "cc2-clean";
            default:
                return "77";
        }
    }

    public static boolean isStatsInfoBaseCurrentMode() {
        return statsInfoMode == 1;
    }

    public static boolean isStatsInfoCc2CleanMode() {
        return statsInfoMode == 2;
    }

    public static int statBaseOrCurrent(Stat2 stat) {
        if (stat == null) {
            return 0;
        }
        return isStatsInfoBaseCurrentMode() ? stat.getCurrent() : stat.getBase();
    }

    public static int statBaseForCc2Tooltip(Stat2 stat) {
        if (stat == null) {
            return 0;
        }
        // The final CC2 mode must send base values, not current values, so green +bonus is meaningful.
        return stat.getBase();
    }

    public static boolean setStatsInfoMode(String mode) {
        if (mode == null) {
            return false;
        }
        String normalized = mode.trim().toLowerCase();
        if ("77".equals(normalized) || "7.7".equals(normalized) || "default".equals(normalized) || "normal".equals(normalized) || "off".equals(normalized)) {
            statsInfoMode = 0;
            log.info("[MAME-CC2][STATS_MODE] mode=" + getStatsInfoModeName());
            return true;
        }
        if ("base-current".equals(normalized) || "basecurrent".equals(normalized) || "currentbase".equals(normalized) || "probe".equals(normalized)) {
            statsInfoMode = 1;
            log.info("[MAME-CC2][STATS_MODE] mode=" + getStatsInfoModeName());
            return true;
        }
        if ("cc2".equals(normalized) || "cc2-clean".equals(normalized) || "cc2clean".equals(normalized) || "clean".equals(normalized) || "fix".equals(normalized)) {
            statsInfoMode = 2;
            log.info("[MAME-CC2][STATS_MODE] mode=" + getStatsInfoModeName());
            return true;
        }
        return false;
    }

    public static int getPhysicalSkillDamageMode() {
        return physicalSkillDamageMode;
    }

    public static String getPhysicalSkillDamageModeName() {
        switch (physicalSkillDamageMode) {
            default:
                return "legacy-initial-source";
        }
    }

    public static boolean isModernDamageMode() {
        return false;
    }

    public static boolean isAggressiveDamageMode() {
        return false;
    }

    public static boolean setPhysicalSkillDamageMode(String mode) {
        // v45: damage formula is intentionally locked to the initial legacy source calculation.
        // Accept old command aliases so GM scripts do not fail, but never switch to the
        // temporary v39-v41 probe formulas.
        physicalSkillDamageMode = 0;
        log.info("[MAME-CC2][DAMAGE_MODE] mode=" + getPhysicalSkillDamageModeName());
        return true;
    }

    public static int applyPhysicalSkillDamageMode(Creature effector, int skillDamage) {
        return applyPhysicalSkillDamageMode(effector, null, skillDamage);
    }

    public static int applyPhysicalSkillDamageMode(Creature effector, Creature target, int skillDamage) {
        // Initial legacy source path: Func.ADD skill damage is added as-is.
        return skillDamage;
    }

    public static int scalePhysicalSkillDamage(Creature effector, int skillDamage, boolean aggressive) {
        return scalePhysicalSkillDamage(effector, null, skillDamage, aggressive);
    }

    public static int scalePhysicalSkillDamage(Creature effector, Creature target, int skillDamage, boolean aggressive) {
        if (effector == null || skillDamage <= 0) {
            return skillDamage;
        }
        try {
            int physBoost = effector.getGameStats().getPhysicPowerBoost().getCurrent();
            int physDmgBoost = effector.getGameStats().getPhysicDamageBoost().getCurrent();
            int targetPhysResist = target == null ? 0 : target.getGameStats().getPhysicPowerBoostResist().getCurrent();
            int targetPhysDmgResist = target == null ? 0 : target.getGameStats().getPhysicDamageBoostResist().getCurrent();

            // NET means attacker attack minus target defence. Do not clamp negative net values to zero;
            // otherwise player Physical Defence only removes bonus damage and can never reduce the skill base.
            // MameAion75 v41 uses a compressed 7.x-compatible layer because modern/private stat values
            // such as 29000 attack vs 13000 defence must not become a 17x PvP multiplier.
            // PvP/PvE Attack/Defence is still applied exactly once in StatFunctions.adjustDamages().
            int netPhysBoost = physBoost - targetPhysResist;
            int netPhysDmg = physDmgBoost - targetPhysDmgResist;
            float multiplier = getSevenXPowerNetDamageMultiplier(netPhysBoost + netPhysDmg, 0.35f, aggressive ? 2.50f : 2.00f);
            return Math.round(skillDamage * multiplier);
        } catch (Exception e) {
            return skillDamage;
        }
    }

    public static int getDamageContextAttackBoost(Creature effector, Creature target) {
        if (effector == null || target == null) {
            return 0;
        }
        if (target instanceof Player || effector.isPvpTarget(target)) {
            return effector.getGameStats().getPvpPowerBoost().getCurrent();
        }
        if (target instanceof Npc) {
            return effector.getGameStats().getPvePowerBoost().getCurrent();
        }
        return 0;
    }

    public static int getDamageContextResist(Creature target) {
        if (target == null) {
            return 0;
        }
        if (target instanceof Player) {
            return target.getGameStats().getPvpPowerBoostResist().getCurrent();
        }
        if (target instanceof Npc) {
            return target.getGameStats().getPvePowerBoostResist().getCurrent();
        }
        return 0;
    }

    public static float getDamageContextMultiplier(Creature effector, Creature target) {
        int contextAttack = getDamageContextAttackBoost(effector, target);
        int contextResist = getDamageContextResist(target);
        int net = contextAttack - contextResist;
        return getSevenXContextNetDamageMultiplier(net, 0.35f, 2.50f);
    }

    public static float getSevenXPowerNetDamageMultiplier(int netAttackMinusDefense, float minMultiplier, float maxMultiplier) {
        return clampMultiplier(1.0f + (netAttackMinusDefense / POWER_NET_DIVISOR), minMultiplier, maxMultiplier);
    }

    public static float getSevenXContextNetDamageMultiplier(int netAttackMinusDefense, float minMultiplier, float maxMultiplier) {
        return clampMultiplier(1.0f + (netAttackMinusDefense / CONTEXT_NET_DIVISOR), minMultiplier, maxMultiplier);
    }

    /**
     * Kept for old calls/macros. In v41 this points to the compressed Physical/Magical
     * Attack-vs-Defence layer, not the retired raw 10 stat = 1% layer.
     */
    public static float getSevenXNetDamageMultiplier(int netAttackMinusDefense, float minMultiplier, float maxMultiplier) {
        return getSevenXPowerNetDamageMultiplier(netAttackMinusDefense, minMultiplier, maxMultiplier);
    }

    private static float clampMultiplier(float multiplier, float minMultiplier, float maxMultiplier) {
        if (multiplier < minMultiplier) {
            multiplier = minMultiplier;
        }
        if (multiplier > maxMultiplier) {
            multiplier = maxMultiplier;
        }
        return multiplier;
    }

    public static String getDamageContextName(Creature effector, Creature target) {
        if (target == null) {
            return "none";
        }
        if (target instanceof Player || (effector != null && effector.isPvpTarget(target))) {
            return "pvp";
        }
        if (target instanceof Npc) {
            return "pve";
        }
        return "none";
    }

    public static String physicalSkillPreview(Creature effector, int skillDamage) {
        return physicalSkillPreview(effector, null, skillDamage);
    }

    public static String physicalSkillPreview(Creature effector, Creature target, int skillDamage) {
        if (effector == null) {
            return "preview=null";
        }
        try {
            int physBoost = effector.getGameStats().getPhysicPowerBoost().getCurrent();
            int pveBoost = effector.getGameStats().getPvePowerBoost().getCurrent();
            int pvpBoost = effector.getGameStats().getPvpPowerBoost().getCurrent();
            int physDmgBoost = effector.getGameStats().getPhysicDamageBoost().getCurrent();
            int contextBoost = getDamageContextAttackBoost(effector, target);
            int contextResist = getDamageContextResist(target);
            int targetPhysResist = target == null ? 0 : target.getGameStats().getPhysicPowerBoostResist().getCurrent();
            int targetPhysDmgResist = target == null ? 0 : target.getGameStats().getPhysicDamageBoostResist().getCurrent();
            int soft = scalePhysicalSkillDamage(effector, target, skillDamage, false);
            int boost = scalePhysicalSkillDamage(effector, target, skillDamage, true);
            return "skillRaw=" + skillDamage
                + " legacyAdd=" + skillDamage
                + " softScale=" + soft
                + " boostScale=" + boost
                + " context=" + getDamageContextName(effector, target)
                + " contextBoost=" + contextBoost
                + " contextResist=" + contextResist
                + " contextMultiplier=" + getDamageContextMultiplier(effector, target)
                + " powerNetMultiplier=" + getSevenXPowerNetDamageMultiplier((physBoost - targetPhysResist) + (physDmgBoost - targetPhysDmgResist), 0.35f, 2.00f)
                + " physBoost=" + physBoost
                + " targetPhysResist=" + targetPhysResist
                + " pveBoost=" + pveBoost
                + " pvpBoost=" + pvpBoost
                + " physDmgBoost=" + physDmgBoost
                + " targetPhysDmgResist=" + targetPhysDmgResist;
        } catch (Exception e) {
            return "skillRaw=" + skillDamage + " preview=error:" + e.getClass().getSimpleName();
        }
    }

    public static void logPhysicalSkillFormula(Effect effect, int rawSkillDamage, int baseDamage, int appliedSkillDamage, int afterSkillDamage, String funcName) {
        if (effect == null || !involves(effect.getEffector(), effect.getEffected())) {
            return;
        }
        log.info("[MAME-CC2][SKILL_FORMULA] attacker=" + describe(effect.getEffector())
            + " target=" + describe(effect.getEffected())
            + " skillId=" + effect.getSkillId()
            + " skillLevel=" + effect.getSkillLevel()
            + " func=" + funcName
            + " dmgMode=" + getPhysicalSkillDamageModeName()
            + " baseDamageBeforeSkill=" + baseDamage
            + " rawSkillDamage=" + rawSkillDamage
            + " appliedSkillDamage=" + appliedSkillDamage
            + " afterSkillDamage=" + afterSkillDamage
            + " preview=" + physicalSkillPreview(effect.getEffector(), effect.getEffected(), rawSkillDamage)
            + " attackerStats=" + statsForCreature(effect.getEffector())
            + " targetHp=" + hpForCreature(effect.getEffected()));
    }

    public static String magicalSkillPreview(Creature speller, Creature target, int baseDamages, boolean useMagicBoost, boolean useKnowledge) {
        if (speller == null) {
            return "preview=null";
        }
        try {
            int magicBoost = useMagicBoost ? speller.getGameStats().getMagicPowerBoost().getCurrent() : 0;
            int magicDamageBoost = useMagicBoost ? speller.getGameStats().getMagicDamageBoost().getCurrent() : 0;
            int contextBoost = useMagicBoost ? getDamageContextAttackBoost(speller, target) : 0;
            int contextResist = useMagicBoost ? getDamageContextResist(target) : 0;
            int magicBoostResist = target == null ? 0 : target.getGameStats().getMagicPowerBoostResist().getCurrent();
            int magicDamageResist = target == null ? 0 : target.getGameStats().getMagicDamageBoostResist().getCurrent();
            int mDef = target == null ? 0 : target.getGameStats().getMDef().getCurrent();
            int knowledge = useKnowledge ? speller.getGameStats().getKnowledge().getCurrent() : 0;
            int netMagicBoost = magicBoost - magicBoostResist - mDef;
            int netContext = contextBoost - contextResist;
            int netMagicDamage = magicDamageBoost - magicDamageResist;
            float statScale = useKnowledge ? knowledge / 100f : 1.0f;
            if (statScale < 1.0f) {
                statScale = 1.0f;
            }
            float powerMultiplier = getSevenXPowerNetDamageMultiplier(netMagicBoost + netMagicDamage, 0.35f, 2.00f);
            float contextMultiplier = getSevenXContextNetDamageMultiplier(netContext, 0.35f, 2.50f);
            float softMultiplier = statScale * powerMultiplier * contextMultiplier;
            float boostMultiplier = statScale * getSevenXPowerNetDamageMultiplier(netMagicBoost + netMagicDamage, 0.35f, 2.50f) * contextMultiplier;
            return "baseRaw=" + baseDamages
                + " softScale=" + Math.round(baseDamages * softMultiplier)
                + " boostScale=" + Math.round(baseDamages * boostMultiplier)
                + " context=" + getDamageContextName(speller, target)
                + " contextBoost=" + contextBoost
                + " contextResist=" + contextResist
                + " contextMultiplier=" + getDamageContextMultiplier(speller, target)
                + " powerNetMultiplier=" + powerMultiplier
                + " magicBoost=" + magicBoost
                + " magicBoostResist=" + magicBoostResist
                + " mDef=" + mDef
                + " magicDmgBoost=" + magicDamageBoost
                + " magicDmgResist=" + magicDamageResist
                + " knowledge=" + knowledge;
        } catch (Exception e) {
            return "baseRaw=" + baseDamages + " preview=error:" + e.getClass().getSimpleName();
        }
    }

    public static void logMagicalSkillFormula(Creature speller, Creature target, int baseDamages, int bonus, float formulaDamage, float beforeAdjust, float afterAdjust, SkillElement element, boolean useMagicBoost, boolean useKnowledge, boolean noReduce, int pvpDamage) {
        if (!involves(speller, target)) {
            return;
        }
        log.info("[MAME-CC2][MAGIC_FORMULA] attacker=" + describe(speller)
            + " target=" + describe(target)
            + " element=" + element
            + " dmgMode=" + getPhysicalSkillDamageModeName()
            + " useMagicBoost=" + useMagicBoost
            + " useKnowledge=" + useKnowledge
            + " noReduce=" + noReduce
            + " pvpDamage=" + pvpDamage
            + " baseDamages=" + baseDamages
            + " bonus=" + bonus
            + " formulaDamage=" + Math.round(formulaDamage)
            + " beforeAdjust=" + Math.round(beforeAdjust)
            + " afterAdjust=" + Math.round(afterAdjust)
            + " preview=" + magicalSkillPreview(speller, target, baseDamages, useMagicBoost, useKnowledge)
            + " attackerStats=" + statsForCreature(speller)
            + " targetHp=" + hpForCreature(target));
    }

    public static boolean involves(Creature a, Creature b) {
        return isTarget(asPlayer(a)) || isTarget(asPlayer(b));
    }

    public static Player asPlayer(Object object) {
        return object instanceof Player ? (Player) object : null;
    }

    public static String describe(VisibleObject object) {
        if (object == null) {
            return "null";
        }
        String name;
        try {
            name = object.getName();
        } catch (Exception e) {
            name = object.getClass().getSimpleName();
        }
        return name + "#" + object.getObjectId() + "/" + object.getClass().getSimpleName();
    }

    public static String statsSummary(Player player) {
        if (player == null) {
            return "player=null";
        }
        PlayerGameStats pgs = player.getGameStats();
        PlayerLifeStats pls = player.getLifeStats();
        return "player=" + describe(player)
            + " class=" + player.getPlayerClass()
            + " level=" + player.getLevel()
            + " hp=" + pls.getCurrentHp() + "/" + pgs.getMaxHp().getCurrent()
            + " mp=" + pls.getCurrentMp() + "/" + pgs.getMaxMp().getCurrent()
            + " mainPAtk=" + statFull(pgs.getMainHandPAttack())
            + " offPAtk=" + statFull(pgs.getOffHandPAttack())
            + " mainMAtk=" + statFull(pgs.getMainHandMAttack())
            + " offMAtk=" + statFull(pgs.getOffHandMAttack())
            + " pAcc=" + statFull(pgs.getPAccuracy())
            + " offPAcc=" + statFull(pgs.getOffHandPAccuracy())
            + " pCrit=" + statFull(pgs.getPCritical())
            + " offPCrit=" + statFull(pgs.getOffHandPCritical())
            + " mAcc=" + statFull(pgs.getMAccuracy())
            + " mCrit=" + statFull(pgs.getMCritical())
            + " atkSpeed=" + statFull(pgs.getAttackSpeed())
            + " atkRange=" + statFull(pgs.getAttackRange())
            + " physBoost=" + statFull(pgs.getPhysicPowerBoost())
            + " physDmgBoost=" + statFull(pgs.getPhysicDamageBoost())
            + " physBoostRes=" + statFull(pgs.getPhysicPowerBoostResist())
            + " physDmgRes=" + statFull(pgs.getPhysicDamageBoostResist())
            + " magicBoost=" + statFull(pgs.getMagicPowerBoost())
            + " magicDmgBoost=" + statFull(pgs.getMagicDamageBoost())
            + " magicBoostRes=" + statFull(pgs.getMagicPowerBoostResist())
            + " magicDmgRes=" + statFull(pgs.getMagicDamageBoostResist())
            + " classicPDef=" + statFull(pgs.getPDef())
            + " classicMDef=" + statFull(pgs.getMDef())
            + " pveBoost=" + statFull(pgs.getPvePowerBoost())
            + " pveBoostRes=" + statFull(pgs.getPvePowerBoostResist())
            + " pvpBoost=" + statFull(pgs.getPvpPowerBoost())
            + " pvpBoostRes=" + statFull(pgs.getPvpPowerBoostResist())
            + " mResist=" + statFull(pgs.getMResist())
            + " mDefAlias=" + statFull(pgs.getMDef())
            + " statsMode=" + getStatsInfoModeName()
            + " dmgMode=" + getPhysicalSkillDamageModeName();
    }

    private static String statFull(Stat2 stat) {
        if (stat == null) {
            return "null";
        }
        return stat.getCurrent() + "(base=" + stat.getBase() + ",bonus=" + stat.getBonus() + ")";
    }

    public static String equipmentSummary(Player player) {
        if (player == null) {
            return "player=null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(itemSummary("main", player.getEquipment().getMainHandWeapon()));
        sb.append(" | ").append(itemSummary("off", player.getEquipment().getOffHandWeapon()));
        int count = 0;
        for (Item equipped : player.getEquipment().getEquippedItems()) {
            if (equipped == null || equipped == player.getEquipment().getMainHandWeapon() || equipped == player.getEquipment().getOffHandWeapon()) {
                continue;
            }
            sb.append(" | ").append(itemSummary("eq" + count, equipped));
            count++;
            if (count >= 24) {
                sb.append(" | ...");
                break;
            }
        }
        return sb.toString();
    }

    private static String itemSummary(String slot, Item item) {
        if (item == null) {
            return slot + "=null";
        }
        ItemTemplate tpl = item.getItemTemplate();
        if (tpl == null) {
            return slot + "=item#" + item.getObjectId() + " template=null";
        }
        WeaponStats ws = tpl.getWeaponStats();
        String weapon = ws == null ? "weaponStats=null"
            : "min=" + ws.getMinDamage()
            + ",max=" + ws.getMaxDamage()
            + ",mean=" + ws.getMeanDamage()
            + ",pPower=" + ws.getPhysicalPowerBoost()
            + ",pDef=" + ws.getPhysicalPowerBoostResist()
            + ",mPower=" + ws.getMagicalPowerBoost()
            + ",mDef=" + ws.getMagicalPowerBoostResist()
            + ",pAcc=" + ws.getPhysicalAccuracy()
            + ",pCrit=" + ws.getPhysicalCritical()
            + ",mAcc=" + ws.getMagicalAccuracy()
            + ",parry=" + ws.getParry()
            + ",range=" + ws.getAttackRange()
            + ",speed=" + ws.getAttackSpeed();
        return slot + "=obj#" + item.getObjectId()
            + "/itemId=" + tpl.getTemplateId()
            + "/name=" + tpl.getName()
            + "/type=" + tpl.getWeaponType()
            + "/attackType=" + tpl.getAttackType()
            + "/slot=" + item.getEquipmentSlot()
            + "/enchant=" + item.getEnchantLevel()
            + "/enchantPvPvE=" + item.getEnchantPvPvELevel()
            + "/authorize=" + item.getAuthorizeLevel()
            + "/enchantTable=" + tpl.getEnchantTableId()
            + "/temperingTable=" + tpl.getTemperingTableId()
            + "/enchantType=" + tpl.getEnchantType()
            + "/rndBonusCount=" + (item.getRndBonus() == null ? 0 : item.getRndBonus().size())
            + "/rndBonus=" + rndBonusSummary(item)
            + "/currentMods=" + (item.getCurrentModifiers() == null ? 0 : item.getCurrentModifiers().size())
            + "/currentModsDetail=" + statFunctionSummary(item.getCurrentModifiers())
            + "/" + weapon
            + "/templateMods=" + modifierSummary(tpl);
    }

    private static String rndBonusSummary(Item item) {
        if (item == null || item.getRndBonus() == null || item.getRndBonus().isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        int count = 0;
        for (java.util.Map.Entry<Integer, com.aionemu.gameserver.model.gameobjects.item.ItemRndBonus> entry : item.getRndBonus().entrySet()) {
            if (entry == null || entry.getValue() == null) {
                continue;
            }
            if (count > 0) {
                sb.append(',');
            }
            sb.append(entry.getKey()).append('=').append(entry.getValue().getValue());
            count++;
            if (count >= 16) {
                sb.append(",...");
                break;
            }
        }
        return sb.append(']').toString();
    }

    private static String statFunctionSummary(java.util.List<StatFunction> functions) {
        if (functions == null || functions.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        int count = 0;
        for (StatFunction function : functions) {
            if (function == null) {
                continue;
            }
            if (count > 0) {
                sb.append(',');
            }
            sb.append(function.getName()).append('=').append(function.getValue()).append(function.isBonus() ? "/bonus" : "/base");
            count++;
            if (count >= 24) {
                sb.append(",...");
                break;
            }
        }
        return sb.append(']').toString();
    }

    private static String modifierSummary(ItemTemplate tpl) {
        if (tpl == null || tpl.getModifiers() == null || tpl.getModifiers().isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        int count = 0;
        for (StatFunction function : tpl.getModifiers()) {
            if (function == null) {
                continue;
            }
            if (count > 0) {
                sb.append(',');
            }
            sb.append(function.getName()).append('=').append(function.getValue()).append(function.isBonus() ? "/bonus" : "/base");
            count++;
            if (count >= 24) {
                sb.append(",...");
                break;
            }
        }
        sb.append(']');
        return sb.toString();
    }

    public static void logAudit(Player player, String source) {
        if (!isTarget(player)) {
            return;
        }
        log.info("[MAME-CC2][AUDIT][" + source + "] " + statsSummary(player));
        log.info("[MAME-CC2][AUDIT][" + source + "] " + equipmentSummary(player));
    }

    public static void logStats(Player player, String source) {
        if (!isTarget(player)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastStatsLog < 250L && !"manual".equals(source)) {
            return;
        }
        lastStatsLog = now;
        log.info("[MAME-CC2][STATS][" + source + "] " + statsSummary(player));
    }

    public static void logCastPacket(Player player, int spellId, int level, int targetType, int targetObjectId,
            float x, float y, float z, int hitTime, int unk, SkillTemplate template) {
        if (!isTarget(player)) {
            return;
        }
        String templateText = template == null ? "null" : (template.getSkillId() + "/" + template.getName() + "/type=" + template.getType() + "/sub=" + template.getSubType() + "/passive=" + template.isPassive());
        log.info("[MAME-CC2][CM_CASTSPELL] player=" + describe(player)
            + " spellId=" + spellId
            + " level=" + level
            + " targetType=" + targetType
            + " targetObjectId=" + targetObjectId
            + " xyz=" + x + "," + y + "," + z
            + " hitTime=" + hitTime
            + " unk=" + unk
            + " template=" + templateText
            + " currentTarget=" + describe(player.getTarget()));
        logStats(player, "cast-packet");
    }

    public static void logAttackPacket(Player player, int targetObjectId, int attackNo, int time, int type, VisibleObject resolvedTarget) {
        if (!isTarget(player)) {
            return;
        }
        log.info("[MAME-CC2][CM_ATTACK] player=" + describe(player)
            + " targetObjectId=" + targetObjectId
            + " resolvedTarget=" + describe(resolvedTarget)
            + " attackNo=" + attackNo
            + " time=" + time
            + " type=" + type
            + " currentTarget=" + describe(player.getTarget()));
        logStats(player, "attack-packet");
    }

    public static void logSkillResolved(Player player, SkillTemplate template, Skill skill, int targetType,
            float x, float y, float z, int clientHitTime, int requestedSkillLevel) {
        if (!isTarget(player)) {
            return;
        }
        String skillText = skill == null ? "null" : (skill.getSkillId() + "/lvl=" + skill.getSkillLevel() + "/target=" + describe(skill.getFirstTarget()));
        String templateText = template == null ? "null" : (template.getSkillId() + "/" + template.getName() + "/type=" + template.getType() + "/sub=" + template.getSubType());
        log.info("[MAME-CC2][SKILL_RESOLVE] player=" + describe(player)
            + " template=" + templateText
            + " requestedLevel=" + requestedSkillLevel
            + " resolvedSkill=" + skillText
            + " targetType=" + targetType
            + " xyz=" + x + "," + y + "," + z
            + " clientHitTime=" + clientHitTime
            + " currentTarget=" + describe(player.getTarget()));
    }

    public static void logDamage(Creature target, Creature attacker, int skillId, Object type, int damage, Object attackLog) {
        if (!involves(target, attacker)) {
            return;
        }
        log.info("[MAME-CC2][DAMAGE] attacker=" + describe(attacker)
            + " target=" + describe(target)
            + " skillId=" + skillId
            + " type=" + type
            + " log=" + attackLog
            + " damage=" + damage
            + " attackerStats=" + statsForCreature(attacker)
            + " targetHp=" + hpForCreature(target));
    }

    private static String hpForCreature(Creature creature) {
        if (creature == null) {
            return "null";
        }
        try {
            return creature.getLifeStats().getCurrentHp() + "/" + creature.getGameStats().getMaxHp().getCurrent();
        } catch (Exception e) {
            return "?";
        }
    }

    private static String statsForCreature(Creature creature) {
        if (creature == null) {
            return "null";
        }
        try {
            return "pAtk=" + creature.getGameStats().getMainHandPAttack().getCurrent()
                + "/mAtk=" + creature.getGameStats().getMainHandMAttack().getCurrent()
                + "/pAcc=" + creature.getGameStats().getPAccuracy().getCurrent()
                + "/pCrit=" + creature.getGameStats().getPCritical().getCurrent()
                + "/physBoost=" + creature.getGameStats().getPhysicPowerBoost().getCurrent()
                + "/physDmgBoost=" + creature.getGameStats().getPhysicDamageBoost().getCurrent()
                + "/pveBoost=" + creature.getGameStats().getPvePowerBoost().getCurrent()
                + "/pveRes=" + creature.getGameStats().getPvePowerBoostResist().getCurrent()
                + "/pvpBoost=" + creature.getGameStats().getPvpPowerBoost().getCurrent()
                + "/pvpRes=" + creature.getGameStats().getPvpPowerBoostResist().getCurrent()
                + "/magicBoost=" + creature.getGameStats().getMagicPowerBoost().getCurrent();
        } catch (Exception e) {
            return "?";
        }
    }
}
