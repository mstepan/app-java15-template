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

        private static int incWaitCount(int state){
            return state + (1 << 16);
        }

        @Override
        protected int tryAcquireShared(int notUsed) {

            State state = new State(getState());

            // latch 'closed'
            if (state.releaseCnt != 0) {
                return -1;
            }

            // latch 'open'
            state.waitCnt -= 1;

            // last waiting thread should reset latch
            if (state.waitCnt == 0) {

                state.releaseCnt = totalCnt;
                compareAndSetState(state.getOldState(), state.getState());

                // '1' - allow acquire
                return 1;
            }
            else {
                // '0' won't allow any additional acquires, till latch not fully reset
                return 0;
            }

        }

        @Override
        protected boolean tryReleaseShared(int notUsed) {
            while (true) {
                State state = new State(getState());

                // proceed only if 'releaseCnt' > 0, otherwise just spin
                if (state.releaseCnt > 0) {

                    state.releaseCnt -= 1;
                    int newState = state.getState();

                    if (compareAndSetState(state.getOldState(), newState)) {
                        return (newState & 0xFF) == 0;
                    }
                }
            }
        }
    }

    private static final class State {

        final int oldState;

        // lower 16 bits of state
        int releaseCnt;

        // higher 16 bits of state
        int waitCnt;

        State(int state) {
            this.oldState = state;
            this.releaseCnt = state & 0xFFFF;
            this.waitCnt = state >>> 16;
        }

        public int getOldState() {
            return oldState;
        }

        int getState() {
            return (waitCnt << 16) | releaseCnt;
        }

        @Override
        public String toString() {
            return String.format("waitCnt: %d, releaseCnt: %d", waitCnt, releaseCnt);
        }

    }
}
