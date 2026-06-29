/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.administration;

import com.aionemu.commons.configuration.Property;

/**
 * @author ATracer
 */
public class DeveloperConfig {

	/**
	 * if false - not spawns will be loaded
	 */
	@Property(key = "gameserver.developer.spawn.enable", defaultValue = "true")
	public static boolean SPAWN_ENABLE;
	/**
	 * if true - checks spawns being outside any known zones
	 */
	@Property(key = "gameserver.developer.spawn.check", defaultValue = "false")
	public static boolean SPAWN_CHECK;
	/**
	 * if set, adds specified stat bonus for items with random bonusess
	 */
	@Property(key = "gameserver.developer.itemstat.id", defaultValue = "0")
	public static int ITEM_STAT_ID;
	/**
	 * Show sended cm/sm packets in game server log
	 */
	@Property(key = "gameserver.developer.showpackets.enable", defaultValue = "false")
	public static boolean SHOW_PACKETS;
	/**
	 * Display Packets Name in Chat Window
	 */
	@Property(key = "gameserver.developer.show.packetnames.inchat.enable", defaultValue = "false")
	public static boolean SHOW_PACKET_NAMES_INCHAT;
	/**
	 * Display Packets Hex-Bytes in Chat Window
	 */
	@Property(key = "gameserver.developer.show.packetbytes.inchat.enable", defaultValue = "false")
	public static boolean SHOW_PACKET_BYTES_INCHAT;
	/**
	 * How many Packet Bytes should be shown in Chat Window? Default: 200-Hexed bytes
	 */
	@Property(key = "gameserver.developer.show.packetbytes.inchat.total", defaultValue = "200")
	public static int TOTAL_PACKET_BYTES_INCHAT;
	/**
	 * Filters which Packets should be shown in Chat Windows? Default: * e.g. SM_MOVE, CM_CASTSPELL, CM_ATTACK
	 */
	@Property(key = "gameserver.developer.filter.packets.inchat", defaultValue = "*")
	public static String FILTERED_PACKETS_INCHAT;
	/**
	 * if Player Access Level is meet, display Packets-Name or Hex-Bytes in Chat Window Tip: Player Access-Level higher than or equal to 3 is recommended 10 - Server-Owner 9 - Server-CoOwner 8 -
	 * Server-Admin 7 - Server-CoAdmin 6 - Developer 5 - Admin 4 - Head-GM 3 - Senior-GM 2 - Junior-GM 1 - Supporter 0 - Players
	 */
	@Property(key = "gameserver.developer.show.packets.inchat.accesslevel", defaultValue = "6")
	public static int SHOW_PACKETS_INCHAT_ACCESSLEVEL;
	/**
	 * Lightweight per-connection protocol trace for 7.8 opcode/JDK25 migration.
	 */
	@Property(key = "gameserver.developer.protocol.trace.enable", defaultValue = "true")
	public static boolean PROTOCOL_TRACE_ENABLE;
	@Property(key = "gameserver.developer.protocol.trace.dir", defaultValue = "log/protocol")
	public static String PROTOCOL_TRACE_DIR;
	@Property(key = "gameserver.developer.protocol.trace.last.packets", defaultValue = "200")
	public static int PROTOCOL_TRACE_LAST_PACKETS;
	@Property(key = "gameserver.developer.protocol.trace.hex.bytes", defaultValue = "128")
	public static int PROTOCOL_TRACE_HEX_BYTES;
	@Property(key = "gameserver.developer.protocol.trace.dump.on.disconnect", defaultValue = "true")
	public static boolean PROTOCOL_TRACE_DUMP_ON_DISCONNECT;
	@Property(key = "gameserver.developer.protocol.trace.dump.on.exception", defaultValue = "true")
	public static boolean PROTOCOL_TRACE_DUMP_ON_EXCEPTION;
	@Property(key = "gameserver.developer.protocol.trace.enterworld", defaultValue = "true")
	public static boolean PROTOCOL_TRACE_ENTER_WORLD;
	@Property(key = "gameserver.developer.protocol.trace.log.client.packets", defaultValue = "false")
	public static boolean PROTOCOL_TRACE_LOG_CLIENT_PACKETS;
	@Property(key = "gameserver.developer.protocol.trace.log.server.packets", defaultValue = "false")
	public static boolean PROTOCOL_TRACE_LOG_SERVER_PACKETS;

