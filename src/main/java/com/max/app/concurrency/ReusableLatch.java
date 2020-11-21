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

        private void incWaitCounter(){
            // increment 'waitCnt'
            while (true) {

                State state = new State(getState());
                state.waitCnt += 1;

                if (compareAndSetState(state.getOldState(), state.getState())) {
                    break;
                }
            }
        }

        @Override
        protected int tryAcquireShared(int notUsed) {

            State state = new State(getState());

            if (state.releaseCnt == 0) {

                state.waitCnt -= 1;

                // last wait thread should reset latch
                if (state.waitCnt == 0) {

//                    System.out.println("RESETTING LATCH");

                    state.releaseCnt = totalCnt;

                    compareAndSetState(state.getOldState(), state.getState());
//                    System.out.println("tryAcquireShared called 0, waitCnt: " + state.waitCnt);
                    return 1;
                }
                else {
//                    System.out.println("tryAcquireShared called 1, waitCnt: " + state.waitCnt);
                    return 0;
                }
            }

//            System.out.println("tryAcquireShared called -1, waitCnt: " + state.waitCnt);
            return -1;
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
//                        System.out.printf("HERE: %b%n", ((newState & 0xFF) == 0));
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
