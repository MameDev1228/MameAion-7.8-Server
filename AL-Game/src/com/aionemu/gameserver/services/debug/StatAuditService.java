/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 */
package com.aionemu.gameserver.services.debug;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.RealRandomBonus;
import com.aionemu.gameserver.model.items.RealRandomBonusStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.WeaponStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * Development-only stat/combat audit helper for the 7.8/JDK25 migration.
 *
 * Keep gameplay code small and keep noisy details in separate log files.
 */
public final class StatAuditService {

	private static final Logger log = LoggerFactory.getLogger(StatAuditService.class);
	private static final StatAuditService INSTANCE = new StatAuditService();
	private static final Object FILE_LOCK = new Object();
	private static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private StatAuditService() {
	}

	public static StatAuditService getInstance() {
		return INSTANCE;
	}

	public StatSnapshot snapshot(Player player) {
		return new StatSnapshot(player);
	}

	public void equipment(Player player, Item item, String action, StatSnapshot before, StatSnapshot after) {
		if (!DeveloperConfig.EQUIPMENT_TRACE_ENABLE || player == null || item == null || item.getItemTemplate() == null) {
			return;
		}
		String itemInfo = itemInfo(item);
		String delta = StatSnapshot.delta(before, after);
		if (DeveloperConfig.EQUIPMENT_TRACE_CONSOLE) {
			log.info("[EQUIP_TRACE] action={} player={} {} {}", new Object[] { action, player.getName(), itemInfo, delta });
		}
		appendLine("equipment_stats.tsv", timestamp() + '\t' + "action=" + safe(action) + '\t' + playerInfo(player) + '\t' + itemInfo + '\t' + delta);
		if (DeveloperConfig.EQUIPMENT_TRACE_VERBOSE) {
			appendLine("equipment_modifiers.tsv", timestamp() + '\t' + "action=" + safe(action) + '\t' + playerInfo(player) + '\t' + itemInfo + '\t' + modifiersInfo(item));
		}
		warnSuspiciousEquipment(player, item, action, before, after);
	}

	public void combatAttackStatus(Creature victim, Creature attacker, SM_ATTACK_STATUS.TYPE type, int skillId, int damage, SM_ATTACK_STATUS.LOG attackLog, boolean notifyAttack, String context) {
		if (!DeveloperConfig.COMBAT_TRACE_ENABLE) {
			return;
		}
		if (DeveloperConfig.COMBAT_TRACE_PLAYER_ONLY && !isPlayerRelated(victim, attacker)) {
			return;
		}
		String line = timestamp() + '\t'
			+ "context=" + safe(context) + '\t'
			+ "type=" + (type != null ? type.name() : "-") + '\t'
			+ "skillId=" + skillId + '\t'
			+ "damage=" + damage + '\t'
			+ "notify=" + notifyAttack + '\t'
			+ "log=" + (attackLog != null ? attackLog.name() : "-") + '\t'
			+ "victim=" + creatureInfo(victim) + '\t'
			+ "attacker=" + creatureInfo(attacker);
		appendLine("combat_attack_status.tsv", line);
		if (DeveloperConfig.COMBAT_TRACE_CONSOLE) {
			if (victim instanceof Player) {
				log.info("[COMBAT_TRACE] victimPlayer={} attacker={} attackerType={} skillId={} type={} damage={} notify={} hp={}/{}", new Object[] {
					((Player) victim).getName(), attacker != null ? attacker.getObjectId() : 0, attacker != null ? attacker.getClass().getSimpleName() : "null", skillId, type, damage, notifyAttack,
					victim.getLifeStats().getCurrentHp(), victim.getLifeStats().getMaxHp() });
			}
			else {
				log.info("[COMBAT_TRACE] victim={} attacker={} attackerType={} skillId={} type={} damage={} notify={} hp={}/{}", new Object[] {
					creatureInfo(victim), attacker != null ? attacker.getObjectId() : 0, attacker != null ? attacker.getClass().getSimpleName() : "null", skillId, type, damage, notifyAttack,
					victim != null ? victim.getLifeStats().getCurrentHp() : 0, victim != null ? victim.getLifeStats().getMaxHp() : 0 });
			}
		}
	}

	public boolean shouldSuppressZeroDamageNpcToPlayer(Creature victim, Creature attacker, int damage) {
		return DeveloperConfig.COMBAT_SUPPRESS_ZERO_DAMAGE_NPC_TO_PLAYER && damage <= 0 && victim instanceof Player && attacker != null && !(attacker instanceof Player) && !(attacker instanceof Summon);
	}

