package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

import java.util.Calendar;

public class GSConfig
{
	@Property(key = "gameserver.country.code", defaultValue = "1")
	public static int SERVER_COUNTRY_CODE;
    @Property(key="gameserver.name", defaultValue="Encom")
    public static String SERVER_NAME;
    @Property(key="gameserver.players.max.level", defaultValue="80")
    public static int PLAYER_MAX_LEVEL;
	@Property(key = "gameserver.timezone", defaultValue = "")
	public static String TIME_ZONE_ID = Calendar.getInstance().getTimeZone().getID();
	@Property(key = "gameserver.chatserver.enable", defaultValue = "false")
	public static boolean ENABLE_CHAT_SERVER;
	@Property(key = "gameserver.revisiondisplay.enable", defaultValue = "false")
	public static boolean SERVER_MOTD_DISPLAYREV;
    @Property(key="gameserver.character.creation.mode", defaultValue="0")
    public static int CHARACTER_CREATION_MODE;
	@Property(key = "gameserver.character.limit.count", defaultValue = "8")
	public static int CHARACTER_LIMIT_COUNT;
	@Property(key="gameserver.character.faction.limitation.mode", defaultValue="0")
	public static int CHARACTER_FACTION_LIMITATION_MODE;
	@Property(key="gameserver.ratio.limitation.enable", defaultValue="false")
	public static boolean ENABLE_RATIO_LIMITATION;
	@Property(key="gameserver.ratio.min.value", defaultValue="60")
	public static int RATIO_MIN_VALUE;
	@Property(key="gameserver.ratio.min.required.level", defaultValue="10")
	public static int RATIO_MIN_REQUIRED_LEVEL;
	@Property(key="gameserver.ratio.min.characters_count", defaultValue="50")
	public static int RATIO_MIN_CHARACTERS_COUNT;
	@Property(key="gameserver.ratio.high_player_count.disabling", defaultValue="500")
	public static int RATIO_HIGH_PLAYER_COUNT_DISABLING;
	@Property(key = "gameserver.abyssranking.small.cache", defaultValue = "false")
	public static boolean ABYSSRANKING_SMALL_CACHE;
	@Property(key = "gameserver.character.reentry.time", defaultValue = "20")
	public static int CHARACTER_REENTRY_TIME;

	/**
	 * Low-latency tolerance for clients sending the next skill slightly before
	 * the server-side animation gate expires. Does not modify skill cooldowns.
	 */
	@Property(key = "gameserver.skill.next_skill_use_grace_millis", defaultValue = "300")
	public static int NEXT_SKILL_USE_GRACE_MILLIS;

	/**
	 * Audit only clearly suspicious next-skill packets. Set to 0 to log every early packet.
	 */
	@Property(key = "gameserver.skill.next_skill_use_audit_threshold_millis", defaultValue = "500")
	public static int NEXT_SKILL_USE_AUDIT_THRESHOLD_MILLIS;

	/**
	 * MameAion75 v69: use ArchSoft-style 6.x/7.x stat formulas for damage.
	 * The formula uses Physical/Magical Power Boost + PvE/PvP Power Boost versus
	 * the matching resist stats. It deliberately does not mix PvP defence into
	 * evasion/parry/block/crit/resist checks.
	 */

	@Property(key = "gameserver.stats.archsoft_core.enable", defaultValue = "true")
	public static boolean ARCHSOFT_STATS_CORE_ENABLE;
	@Property(key = "gameserver.damage.archsoft_formula.enable", defaultValue = "true")
	public static boolean ARCHSOFT_DAMAGE_FORMULA_ENABLE;

	@Property(key = "gameserver.damage.archsoft_pvp_reduction", defaultValue = "0.26")
	public static float ARCHSOFT_DAMAGE_PVP_REDUCTION;


	@Property(key = "gameserver.combat.archsoft_avoidance_caps.enable", defaultValue = "false")
	public static boolean ARCHSOFT_AVOIDANCE_CAPS_ENABLE;

