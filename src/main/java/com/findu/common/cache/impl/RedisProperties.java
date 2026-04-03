package com.findu.common.cache.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 配置属性。
 */
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    /**
     * Redis 服务器地址。
     */
    private String host = "localhost";

    /**
     * Redis 服务器端口。
     */
    private int port = 6379;

    /**
     * Redis 密码。
     */
    private String password;

    /**
     * 数据库索引（默认为 0）。
     */
    private int database = 0;

    /**
     * 连接超时时间（毫秒）。
     */
    private long timeout = 3000;

    /**
     * 连接池配置。
     */
    private Pool pool = new Pool();

    /**
     * Lettuce 连接池配置。
     */
    public static class Pool {
        /**
         * 连接池最大连接数（使用负值表示没有限制）。
         */
        private int maxActive = 8;

        /**
         * 连接池最大阻塞等待时间（使用负值表示没有限制）。
         */
        private long maxWait = -1;

        /**
         * 连接池中的最大空闲连接。
         */
        private int maxIdle = 8;

        /**
         * 连接池中的最小空闲连接。
         */
        private int minIdle = 0;

        public int getMaxActive() {
            return maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public long getMaxWait() {
            return maxWait;
        }

        public void setMaxWait(long maxWait) {
            this.maxWait = maxWait;
        }

        public int getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }
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

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }
}

