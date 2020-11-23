package com.max.app.session.tracker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;

public class SessionTracker {

    private final DelayQueue<SessionExpiration> aboutToExpireQueue = new DelayQueue<>();

    private final ConcurrentMap<String, SessionExpiration> userTasks = new ConcurrentHashMap<>();

    public void addUser(String user) {
        SessionExpiration task = new SessionExpiration(user);
        userTasks.putIfAbsent(user, task);
        aboutToExpireQueue.add(task);
    }

    public void accessSession(String user) {
        SessionExpiration task = userTasks.get(user);
        boolean wasRemoved = aboutToExpireQueue.remove(task);

        if (!wasRemoved) {
            throw new IllegalStateException("Can't find delayed task for user: " + user);
        }

        SessionExpiration newTask = new SessionExpiration(user);
        userTasks.put(user, newTask);
        aboutToExpireQueue.add(newTask);
    }


    public DelayQueue<SessionExpiration> getAboutToExpireQueue() {
        return aboutToExpireQueue;
    }
}