	public void suppressedAttackStatus(Creature victim, Creature attacker, int skillId, int damage, String context) {
		if (!DeveloperConfig.COMBAT_TRACE_ENABLE) {
			return;
		}
		String line = timestamp() + '\t' + "context=" + safe(context) + '\t' + "skillId=" + skillId + '\t' + "damage=" + damage + '\t' + "victim=" + creatureInfo(victim) + '\t' + "attacker=" + creatureInfo(attacker);
		appendLine("combat_attack_status_suppressed.tsv", line);
		if (DeveloperConfig.COMBAT_TRACE_CONSOLE) {
			log.info("[COMBAT_TRACE_SKIP] zeroDamageNpcToPlayer victim={} attacker={} skillId={} damage={}", new Object[] { creatureInfo(victim), creatureInfo(attacker), skillId, damage });
		}
	}

	public void buffStart(Effect effect, List<? extends IStatFunction> modifiers, StatSnapshot before, StatSnapshot after) {
		if (!DeveloperConfig.BUFF_TRACE_ENABLE || effect == null || !(effect.getEffected() instanceof Player)) {
			return;
		}
		String line = timestamp() + '\t'
			+ "phase=start" + '\t'
			+ effectInfo(effect) + '\t'
			+ "modifiers=" + functionsInfo(modifiers) + '\t'
			+ StatSnapshot.delta(before, after);
		appendLine("buff_stats.tsv", line);
		if (DeveloperConfig.BUFF_TRACE_CONSOLE) {
			log.info("[BUFF_TRACE] phase=start {} {}", new Object[] { effectInfo(effect), StatSnapshot.delta(before, after) });
		}
	}

	public void buffEnd(Effect effect, StatSnapshot before, StatSnapshot after) {
		if (!DeveloperConfig.BUFF_TRACE_ENABLE || effect == null || !(effect.getEffected() instanceof Player)) {
			return;
		}
		String line = timestamp() + '\t'
			+ "phase=end" + '\t'
			+ effectInfo(effect) + '\t'
			+ StatSnapshot.delta(before, after);
		appendLine("buff_stats.tsv", line);
		if (DeveloperConfig.BUFF_TRACE_CONSOLE) {
			log.info("[BUFF_TRACE] phase=end {} {}", new Object[] { effectInfo(effect), StatSnapshot.delta(before, after) });
		}
	}


	public void skillTrace(Skill skill, String phase, String detail) {
		if (!DeveloperConfig.SKILL_TRACE_ENABLE || skill == null) {
			return;
		}
		Creature effector = skill.getEffector();
		Creature firstTarget = skill.getFirstTarget();
		if (!(effector instanceof Player) && !(firstTarget instanceof Player)) {
			return;
		}
		String line = timestamp() + '\t'
			+ "phase=" + safe(phase) + '\t'
			+ "detail=" + safe(detail) + '\t'
			+ "skillId=" + skill.getSkillId() + '\t'
			+ "skillName=" + safe(skill.getSkillTemplate() != null ? skill.getSkillTemplate().getName() : null) + '\t'
			+ "skillLevel=" + skill.getSkillLevel() + '\t'
			+ "targetType=" + skill.getTargetType() + '\t'
			+ "duration=" + skill.getDuration() + '\t'
			+ "hitTime=" + skill.getHitTime() + '\t'
			+ "effector=" + creatureInfo(effector) + '\t'
			+ "firstTarget=" + creatureInfo(firstTarget);
		appendLine("skill_trace.tsv", line);
		if (DeveloperConfig.SKILL_TRACE_CONSOLE) {
			log.info("[SKILL_TRACE] phase={} skillId={} skillName={} effector={} firstTarget={} detail={}", new Object[] {
				phase, skill.getSkillId(), skill.getSkillTemplate() != null ? skill.getSkillTemplate().getName() : "-", creatureInfo(effector), creatureInfo(firstTarget), detail });
		}
	}

