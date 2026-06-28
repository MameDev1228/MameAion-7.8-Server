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
		log.info("MameAion Runtime SelfCheck: " + safe(serverName));
		log.info("java.version=" + System.getProperty("java.version"));
		log.info("java.vendor=" + System.getProperty("java.vendor"));
		log.info("java.home=" + System.getProperty("java.home"));
		log.info("java.vm.name=" + System.getProperty("java.vm.name"));
		log.info("os.name=" + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
		log.info("input.arguments=" + ManagementFactory.getRuntimeMXBean().getInputArguments());

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
			warnings.add("JDK11+ requires external JAXB/Activation runtime jars in libs.");
		}
		if (!mysql8 && !mysql5) {
			warnings.add("No MySQL JDBC driver detected. Login/Game DB startup will fail.");
		}
		if (!hikari && !bonecp) {
			warnings.add("No supported DB pool detected. Add HikariCP or keep BoneCP fallback libs.");
		}
		for (String warning : warnings) {
			log.warn("MameAion Runtime SelfCheck warning: " + warning);
		}
		if (warnings.isEmpty()) {
			log.info("MameAion Runtime SelfCheck completed without dependency warnings.");
		}
		log.info("==================================================");
	}

	private static void logDependency(String name, boolean present, boolean requiredForJdk25Runtime) {
		String level = present ? "OK" : (requiredForJdk25Runtime ? "MISSING" : "optional/missing");
		if (present || !requiredForJdk25Runtime) {
			log.info("Runtime dependency " + level + ": " + name);
		} else {
			log.warn("Runtime dependency " + level + ": " + name);
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
