package com.max.app.redis.sentinel;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Set;

public final class SentinelMain {

    private enum RedisTier {
        DEV(0),
        STAGE(1),
        PROD(2);

        private final int dbIndex;

        RedisTier(int dbIndex) {
            this.dbIndex = dbIndex;
        }
    }

    private static final String REDIS_PASSWORD = "611191";

    public static void main(String[] args) throws Exception {

        Set<String> sentinelHosts = Set.of("localhost:26379", "localhost:26380", "localhost:26381");

        JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinelHosts);

        Jedis jedis = pool.getResource();
        jedis.auth(REDIS_PASSWORD);
        try {
            jedis.select(RedisTier.DEV.dbIndex);
            jedis.mset("key1", "111", "key2", "222", "key3", "value3");
        }
        finally {
            jedis.close();
        }

        System.out.printf("RedisMain completed. java version: %s%n", System.getProperty("java.version"));
    }

}