	public void effectTrace(String phase, Effect effect, String templateName, StatSnapshot before, StatSnapshot after) {
		if (!DeveloperConfig.EFFECT_TRACE_ALL_ENABLE || effect == null || !(effect.getEffected() instanceof Player || effect.getEffector() instanceof Player)) {
			return;
		}
		String line = timestamp() + '\t'
			+ "phase=" + safe(phase) + '\t'
			+ "template=" + safe(templateName) + '\t'
			+ effectInfo(effect) + '\t'
			+ StatSnapshot.delta(before, after);
		appendLine("effect_stats.tsv", line);
		if (DeveloperConfig.EFFECT_TRACE_CONSOLE) {
			log.info("[EFFECT_TRACE] phase={} template={} {} {}", new Object[] { phase, templateName, effectInfo(effect), StatSnapshot.delta(before, after) });
		}
	}

	public void npcVisibility(Player viewer, Npc npc, String phase, long elapsedMs) {
		if (!DeveloperConfig.NPC_VISIBILITY_TRACE_ENABLE || viewer == null || npc == null) {
			return;
		}
		String line = timestamp() + '\t'
			+ "phase=" + safe(phase) + '\t'
			+ "elapsedMs=" + elapsedMs + '\t'
			+ playerInfo(viewer) + '\t'
			+ "npcObj=" + npc.getObjectId() + '\t'
			+ "npcId=" + npc.getNpcId() + '\t'
			+ "npcName=" + safe(npc.getName()) + '\t'
			+ "type=" + npc.getObjectTemplate().getNpcTemplateType() + '\t'
			+ "state=" + npc.getState() + '\t'
			+ "world=" + npc.getWorldId() + '\t'
			+ "x=" + npc.getX() + '\t'
			+ "y=" + npc.getY() + '\t'
			+ "z=" + npc.getZ() + '\t'
			+ "heading=" + npc.getHeading() + '\t'
			+ "gear=" + (npc.getObjectTemplate().getEquipment() != null ? npc.getObjectTemplate().getEquipment().getItemsMask() : 0) + '\t'
			+ "walkerId=" + safe(npc.getSpawn() != null ? npc.getSpawn().getWalkerId() : null) + '\t'
			+ "randomWalk=" + (npc.getSpawn() != null ? npc.getSpawn().getRandomWalk() : 0) + '\t'
			+ "moveMask=" + npc.getMoveController().getMovementMask() + '\t'
			+ "target=" + (npc.getTarget() != null ? npc.getTarget().getObjectId() : 0);
		appendLine("npc_visibility.tsv", line);
		if (DeveloperConfig.NPC_VISIBILITY_TRACE_CONSOLE) {
			log.info("[NPC_VIS_TRACE] phase={} viewer={} npcId={} obj={} elapsedMs={} gearMask={} walkerId={}", new Object[] {
				phase, viewer.getName(), npc.getNpcId(), npc.getObjectId(), elapsedMs, npc.getObjectTemplate().getEquipment() != null ? npc.getObjectTemplate().getEquipment().getItemsMask() : 0,
				npc.getSpawn() != null ? npc.getSpawn().getWalkerId() : "-" });
		}
	}

	public void aiWalk(Npc npc, String phase, String detail) {
		if (!DeveloperConfig.AI_WALK_TRACE_ENABLE || npc == null) {
			return;
		}
		String line = timestamp() + '\t'
			+ "phase=" + safe(phase) + '\t'
			+ "detail=" + safe(detail) + '\t'
			+ "npcObj=" + npc.getObjectId() + '\t'
			+ "npcId=" + npc.getNpcId() + '\t'
			+ "npcName=" + safe(npc.getName()) + '\t'
			+ "world=" + npc.getWorldId() + '\t'
			+ "x=" + npc.getX() + '\t'
			+ "y=" + npc.getY() + '\t'
			+ "z=" + npc.getZ() + '\t'
			+ "walkerId=" + safe(npc.getSpawn() != null ? npc.getSpawn().getWalkerId() : null) + '\t'
			+ "randomWalk=" + (npc.getSpawn() != null ? npc.getSpawn().getRandomWalk() : 0) + '\t'
			+ "moveSpeed=" + npc.getGameStats().getMovementSpeedFloat() + '\t'
			+ "state=" + npc.getAi2().getState() + '\t'
			+ "subState=" + npc.getAi2().getSubState() + '\t'
			+ "moveMask=" + npc.getMoveController().getMovementMask();
		appendLine("ai_walk.tsv", line);
		if (DeveloperConfig.AI_WALK_TRACE_CONSOLE) {
			log.info("[AI_WALK_TRACE] phase={} npcId={} obj={} walkerId={} randomWalk={} detail={}", new Object[] {
				phase, npc.getNpcId(), npc.getObjectId(), npc.getSpawn() != null ? npc.getSpawn().getWalkerId() : "-", npc.getSpawn() != null ? npc.getSpawn().getRandomWalk() : 0, detail });
		}
	}