	@Property(key = "gameserver.developer.equipment.trace.enable", defaultValue = "true")
	public static boolean EQUIPMENT_TRACE_ENABLE;
	@Property(key = "gameserver.developer.equipment.trace.console", defaultValue = "true")
	public static boolean EQUIPMENT_TRACE_CONSOLE;
	@Property(key = "gameserver.developer.equipment.trace.verbose", defaultValue = "true")
	public static boolean EQUIPMENT_TRACE_VERBOSE;
	@Property(key = "gameserver.developer.combat.trace.enable", defaultValue = "true")
	public static boolean COMBAT_TRACE_ENABLE;
	@Property(key = "gameserver.developer.combat.trace.console", defaultValue = "true")
	public static boolean COMBAT_TRACE_CONSOLE;
	@Property(key = "gameserver.developer.combat.trace.player.only", defaultValue = "true")
	public static boolean COMBAT_TRACE_PLAYER_ONLY;
	@Property(key = "gameserver.developer.combat.suppress.zero.damage.npc.to.player", defaultValue = "true")
	public static boolean COMBAT_SUPPRESS_ZERO_DAMAGE_NPC_TO_PLAYER;
	@Property(key = "gameserver.developer.buff.trace.enable", defaultValue = "true")
	public static boolean BUFF_TRACE_ENABLE;
	@Property(key = "gameserver.developer.buff.trace.console", defaultValue = "true")
	public static boolean BUFF_TRACE_CONSOLE;
	@Property(key = "gameserver.developer.stat.audit.file.enable", defaultValue = "true")
	public static boolean STAT_AUDIT_FILE_ENABLE;
	@Property(key = "gameserver.developer.stat.audit.dir", defaultValue = "log/devprobe")
	public static String STAT_AUDIT_DIR;

	@Property(key = "gameserver.developer.skill.trace.enable", defaultValue = "true")
	public static boolean SKILL_TRACE_ENABLE;
	@Property(key = "gameserver.developer.skill.trace.console", defaultValue = "true")
	public static boolean SKILL_TRACE_CONSOLE;
	@Property(key = "gameserver.developer.effect.trace.all.enable", defaultValue = "true")
	public static boolean EFFECT_TRACE_ALL_ENABLE;
	@Property(key = "gameserver.developer.effect.trace.console", defaultValue = "false")
	public static boolean EFFECT_TRACE_CONSOLE;
	@Property(key = "gameserver.developer.npc.visibility.trace.enable", defaultValue = "true")
	public static boolean NPC_VISIBILITY_TRACE_ENABLE;
	@Property(key = "gameserver.developer.npc.visibility.trace.console", defaultValue = "false")
	public static boolean NPC_VISIBILITY_TRACE_CONSOLE;
	@Property(key = "gameserver.developer.ai.walk.trace.enable", defaultValue = "true")
	public static boolean AI_WALK_TRACE_ENABLE;
	@Property(key = "gameserver.developer.ai.walk.trace.console", defaultValue = "false")
	public static boolean AI_WALK_TRACE_CONSOLE;
	@Property(key = "gameserver.developer.ai.default.think.enable", defaultValue = "true")
	public static boolean AI_DEFAULT_THINK_ENABLE;
	@Property(key = "gameserver.developer.packet.attackstatus.skill.skin", defaultValue = "false")
	public static boolean PACKET_ATTACK_STATUS_SKILL_SKIN;

	@Property(key = "gameserver.developer.stat.cap.trace.enable", defaultValue = "true")
	public static boolean STAT_CAP_TRACE_ENABLE;
	@Property(key = "gameserver.developer.stat.cap.trace.console", defaultValue = "true")
	public static boolean STAT_CAP_TRACE_CONSOLE;

}
