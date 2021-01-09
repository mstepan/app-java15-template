package com.max.app;

import com.max.app.movie.Movie;
import com.max.app.movie.MovieService;

import java.util.concurrent.ThreadLocalRandom;

public final class Main {

    public static void main(String[] args) throws Exception {

        try (MovieService service = new MovieService()) {

            Movie alien2 = new Movie("id-133", "Alien 2", "Cool film about aliens", 0);

            service.store(alien2);

            for (int i = 0; i < 10; ++i) {
                service.upVote(alien2);
            }

            service.downVote(alien2);
            service.downVote(alien2);
            service.downVote(alien2);

            Movie movieSummary = service.findById(alien2.getId());

            System.out.println(movieSummary);

            service.delete(movieSummary);
        }

        System.out.printf("Main completed. java version: %s%n", System.getProperty("java.version"));
    }

    private static final int ALPHA_RANGE = 'z' - 'a' + 1;
    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    private static String generateBigString(int sizeInBytes) {
        char[] arr = new char[sizeInBytes];

        for (int i = 0; i < arr.length; ++i) {
            arr[i] = (char) ('a' + RAND.nextInt(ALPHA_RANGE));
        }
        return String.valueOf(arr);
    }

}