	public void statCap(Stat2 stat, int beforeCurrent, int afterCurrent, int lowerCap, int upperCap, String direction) {
		if (!DeveloperConfig.STAT_CAP_TRACE_ENABLE || stat == null) {
			return;
		}
		String line = timestamp() + '\t'
			+ "direction=" + safe(direction) + '\t'
			+ "stat=" + stat.getStat() + '\t'
			+ "base=" + stat.getBase() + '\t'
			+ "bonus=" + stat.getBonus() + '\t'
			+ "before=" + beforeCurrent + '\t'
			+ "after=" + afterCurrent + '\t'
			+ "lowerCap=" + lowerCap + '\t'
			+ "upperCap=" + upperCap + '\t'
			+ "owner=" + simpleCreatureInfo(stat.getOwner());
		appendLine("stat_cap.tsv", line);
		if (DeveloperConfig.STAT_CAP_TRACE_CONSOLE) {
			log.warn("[STAT_CAP_TRACE] direction={} stat={} base={} before={} after={} cap=[{},{}] owner={}", new Object[] { direction, stat.getStat(), stat.getBase(), beforeCurrent, afterCurrent, lowerCap, upperCap, simpleCreatureInfo(stat.getOwner()) });
		}
	}

	public void attackStatusPacket(Creature victim, Creature attacker, SM_ATTACK_STATUS.TYPE type, int skillId, int value, int logId, int skinId) {
		if (!DeveloperConfig.COMBAT_TRACE_ENABLE || !isPlayerRelated(victim, attacker)) {
			return;
		}
		appendLine("attack_status_packet.tsv", timestamp() + '\t'
			+ "victim=" + creatureInfo(victim) + '\t'
			+ "attacker=" + creatureInfo(attacker) + '\t'
			+ "type=" + (type != null ? type.name() : "-") + ':' + (type != null ? type.getValue() : -1) + '\t'
			+ "skillId=" + skillId + '\t'
			+ "value=" + value + '\t'
			+ "logId=" + logId + '\t'
			+ "skinId=" + skinId + '\t'
			+ "skinEnabled=" + DeveloperConfig.PACKET_ATTACK_STATUS_SKILL_SKIN);
	}

	public boolean isPlayerRelated(Creature victim, Creature attacker) {
		return asPlayer(victim) != null || asPlayer(attacker) != null;
	}

	private Player asPlayer(Creature creature) {
		if (creature instanceof Player) {
			return (Player) creature;
		}
		if (creature instanceof Summon) {
			return ((Summon) creature).getMaster();
		}
		return null;
	}

	private void warnSuspiciousEquipment(Player player, Item item, String action, StatSnapshot before, StatSnapshot after) {
		if (before == null || after == null || item == null || item.getItemTemplate() == null) {
			return;
		}
		int declaredHp = declaredAdd(item, StatEnum.MAXHP);
		if ("equip".equals(action) && declaredHp > 0 && after.maxHp <= before.maxHp) {
			String line = timestamp() + '\t' + "type=MAXHP_NOT_APPLIED" + '\t' + playerInfo(player) + '\t' + itemInfo(item) + '\t' + "declaredMaxHp=" + declaredHp + '\t' + "beforeMaxHp=" + before.maxHp + '\t' + "afterMaxHp=" + after.maxHp + '\t' + modifiersInfo(item);
			appendLine("stat_mismatch.tsv", line);
			log.warn("[STAT_AUDIT_MISMATCH] MAXHP_NOT_APPLIED player={} itemId={} declaredMaxHp={} maxHp={}->{}", new Object[] { player.getName(), item.getItemId(), declaredHp, before.maxHp, after.maxHp });
		}
	}

	private int declaredAdd(Item item, StatEnum stat) {
		int value = 0;
		ItemTemplate template = item.getItemTemplate();
		List<StatFunction> modifiers = template.getModifiers();
		if (modifiers != null) {
			for (StatFunction f : modifiers) {
				if (f != null && f.getName() == stat) {
					value += f.getValue();
				}
			}
		}
		RealRandomBonus real = item.getRealRndBonus();
		if (real != null && real.getStats() != null) {
			for (RealRandomBonusStat rs : real.getStats()) {
				if (rs != null && rs.getStat() == stat) {
					value += rs.getValue();
				}
			}
		}
		return value;
	}

