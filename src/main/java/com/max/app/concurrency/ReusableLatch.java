package com.max.app.concurrency;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class ReusableLatch {

    private static final int NOT_USED = -1;

    private final Sync sync;

    public ReusableLatch(int cnt) {
        this.sync = new Sync(cnt);
    }

    public void countDown() {
        sync.releaseShared(NOT_USED);
    }

    public void await() {
        sync.incWaitCounter();
        sync.acquireShared(NOT_USED);
    }

    private static final class Sync extends AbstractQueuedSynchronizer {

        private final int totalCnt;

        public Sync(int cnt) {
            this.totalCnt = cnt;
            setState(cnt);
        }

        private void incWaitCounter() {
            // increment 'waitCnt'
            while (true) {

                int oldState = getState();
                int newState = incWaitCount(oldState);

                if (compareAndSetState(oldState, newState)) {
                    break;
                }
            }
        }

        private static int incWaitCount(int state) {
            return state + (1 << 16);
        }

        private static int decWaitCnt(int state) {
            int waitCnt = (state >>> 16) - 1;
            return (state & 0xFF_FF) | (waitCnt << 16);
        }

        private static int decReleaseCnt(int state) {
            return state - 1;
        }

        private static int updateReleaseCnt(int state, int cnt) {
            return (state & 0xFF_FF_00_00) | cnt;
        }

        private static int releaseCnt(int state) {
            return state & 0xFF_FF;
        }

        private static int waitCnt(int state) {
            return state >>> 16;
        }

        @Override
        protected int tryAcquireShared(int notUsed) {

            // latch 'closed'
            if (releaseCnt(getState()) != 0) {
                return -1;
            }

            // latch 'open'
            while (true) {
                int state = getState();
                int newState = decWaitCnt(state);

                boolean lastWaitingThread = waitCnt(newState) == 0;

                if (lastWaitingThread) {
                    // last waiting thread should reset latch
                    newState = updateReleaseCnt(newState, totalCnt);
                }

                if (compareAndSetState(state, newState)) {
                    return lastWaitingThread ? 1 : 0;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(int notUsed) {
            while (true) {
                int oldState = getState();

                // proceed only if 'releaseCnt' > 0, otherwise just spin
                if (releaseCnt(oldState) > 0) {

                    int newState = decReleaseCnt(oldState);

                    if (compareAndSetState(oldState, newState)) {
                        return releaseCnt(newState) == 0;
                    }
                }
            }
        }
    }
}
