package com.max.app.session.tracker;

import java.util.concurrent.TimeUnit;

public final class MainDelayQueue {

    public static void main(String[] args) throws Exception {

        final SessionTracker tracker = new SessionTracker();

        new Thread(() -> {
            System.out.println("Expiration tracker started");

            while (true) {
                try {
                    SessionExpiration sessionExp = tracker.getAboutToExpireQueue().take();
                    System.out.println("Expired for user: " + sessionExp.getUser());
                }
                catch (InterruptedException interEx) {
                    break;
                }
            }

        }).start();

        tracker.addUser("maksym");

        TimeUnit.MILLISECONDS.sleep(200);
        tracker.addUser("olesia");

        TimeUnit.MILLISECONDS.sleep(2000);
        tracker.addUser("zorro");

        TimeUnit.MILLISECONDS.sleep(2000);
        tracker.accessSession("maksym");

        TimeUnit.SECONDS.sleep(60);

        System.out.printf("java version: %s%n", System.getProperty("java.version"));
    }


}
