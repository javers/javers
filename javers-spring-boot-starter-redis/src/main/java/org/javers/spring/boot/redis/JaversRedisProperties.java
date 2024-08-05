package org.javers.spring.boot.redis;

import java.time.Duration;

import org.javers.spring.JaversSpringProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "javers")
public class JaversRedisProperties extends JaversSpringProperties {

	public static class Redis {
		private boolean enabled = true;
		private String host = "localhost";
		private int port = 6379;
		private String password;
		private int timeout = 3000;
		private boolean useSsl;
		private int database = 0;
		private Duration auditDuration = Duration.ofDays(7);
		private boolean cleanExpiredSnapshotsOnStart;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public int getTimeout() {
			return timeout;
		}

		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}

		public boolean isUseSsl() {
			return useSsl;
		}

		public void setUseSsl(boolean useSsl) {
			this.useSsl = useSsl;
		}

		public int getDatabase() {
			return database;
		}

		public void setDatabase(int database) {
			this.database = database;
		}

		public Duration getAuditDuration() {
			return auditDuration;
		}

		public void setAuditDuration(Duration auditDuration) {
			this.auditDuration = auditDuration;
		}

		public boolean isCleanExpiredSnapshotsOnStart() {
			return cleanExpiredSnapshotsOnStart;
		}

		public void setCleanExpiredSnapshotsOnStart(boolean cleanExpiredSnapshotsOnStart) {
			this.cleanExpiredSnapshotsOnStart = cleanExpiredSnapshotsOnStart;
		}
	}

	private Redis redis;

	@Override
	protected String defaultObjectAccessHook() {
		return RedisObjectAccessHook.class.getName();
	}

	public Redis getRedis() {
		return redis;
	}

	public void setRedis(Redis redis) {
		this.redis = redis;
	}

}
