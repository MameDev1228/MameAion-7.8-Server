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

package com.aionemu.commons.database;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.configs.DatabaseConfig;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * <b>Database Factory</b><br>
 * <br>
 * Creates and owns the server DB connection pool.
 * <br>
 * MameAion JDK25 note:
 * HikariCP is supported through reflection so this module can still compile
 * without a direct Hikari jar. Set database.pool=hikari and place HikariCP in
 * libs to force it, or keep database.pool=auto to use Hikari when present and
 * fallback to BoneCP otherwise.
 *
 * @author Disturbing
 * @author SoulKeeper
 */
public class DatabaseFactory {

	private static final Logger log = LoggerFactory.getLogger(DatabaseFactory.class);

	private static DbPoolAdapter connectionPool;
	private static String databaseName;
	private static int databaseMajorVersion;
	private static int databaseMinorVersion;

	public synchronized static void init() {
		if (connectionPool != null) {
			return;
		}

		loadJdbcDriver();
		connectionPool = createPoolAdapter();
		readDatabaseMetadata();
		log.info("Successfully connected to database using {} pool", connectionPool.getPoolName());
	}

	private static void loadJdbcDriver() {
		try {
			DatabaseConfig.DATABASE_DRIVER.getDeclaredConstructor().newInstance();
		} catch (Throwable e) {
			log.error("Error obtaining DB driver: {}", DatabaseConfig.DATABASE_DRIVER, e);
			throw new Error("DB Driver doesn't exist or cannot be initialized: " + DatabaseConfig.DATABASE_DRIVER, e);
		}
	}

	private static DbPoolAdapter createPoolAdapter() {
		String poolMode = DatabaseConfig.DATABASE_POOL == null ? "auto" : DatabaseConfig.DATABASE_POOL.trim().toLowerCase();
		boolean tryHikari = poolMode.equals("auto") || poolMode.equals("hikari");
		boolean tryBoneCp = poolMode.equals("auto") || poolMode.equals("bonecp");

		if (tryHikari) {
			try {
				return new HikariPoolAdapter();
			} catch (ClassNotFoundException e) {
				if (poolMode.equals("hikari")) {
					log.error("database.pool=hikari is configured but HikariCP is missing from libs.", e);
					throw new Error("HikariCP jar is missing. Add HikariCP to libs or set database.pool=auto/bonecp.", e);
				}
				log.warn("HikariCP not found in libs. Falling back to BoneCP. Add HikariCP and keep database.pool=auto to use the JDK25-preferred pool.");
			} catch (Throwable e) {
				if (poolMode.equals("hikari")) {
					log.error("Error while creating HikariCP pool", e);
					throw new Error("DatabaseFactory not initialized with HikariCP!", e);
				}
				log.warn("HikariCP pool creation failed. Falling back to BoneCP because database.pool=auto.", e);
			}
		}

		if (tryBoneCp) {
			return new BoneCpPoolAdapter();
		}

		throw new Error("Unsupported database.pool value: " + DatabaseConfig.DATABASE_POOL + " (expected auto, hikari, or bonecp)");
	}

	private static void readDatabaseMetadata() {
		try {
			Connection c = getConnection();
			try {
				DatabaseMetaData dmd = c.getMetaData();
				databaseName = dmd.getDatabaseProductName();
				databaseMajorVersion = dmd.getDatabaseMajorVersion();
				databaseMinorVersion = dmd.getDatabaseMinorVersion();
			} finally {
				c.close();
			}
		} catch (Exception e) {
			log.error("Error with connection string: " + DatabaseConfig.DATABASE_URL, e);
			throw new Error("DatabaseFactory not initialized!", e);
		}
	}

	public static Connection getConnection() throws SQLException {
		if (connectionPool == null) {
			throw new SQLException("DatabaseFactory is not initialized");
		}

		Connection con = connectionPool.getConnection();

		if (!con.getAutoCommit()) {
			log.error("Connection Settings Error: Connection obtained from database factory should be in auto-commit mode. "
					+ "Forcing auto-commit to true. Please check source code for connections being not properly closed.");
			con.setAutoCommit(true);
		}

		return con;
	}

