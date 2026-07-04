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

	/**
	 * MameAion75 v69: fill the confirmed ArchSoft client stat slots for attack/defence display.
	 * This keeps the 7.7 packet length/order but replaces known zero placeholders with real stats.
	 */
	@Property(key = "gameserver.stats.archsoft_display.enable", defaultValue = "true")
	public static boolean ARCHSOFT_STATS_DISPLAY_ENABLE;

	@Property(key = "gameserver.master.server.enable", defaultValue = "false")
	public static boolean MASTER_SERVER;
}