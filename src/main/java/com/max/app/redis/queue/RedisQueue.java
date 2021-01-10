package com.max.app.redis.queue;

import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * Queue backed by Redis list.
 */
public final class RedisQueue {

    private final String listName;

    public RedisQueue() {
        listName = "dss:uap:async:tasks:queue:" + UUID.randomUUID();
        System.out.printf("list key name: %s%n", listName);
    }

    /**
     * Taking into account that Jedis class is not thread safe by design, we need to use ThreadLocal here.
     */
    private static final ThreadLocal<Jedis> LOCAL_JEDIS = ThreadLocal.withInitial(() -> new Jedis("localhost"));

    /**
     * Use RPUSH Redis command to add value to the tail of a list.
     */
    public void add(String value) {
        LOCAL_JEDIS.get().rpush(listName, value);
    }

    /**
     * Use LPOP Redis command to get value from the head of a list.
     * This operation is not blocking, so returns null immediately if the list is empty.
     */
    public String peek() {
        return LOCAL_JEDIS.get().lpop(listName);
    }

    /**
     * Use LLEN Redis command to obtain list size.
     * <p>
     * TODO: list length can be up to 2^32-1, so int may overflow below.
     */
    public int size() {
        return LOCAL_JEDIS.get().llen(listName).intValue();
    }
}