	public int getActiveConnections() {
		return connectionPool == null ? 0 : connectionPool.getActiveConnections();
	}

	public int getIdleConnections() {
		return connectionPool == null ? 0 : connectionPool.getIdleConnections();
	}

	public static synchronized void shutdown() {
		try {
			if (connectionPool != null) {
				connectionPool.shutdown();
			}
		} catch (Exception e) {
			log.warn("Failed to shutdown DatabaseFactory", e);
		}
		connectionPool = null;
	}

	public static void close(PreparedStatement st, Connection con) {
		close(st);
		close(con);
	}

	public static void close(PreparedStatement st) {
		if (st == null) {
			return;
		}

		try {
			if (!st.isClosed()) {
				st.close();
			}
		} catch (SQLException e) {
			log.error("Can't close Prepared Statement", e);
		}
	}

	public static void close(Connection con) {
		if (con == null)
			return;

		try {
			if (!con.getAutoCommit()) {
				con.setAutoCommit(true);
			}
		} catch (SQLException e) {
			log.error("Failed to set autocommit to true while closing connection: ", e);
		}

		try {
			con.close();
		} catch (SQLException e) {
			log.error("DatabaseFactory: Failed to close database connection!", e);
		}
	}

	public static String getDatabaseName() {
		return databaseName;
	}

	public static int getDatabaseMajorVersion() {
		return databaseMajorVersion;
	}

	public static int getDatabaseMinorVersion() {
		return databaseMinorVersion;
	}

	private DatabaseFactory() {
	}

	private interface DbPoolAdapter {
		Connection getConnection() throws SQLException;
		int getActiveConnections();
		int getIdleConnections();
		void shutdown();
		String getPoolName();
	}

	private static final class BoneCpPoolAdapter implements DbPoolAdapter {
		private final BoneCP pool;

		private BoneCpPoolAdapter() {
			if (DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MIN > DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MAX) {
				log.error("Please check your database configuration. Minimum amount of connections is > maximum");
				DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MAX = DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MIN;
			}

			BoneCPConfig config = new BoneCPConfig();
			config.setPartitionCount(DatabaseConfig.DATABASE_BONECP_PARTITION_COUNT);
			config.setMinConnectionsPerPartition(DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MIN);
			config.setMaxConnectionsPerPartition(DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MAX);
			config.setUsername(DatabaseConfig.DATABASE_USER);
			config.setPassword(DatabaseConfig.DATABASE_PASSWORD);
			config.setJdbcUrl(DatabaseConfig.DATABASE_URL);
			config.setDisableJMX(true);

			try {
				pool = new BoneCP(config);
			} catch (SQLException e) {
				log.error("Error while creating BoneCP DB Connection pool", e);
				throw new Error("DatabaseFactory not initialized with BoneCP!", e);
			}
		}

		@Override
		public Connection getConnection() throws SQLException {
			return pool.getConnection();
		}

		@Override
		public int getActiveConnections() {
			return pool.getTotalLeased();
		}

		@Override
		public int getIdleConnections() {
			return pool.getStatistics().getTotalFree();
		}

		@Override
		public void shutdown() {
			pool.shutdown();
		}

		@Override
		public String getPoolName() {
			return "BoneCP";
		}
	}

	private static final class HikariPoolAdapter implements DbPoolAdapter {
		private final Object dataSource;
		private final Method getConnectionMethod;
		private final Method closeMethod;

