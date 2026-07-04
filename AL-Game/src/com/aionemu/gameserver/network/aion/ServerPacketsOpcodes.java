package com.aionemu.gameserver.network.aion;

import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.network.aion.serverpackets.unk_60.*;
import com.aionemu.gameserver.network.aion.serverpackets.unk_60.SM_UNK_A5;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerPacketsOpcodes
{
	private static Map<Class<? extends AionServerPacket>, Integer> opcodes = new HashMap<Class<? extends AionServerPacket>, Integer>();

	static {
		Set<Integer> idSet = new HashSet<Integer>();
		addPacketOpcode(SM_KEY.class, 0x48, idSet); //7.0 KR
		addPacketOpcode(SM_VERSION_CHECK.class, 0x00, idSet); //7.0 KR
		addPacketOpcode(SM_TIME_CHECK.class, 0x27, idSet); //7.0 KR
		addPacketOpcode(SM_MAC_INFO.class, 0x168, idSet); //7.0 KR
		addPacketOpcode(SM_L2AUTH_LOGIN_CHECK.class, 0xc7, idSet); //7.0 KR
		addPacketOpcode(SM_ACCOUNT_PROPERTIES.class, 0xf0, idSet); //7.0 KR
		addPacketOpcode(SM_CHARACTER_LIST.class, 0xc8, idSet); //7.0 KR
		addPacketOpcode(SM_PONG.class, 0x8e, idSet); //7.0 KR
		addPacketOpcode(SM_CREATE_CHARACTER.class, 0xc9, idSet); //7.0 KR
		addPacketOpcode(SM_NICKNAME_CHECK_RESPONSE.class, 0xea, idSet); //7.0 KR
		addPacketOpcode(SM_MAY_LOGIN_INTO_GAME.class, 0x89, idSet); //7.0 KR
		addPacketOpcode(SM_CHARACTER_SELECT.class, 0xb1, idSet); //7.0 KR
		addPacketOpcode(SM_RECONNECT_KEY.class, 0x101, idSet); //7.0 KR
		addPacketOpcode(SM_SKILL_LIST.class, 0x2c, idSet); //7.0 KR
		addPacketOpcode(SM_QUEST_COMPLETED_LIST.class, 0x7b, idSet); //7.0 KR
		addPacketOpcode(SM_QUEST_LIST.class, 0x47, idSet); //7.0 KR
		addPacketOpcode(SM_INVENTORY_UPDATE_ITEM.class, 0x1d, idSet); //7.0 KR
		addPacketOpcode(SM_TITLE_INFO.class, 0xb0, idSet); //7.0 KR
		addPacketOpcode(SM_MOTION.class, 0x94, idSet); //7.0 KR
		addPacketOpcode(SM_ENTER_WORLD_CHECK.class, 0x0d, idSet); //7.0 KR
		addPacketOpcode(SM_PACKAGE_INFO_NOTIFY.class, 0x10c, idSet); //7.0 KR
		addPacketOpcode(SM_MACRO_LIST.class, 0xe8, idSet); //7.0 KR
		addPacketOpcode(SM_ITEM_COOLDOWN.class, 0x67, idSet); //7.0 KR
		addPacketOpcode(SM_INVENTORY_INFO.class, 0x1a, idSet); //7.0 KR
		addPacketOpcode(SM_CHANNEL_INFO.class, 0xe6, idSet); //7.0 KR
		addPacketOpcode(SM_STATS_INFO.class, 0x01, idSet); //7.0 KR
		addPacketOpcode(SM_BIND_POINT_INFO.class, 0xec, idSet); //7.0 KR
		addPacketOpcode(SM_PLAYER_SPAWN.class, 0x0f, idSet); //7.0 KR
		addPacketOpcode(SM_TOWNS_LIST.class, 0xe3, idSet); //7.0 KR
		addPacketOpcode(SM_GAME_TIME.class, 0x26, idSet); //7.0 KR
		addPacketOpcode(SM_WAREHOUSE_INFO.class, 0xa8, idSet); //7.0 KR
		addPacketOpcode(SM_EMOTION_LIST.class, 0x4f, idSet); //7.0 KR
		addPacketOpcode(SM_SIEGE_LOCATION_INFO.class, 0xd1, idSet); //7.0 KR
		addPacketOpcode(SM_RIFT_ANNOUNCE.class, 0xed, idSet); //7.0 KR
		addPacketOpcode(SM_PRICES.class, 0xfe, idSet); //7.0 KR
		addPacketOpcode(SM_HOTSPOT_TELEPORT.class, 0x12a, idSet); //7.0 KR
		addPacketOpcode(SM_SYSTEM_MESSAGE.class, 0x19, idSet); //7.0 KR
		addPacketOpcode(SM_FRIEND_LIST.class, 0x84, idSet); //7.0 KR
		addPacketOpcode(SM_FRIEND_NOTIFY.class, 0xe2, idSet); //7.0 KR
		addPacketOpcode(SM_AUTO_GROUP.class, 0x7a, idSet); //7.0 KR
		addPacketOpcode(SM_INSTANCE_INFO.class, 0x8d, idSet); //7.0 KR
		addPacketOpcode(SM_MINION.class, 0x16C, idSet); //7.0 KR
		addPacketOpcode(SM_ABYSS_RANK.class, 0xef, idSet); //7.0 KR
		addPacketOpcode(SM_LUNA_SHOP_LIST.class, 0x149, idSet); //7.0 KR
		//addPacketOpcode(SM_ABYSS_ARTIFACT_INFO.class, 0x60, idSet); //7.0 KR
		addPacketOpcode(SM_MAIL_SERVICE.class, 0xa1, idSet); //7.0 KR
		addPacketOpcode(SM_STATUPDATE_HP.class, 0x03, idSet); //7.0 KR
		addPacketOpcode(SM_ICON_INFO.class, 0xaf, idSet); //7.0 KR
		addPacketOpcode(SM_RECIPE_LIST.class, 0xcf, idSet); //7.0 KR
		addPacketOpcode(SM_BROKER_SERVICE.class, 0x92, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_OWNER_INFO.class, 0x109, idSet); //7.0 KR
		addPacketOpcode(SM_FRIEND_STATUS.class, 0xe4, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_SEND_EMBLEM_DATA.class, 0xd6, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_SEND_EMBLEM.class, 0xd5, idSet); //7.0 KR
		addPacketOpcode(SM_MESSAGE.class, 0x18, idSet); //7.0 KR
		addPacketOpcode(SM_CONQUEROR_PROTECTOR.class, 0x54, idSet); //7.0 KR
		addPacketOpcode(SM_PLAYER_INFO.class, 0x20, idSet); //7.0 KR
		addPacketOpcode(SM_PLAYER_STATE.class, 0x44, idSet); //7.0 KR
		addPacketOpcode(SM_ABNORMAL_STATE.class, 0x31, idSet); //7.0 KR
		addPacketOpcode(SM_NPC_INFO.class, 0x0e, idSet); //7.0 KR
		addPacketOpcode(SM_GATHERABLE_INFO.class, 0x11, idSet); //7.0 KR
		addPacketOpcode(SM_EMOTION_NPC.class, 0xc6, idSet); //7.0 KR
		addPacketOpcode(SM_ATTACK_STATUS.class, 0x05, idSet); //7.0 KR
		addPacketOpcode(SM_TARGET_SELECTED.class, 0x29, idSet); //7.0 KR
		addPacketOpcode(SM_QUEST_ACTION.class, 0x7c, idSet); //7.0 KR
		addPacketOpcode(SM_EMOTION.class, 0x25, idSet); //7.0 KR
		addPacketOpcode(SM_MOVE.class, 0x37, idSet); //7.0 KR
		addPacketOpcode(SM_NEARBY_QUESTS.class, 0x7f, idSet); //7.0 KR
		addPacketOpcode(SM_LOOKATOBJECT.class, 0x28, idSet); //7.0 KR
		addPacketOpcode(SM_ATTACK.class, 0x36, idSet); //7.0 KR
		addPacketOpcode(SM_ABYSS_RANK_UPDATE.class, 0x88, idSet); //7.0 KR
		addPacketOpcode(SM_CHAT_INIT.class, 0xe7, idSet); //7.0 KR
		addPacketOpcode(SM_CASTSPELL.class, 0x21, idSet); //7.0 KR
		addPacketOpcode(SM_CASTSPELL_RESULT.class, 0x2b, idSet); //7.0 KR
		addPacketOpcode(SM_UPDATE_PLAYER_APPEARANCE.class, 0x24, idSet); //7.0 KR
		addPacketOpcode(SM_CUBE_UPDATE.class, 0x82, idSet); //7.0 KR
		addPacketOpcode(SM_DELETE.class, 0x16, idSet); //7.0 KR
		addPacketOpcode(SM_STATUPDATE_EXP.class, 0x08, idSet); //7.0 KR
		addPacketOpcode(SM_FLY_TIME.class, 0xf6, idSet); //7.0 KR
		addPacketOpcode(SM_DIALOG_WINDOW.class, 0x3c, idSet); //7.0 KR
		addPacketOpcode(SM_LOOT_STATUS.class, 0xcd, idSet); //7.0 KR
		addPacketOpcode(SM_ABNORMAL_EFFECT.class, 0x32, idSet); //7.0 KR
		addPacketOpcode(SM_INVENTORY_ADD_ITEM.class, 0x1b, idSet); //7.0 KR
		addPacketOpcode(SM_QUIT_RESPONSE.class, 0x62, idSet); //7.0 KR
		addPacketOpcode(SM_STATUPDATE_MP.class, 0x04, idSet); //7.0 KR
		addPacketOpcode(SM_ABYSS_RANKING_PLAYERS.class, 0x8a, idSet); //7.0 KR
		addPacketOpcode(SM_ABYSS_RANKING_LEGIONS.class, 0x8b, idSet); //7.0 KR
		addPacketOpcode(SM_UI_SETTINGS.class, 0x1e, idSet); //7.0 KR
		addPacketOpcode(SM_BLOCK_LIST.class, 0xe1, idSet); //7.0 KR
		addPacketOpcode(SM_SHIELD_EFFECT.class, 0xda, idSet); //7.0 KR
		addPacketOpcode(SM_INFLUENCE_RATIO.class, 0x55, idSet); //7.0 KR
		addPacketOpcode(SM_HEADING_UPDATE.class, 0x39, idSet); //7.0 KR
		addPacketOpcode(SM_TRANSFORM.class, 0x3a, idSet); //7.0 KR
		addPacketOpcode(SM_TRANSFORM_LIST.class, 0x170, idSet); //7.0 KR
		addPacketOpcode(SM_MONSTER_CORE_ADD.class, 0x176, idSet); //7.0 KR
		addPacketOpcode(SM_MONSTER_CORE_LIST.class, 0x175, idSet); //7.0 KR
		addPacketOpcode(SM_DISPUTE_LAND.class, 0x11D, idSet); //7.0 KR
		addPacketOpcode(SM_WEATHER.class, 0x43, idSet); //7.0 KR
		addPacketOpcode(SM_PET.class, 0x65, idSet); //7.0 KR
		addPacketOpcode(SM_WAREHOUSE_ADD_ITEM.class, 0xa9, idSet); //7.0 KR
		addPacketOpcode(SM_DELETE_WAREHOUSE_ITEM.class, 0xaa, idSet); //7.0 KR
		addPacketOpcode(SM_DELETE_ITEM.class, 0x1c, idSet); //7.0 KR
		addPacketOpcode(SM_PLAY_MOVIE.class, 0x69, idSet); //7.0 KR
		addPacketOpcode(SM_ITEM_USAGE_ANIMATION.class, 0xb7, idSet); //7.0 KR
		addPacketOpcode(SM_TRADELIST.class, 0xff, idSet); //7.0 KR
		addPacketOpcode(SM_VIEW_PLAYER_DETAILS.class, 0x41, idSet); //7.0 KR
		addPacketOpcode(SM_PLAYER_SEARCH.class, 0xd3, idSet); //7.0 KR
		addPacketOpcode(SM_FRIEND_RESPONSE.class, 0xDF, idSet); //7.0 KR
		addPacketOpcode(SM_BLOCK_RESPONSE.class, 0xe0, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_UPDATE_EMBLEM.class, 0xd7, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_ADD_MEMBER.class, 0x6F, idSet); //7.0 KR
		addPacketOpcode(SM_SELECT_ITEM.class, 0x11e, idSet); //7.0 KR
		addPacketOpcode(SM_SELECT_ITEM_ADD.class, 0x120, idSet); //7.0 KR
		addPacketOpcode(SM_MACRO_RESULT.class, 0xe9, idSet); //7.0 KR
		addPacketOpcode(SM_UPDATE_NOTE.class, 0x68, idSet); //7.0 KR
		addPacketOpcode(SM_GROUP_INFO.class, 0x5A, idSet); //7.0 KR
		addPacketOpcode(SM_GROUP_MEMBER_INFO.class, 0x5b, idSet); //7.0 KR
		addPacketOpcode(SM_SHOW_BRAND.class, 0xFB, idSet); //7.0 KR
		addPacketOpcode(SM_DIE.class, 0xC1, idSet); //7.0 KR
		addPacketOpcode(SM_LOOT_ITEMLIST.class, 0xce, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_INFO.class, 0x6E, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_UPDATE_NICKNAME.class, 0x0b, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_UPDATE_SELF_INTRO.class, 0x77, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_EDIT.class, 0x9E, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_LEAVE_MEMBER.class, 0x70, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_UPDATE_MEMBER.class, 0x71, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_TABS.class, 0x0c, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_MEMBERLIST.class, 0x9D, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_SEARCH.class, 0x135, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_REQUEST.class, 0x13A, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_REQUEST_INFO.class, 0x137, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_REQUEST_LIST.class, 0x138, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_REQUEST_PLAYER.class, 0x139, idSet); //7.0 KR
		addPacketOpcode(SM_RESTORE_CHARACTER.class, 0xCB, idSet); //7.0 KR
		addPacketOpcode(SM_DELETE_CHARACTER.class, 0xCA, idSet); //7.0 KR
		addPacketOpcode(SM_TARGET_UPDATE.class, 0x51, idSet); //7.0 KR
		addPacketOpcode(SM_CUSTOM_SETTINGS.class, 0xB8, idSet); //7.0 KR
		addPacketOpcode(SM_PET_EMOTE.class, 0xBB, idSet); //7.0 KR
		addPacketOpcode(SM_DUEL.class, 0xB9, idSet); //7.0 KR
		addPacketOpcode(SM_LEAVE_GROUP_MEMBER.class, 0xf9, idSet); //7.0 KR
		addPacketOpcode(SM_GROUP_LOOT.class, 0x87, idSet); //7.0 KR
		addPacketOpcode(SM_EXCHANGE_REQUEST.class, 0x4A, idSet); //7.0 KR
		addPacketOpcode(SM_EXCHANGE_ADD_ITEM.class, 0x4B, idSet); //7.0 KR
		addPacketOpcode(SM_EXCHANGE_ADD_KINAH.class, 0x4D, idSet); //7.0 KR
		addPacketOpcode(SM_EXCHANGE_CONFIRMATION.class, 0x4E, idSet); //7.0 KR
		addPacketOpcode(SM_PING_RESPONSE.class, 0x80, idSet); //7.0 KR
		addPacketOpcode(SM_SELL_ITEM.class, 0x3E, idSet); //7.0 KR
		addPacketOpcode(SM_LEVEL_UPDATE.class, 0x46, idSet); //7.0 KR
		addPacketOpcode(SM_WAREHOUSE_UPDATE_ITEM.class, 0xAB, idSet); //7.0 KR
		addPacketOpcode(SM_TELEPORT_MAP.class, 0xC4, idSet); //7.0 KR
		addPacketOpcode(SM_TELEPORT_LOC.class, 0x14, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_OBJECTS.class, 0x110, idSet); //7.0 KR
		addPacketOpcode(SM_INSTANCE_COUNT_INFO.class, 0x93, idSet); //7.0 KR
		addPacketOpcode(SM_WINDSTREAM_ANNOUNCE.class, 0xA4, idSet); //7.0 KR
		addPacketOpcode(SM_MARK_FRIENDLIST.class, 0x119, idSet); //7.0 KR
		addPacketOpcode(SM_CHALLENGE_LIST.class, 0x11a, idSet); //7.0 KR
		addPacketOpcode(SM_QUESTIONNAIRE.class, 0xBF, idSet); //7.0 KR
		addPacketOpcode(SM_PLAYER_MOVE.class, 0x15, idSet); //7.0 KR
		addPacketOpcode(SM_STATUPDATE_DP.class, 0x06, idSet); //7.0 KR
		addPacketOpcode(SM_DP_INFO.class, 0x07, idSet); //7.0 KR
		addPacketOpcode(SM_PLAYER_STANCE.class, 0x1F, idSet); //7.0 KR
		addPacketOpcode(SM_FORTRESS_STATUS.class, 0x56, idSet); //7.0 KR
		addPacketOpcode(SM_FORTRESS_INFO.class, 0xF4, idSet); //7.0 KR
		addPacketOpcode(SM_QUESTION_WINDOW.class, 0x34, idSet); //7.0 KR
		addPacketOpcode(SM_MANTRA_EFFECT.class, 0xD0, idSet); //7.0 KR
		addPacketOpcode(SM_LEARN_RECIPE.class, 0xF2, idSet); //7.0 KR
		addPacketOpcode(SM_TRADE_IN_LIST.class, 0x097, idSet); //7.0 KR
		addPacketOpcode(SM_REPURCHASE.class, 0xA7, idSet); //7.0 KR
		addPacketOpcode(SM_TARGET_IMMOBILIZE.class, 0xCC, idSet); //7.0 KR
		addPacketOpcode(SM_SKILL_CANCEL.class, 0x2A, idSet); //7.0 KR
		addPacketOpcode(SM_CAPTCHA.class, 0x57, idSet); //7.0 KR
		addPacketOpcode(SM_SIEGE_LOCATION_STATE.class, 0xD2, idSet); //7.0 KR
		addPacketOpcode(SM_SUMMON_PANEL.class, 0x99, idSet); //7.0 KR
		addPacketOpcode(SM_SUMMON_UPDATE.class, 0x9B, idSet); //7.0 KR
		addPacketOpcode(SM_ABYSS_ARTIFACT_INFO3.class, 0xDC, idSet); //7.0 KR
		addPacketOpcode(SM_INSTANCE_SCORE.class, 0x79, idSet); //7.0 KR
		addPacketOpcode(SM_INSTANCE_STAGE_INFO.class, 0x8C, idSet); //7.0 KR
		addPacketOpcode(SM_USE_OBJECT.class, 0xC5, idSet); //7.0 KR
		addPacketOpcode(SM_FORCED_MOVE.class, 0xC3, idSet); //7.0 KR
		addPacketOpcode(SM_SUMMON_PANEL_REMOVE.class, 0x49, idSet); //7.0 KR
		addPacketOpcode(SM_SUMMON_OWNER_REMOVE.class, 0x9A, idSet); //7.0 KR
		addPacketOpcode(SM_SKILL_REMOVE.class, 0x2D, idSet); //7.0 KR
		addPacketOpcode(SM_SHOW_NPC_ON_MAP.class, 0x59, idSet); //7.0 KR
		addPacketOpcode(SM_SUMMON_USESKILL.class, 0xA2, idSet); //7.0 KR
		addPacketOpcode(SM_SKILL_COOLDOWN.class, 0x33, idSet); //7.0 KR
		addPacketOpcode(SM_SKILL_ACTIVATION.class, 0x2E, idSet); //7.0 KR
		addPacketOpcode(SM_GATHER_UPDATE.class, 0x23, idSet); //7.0 KR
		addPacketOpcode(SM_FIND_GROUP.class, 0xA6, idSet); //7.0 KR
		addPacketOpcode(SM_ALLIANCE_INFO.class, 0xf7, idSet); //7.0 KR
		addPacketOpcode(SM_ALLIANCE_MEMBER_INFO.class, 0xf8, idSet); //7.0 KR
		addPacketOpcode(SM_ALLIANCE_READY_CHECK.class, 0xFC, idSet); //7.0 KR
		addPacketOpcode(SM_RENAME.class, 0x58, idSet); //7.0 KR
		addPacketOpcode(SM_PLASTIC_SURGERY.class, 0x53, idSet); //7.0 KR
		addPacketOpcode(SM_PLAYER_REGION.class, 0xD9, idSet); //7.0 KR
		addPacketOpcode(SM_KISK_UPDATE.class, 0x90, idSet); //7.0 KR
		addPacketOpcode(SM_GATHER_STATUS.class, 0x22, idSet); //7.0 KR
		addPacketOpcode(SM_LEGION_UPDATE_TITLE.class, 0x72, idSet); //7.0 KR
		addPacketOpcode(SM_CRAFT_ANIMATION.class, 0xB4, idSet); //7.0 KR
		//addPacketOpcode(SM_NPC_ASSEMBLER.class, 0x0A, idSet); //7.0 KR
		addPacketOpcode(SM_RESURRECT.class, 0xC2, idSet); //7.0 KR
		addPacketOpcode(SM_LOGIN_QUEUE.class, 0x17, idSet); //7.0 KR
		addPacketOpcode(SM_CHAT_WINDOW.class, 0x63, idSet); //7.0 KR
		addPacketOpcode(SM_TRANSFORM_IN_SUMMON.class, 0x9C, idSet); //7.0 KR
		addPacketOpcode(SM_WINDSTREAM.class, 0xA3, idSet); //7.0 KR
		addPacketOpcode(SM_CRAFT_UPDATE.class, 0xB5, idSet); //7.0 KR
		addPacketOpcode(SM_ASCENSION_MORPH.class, 0xB6, idSet); //7.0 KR
		addPacketOpcode(SM_FRIEND_UPDATE.class, 0xF1, idSet); //7.0 KR
		addPacketOpcode(SM_RECIPE_DELETE.class, 0xF3, idSet); //7.0 KR
		addPacketOpcode(SM_RECEIVE_BIDS.class, 0x103, idSet); //7.0 KR
		addPacketOpcode(SM_OBJECT_USE_UPDATE.class, 0x10A, idSet); //7.0 KR
		addPacketOpcode(SM_GROUP_DATA_EXCHANGE.class, 0xB3, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_EDIT.class, 0x52, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_UPDATE.class, 0x3D, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_OBJECT.class, 0x10E, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_BIDS.class, 0x102, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_PAY_RENT.class, 0x108, idSet); //7.0 KR
		addPacketOpcode(SM_DELETE_HOUSE_OBJECT.class, 0x10F, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_RENDER.class, 0x111, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_ACQUIRE.class, 0x115, idSet); //7.0 KR
		addPacketOpcode(SM_DELETE_HOUSE.class, 0x112, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_REGISTRY.class, 0x74, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_SCRIPTS.class, 0x83, idSet); //7.0 KR
		addPacketOpcode(SM_HOUSE_TELEPORT.class, 0xDD, idSet); //7.0 KR
		addPacketOpcode(SM_BUTLER_SALUTE.class, 0xB2, idSet); //7.0 KR
		addPacketOpcode(SM_USE_ROBOT.class, 0x5C, idSet); //7.0 KR
		addPacketOpcode(SM_CASH_BUFF.class, 0xFD, idSet); //7.0 KR
		addPacketOpcode(SM_UPGRADE_ARCADE.class, 0x12C, idSet); //7.0 KR
		addPacketOpcode(SM_LUNA_SHOP.class, 0x14A, idSet); //7.0 KR
		addPacketOpcode(SM_TOLL_INFO.class, 0x9F, idSet); //7.0 KR
		addPacketOpcode(SM_MEGAPHONE_MESSAGE.class, 0x11F, idSet); //7.0 KR
		addPacketOpcode(SM_AETHERFORGING_ANIMATION.class, 0x14C, idSet); //7.0 KR
		addPacketOpcode(SM_AETHERFORGING_PLAYER.class, 0x14D, idSet); //7.0 KR
		addPacketOpcode(SM_BOOST_EVENTS.class, 0x148, idSet); //7.0 KR
		addPacketOpcode(SM_CONDITION_VARIABLE.class, 0xEE, idSet); //7.0 KR
		addPacketOpcode(SM_FLAG_INFO.class, 0x152, idSet); //7.0 KR
		addPacketOpcode(SM_FLAG_UPDATE.class, 0x153, idSet); //7.0 KR
		addPacketOpcode(SM_HOT_SPECTATE.class, 0xDB, idSet); //7.0 KR
		addPacketOpcode(SM_EVENT_WINDOW.class, 0x13E, idSet); //7.0 KR
		addPacketOpcode(SM_EVENT_WINDOW_ITEMS.class, 0x154, idSet); //7.0 KR
		addPacketOpcode(SM_EVERGALE_CANYON.class, 0x16B, idSet); //7.0 KR
		addPacketOpcode(SM_GODSTONE_DESTROY.class, 0x12E, idSet); //7.0 KR
		addPacketOpcode(SM_PLAYER_PROTECTION.class, 0x100, idSet); //7.0 KR
		addPacketOpcode(SM_SEASON_RANKING.class, 0x159, idSet); //7.0 KR
		addPacketOpcode(SM_MY_HISTORY.class, 0x15A, idSet); //7.0 KR
		addPacketOpcode(SM_TUNE_RESULT.class, 0x122, idSet); //7.0 KR
		addPacketOpcode(SM_BLACKCLOUD_TRADE.class, 0x160, idSet); //7.0 KR
		addPacketOpcode(SM_SHUGO_SWEEP.class, 0x14B, idSet); //7.0 KR
		addPacketOpcode(SM_ABYSS_FAVOR.class, 0x162, idSet); //7.0 KR
		//addPacketOpcode(SM_PETITION.class, 0xEF, idSet); //7.0 KR To Do
		addPacketOpcode(SM_SERVER_SERIAL_CHECK.class, 0x129, idSet); //7.0 KR
		addPacketOpcode(SM_EQUIPMENT_SETTING.class, 0x167, idSet); //7.0 KR
		addPacketOpcode(SM_ACCOUNT_TYPE.class, 0x14F, idSet); //7.0 KR
		addPacketOpcode(SM_ACCOUNT_TYPE2.class, 0x14E, idSet); //7.0 KR
		addPacketOpcode(SM_AFTER_TIME_CHECK.class, 0x126, idSet); //7.0 KR
		addPacketOpcode(SM_VERSION.class, 0x16A, idSet); //7.0 KR
		addPacketOpcode(SM_QUNA_INSTANCE_BUFF.class, 0x173, idSet); //7.0 KR
		addPacketOpcode(SM_ROUND_TRIP.class, 0xDE, idSet); //7.0 KR
		addPacketOpcode(SM_SKILL_SKIN.class, 0x150, idSet); //7.0 KR
		addPacketOpcode(SM_AION_TV.class, 0x146, idSet); //7.0 KR
		addPacketOpcode(SM_ACHIEVEMENT_LIST.class, 0x17B, idSet); //7.0 KR
		addPacketOpcode(SM_ACHIEVEMENT_UPDATE.class, 0x17C, idSet); //7.0 KR
		addPacketOpcode(SM_COMPLETE_ACHIEVEMENT.class, 0x17D, idSet); //7.0 KR
		addPacketOpcode(SM_ACHIEVEMENT_EVENT_LIST.class, 0x17E, idSet); //7.0 KR
		addPacketOpcode(SM_LOGIN_EVENT.class, 0x12D, idSet); //7.0 KR
		addPacketOpcode(SM_ESTIMA_BUFF.class, 0x60, idSet); //7.0 KR
		addPacketOpcode(SM_ABYSS_POINTS.class, 0x133, idSet); //7.0 KR
		addPacketOpcode(SM_GM_SPY.class, 0x98, idSet);
		//addPacketOpcode(SM_QUNA_COUNT.class, 0x172, idSet); //7.0 KR
		addPacketOpcode(SM_DAEVANION_SKILL_ENCHANT.class, 0x179, idSet); //7.0 KR
		addPacketOpcode(SM_DAEVANION_SKILL_COMBINE.class, 0x17A, idSet); // 7.0 KR
		addPacketOpcode(SM_TEST.class, 0x177, idSet); //7.0 KR
		addPacketOpcode(SM_REMOVE_DYE.class, 0x1DF, idSet); //7.0 KR
		addPacketOpcode(SM_WORLD_PLAYTIME.class, 0x185, idSet); //7.2 KR
		addPacketOpcode(SM_PLAYER_FAME.class, 0x186, idSet); //7.5 KR
		addPacketOpcode(SM_STRONGHOLDS.class, 0x182, idSet); //7.2 KR
		addPacketOpcode(SM_DAEVA_MEMBERSHIP.class, 0x172, idSet); //7.2 KR

		addPacketOpcode(SM_PLAYER_COLLECTION.class, 0x18E, idSet); //7.7
		addPacketOpcode(SM_PLAYER_COLLECTION_FINISH.class, 0x18F, idSet); //7.7
		addPacketOpcode(SM_PLAYER_COLLECTION_UNK.class, 0x190, idSet); //7.7
		addPacketOpcode(SM_PLAYER_COLLECTION_PROGRESS.class, 0x191, idSet); //7.7
		addPacketOpcode(SM_PLAYER_COLLECTION_COMPLETE.class, 0x192, idSet); //7.7
		addPacketOpcode(SM_PLAYER_COLLECTION_REGISTER.class, 0x193, idSet); //7.7

		addPacketOpcode(SM_LUMIEL_TRASFORM.class, 0x18A, idSet); //7.7
		addPacketOpcode(SM_LUMIEL_TRANSFORM_REWARD_LIST.class, 0x18B, idSet); //7.7
		addPacketOpcode(SM_LUMIEL_TRANSFORM_EXP.class, 0x18C, idSet); //7.7
		addPacketOpcode(SM_LUMIEL_TRANSFORM_REWARD.class, 0x18D, idSet); //7.7

		//Drafts
		addPacketOpcode(SM_UNK_106.class, 0x106, idSet); //7.0 KR
		addPacketOpcode(SM_0x125.class, 0x125, idSet); //7.0 KR
		addPacketOpcode(SM_UNK_7D.class, 0x7D, idSet); //7.0 KR //gameguard
		addPacketOpcode(SM_UNK_131.class, 0x131, idSet); //7.0 KR //stonespire
		addPacketOpcode(SM_UNK_A5.class, 0xA5, idSet); //7.0 KR
		addPacketOpcode(SM_UNK_7E.class, 0x7E, idSet); //7.0 KR //landing level
		addPacketOpcode(SM_CUSTOM_PACKET.class, 99999, idSet);
	}
	
	static int getOpcode(Class<? extends AionServerPacket> packetClass) {
		Integer opcode = opcodes.get(packetClass);
		if (opcode == null) {
			throw new IllegalArgumentException("There is no opcode for " + packetClass + " defined.");
		}
		return opcode;
	}
	
	private static void addPacketOpcode(Class<? extends AionServerPacket> packetClass, int opcode, Set<Integer> idSet) {
		if (opcode < 0) {
			return;
		} if (idSet.contains(opcode)) {
			throw new IllegalArgumentException(String.format("There already exists another packet with id 0x%02X", opcode));
		}
		idSet.add(opcode);
		opcodes.put(packetClass, opcode);
	}
}