	private String modifiersInfo(Item item) {
		StringBuilder sb = new StringBuilder(256);
		ItemTemplate template = item.getItemTemplate();
		sb.append("templateModifiers=").append(functionsInfo(template.getModifiers()));
		RealRandomBonus real = item.getRealRndBonus();
		if (real != null) {
			sb.append("\trealRndStats=");
			if (real.getStats() == null || real.getStats().isEmpty()) {
				sb.append("-");
			}
			else {
				boolean first = true;
				for (RealRandomBonusStat rs : real.getStats()) {
					if (!first) {
						sb.append(',');
					}
					first = false;
					sb.append(rs.getStat()).append(':').append(rs.getValue()).append(rs.isFusion() ? ":fusion" : "");
				}
			}
			sb.append("\trealRndFunctions=").append(functionsInfo(real.getFunctions()));
			sb.append("\trealRndFusionFunctions=").append(functionsInfo(real.getFusionFunctions()));
		}
		return sb.toString();
	}

	private String functionsInfo(List<? extends IStatFunction> functions) {
		if (functions == null || functions.isEmpty()) {
			return "-";
		}
		StringBuilder sb = new StringBuilder(256);
		boolean first = true;
		for (IStatFunction f : functions) {
			if (f == null) {
				continue;
			}
			if (!first) {
				sb.append(',');
			}
			first = false;
			sb.append(f.getClass().getSimpleName()).append(':').append(f.getName()).append(':').append(f.getValue()).append(f.isBonus() ? ":bonus" : ":base");
		}
		return sb.length() == 0 ? "-" : sb.toString();
	}

	private String itemInfo(Item item) {
		ItemTemplate template = item.getItemTemplate();
		WeaponStats weaponStats = template.getWeaponStats();
		return "itemObj=" + item.getObjectId()
			+ "\titemId=" + item.getItemId()
			+ "\tname=" + safe(template.getName())
			+ "\tslot=" + item.getEquipmentSlot()
			+ "\tequipType=" + (template.getEquipmentType() != null ? template.getEquipmentType().name() : "-")
			+ "\titemType=" + (template.getItemType() != null ? template.getItemType().name() : "-")
			+ "\tweaponType=" + (template.getWeaponType() != null ? template.getWeaponType().name() : "-")
			+ "\tattackType=" + (template.getAttackType() != null ? template.getAttackType().name() : "-")
			+ "\tweaponStats=" + (weaponStats != null ? weaponStats.getMinDamage() + "-" + weaponStats.getMaxDamage() : "-")
			+ "\tmodifiers=" + (template.getModifiers() != null ? template.getModifiers().size() : 0)
			+ "\trealRnd=" + (item.getRealRndBonus() != null ? "yes" : "no");
	}

	private String effectInfo(Effect effect) {
		return "skillId=" + effect.getSkillId()
			+ "\tskillName=" + safe(effect.getSkillName())
			+ "\tskillLevel=" + effect.getSkillLevel()
			+ "\teffector=" + creatureInfo(effect.getEffector())
			+ "\teffected=" + creatureInfo(effect.getEffected());
	}

	private String playerInfo(Player player) {
		return "player=" + safe(player.getName()) + "\tobjectId=" + player.getObjectId() + "\tmap=" + player.getWorldId();
	}

	private String simpleCreatureInfo(Creature creature) {
		if (creature == null) {
			return "-";
		}
		return creature.getClass().getSimpleName() + ":" + creature.getObjectId() + ":" + safe(creature.getName());
	}

	private String creatureInfo(Creature creature) {
		if (creature == null) {
			return "-";
		}
		return creature.getClass().getSimpleName() + ":" + creature.getObjectId() + ":" + safe(creature.getName()) + ":hp=" + creature.getLifeStats().getCurrentHp() + "/" + creature.getLifeStats().getMaxHp();
	}

	private void appendLine(String fileName, String line) {
		if (!DeveloperConfig.STAT_AUDIT_FILE_ENABLE) {
			return;
		}
		synchronized (FILE_LOCK) {
			File dir = new File(DeveloperConfig.STAT_AUDIT_DIR);
			if (!dir.exists() && !dir.mkdirs()) {
				return;
			}
			File file = new File(dir, fileName);
			try (FileWriter writer = new FileWriter(file, true)) {
				writer.write(line);
				writer.write(System.lineSeparator());
			}
			catch (IOException e) {
				log.warn("Failed to write stat audit file " + fileName, e);
			}
		}
	}

