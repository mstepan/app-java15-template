package com.max.app.movie;

public final class RedisMain {

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
}
