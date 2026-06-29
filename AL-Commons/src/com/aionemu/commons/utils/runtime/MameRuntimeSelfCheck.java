/*
 * MameAion JDK25 runtime self-check.
 */
package com.aionemu.commons.utils.runtime;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lightweight runtime dependency audit for the JDK25 migration path.
 *
 * This class intentionally uses string based Class.forName checks so it does
 * not add compile-time dependencies. It is safe for Commons/Login/Game/Chat.
 */
public final class MameRuntimeSelfCheck {

	private static final Logger log = LoggerFactory.getLogger(MameRuntimeSelfCheck.class);
	private static volatile boolean printed;

	private MameRuntimeSelfCheck() {
	}

	public static void printOnce(String serverName) {
		if (printed) {
			return;
		}
		synchronized (MameRuntimeSelfCheck.class) {
			if (printed) {
				return;
			}
			printed = true;
			print(serverName);
		}
	}

	private static void print(String serverName) {
		log.info("==================================================");
		log.info("MameAion 実行環境セルフチェック: " + safe(serverName));
		log.info("Javaバージョン=" + System.getProperty("java.version"));
		log.info("Javaベンダー=" + System.getProperty("java.vendor"));
		log.info("Javaホーム=" + System.getProperty("java.home"));
		log.info("VM名=" + System.getProperty("java.vm.name"));
		log.info("OS=" + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
		log.info("起動引数=" + ManagementFactory.getRuntimeMXBean().getInputArguments());

		boolean jaxbApi = exists("javax.xml.bind.JAXBContext");
		boolean jaxbRuntime = exists("com.sun.xml.bind.v2.ContextFactory") || exists("org.glassfish.jaxb.runtime.v2.ContextFactory");
		boolean activation = exists("javax.activation.DataSource");
		boolean mysql8 = exists("com.mysql.cj.jdbc.Driver");
		boolean mysql5 = exists("com.mysql.jdbc.Driver");
		boolean hikari = exists("com.zaxxer.hikari.HikariDataSource");
		boolean bonecp = exists("com.jolbox.bonecp.BoneCPDataSource");

		logDependency("JAXB API javax.xml.bind.JAXBContext", jaxbApi, true);
		logDependency("JAXB Runtime ContextFactory", jaxbRuntime, true);
		logDependency("Activation javax.activation.DataSource", activation, true);
		logDependency("MySQL Connector/J 8 com.mysql.cj.jdbc.Driver", mysql8, false);
		logDependency("Legacy MySQL Connector/J 5 com.mysql.jdbc.Driver", mysql5, false);
		logDependency("HikariCP com.zaxxer.hikari.HikariDataSource", hikari, false);
		logDependency("BoneCP fallback com.jolbox.bonecp.BoneCPDataSource", bonecp, false);

		List<String> warnings = new ArrayList<String>();
		if (isJdkAtLeast(11) && (!jaxbApi || !jaxbRuntime || !activation)) {
			warnings.add("JDK11以降では libs に JAXB / Activation の実行時jarが必要です。");
		}
		if (!mysql8 && !mysql5) {
			warnings.add("MySQL JDBC driver が見つかりません。Login/Game のDB起動に失敗します。");
		}
		if (!hikari && !bonecp) {
			warnings.add("利用可能なDB接続プールが見つかりません。HikariCP または BoneCP fallback を libs に入れてください。");
		}
		for (String warning : warnings) {
			log.warn("MameAion 実行環境セルフチェック警告: " + warning);
		}
		if (warnings.isEmpty()) {
			log.info("MameAion 実行環境セルフチェック完了: 依存関係の警告なし。");
		}
		log.info("==================================================");
	}

	private static void logDependency(String name, boolean present, boolean requiredForJdk25Runtime) {
		String level = present ? "OK" : (requiredForJdk25Runtime ? "不足" : "任意/未導入");
		if (present || !requiredForJdk25Runtime) {
			log.info("実行時依存関係 " + level + ": " + name);
		} else {
			log.warn("実行時依存関係 " + level + ": " + name);
		}
	}

	private static boolean exists(String className) {
		try {
			Class.forName(className, false, MameRuntimeSelfCheck.class.getClassLoader());
			return true;
		} catch (Throwable ignored) {
			return false;
		}
	}

	private static boolean isJdkAtLeast(int major) {
		return parseMajor(System.getProperty("java.specification.version")) >= major;
	}

	private static int parseMajor(String version) {
		if (version == null || version.trim().isEmpty()) {
			return 0;
		}
		String normalized = version.trim();
		if (normalized.startsWith("1.")) {
			normalized = normalized.substring(2);
		}
		int dot = normalized.indexOf('.');
		if (dot > 0) {
			normalized = normalized.substring(0, dot);
		}
		try {
			return Integer.parseInt(normalized);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private static String safe(String value) {
		return value == null ? "unknown" : value;
	}
}