		private HikariPoolAdapter() throws Exception {
			Class<?> dataSourceClass = Class.forName("com.zaxxer.hikari.HikariDataSource");
			dataSource = dataSourceClass.getDeclaredConstructor().newInstance();

			invokeRequired(dataSource, "setPoolName", "MameAion-Hikari");
			invokeRequired(dataSource, "setDriverClassName", DatabaseConfig.DATABASE_DRIVER.getName());
			invokeRequired(dataSource, "setJdbcUrl", DatabaseConfig.DATABASE_URL);
			invokeRequired(dataSource, "setUsername", DatabaseConfig.DATABASE_USER);
			invokeRequired(dataSource, "setPassword", DatabaseConfig.DATABASE_PASSWORD);
			invokeRequired(dataSource, "setMaximumPoolSize", DatabaseConfig.DATABASE_CONNECTIONS_MAX);
			invokeRequired(dataSource, "setConnectionTimeout", DatabaseConfig.DATABASE_CONNECTION_TIMEOUT);
			invokeRequired(dataSource, "setValidationTimeout", DatabaseConfig.DATABASE_VALIDATION_TIMEOUT);
			invokeOptional(dataSource, "setMaxLifetime", DatabaseConfig.DATABASE_MAX_LIFETIME);
			invokeOptional(dataSource, "setIdleTimeout", DatabaseConfig.DATABASE_IDLE_TIMEOUT);
			invokeOptional(dataSource, "setKeepaliveTime", DatabaseConfig.DATABASE_KEEPALIVE_TIME);
			invokeOptional(dataSource, "setLeakDetectionThreshold", DatabaseConfig.DATABASE_LEAK_DETECTION_THRESHOLD);
			if (DatabaseConfig.DATABASE_MINIMUM_IDLE >= 0) {
				invokeOptional(dataSource, "setMinimumIdle", DatabaseConfig.DATABASE_MINIMUM_IDLE);
			}

			getConnectionMethod = dataSourceClass.getMethod("getConnection");
			closeMethod = dataSourceClass.getMethod("close");
		}

		@Override
		public Connection getConnection() throws SQLException {
			try {
				return (Connection) getConnectionMethod.invoke(dataSource);
			} catch (Throwable e) {
				Throwable cause = e.getCause() != null ? e.getCause() : e;
				if (cause instanceof SQLException) {
					throw (SQLException) cause;
				}
				SQLException sqlException = new SQLException("Failed to obtain HikariCP connection", cause);
				throw sqlException;
			}
		}

		@Override
		public int getActiveConnections() {
			return getPoolMetric("getActiveConnections");
		}

		@Override
		public int getIdleConnections() {
			return getPoolMetric("getIdleConnections");
		}

		@Override
		public void shutdown() {
			try {
				closeMethod.invoke(dataSource);
			} catch (Throwable e) {
				throw new RuntimeException("Failed to close HikariCP datasource", e);
			}
		}

		@Override
		public String getPoolName() {
			return "HikariCP";
		}

		private int getPoolMetric(String methodName) {
			try {
				Object mxBean = dataSource.getClass().getMethod("getHikariPoolMXBean").invoke(dataSource);
				if (mxBean == null) {
					return -1;
				}
				Object value = mxBean.getClass().getMethod(methodName).invoke(mxBean);
				return value instanceof Number ? ((Number) value).intValue() : -1;
			} catch (Throwable e) {
				return -1;
			}
		}
	}

	private static void invokeRequired(Object target, String methodName, Object value) throws Exception {
		Method method = findSetter(target.getClass(), methodName, value);
		if (method == null) {
			throw new NoSuchMethodException(target.getClass().getName() + "." + methodName + "(" + value.getClass().getSimpleName() + ")");
		}
		method.invoke(target, value);
	}

	private static void invokeOptional(Object target, String methodName, Object value) throws Exception {
		Method method = findSetter(target.getClass(), methodName, value);
		if (method != null) {
			method.invoke(target, value);
		}
	}

	private static Method findSetter(Class<?> clazz, String methodName, Object value) {
		Class<?> valueClass = value.getClass();
		for (Method method : clazz.getMethods()) {
			if (!method.getName().equals(methodName) || method.getParameterTypes().length != 1) {
				continue;
			}
			Class<?> paramType = method.getParameterTypes()[0];
			if (paramType.isPrimitive()) {
				if ((paramType == int.class && valueClass == Integer.class) || (paramType == long.class && valueClass == Long.class)
						|| (paramType == boolean.class && valueClass == Boolean.class)) {
					return method;
				}
			} else if (paramType.isAssignableFrom(valueClass)) {
				return method;
			}
		}
		return null;
	}
}
