/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class CustomConfig
{
	@Property(key = "gameserver.premium.notify", defaultValue = "false")
	public static boolean PREMIUM_NOTIFY;
	@Property(key = "gameserver.enchant.announce.enable", defaultValue = "true")
	public static boolean ENABLE_ENCHANT_ANNOUNCE;
	@Property(key = "gameserver.chat.factions.enable", defaultValue = "false")
	public static boolean SPEAKING_BETWEEN_FACTIONS;
	@Property(key = "gameserver.chat.whisper.level", defaultValue = "5")
	public static int LEVEL_TO_WHISPER;
	@Property(key = "gameserver.search.factions.mode", defaultValue = "false")
	public static boolean FACTIONS_SEARCH_MODE;
	@Property(key = "gameserver.search.gm.list", defaultValue = "false")
	public static boolean SEARCH_GM_LIST;
	@Property(key = "gameserver.cross.faction.binding", defaultValue = "false")
	public static boolean ENABLE_CROSS_FACTION_BINDING;
	@Property(key = "gameserver.simple.secondclass.enable", defaultValue = "false")
	public static boolean ENABLE_SIMPLE_2NDCLASS;
	@Property(key = "gameserver.skill.chain.triggerrate", defaultValue = "true")
	public static boolean SKILL_CHAIN_TRIGGERRATE;
	@Property(key = "gameserver.unstuck.delay", defaultValue = "3600")
	public static int UNSTUCK_DELAY;
	@Property(key = "gameserver.admin.dye.price", defaultValue = "1000000")
	public static int DYE_PRICE;
	@Property(key = "gameserver.oldnames.coupon.disable", defaultValue = "false")
	public static boolean OLD_NAMES_COUPON_DISABLED;
	@Property(key = "gameserver.oldnames.command.disable", defaultValue = "true")
	public static boolean OLD_NAMES_COMMAND_DISABLED;
	@Property(key = "gameserver.friendlist.size", defaultValue = "90")
	public static int FRIENDLIST_SIZE;
	@Property(key = "gameserver.basic.questsize.limit", defaultValue = "50")
	public static int BASIC_QUEST_SIZE_LIMIT;
	@Property(key = "gameserver.instances.enable", defaultValue = "true")
	public static boolean ENABLE_INSTANCES;
	@Property(key = "gameserver.instances.mob.aggro", defaultValue = "300030000,300160000,300170000,300190000,300200000,300230000,300250000,300280000,300360000,300450000,300510000,300520000,301310000,301390000,301400000,301520000,301550000,301630000,301631000,301632000,301640000,301650000,301700000,301720000,301730000,302000000,302330000,302340000,302350000,302430000,302440000,302450000,302460000,302470000,302480000,302490000,302500000,302510000,302520000,302540000,302550000,302560000,302570000,302580000,302610000,302620000,302630000,302640000,302650000,302660000,302670000,302680000,302690000,310050000,310090000,310160000,310161000,320100000,320110000,320160000,320161000,320170000")
	public static String INSTANCES_MOB_AGGRO;
	@Property(key = "gameserver.instances.cooldown.filter", defaultValue = "0")
	public static String INSTANCES_COOL_DOWN_FILTER;
	@Property(key = "gameserver.instances.cooldown.rate", defaultValue = "1")
	public static int INSTANCES_RATE;
	@Property(key = "gameserver.enable.kinah.cap", defaultValue = "false")
	public static boolean ENABLE_KINAH_CAP;
	@Property(key = "gameserver.kinah.cap.value", defaultValue = "1000000000")
	public static long KINAH_CAP_VALUE;
	@Property(key = "gameserver.noap.mentor.group", defaultValue = "false")
	public static boolean MENTOR_GROUP_AP;
	@Property(key = "gameserver.faction.free", defaultValue = "true")
	public static boolean FACTION_FREE_USE;
	@Property(key = "gameserver.faction.prices", defaultValue = "10000")
	public static int FACTION_USE_PRICE;
	@Property(key = "gameserver.faction.cmdchannel", defaultValue = "true")
	public static boolean FACTION_CMD_CHANNEL;
	@Property(key = "gameserver.dialog.show.id", defaultValue = "true")
	public static boolean ENABLE_SHOW_DIALOG_ID;
	@Property(key = "gameserver.reward.service.enable", defaultValue = "false")
	public static boolean ENABLE_REWARD_SERVICE;
	@Property(key = "gameserver.limits.enable", defaultValue = "true")
	public static boolean LIMITS_ENABLED;
	@Property(key = "gameserver.limits.update", defaultValue = "0 0 0 ? * *")
	public static String LIMITS_UPDATE;
    @Property(key = "gameserver.limits.rate", defaultValue="1")
    public static int LIMITS_RATE;
    @Property(key = "gameserver.chat.text.length", defaultValue="150")
    public static int MAX_CHAT_TEXT_LENGHT;
    @Property(key = "gameserver.abyssxform.afterlogout", defaultValue="false")
    public static boolean ABYSSXFORM_LOGOUT;
    @Property(key = "gameserver.instance.duel.enable", defaultValue="true")
    public static boolean INSTANCE_DUEL_ENABLE;
    @Property(key =" gameserver.ride.restriction.enable", defaultValue="true")
    public static boolean ENABLE_RIDE_RESTRICTION;
	@Property(key = "gameserver.challenge.tasks.enabled", defaultValue = "false")
	public static boolean CHALLENGE_TASKS_ENABLED;
	@Property(key = "gameserver.commands.admin.dot.enable", defaultValue = "false")
	public static boolean ENABLE_ADMIN_DOT_COMMANDS;
	@Property(key = "gameserver.rift.enable", defaultValue = "true")
	public static boolean RIFT_ENABLED;
	@Property(key = "gameserver.rift.duration", defaultValue = "1")
	public static int RIFT_DURATION;
	@Property(key = "gameserver.rift.appear.chance", defaultValue = "50")
    public static int RIFT_APPEAR_CHANCE;
	
	//Dispute Land
	@Property(key = "gameserver.dispute.land.enable", defaultValue = "true")
	public static boolean DISPUTE_LAND_ENABLED;
	@Property(key = "gameserver.dispute.land.schedule", defaultValue = "0 0 2 ? * *")
	public static String DISPUTE_LAND_SCHEDULE;
	@Property(key = "gameserver.dispute.land.duration", defaultValue = "2")
	public static int DISPUTE_LAND_DURATION;
	
	//Shugo Merchant League
	@Property(key = "gameserver.dynamic.rift.enable", defaultValue = "true")
	public static boolean DYNAMIC_RIFT_ENABLED;
	@Property(key = "gameserver.dynamic.rift.duration", defaultValue = "1")
	public static int DYNAMIC_RIFT_DURATION;
	
	//Conquest/Offering
	@Property(key = "gameserver.conquest.enable", defaultValue = "true")
	public static boolean CONQUEST_ENABLED;
	@Property(key = "gameserver.conquest.duration", defaultValue = "1")
	public static int CONQUEST_DURATION;
	
   /**
	* On official server "KOR/JAP/NA" every time a player disconnect from
	* server, after reconnect is he always teleport to "Bind Point"
	*/
	@Property(key = "gameserver.reconnect.to.bind.point", defaultValue = "true")
	public static boolean ENABLE_RECONNECT_TO_BIND_POINT;
	
	//Base Rewards
	@Property(key = "gameserver.base.rewards.enable", defaultValue = "true")
	public static boolean ENABLE_BASE_REWARDS;
	
	//Protector/Conqueror
	@Property(key = "gameserver.protector.conqueror.enable", defaultValue = "true")
	public static boolean PROTECTOR_CONQUEROR_ENABLE;
	@Property(key = "gameserver.protector.conqueror.handled.worlds", defaultValue = "")
	public static String PROTECTOR_CONQUEROR_WORLDS = "";
	@Property(key = "gameserver.protector.conqueror.kills.refresh", defaultValue = "5")
	public static int PROTECTOR_CONQUEROR_REFRESH;
	@Property(key = "gameserver.protector.conqueror.kills.decrease", defaultValue = "1")
	public static int PROTECTOR_CONQUEROR_DECREASE;
	@Property(key = "gameserver.protector.conqueror.level.diff", defaultValue = "10")
	public static int PROTECTOR_CONQUEROR_LEVEL_DIFF;
	@Property(key = "gameserver.protector.conqueror.1st.rank.kills", defaultValue = "5")
	public static int PROTECTOR_CONQUEROR_1ST_RANK_KILLS;
	@Property(key = "gameserver.protector.conqueror.2nd.rank.kills", defaultValue = "10")
	public static int PROTECTOR_CONQUEROR_2ND_RANK_KILLS;
	
	//Luna Shop.
	@Property(key = "gameserver.enable.luna.cap", defaultValue = "false")
	public static boolean ENABLE_LUNA_CAP;
	@Property(key = "gameserver.luna.cap.value", defaultValue = "9999999")
	public static long LUNA_CAP_VALUE;
	
	@Property(key = "gameserver.combine.minion", defaultValue = "50")
	public static float COMBINE_MINION;
	// MameAion private-server convenience: always-on burning and default transform unlock.
	@Property(key = "gameserver.mame.burning.enable", defaultValue = "true")
	public static boolean MAME_BURNING_ENABLED;
	@Property(key = "gameserver.mame.burning.target.level", defaultValue = "80")
	public static int MAME_BURNING_TARGET_LEVEL;
	@Property(key = "gameserver.mame.burning.login.delay.ms", defaultValue = "3000")
	public static int MAME_BURNING_LOGIN_DELAY_MS;
	@Property(key = "gameserver.mame.burning.grant.starter.kit", defaultValue = "true")
	public static boolean MAME_BURNING_GRANT_STARTER_KIT;
	@Property(key = "gameserver.mame.burning.grant.stigmas", defaultValue = "true")
	public static boolean MAME_BURNING_GRANT_STIGMAS;
	@Property(key = "gameserver.mame.burning.grant.ap", defaultValue = "2000000")
	public static int MAME_BURNING_GRANT_AP;
	@Property(key = "gameserver.mame.burning.grant.gp", defaultValue = "3200")
	public static int MAME_BURNING_GRANT_GP;
	@Property(key = "gameserver.mame.transform.unlock.nonultimate", defaultValue = "true")
	public static boolean MAME_TRANSFORM_UNLOCK_NON_ULTIMATE;

	// MameAion convenience: free remodel / FFA / timeless hourglass worlds.
	@Property(key = "gameserver.mame.remodel.free", defaultValue = "true")
	public static boolean MAME_REMODEL_FREE;
	@Property(key = "gameserver.mame.ffa.enable", defaultValue = "true")
	public static boolean MAME_FFA_ENABLED;
	@Property(key = "gameserver.mame.ffa.world", defaultValue = "320150000")
	public static int MAME_FFA_WORLD;
	@Property(key = "gameserver.mame.ffa.x", defaultValue = "385")
	public static float MAME_FFA_X;
	@Property(key = "gameserver.mame.ffa.y", defaultValue = "506")
	public static float MAME_FFA_Y;
	@Property(key = "gameserver.mame.ffa.z", defaultValue = "66")
	public static float MAME_FFA_Z;
	@Property(key = "gameserver.mame.ffa.h", defaultValue = "0")
	public static byte MAME_FFA_H;
	@Property(key = "gameserver.mame.worldplaytime.timeless", defaultValue = "true")
	public static boolean MAME_WORLDPLAYTIME_TIMELESS;
	@Property(key = "gameserver.mame.worldplaytime.timeless.worlds", defaultValue = "800030000,800040000,800050000,800060000,800070000")
	public static String MAME_WORLDPLAYTIME_TIMELESS_WORLDS;
	@Property(key = "gameserver.mame.worldplaytime.default.minutes", defaultValue = "300")
	public static int MAME_WORLDPLAYTIME_DEFAULT_MINUTES;

}