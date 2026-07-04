package com.aionemu.gameserver.configs;

import java.io.File;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.configs.main.NameConfig;

/**
 * MameAion75 CC2/EU7.7 compatibility runtime guard.
 *
 * This does not change validation rules. It only prints a clear startup audit so
 * stale extracted/build/dist folders are immediately visible before Japanese
 * name tests fail silently.
 */
public final class ConfigRuntimeAudit {

	private static final Logger log = LoggerFactory.getLogger(ConfigRuntimeAudit.class);
	private static final String PREFIX = "[MAME-CC2][CONFIG_GUARD] ";

	private ConfigRuntimeAudit() {
	}

	public static void logStartupAudit() {
		try {
			String cwd = new File(".").getCanonicalPath();
			log.info(PREFIX + "runtimeDir='{}'", cwd);
			if (looksLikeBuildDist(cwd)) {
				log.warn(PREFIX + "runtimeDir looks like GameServer/build/dist. Japanese-name tests can fail if stale config was not rebuilt. Prefer BuildAll_JDK25_ReleaseFolder_OpenConsole.bat release output.");
			}

			logPatternCheck("character", NameConfig.CHAR_NAME_PATTERN, "まめ太郎", "MameTEST");
			logPatternCheck("legion", LegionConfig.LEGION_NAME_PATTERN, "まめ軍団", "MameLegion");
		} catch (Exception e) {
			log.warn(PREFIX + "failed to run config audit", e);
		}
	}

	private static boolean looksLikeBuildDist(String path) {
		String normalized = path.replace('\\', '/').toLowerCase();
		return normalized.contains("/gameserver/build/dist") || normalized.endsWith("/build/dist/gameserver") || normalized.contains("/build/dist/gameserver");
	}

	private static void logPatternCheck(String label, Pattern pattern, String jpSample, String asciiCaseSample) {
		String patternText = pattern == null ? "<null>" : pattern.pattern();
		boolean jpOk = matches(pattern, jpSample);
		boolean asciiCaseOk = matches(pattern, asciiCaseSample);
		log.info(PREFIX + "{}Pattern='{}' jpSample='{}' jpAllowed={} asciiCaseSample='{}' asciiCaseAllowed={}", new Object[] { label, patternText, jpSample, jpOk, asciiCaseSample, asciiCaseOk });
		if (!jpOk) {
			log.warn(PREFIX + "{}Pattern does not accept Japanese sample '{}'. Check config/main/name.properties or config/main/legions.properties in the folder you actually started.", label, jpSample);
		}
		if (!asciiCaseOk) {
			log.warn(PREFIX + "{}Pattern does not accept mixed-case sample '{}'. Check stale name/legion config.", label, asciiCaseSample);
		}
	}

	private static boolean matches(Pattern pattern, String value) {
		return pattern != null && pattern.matcher(value).matches();
	}
}
