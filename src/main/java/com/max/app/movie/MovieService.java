package com.max.app.movie;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieService implements AutoCloseable {

    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    private final Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);

    public void store(Movie movie) {
        Transaction transaction = jedis.multi();

        Map<String, String> info = new HashMap<>();
        info.put("title", movie.getTitle());
        info.put("description", movie.getDescription());
        info.put("votes", "0");

        transaction.hset(String.format("movies:%s:info", movie.getId()), info);

        transaction.exec();
    }

    public Movie findById(String id) {
        List<String> info = jedis.hmget(String.format("movies:%s:info", id), "title", "description", "votes");

        return new Movie(id, info.get(0), info.get(1), Integer.parseInt(info.get(2)));
    }

    public void delete(Movie movie) {
        Transaction tr = jedis.multi();

        tr.del(String.format("movies:%s:info", movie.getId()));

        tr.exec();
    }

    public void upVote(Movie movie) {
        jedis.hincrBy(String.format("movies:%s:info", movie.getId()), "votes", 1);
    }

    public void downVote(Movie movie) {
        jedis.hincrBy(String.format("movies:%s:info", movie.getId()), "votes", -1);
    }

    @Override
    public void close() throws Exception {
        jedis.close();
    }
}
