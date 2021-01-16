package com.max.app.redis;

import redis.clients.jedis.Jedis;

public final class RedisMain {

    public static void main(String[] args) throws Exception {
        Jedis jedis = new Jedis("localhost", 6379);

        try {
            jedis.select(0);
            jedis.set("key1", "some value 111");
            System.out.println(jedis.get("key1"));
        }
        finally {
            jedis.close();
        }

        System.out.printf("RedisMain completed. java version: %s%n", System.getProperty("java.version"));
    }

}
