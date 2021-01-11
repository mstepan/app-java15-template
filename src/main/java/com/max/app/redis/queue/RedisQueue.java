package com.max.app.redis.queue;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

/**
 * Queue backed by Redis list.
 */
public final class RedisQueue {

    // 0, means infinity timeout
    private static final int INFINITY_TIMEOUT = 0;

    private final String listName;
    private final String backupListName;

    public RedisQueue() {
        listName = "dss:uap:async:tasks:queue:" + UUID.randomUUID();
        backupListName = "dss:uap:async:tasks:queue:" + UUID.randomUUID();

        System.out.printf("list name: %s%n", listName);
        System.out.printf("backup list name: %s%n", backupListName);
    }

    /**
     * Taking into account that Jedis class is not thread safe by design, we need to use ThreadLocal here.
     */
    private static final ThreadLocal<Jedis> LOCAL_JEDIS = ThreadLocal.withInitial(() -> new Jedis("localhost"));

    /**
     * Use LPUSH Redis command to add value to the head of a list.
     */
    public void add(String value) {
        LOCAL_JEDIS.get().lpush(listName, value);
    }

    /**
     * Use RPOP Redis command to get value from the tail of a list.
     * This operation is not blocking, so returns null immediately if the list is empty.
     */
    public String peek() {
        return LOCAL_JEDIS.get().rpop(listName);
    }

    /**
     * Use BRPOP Redis command, which is similar to RPOP, but will block the thread if list is empty.
     */
    public String take() {
        // res[0] - list name, res[1] - popped value
        List<String> res = LOCAL_JEDIS.get().brpop(INFINITY_TIMEOUT, listName);
        return res.get(1);
    }

    public String takeReliable(){
        return takeReliable(INFINITY_TIMEOUT);
    }

    /**
     * Use BRPOPLPUSH Redis command & reliable queue pattern (https://redis.io/commands/rpoplpush).
     */
    public String takeReliable(int timeout){
        return LOCAL_JEDIS.get().brpoplpush(listName, backupListName, timeout);
    }

    /**
     * Use LREM Redis command to remove element from back up queue.
     */
    public void removeFromBackup(String value){
        LOCAL_JEDIS.get().lrem(backupListName, 1, value);
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
