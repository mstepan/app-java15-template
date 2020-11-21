package com.max.app.concurrency;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class OneTimeLatch {

    private static final int NOT_USED = -1;

    private final Sync sync;

    public OneTimeLatch(int cnt) {
        this.sync = new Sync(cnt);
    }

    public void countDown() {
        sync.releaseShared(NOT_USED);
    }

    public void await() {
        sync.acquireShared(NOT_USED);
    }

    private static final class Sync extends AbstractQueuedSynchronizer {
        public Sync(int cnt) {
            setState(cnt);
        }

        @Override
        protected boolean tryReleaseShared(int notUsed) {

            while (true) {
                int state = getState();

                if (compareAndSetState(state, state - 1)) {
                    if (getState() == 0) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            }
        }

        @Override
        protected int tryAcquireShared(int notUsed) {
            int state = getState();
            return state == 0 ? 1 : -1;
        }
    }
}
