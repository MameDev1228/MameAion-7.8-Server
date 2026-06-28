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

package com.aionemu.commons.configs;

import java.io.File;

import com.aionemu.commons.configuration.Property;

/**
 * This class holds all configuration of database
 * 
 * @author SoulKeeper
 */
public class DatabaseConfig {

	/**
	 * Default database url.
	 */
	@Property(key = "database.url", defaultValue = "jdbc:mysql://localhost:3306/aion_uni?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&tcpKeepAlive=true&connectTimeout=10000&socketTimeout=120000")
	public static String DATABASE_URL;

	/**
	 * Name of database Driver
	 */
	@Property(key = "database.driver", defaultValue = "com.mysql.cj.jdbc.Driver")
	public static Class<?> DATABASE_DRIVER;

	/**
	 * Default database user
	 */
	@Property(key = "database.user", defaultValue = "root")
	public static String DATABASE_USER;

	/**
	 * Default database password
	 */
	@Property(key = "database.password", defaultValue = "root")
	public static String DATABASE_PASSWORD;


	/**
	 * Preferred database pool. Use auto to prefer HikariCP when present and fallback to BoneCP.
	 */
	@Property(key = "database.pool", defaultValue = "auto")
	public static String DATABASE_POOL;

	/**
	 * Max connections used by the JDK25/HikariCP pool.
	 */
	@Property(key = "database.connectionpool.connections.max", defaultValue = "10")
	public static int DATABASE_CONNECTIONS_MAX;

	/**
	 * Max wait time when obtaining a DB connection.
	 */
	@Property(key = "database.connectionpool.timeout", defaultValue = "10000")
	public static long DATABASE_CONNECTION_TIMEOUT;

	/**
	 * Recycle DB connections before MySQL/network silently closes them.
	 */
	@Property(key = "database.connectionpool.max_lifetime", defaultValue = "600000")
	public static long DATABASE_MAX_LIFETIME;

	/**
	 * Close idle connections after this time.
	 */
	@Property(key = "database.connectionpool.idle_timeout", defaultValue = "300000")
	public static long DATABASE_IDLE_TIMEOUT;

	/**
	 * Keep idle connections alive. HikariCP versions without this setter ignore it.
	 */
	@Property(key = "database.connectionpool.keepalive_time", defaultValue = "120000")
	public static long DATABASE_KEEPALIVE_TIME;

	/**
	 * Max time for connection validation.
	 */
	@Property(key = "database.connectionpool.validation_timeout", defaultValue = "5000")
	public static long DATABASE_VALIDATION_TIMEOUT;

	/**
	 * -1 = use pool default behavior.
	 */
	@Property(key = "database.connectionpool.minimum_idle", defaultValue = "-1")
	public static int DATABASE_MINIMUM_IDLE;

	/**
	 * 0 = disabled. Use only when debugging connection leaks.
	 */
	@Property(key = "database.connectionpool.leak_detection_threshold", defaultValue = "0")
	public static long DATABASE_LEAK_DETECTION_THRESHOLD;

	/**
	 * Amount of partitions used by BoneCP
	 */
	@Property(key = "database.bonecp.partition.count", defaultValue = "2")
	public static int DATABASE_BONECP_PARTITION_COUNT;

	/**
	 * Minimum amount of connections that are always active in bonecp partition
	 */
	@Property(key = "database.bonecp.partition.connections.min", defaultValue = "2")
	public static int DATABASE_BONECP_PARTITION_CONNECTIONS_MIN;

	/**
	 * Maximum amount of connections that are allowed to use in bonecp partition
	 */
	@Property(key = "database.bonecp.partition.connections.max", defaultValue = "5")
	public static int DATABASE_BONECP_PARTITION_CONNECTIONS_MAX;

	/**
	 * Location of database script context descriptor
	 */
	@Property(key = "database.scriptcontext.descriptor", defaultValue = "./data/scripts/system/database/database.xml")
	public static File DATABASE_SCRIPTCONTEXT_DESCRIPTOR;
}