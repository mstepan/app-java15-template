package com.max.app.redis;

import redis.clients.jedis.Jedis;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class RedisMain {

    private enum NodeType {
        MASTER("localhost", 6390),
        SLAVE("localhost", 6391);

        private final String hostname;
        private final int port;

        NodeType(String hostname, int port) {
            this.hostname = hostname;
            this.port = port;
        }
    }

    public static void main(String[] args) throws Exception {
        Jedis jedis = new Jedis(NodeType.MASTER.hostname, NodeType.MASTER.port);
        jedis.auth("611191");

        try {
            jedis.mset("key1", "222");
            System.out.println(jedis.get("key1"));
        }
        finally {
            jedis.close();
        }

        System.out.printf("RedisMain completed. java version: %s%n", System.getProperty("java.version"));
    }

}
