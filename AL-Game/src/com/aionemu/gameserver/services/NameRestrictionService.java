package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.NameConfig;

public class NameRestrictionService {

	private static final String ENCODED_BAD_WORD = "----";
	private static final String MAME_JP_CHARACTER_PATTERN = "[a-zA-Z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\u31F0-\\u31FF\\u3400-\\u4DBF\\u4E00-\\u9FFF\\uF900-\\uFAFF\\u3005\\u3006\\u3007\\u30FC\\u30FB]{2,16}";
	private static String[] forbiddenSequences;
	private static String[] forbiddenByClient;

	public static boolean isValidName(String name) {
		if (name == null) {
			return false;
		}
		if (NameConfig.CHAR_NAME_PATTERN != null && NameConfig.CHAR_NAME_PATTERN.matcher(name).matches()) {
			return true;
		}
		// MameAion75: tolerate Japanese/Unicode names even if an old runtime
		// name.properties was copied into the release folder by accident.
		return name.matches(MAME_JP_CHARACTER_PATTERN);
	}

	public static boolean isForbiddenWord(String name) {
		return isForbiddenByClient(name) || isForbiddenBySequence(name);
	}

	private static boolean isForbiddenByClient(String name) {
        if (!NameConfig.NAME_FORBIDDEN_ENABLE || NameConfig.NAME_FORBIDDEN_CLIENT.equals("")) {
			return false;
		}
		if ((forbiddenByClient == null) || (forbiddenByClient.length == 0)) {
			forbiddenByClient = NameConfig.NAME_FORBIDDEN_CLIENT.split(",");
		}
		for (String s : forbiddenByClient) {
			if (name.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}

	private static boolean isForbiddenBySequence(String name) {
		if (name == null || NameConfig.NAME_SEQUENCE_FORBIDDEN.equals("")) {
			return false;
		}
		if (forbiddenSequences == null || forbiddenSequences.length == 0) {
			forbiddenSequences = NameConfig.NAME_SEQUENCE_FORBIDDEN.toLowerCase().split(",");
		}
		for (String s : forbiddenSequences) {
			if (name.toLowerCase().contains(s))
				return true;
		}
		return false;
	}

	public static String filterMessage(String message) {
		for (String word : message.split(" ")) {
			if (isForbiddenWord(word))
				message.replace(word, ENCODED_BAD_WORD);
		}
		return message;
	}
}