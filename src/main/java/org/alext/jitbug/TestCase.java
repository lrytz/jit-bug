package org.alext.jitbug;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class TestCase {
    public static void main(String[] args) throws InterruptedException {
        int count = 10;
        for (int i = 0; i < 3; i++) {
            CountDownLatch l = new CountDownLatch(count);
            for (int j = 0; j < count; j++) {
                Thread t = new Thread(new Worker(l));
                t.start();
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
                List<String> someIds= new ArrayList<>();
                for (int i = 1; i <= 20; i++)
                    someIds.add("someId=" + i);

                for (long i = 0; i < 40_000L; i++) {
                    SomeEnum valueFromEnum = SomeEnum.SOME_VALUE;
                    JTuple6<Long, Long, Byte, String, Byte, String> res = doStuff(someIds, 192837934738L, valueFromEnum, "0");
                    if (res.t3 == null) {
                        System.out.println(" >> Found null: " + Thread.currentThread().getName() + " at step: " + i + " res:  " + res);
                        break;
                    }
                }
            } finally {
                latch.countDown();
            }
        }

        private JTuple6<Long, Long, Byte, String, Byte, String> doStuff(
                List<String> someIds,
                long anotherId,
                SomeEnum someEnum,
                String someString) {
            C<JTuple6<Long, Long, Byte, String, Byte, String>> r = new C<>(null);
            someIds.forEach(id -> r.t = new JTuple6<>(null, anotherId, someEnum.getId(), null, null, someString));
            return r.t;
        }
    }

    static class C<T> {
        T t;

        public C(T t) {
            this.t = t;
        }
    }

    static enum SomeEnum {
        SOME_VALUE((byte) 9);

        private Byte id;

        SomeEnum(byte id) {
            this.id = id;
        }

        public Byte getId() {
            return id;
        }

    }

    static class JTuple6<T1, T2, T3, T4, T5, T6> {
        public T1 t1;
        public T2 t2;
        public T3 t3;
        public T4 t4;
        public T5 t5;
        public T6 t6;

        public JTuple6(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
            this.t1 = t1;
            this.t2 = t2;
            this.t3 = t3;
            this.t4 = t4;
            this.t5 = t5;
            this.t6 = t6;
        }

        @Override
        public String toString() {
            return "JTuple6{" +
                    "t1=" + t1 +
                    ", t2=" + t2 +
                    ", t3=" + t3 +
                    ", t4=" + t4 +
                    ", t5=" + t5 +
                    ", t6=" + t6 +
                    '}';
        }
    }
}
