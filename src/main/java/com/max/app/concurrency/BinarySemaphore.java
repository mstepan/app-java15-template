package com.max.app.concurrency;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Binary semaphore, aka mutex.
 */
public final class BinarySemaphore {

    private static final int NOT_USED = 1;
    private final Sync sync = new Sync();

    public void acquire() {
        sync.acquire(NOT_USED);
    }

    public void release() {
        sync.release(NOT_USED);
    }

    private static final class Sync extends AbstractQueuedSynchronizer {
        Sync() {
            setState(1);
        }

        @Override
        protected boolean tryAcquire(int notUsed) {
            int state = getState();

            if (state == 1 && compareAndSetState(1, 0)) {
                return true;
            }

            return false;
        }

        @Override
        protected boolean tryRelease(int notUsed) {
            setState(1);
            return true;
        }
    }

}