	private static String timestamp() {
		return DATE.format(new Date());
	}

	private static String safe(String value) {
		if (value == null) {
			return "-";
		}
		return value.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
	}

	public static final class StatSnapshot {
		public final int maxHp;
		public final int currentHp;
		public final int maxMp;
		public final int currentMp;
		public final int pAttack;
		public final int mAttack;
		public final int pAccuracy;
		public final int mAccuracy;
		public final int pCrit;
		public final int mCrit;
		public final int pDef;
		public final int mDef;
		public final int mResist;
		public final int evasion;
		public final int block;
		public final int parry;
		public final int attackSpeed;
		public final int castingSpeed;
		public final int moveSpeed;
		public final int pvpAttack;
		public final int pvpDefense;
		public final int pveAttack;
		public final int pveDefense;

		private StatSnapshot(Player player) {
			PlayerGameStats stats = player != null ? player.getGameStats() : null;
			this.maxHp = safeStat(stats, StatReader.MAX_HP);
			this.currentHp = player != null && player.getLifeStats() != null ? player.getLifeStats().getCurrentHp() : 0;
			this.maxMp = safeStat(stats, StatReader.MAX_MP);
			this.currentMp = player != null && player.getLifeStats() != null ? player.getLifeStats().getCurrentMp() : 0;
			this.pAttack = safeStat(stats, StatReader.P_ATTACK);
			this.mAttack = safeStat(stats, StatReader.M_ATTACK);
			this.pAccuracy = safeStat(stats, StatReader.P_ACCURACY);
			this.mAccuracy = safeStat(stats, StatReader.M_ACCURACY);
			this.pCrit = safeStat(stats, StatReader.P_CRIT);
			this.mCrit = safeStat(stats, StatReader.M_CRIT);
			this.pDef = safeStat(stats, StatReader.P_DEF);
			this.mDef = safeStat(stats, StatReader.M_DEF);
			this.mResist = safeStat(stats, StatReader.M_RESIST);
			this.evasion = safeStat(stats, StatReader.EVASION);
			this.block = safeStat(stats, StatReader.BLOCK);
			this.parry = safeStat(stats, StatReader.PARRY);
			this.attackSpeed = safeStat(stats, StatReader.ATTACK_SPEED);
			this.castingSpeed = safeStat(stats, StatReader.CASTING_SPEED);
			this.moveSpeed = safeStat(stats, StatReader.MOVE_SPEED);
			this.pvpAttack = safeStat(stats, StatReader.PVP_ATTACK);
			this.pvpDefense = safeStat(stats, StatReader.PVP_DEFENSE);
			this.pveAttack = safeStat(stats, StatReader.PVE_ATTACK);
			this.pveDefense = safeStat(stats, StatReader.PVE_DEFENSE);
		}

		private static int safeStat(PlayerGameStats stats, StatReader reader) {
			if (stats == null) {
				return 0;
			}
			try {
				return reader.read(stats);
			}
			catch (Exception e) {
				return Integer.MIN_VALUE;
			}
		}

		public static String delta(StatSnapshot before, StatSnapshot after) {
			if (before == null && after == null) {
				return "stats=-";
			}
			if (before == null) {
				return "after=" + after.summary();
			}
			if (after == null) {
				return "before=" + before.summary();
			}
			return "maxHp=" + before.maxHp + "->" + after.maxHp
				+ "\tcurHp=" + before.currentHp + "->" + after.currentHp
				+ "\tmaxMp=" + before.maxMp + "->" + after.maxMp
				+ "\tcurMp=" + before.currentMp + "->" + after.currentMp
				+ "\tpAtk=" + before.pAttack + "->" + after.pAttack
				+ "\tmAtk=" + before.mAttack + "->" + after.mAttack
				+ "\tpAcc=" + before.pAccuracy + "->" + after.pAccuracy
				+ "\tmAcc=" + before.mAccuracy + "->" + after.mAccuracy
				+ "\tpCrit=" + before.pCrit + "->" + after.pCrit
				+ "\tmCrit=" + before.mCrit + "->" + after.mCrit
				+ "\tpDef=" + before.pDef + "->" + after.pDef
				+ "\tmDef=" + before.mDef + "->" + after.mDef
				+ "\tmRes=" + before.mResist + "->" + after.mResist
				+ "\tevasion=" + before.evasion + "->" + after.evasion
				+ "\tblock=" + before.block + "->" + after.block
				+ "\tparry=" + before.parry + "->" + after.parry
				+ "\tatkSpeed=" + before.attackSpeed + "->" + after.attackSpeed
				+ "\tcastSpeed=" + before.castingSpeed + "->" + after.castingSpeed
				+ "\tmoveSpeed=" + before.moveSpeed + "->" + after.moveSpeed
				+ "\tpvpAtk=" + before.pvpAttack + "->" + after.pvpAttack
				+ "\tpvpDef=" + before.pvpDefense + "->" + after.pvpDefense
				+ "\tpveAtk=" + before.pveAttack + "->" + after.pveAttack
				+ "\tpveDef=" + before.pveDefense + "->" + after.pveDefense;
		}

