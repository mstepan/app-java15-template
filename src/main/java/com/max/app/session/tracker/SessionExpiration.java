package com.max.app.session.tracker;

import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public final class SessionExpiration implements Delayed {

    private static final long SESSION_TIME_IN_MS = 10_000L; // 5 sec

    private final String user;
    private long expirationTime;

    public SessionExpiration(String user) {
        this.user = user;
        this.expirationTime = System.currentTimeMillis() + SESSION_TIME_IN_MS;
    }

    public String getUser() {
        return user;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != SessionExpiration.class) {
            return false;
        }

        SessionExpiration other = (SessionExpiration) obj;

        return Objects.equals(user, other.user);
    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = expirationTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        return Long.compare(expirationTime, ((SessionExpiration) other).expirationTime);
    }
}
