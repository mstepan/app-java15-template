package com.max.app.movie;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MovieService implements AutoCloseable {

    private static final String KEY_FORMAT = "movies:%s:info";

    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    private final Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);

    public void store(Movie movie) {
        jedis.hset(key(movie.getId()), toMap(movie));
    }

    public Movie findById(String id) {
        return toMovie(jedis.hmget(key(id), "id", "title", "description", "year", "votes"));
    }

    public void delete(Movie movie) {
        jedis.del(key(movie.getId()));
    }

    public void upVote(Movie movie) {
        jedis.hincrBy(key(movie.getId()), "votes", 1);
    }

    public void downVote(Movie movie) {
        jedis.hincrBy(key(movie.getId()), "votes", -1);
    }

    @Override
    public void close() {
        jedis.close();
    }

    private static String key(String id) {
        return String.format(KEY_FORMAT, id);
    }

    private static Map<String, String> toMap(Movie movie) {
        Map<String, String> map = new HashMap<>();
        map.put("id", movie.getId());
        map.put("title", movie.getTitle());
        map.put("description", movie.getDescription());
        map.put("year", String.valueOf(movie.getYear()));
        map.put("votes", String.valueOf(movie.getVotes()));
        return map;
    }

    private static Movie toMovie(List<String> data) {
        assert data.size() == 5 : "Incorrect hash length from Redis";
        return new Movie(data.get(0), data.get(1), data.get(2),
                         Integer.parseInt(data.get(3)),
                         Integer.parseInt(data.get(4)));
    }

}
