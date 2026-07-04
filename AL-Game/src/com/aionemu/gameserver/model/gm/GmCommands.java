package com.aionemu.gameserver.model.gm;

public enum GmCommands
{
	GM_DIALOG_TELEPORTTO,
	GM_DIALOG_RECALL,
	GM_DIALOG,
	GM_DIALOG_POS,
	GM_DIALOG_MEMO,
	GM_DIALOG_BOOKMARK,
	GM_DIALOG_INVENTORY,
	GM_DIALOG_SKILL,
	GM_DIALOG_STATUS,
	GM_DIALOG_QUEST,
	GM_DIALOG_REFRESH,
	GM_DIALOG_WAREHOUSE,
	GM_DIALOG_MAIL,
	GM_POLL_DIALOG,
	GM_POLL_DIALOG_SUBMIT,
	GM_BOOKMARK_DIALOG,
	GM_BOOKMARK_DIALOG_ADD_BOOKMARK,
	GM_MEMO_DIALOG,
	GM_MEMO_DIALOG_ADD_MEMO,
	GM_DIALOG_CHECK_BOT1,
	GM_DIALOG_CHECK_BOT99,
	GM_INDICATOR_DIALOG_TOOLTIP_HOUSING_MODE,
	GM_DIALOG_CHARACTER,
	GM_DIALOG_OPTION,
	GM_DIALOG_BUILDER_CONTROL,
	GM_DIALOG_BUILDER_COMMAND,
	CLASSUP,
	WISH,
	ITEM,
	ADDITEM,
	LEVEL,
	SETLEVEL,
	UNKNOWN;
	
	public static GmCommands getValue(String command) {
		if (command == null) {
			return UNKNOWN;
		}
		String normalized = command.trim().toUpperCase().replace('-', '_');
		if (normalized.length() == 0) {
			return UNKNOWN;
		}
		// CC2/EU 7.7 GM panel uses shorter command ids for some buttons.
		if ("WISH".equals(normalized) || "GM_WISH".equals(normalized) || "GIVEITEM".equals(normalized) || "ITEMADD".equals(normalized)) {
			return WISH;
		}
		if ("ITEM".equals(normalized) || "ADD".equals(normalized) || "ADDITEM".equals(normalized)) {
			return ITEM;
		}
		if ("LV".equals(normalized) || "LEVEL".equals(normalized) || "SETLV".equals(normalized) || "SETLEVEL".equals(normalized)) {
			return LEVEL;
		}
		for (GmCommands value : values()) {
			if (value.name().equals(normalized)) {
				return value;
		    }
		}
		return UNKNOWN;
	}
}