	/**
	 * MameAion75 v77b: ReFly 6.0/7.0 damage formula strict mode.
	 * Implements the PDF equations directly: auto attacks, skill damage, A_net 0..20000,
	 * old PvE/PvP attack cap/net floor, PvP 0.26, shard 1.25, movement and level rules.
	 */
	@Property(key = "gameserver.damage.refly_formula.enable", defaultValue = "true")
	public static boolean REFLY_DAMAGE_FORMULA_ENABLE;

	/**
	 * ReFly A_net upper cap. Official strict value is 20,000.
	 * Set to 0 to disable the upper cap on custom high-stat servers.
	 */
	@Property(key = "gameserver.damage.refly_anet_cap", defaultValue = "0")
	public static int REFLY_DAMAGE_ANET_CAP;

	/**
	 * v87: use the ReFly Stage2 official coefficient mapping.
	 * false keeps the previous v83/v84 compatibility approximation.
	 */
	@Property(key = "gameserver.damage.refly_stage2.enable", defaultValue = "true")
	public static boolean REFLY_DAMAGE_STAGE2_ENABLE; // Deprecated in v88: ReFly formula always uses Stage2 while REFLY_DAMAGE_FORMULA_ENABLE is true.

	@Property(key = "gameserver.damage.refly_debug.enable", defaultValue = "false")
	public static boolean REFLY_DAMAGE_DEBUG_ENABLE;

	/**
	 * MameAion75 v69: fill the confirmed ArchSoft client stat slots for attack/defence display.
	 * This keeps the 7.7 packet length/order but replaces known zero placeholders with real stats.
	 */
	@Property(key = "gameserver.stats.archsoft_display.enable", defaultValue = "true")
	public static boolean ARCHSOFT_STATS_DISPLAY_ENABLE;

	/**
	 * v78: avoid CC2/KR profile red negative values caused by mismatched current/base
	 * comparison slots. When ArchSoft display is enabled, base comparison fields for
	 * combat stats are written from the same stat family as the current fields.
	 */
	@Property(key = "gameserver.stats.archsoft_display.force_base_current", defaultValue = "true")
	public static boolean ARCHSOFT_STATS_DISPLAY_FORCE_BASE_CURRENT;

	/**
	 * v88: avoid writing duplicate 7.x modern magic attack/defence values into the
	 * secondary/unknown current slots. Some 7.7 clients appear to merge adjacent
	 * attack/defence fields in the profile window when both blocks are populated.
	 */
	@Property(key = "gameserver.stats.archsoft_display.single_source.enable", defaultValue = "true")
	public static boolean ARCHSOFT_STATS_DISPLAY_SINGLE_SOURCE_ENABLE;

	/**
	 * MameAion75 v90: server-side scheduled restart control.
	 * Keep daily reset available, but do not restart the game server unless explicitly enabled.
	 */
	@Property(key = "gameserver.restart.service.enable", defaultValue = "true")
	public static boolean RESTART_SERVICE_ENABLE;

	@Property(key = "gameserver.restart.daily_reset.enable", defaultValue = "true")
	public static boolean RESTART_DAILY_RESET_ENABLE;

	@Property(key = "gameserver.restart.daily_reset.schedule", defaultValue = "0 0 9 ? * * *")
	public static String RESTART_DAILY_RESET_SCHEDULE;

	@Property(key = "gameserver.restart.daily_reset.restart_after_reset", defaultValue = "false")
	public static boolean RESTART_DAILY_RESET_RESTART_AFTER_RESET;

	@Property(key = "gameserver.restart.server_reboot.enable", defaultValue = "false")
	public static boolean RESTART_SERVER_REBOOT_ENABLE;

	@Property(key = "gameserver.restart.server_reboot.schedule", defaultValue = "0 0 0/12 ? * * *")
	public static String RESTART_SERVER_REBOOT_SCHEDULE;

	@Property(key = "gameserver.restart.shutdown.delay", defaultValue = "300")
	public static int RESTART_SHUTDOWN_DELAY;

	@Property(key = "gameserver.restart.shutdown.announce_interval", defaultValue = "20")
	public static int RESTART_SHUTDOWN_ANNOUNCE_INTERVAL;

	@Property(key = "gameserver.master.server.enable", defaultValue = "false")
	public static boolean MASTER_SERVER;
}