		private String summary() {
			return "maxHp=" + maxHp + ",maxMp=" + maxMp + ",pAtk=" + pAttack + ",mAtk=" + mAttack + ",pAcc=" + pAccuracy + ",mAcc=" + mAccuracy + ",pCrit=" + pCrit + ",mCrit=" + mCrit;
		}
	}

	private static interface StatReader {
		int read(PlayerGameStats stats);

		StatReader MAX_HP = new StatReader() { public int read(PlayerGameStats s) { return s.getMaxHp().getCurrent(); } };
		StatReader MAX_MP = new StatReader() { public int read(PlayerGameStats s) { return s.getMaxMp().getCurrent(); } };
		StatReader P_ATTACK = new StatReader() { public int read(PlayerGameStats s) { return s.getMainHandPAttack().getCurrent(); } };
		StatReader M_ATTACK = new StatReader() { public int read(PlayerGameStats s) { return s.getMAttack().getCurrent(); } };
		StatReader P_ACCURACY = new StatReader() { public int read(PlayerGameStats s) { return s.getMainHandPAccuracy().getCurrent(); } };
		StatReader M_ACCURACY = new StatReader() { public int read(PlayerGameStats s) { return s.getMAccuracy().getCurrent(); } };
		StatReader P_CRIT = new StatReader() { public int read(PlayerGameStats s) { return s.getMainHandPCritical().getCurrent(); } };
		StatReader M_CRIT = new StatReader() { public int read(PlayerGameStats s) { return s.getMCritical().getCurrent(); } };
		StatReader P_DEF = new StatReader() { public int read(PlayerGameStats s) { return s.getPDef().getCurrent(); } };
		StatReader M_DEF = new StatReader() { public int read(PlayerGameStats s) { return s.getMDef().getCurrent(); } };
		StatReader M_RESIST = new StatReader() { public int read(PlayerGameStats s) { return s.getMResist().getCurrent(); } };
		StatReader EVASION = new StatReader() { public int read(PlayerGameStats s) { return s.getEvasion().getCurrent(); } };
		StatReader BLOCK = new StatReader() { public int read(PlayerGameStats s) { return s.getBlock().getCurrent(); } };
		StatReader PARRY = new StatReader() { public int read(PlayerGameStats s) { return s.getParry().getCurrent(); } };
		StatReader ATTACK_SPEED = new StatReader() { public int read(PlayerGameStats s) { return s.getAttackSpeed().getCurrent(); } };
		StatReader CASTING_SPEED = new StatReader() { public int read(PlayerGameStats s) { return s.getBCastingTime().getCurrent(); } };
		StatReader MOVE_SPEED = new StatReader() { public int read(PlayerGameStats s) { return s.getMovementSpeed().getCurrent(); } };
		StatReader PVP_ATTACK = new StatReader() { public int read(PlayerGameStats s) { return s.getPVPAttack().getCurrent(); } };
		StatReader PVP_DEFENSE = new StatReader() { public int read(PlayerGameStats s) { return s.getPVPDefense().getCurrent(); } };
		StatReader PVE_ATTACK = new StatReader() { public int read(PlayerGameStats s) { return s.getPVEAttack().getCurrent(); } };
		StatReader PVE_DEFENSE = new StatReader() { public int read(PlayerGameStats s) { return s.getPVEDefense().getCurrent(); } };
	}
}
