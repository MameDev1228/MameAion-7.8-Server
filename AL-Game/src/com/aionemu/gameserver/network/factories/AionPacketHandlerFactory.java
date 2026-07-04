package com.aionemu.gameserver.network.factories;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.AionPacketHandler;
import com.aionemu.gameserver.network.aion.clientpackets.*;

public class AionPacketHandlerFactory
{
	private AionPacketHandler handler;
	
	public static AionPacketHandlerFactory getInstance() {
		return SingletonHolder.instance;
	}
	
	public AionPacketHandlerFactory() {
		handler = new AionPacketHandler();
			///====// KR 7.5 //====//
			///==================[CONNECTION]==================
			addPacket(new CM_VERSION_CHECK(0xD6, State.CONNECTED)); ///7.5 KR
			addPacket(new CM_TIME_CHECK(0xE4, State.CONNECTED, State.AUTHED, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_L2AUTH_LOGIN_CHECK(0x158, State.CONNECTED)); ///7.5 KR
			addPacket(new CM_MAC_ADDRESS(0x180, State.CONNECTED, State.AUTHED, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_S_REP_WEB_SESSIONKEY(0x0100, State.CONNECTED, State.AUTHED, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_GG(0x13F, State.IN_GAME, State.AUTHED, State.CONNECTED)); ///7.5 KR
			addPacket(new CM_CHARACTER_LIST(0x159, State.AUTHED)); ///7.5 KR
			addPacket(new CM_PING(0x2F2, State.AUTHED, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_RECONNECT_AUTH(0x018E, State.AUTHED)); ///7.5 KR
			///==================[CHARACTER LIST]==================
			addPacket(new CM_CREATE_CHARACTER(0x016E, State.AUTHED));///7.5 KR
			addPacket(new CM_CHECK_NICKNAME(0x184, State.AUTHED)); ///7.5 KR
			addPacket(new CM_DELETE_CHARACTER(0x16F, State.AUTHED));///7.5 KR
			addPacket(new CM_RESTORE_CHARACTER(0x16C, State.AUTHED)); ///7.5 KR
			addPacket(new CM_CHARACTER_PASSKEY(0x1A5, State.AUTHED)); ///7.5 KR
			///==================[ENTER GAME]==================
			addPacket(new CM_MAY_LOGIN_INTO_GAME(0x018D, State.AUTHED)); ///7.5 KR			
			addPacket(new CM_QUIT(0x00D5, State.AUTHED, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_ENTER_WORLD(0xDE, State.AUTHED)); ///7.5 KR
			addPacket(new CM_CHAT_MESSAGE_PUBLIC(0x00ED, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_UI_SETTINGS(0x00DC, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LEVEL_READY(0x00DF, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_CUSTOM_SETTINGS(0x00D2, State.IN_GAME)); ///7.9 KR
			addPacket(new CM_MOVE(0x0106, State.IN_GAME, State.AUTHED)); ///7.5 KR
			addPacket(new CM_CHAT_AUTH(0x171, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_TARGET_SELECT(0x00E1, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_MAY_QUIT(0x00CA, State.AUTHED, State.IN_GAME)); ///7.5 KR
			///==================[COMBAT]==================
			addPacket(new CM_CASTSPELL(0x00F7, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_COMBAT_SUPPORT(0x03FC, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_ATTACK(0x00F6, State.IN_GAME)); ///7.5 KR
			///==================[ITEMS]==================
			addPacket(new CM_DELETE_ITEM(0x013B, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_MOVE_ITEM(0x0163, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_EQUIP_ITEM(0x00E8, State.IN_GAME)); ///7.5 KR
		    addPacket(new CM_USE_ITEM(0x00EB, State.IN_GAME)); ///7.5 KR
		    addPacket(new CM_TRANSFORM_LIST(0x01E3, State.IN_GAME)); ///7.5 KR
		    addPacket(new CM_MINIONS(0x01ED, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_EQUIPMENT_SETTING_USE(0x01EF, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_EQUIPMENT_SET_SETTING(0x01EE, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_TUNE(0x01B2, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_TUNE_LUNA(0x01F6, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_MONSTER_CORE(0x01D8, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SPLIT_ITEM(0x0160, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SELECT_ITEM(0x01B3, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_ENCHANTMENT_EXTRACTION(0x01CB, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_DAEVANION_SKILL_COMBINE(0x01EB, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_DAEVANION_SKILL_ENCHANT(0x01EA, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_FUSION_WEAPONS(0x0191, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BREAK_WEAPONS(0x01A6, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_AETHERFORGING(0x1D3, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_CRAFT(0x150, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SELL_TERMINATED_ITEMS(0x1DF, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_PURIFICATION_ITEM(0x01CE, State.IN_GAME)); ///7.5 KR
			///==================[PLAYER]==================
			addPacket(new CM_EMOTION(0x02FD, State.IN_GAME)); ///7.5 KR
		    addPacket(new CM_CUBE_EXPAND(0x01C2, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_REVIVE(0x00CB, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_CLIENT_COMMAND_ROLL(0x0132, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_PING_REQUEST(0x013E, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SKILL_SKIN(0x01D0, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_QUESTIONNAIRE(0x0164, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_MACRO_CREATE(0x0186, State.IN_GAME)); ///7.2 KR
			addPacket(new CM_MACRO_DELETE(0x0187, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_START_LOOT(0x016D, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LOOT_ITEM(0x0162, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_PLAYER_LISTENER(0x02FE, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_CHAT_MESSAGE_WHISPER(0x00E2, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_VIEW_PLAYER_DETAILS(0x012B, State.IN_GAME)); ///7.5 KR
		    ///==================[EVENT]==================
		    addPacket(new CM_LOGIN_EVENT(0x01CF, State.IN_GAME)); ///7.5 KR
		    addPacket(new CM_UPGRADE_ARCADE(0x01B9, State.IN_GAME)); ///7.5 KR
		    addPacket(new CM_SHUGO_SWEEP(0x01DD, State.IN_GAME)); ///7.5 KR
		    addPacket(new CM_LUNA_SHOP(0x01DE, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_EVERGALE_CANYON(0x01EC, State.IN_GAME)); ///7.5 KR
			///==================[RANKING]==================
			addPacket(new CM_SEASON_RANKING(0x01E7, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_MY_HISTORY(0x01E4, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_ABYSS_RANKING_PLAYERS( 0x0139, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_ABYSS_RANKING_LEGIONS(0x0183, State.IN_GAME)); ///7.5 KR
			///==================[DIALOG]==================
			addPacket(new CM_SHOW_DIALOG(0x02FA, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_DIALOG_SELECT(0x02F8, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_CLOSE_DIALOG(0x02FB, State.IN_GAME)); ///7.5 KR
			///==================[TELEPORT]================
			addPacket(new CM_HOTSPOT_TELEPORT(0x01BB, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_TELEPORT_DONE(0x00D1, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_MOVE_IN_AIR(0x0107, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_TELEPORT_SELECT(0x015B, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_TEAM_INVITE(0x0134, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_DUEL_REQUEST(0x0145, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_INSTANCE_INFO(0x0197, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LUNA_BUFF(0x01E1, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_PLAYER_SEARCH(0x0176, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_WINDSTREAM(0x108, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_AUTOMATIC_BERDIN_STAR(0x01D1, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_QUESTION_RESPONSE(0x0104, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_OBJECT_SEARCH(0x00DD, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SHOW_BRAND(0x0178, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SET_NOTE(0x010C, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BUY_ITEM(0x0105, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BUY_TRADE_IN_TRADE(0x012E, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_ENCHANMENT_STONES(0x011C, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_AUTO_GROUP(0x019F, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_MOTION(0x0109, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_TITLE_SET(0x0152, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BONUS_TITLE(0x01BC, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_REMOVE_ALTERED_STATE(0x00F5, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_MEGAPHONE_MESSAGE(0x01B0, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_INTRUDER_SCAN(0x018B, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_GATHER(0x00E5, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_EQUIP_STIGMA(0x01F5, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_OPEN_STATICDOOR(0x00D9, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_DISTRIBUTION_SETTINGS(0x018C, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_ITEM_REMODEL(0x012C, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_CHARGE_SKILL(0x01BD, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_CHARACTER_EDIT(0x00C9, State.AUTHED)); ///7.5 KR
			addPacket(new CM_APPEARANCE(0x0188, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_PLAY_MOVIE_END(0x0127, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_TOGGLE_SKILL_DEACTIVATE(0x00F4, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_DELETE_QUEST(0x0126, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_INSTANCE_LEAVE(0x02F0, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_GROUP_DISTRIBUTION(0x0133, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_FIND_GROUP(0x0113, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_GROUP_DATA_EXCHANGE(0x0111, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_GROUP_PLAYER_STATUS_INFO(0x0137, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_GROUP_LOOT(0x018F, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_ROUND_TRIP(0x0136, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_QUEST_SHARE(0x016B, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_COMPLETE_ACHIEVEMENT(0x01E9, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_COMPLETE_ACHIEVEMENT_EVENT(0x03FE, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_INSTANCE_ENTRY(0x01CD, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_REMOVE_DYE(0x1DA, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LUMIEL_TRANSFORM(0x03FD, State.IN_GAME)); ///7.7 KR
			addPacket(new CM_COLLECTION_REGISTER(0x03F2, State.IN_GAME)); ///7.7 KR
			///==================[PET]==================
			addPacket(new CM_PET(0x00D8, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_PET_EMOTE(0x00DB, State.IN_GAME)); ///7.5 KR
			///==================[MAIL]==================
			addPacket(new CM_SEND_MAIL(0x014B, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_READ_MAIL(0x0149, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_READ_EXPRESS_MAIL(0x0175, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_GET_MAIL_ATTACHMENT(0x015F, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_CHECK_MAIL_SIZE(0x0148, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_CHECK_MAIL_SIZE_2(0x0198, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_DELETE_MAIL(0x015C, State.IN_GAME)); ///7.5 KR
			///==================[FRIEND LIST]=============
			addPacket(new CM_FRIEND_ADD(0x0146, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_FRIEND_STATUS(0x017D, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SHOW_FRIENDLIST(0x01A9, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_FRIEND_EDIT(0x01C6, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_FRIEND_DEL(0x0147, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BLOCK_ADD(0x0169, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SHOW_BLOCKLIST(0x0161, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BLOCK_DEL(0x017E, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BLOCK_SET_REASON(0x017A, State.IN_GAME)); ///7.5 KR
			///==================[BROKER]==================
			addPacket(new CM_BROKER_START_REGISTER(0x0138, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BROKER_REGISTERED_LIST(0x0140, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BROKER_LIST(0x0142, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BROKER_CANCEL_REGISTERED(0x0157, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BROKER_SOLD_LIST(0x0154, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BUY_BROKER_ITEM(0x0141, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_REGISTER_BROKER_ITEM(0x0156, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BROKER_SEARCH(0x0143, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_BROKER_COLLECT_SOLD_ITEMS(0x0155, State.IN_GAME)); ///7.5 KR
			///==================[EXCHANGE]==================
			addPacket(new CM_EXCHANGE_ADD_ITEM(0x0116, State.IN_GAME)); ///7.2 KR
			addPacket(new CM_EXCHANGE_CANCEL(0x010B, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_EXCHANGE_LOCK(0x0115, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_EXCHANGE_OK(0x010A, State.IN_GAME)); ///7.2 KR
			addPacket(new CM_EXCHANGE_REQUEST(0x0101, State.IN_GAME)); ///7.5 KR
			///==================[HOUSING]==================
			addPacket(new CM_CHALLENGE_LIST(0x01BF, State.IN_GAME)); ///7.5 KR
			//addPacket(new CM_BUTLER_SALUTE(0x2FE, State.IN_GAME)); ///7.2 KR
			addPacket(new CM_HOUSE_EDIT(0x0124, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_HOUSE_SCRIPT(0x00E0, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_HOUSE_KICK(0x011E, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_HOUSE_SETTINGS(0x011F, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_PLACE_BID(0x01A0, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_USE_HOUSE_OBJECT(0x01AC, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_HOUSE_PAY_RENT(0x01B6, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_GET_HOUSE_BIDS(0x01AD, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_REGISTER_HOUSE(0x01A2, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_MARK_FRIENDLIST(0x0131, State.IN_GAME)); ///7.5 KR
			//addPacket(new CM_RELEASE_HOUSE_OBJECT(0x01B4, State.IN_GAME)); ///7.5 KR
			//addPacket(new CM_HOUSE_DECORATE(0x0124, State.IN_GAME)); ///7.2 KR
			addPacket(new CM_HOUSE_OPEN_DOOR(0x01B5, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_HOUSE_TELEPORT(0x1BA, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_HOUSE_TELEPORT_BACK(0x121, State.IN_GAME)); ///7.5 KR
			///==================[SUMMON SPIRIT]==================
			addPacket(new CM_SUMMON_EMOTION(0x019D, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SUMMON_ATTACK(0x0192, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SUMMON_COMMAND(0x014C, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SUMMON_CASTSPELL(0x0190, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_SUMMON_MOVE(0x019C, State.IN_GAME)); ///7.5 KR
			///==================[LEGION]==================
			addPacket(new CM_LEGION(0x02F3, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LEGION_SEARCH(0x01C3, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LEGION_SEND_EMBLEM(0xE6, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LEGION_TABS(0x02F9, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LEGION_JOIN_REQUEST(0x01C0, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LEGION_JOIN_CANCEL(0x01C1, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LEGION_MODIFY_EMBLEM(0x010D, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LEGION_SEND_EMBLEM_INFO(0x02F1, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LEGION_UPLOAD_INFO(0x0177, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_LEGION_UPLOAD_EMBLEM(0x0174, State.IN_GAME)); ///7.5 KR
			addPacket(new CM_GM_COMMAND_SEND(0x02FC, State.IN_GAME));
			addPacket(new CM_GM_BOOKMARK(0x02FC, State.IN_GAME));
			
			/*====// KR 7.2 //====//
			addPacket(new CM_CHAT_PLAYER_INFO(0x0137, State.IN_GAME)); ///7.2 KR
			addPacket(new CM_CHAT_GROUP_INFO(0x128, State.IN_GAME)); ///7.2 KR
			addPacket(new CM_STOP_TRAINING(0x119, State.IN_GAME)); ///7.2 KR
		*/
		
		/*Draft.
		addPacket(new CM_REPORT_PLAYER(0x196, State.IN_GAME));
		addPacket(new CM_CHARGE_ITEM(0x2F9, State.IN_GAME));
		addPacket(new CM_LEGION_WH_KINAH(0x0117, State.IN_GAME));
		addPacket(new CM_CHANGE_CHANNEL(0x188, State.IN_GAME));
		addPacket(new CM_GM_PANEL(0x02FC, State.IN_GAME));
		addPacket(new CM_COMPOSITE_STONES(0x1a8, State.IN_GAME));
		addPacket(new CM_REPLACE_ITEM(0x17e, State.IN_GAME));
		addPacket(new CM_SHOW_MAP(0x00e3, State.IN_GAME));
		addPacket(new CM_HOT_SPECTATE(0x0172, State.IN_GAME));
		addPacket(new CM_UNPACK_ITEM(0x1B2, State.IN_GAME));
		addPacket(new CM_SUBZONE_CHANGE(0x0162, State.IN_GAME));
		addPacket(new CM_GM_BOOKMARK(0x02FC, State.IN_GAME));
		*/
	}
	
	public AionPacketHandler getPacketHandler() {
		return handler;
	}
	
	private void addPacket(AionClientPacket prototype) {
		handler.addPacketPrototype(prototype);
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final AionPacketHandlerFactory instance = new AionPacketHandlerFactory();
	}
}