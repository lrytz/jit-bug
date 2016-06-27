package org.alext.jitbug;

import java.util.*;
import java.util.concurrent.CountDownLatch;


import scala.Tuple6;

public class TestCase {
    public static void main(String[] args) throws InterruptedException {
        int count = 10;
        for (int i = 0; i < 3; i++) {
            CountDownLatch l = new CountDownLatch(count);
            for (int j = 0; j < count; j++) {
                Thread t = new Thread(new Worker(l));
                t.start();
                System.out.println(" Started thread: " + t.getName());
            }
            l.await();
        }
    }

    public static class Worker implements Runnable {

        private CountDownLatch latch;

        public Worker(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                List<String> demoIds = new ArrayList<>();
                for (int i = 1; i <= 20; i++)
                    demoIds.add("demoID=" + i);

                for (long i = 0; i < 40_000l; i++) {
                    TimeShiftingViewingPeriod viewingPeriod = TimeShiftingViewingPeriod.LIVE_7;
                    Tuple6<Long, Long, Byte, String, Byte, String> res = doStuff(demoIds, 192837934738l, viewingPeriod, "0");
                    if (res._3() == null) {
                        System.out.println(" Start reproducing: Thread" + Thread.currentThread().getName() + " at step: " + i + " res:  " + res);
                        break;
                    }
                }
            } finally {
                latch.countDown();
            }
        }

        private Tuple6<Long, Long, Byte, String, Byte, String> doStuff(
                List<String> demoIds,
                long programId,
                TimeShiftingViewingPeriod viewingPeriod,
                String marketBreak) {
            C<Tuple6<Long, Long, Byte, String, Byte, String>> r = new C<>(null);
            demoIds.forEach(demoId -> r.t = new Tuple6<>(null, programId, viewingPeriod.getId(), null, null, marketBreak));
            return r.t;
        }

    }

    static class C<T> {
        T t;

        public C(T t) {
            this.t = t;
        }
    }

    static enum TimeShiftingViewingPeriod {
        LIVE_7((byte) 9);

        private Byte id;

        TimeShiftingViewingPeriod(byte id) {
            this.id = id;
        }

        public Byte getId() {
            return id;
        }

    }
}
