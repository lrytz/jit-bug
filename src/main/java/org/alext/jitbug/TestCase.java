package org.alext.jitbug;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;


import scala.Tuple6;

public class TestCase {

    public static void main(String[] args) throws InterruptedException {
        int count = 10;
        for (int i = 0; i< 3; i++) {
            CountDownLatch l = new CountDownLatch(count);
            for (int j = 0; j<count; j++) {
                Thread t = new Thread(new Worker(l));
                t.start();
                System.out.println(" Started thread: " + t.getName());
            }
            l.await();
        }
    }

    public static class Worker implements Runnable  {

        private CountDownLatch latch;

        public Worker(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                List<String> demoIds = new ArrayList<>();
                for (int i = 1; i<= 20; i++) {
                    demoIds.add("demoID="+i);
                }
                TimeShiftingViewingPeriod[] periods = TimeShiftingViewingPeriod.values();
                Contribution[] contributions = Contribution.values();
                Random r = new Random(System.nanoTime());
                for (long i =0; i<40_000l; i++) {
                    TimeShiftingViewingPeriod viewingPeriod = periods[r.nextInt(periods.length)];
                    Contribution contribution = contributions[r.nextInt(contributions.length)];
                    Set<Tuple6<Long, Long, Byte, String, Byte, String>> res = doStuff(demoIds, 192837934738l, 17210l, viewingPeriod, contribution, "0");
                    if (res.iterator().next()._3() == null ) {
                        System.out.println(" Start reproducing: Thread"+ Thread.currentThread().getName() +" at step: " + i + " res:  " + res);
                        break;
                    }
                }
            } finally {
                latch.countDown();
            }
        }

        private Set<Tuple6<Long, Long, Byte, String, Byte, String>> doStuff(List<String> demoIds,
                long programId, long date, TimeShiftingViewingPeriod viewingPeriod, Contribution contribution, String marketBreak) {
            Set<Tuple6<Long, Long, Byte, String, Byte, String>> res = new HashSet<>();
            demoIds.forEach(demoId -> res.add(new Tuple6<>(date, programId, viewingPeriod.getId(), demoId, contribution.getId(), marketBreak)));

            return res;

        }

    }

}
