package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

import java.util.regex.Pattern;

public class NameConfig
{
	@Property(key = "gameserver.name.characterpattern", defaultValue = "[a-zA-Z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\u31F0-\\u31FF\\u3400-\\u4DBF\\u4E00-\\u9FFF\\uF900-\\uFAFF\\u3005\\u3006\\u3007\\u30FC\\u30FB]{2,16}")
	public static Pattern CHAR_NAME_PATTERN;
	@Property(key = "gameserver.name.forbidden.sequences", defaultValue = "")
	public static String NAME_SEQUENCE_FORBIDDEN;
	@Property(key = "gameserver.name.forbidden.enable.client", defaultValue = "false")
	public static boolean NAME_FORBIDDEN_ENABLE;
	@Property(key = "gameserver.name.forbidden.client", defaultValue = "")
	public static String NAME_FORBIDDEN_CLIENT;
	@Property(key = "gameserver.pet.name.change.enable", defaultValue = "true")
	public static boolean PET_NAME_CHANGE_ENABLE;
}