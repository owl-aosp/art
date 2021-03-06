/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Test synchronization primitives.
 *
 * TODO: this should be re-written to be a little more rigorous and/or
 * useful.  Also, the ThreadDeathHandler stuff should be exposed or
 * split out.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Sleep Test");
        sleepTest();

        System.out.println("\nCount Test");
        countTest();

        System.out.println("\nInterrupt Test");
        interruptTest();
    }

    static void sleepTest() {
        System.out.println("GOING");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            System.out.println("INTERRUPT!");
            ie.printStackTrace(System.out);
        }
        System.out.println("GONE");
    }

    static void countTest() {
        CpuThread one, two;

        one = new CpuThread(1);
        two = new CpuThread(2);

        synchronized (one) {
            one.start();
            try {
                one.wait();
            } catch (InterruptedException ie) {
                System.out.println("INTERRUPT!");
                ie.printStackTrace(System.out);
            }
        }

        two.start();

        //System.out.println("main: off and running");

        try {
            one.join();
            two.join();
        } catch (InterruptedException ie) {
            System.out.println("INTERRUPT!");
            ie.printStackTrace(System.out);
        }
        System.out.println("main: all done");
    }

    static void interruptTest() {
        SleepyThread sleepy, pesky;

        sleepy = new SleepyThread(null);
        pesky = new SleepyThread(sleepy);

        sleepy.setPriority(4);
        sleepy.start();
        pesky.start();
        pesky.setPriority(3);
    }
}

class CpuThread extends Thread {
    static Object mSyncable = new Object();
    static int mCount = 0;
    int mNumber;

    CpuThread(int num) {
        super("CpuThread " + num);
        mNumber = num;
    }

    public void run() {
        //System.out.print("thread running -- ");
        //System.out.println(Thread.currentThread().getName());

        synchronized (mSyncable) {
            synchronized (this) {
                this.notify();
            }
            for (int i = 0; i < 10; i++) {
                output(mNumber);
            }

            System.out.print("Final result: ");
            System.out.println(mCount);
        }
    }

    void output(int num) {
        int count = mCount;

        System.out.print("going: ");
        System.out.println(num);

        /* burn CPU; adjust end value so we exceed scheduler quantum */
        for (int j = 0; j < 5000; j++) {
            ;
        }

        count++;
        mCount = count;
    }
}

class SleepyThread extends Thread {
    private SleepyThread mOther;
    private Integer[] mWaitOnMe;      // any type of object will do
    private volatile boolean otherDone;

    private static int count = 0;

    SleepyThread(SleepyThread other) {
        mOther = other;
        otherDone = false;
        mWaitOnMe = new Integer[] { 1, 2 };

        setName("thread#" + count);
        count++;
    }

    public void run() {
        System.out.println("SleepyThread.run starting");

        if (false) {
            ThreadDeathHandler threadHandler =
                new ThreadDeathHandler("SYNC THREAD");
            Thread.currentThread().setUncaughtExceptionHandler(threadHandler);
            throw new NullPointerException("die");
        }

        if (mOther == null) {
            boolean intr = false;

            try {
              do {
                synchronized (mWaitOnMe) {
                    mWaitOnMe.wait(9000);
                }
              } while (!otherDone);
            } catch (InterruptedException ie) {
                // Expecting this; interrupted should be false.
                System.out.println(Thread.currentThread().getName() +
                        " interrupted, flag=" + Thread.interrupted());
                intr = true;
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }

            if (!intr)
                System.out.println("NOT INTERRUPTED");
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                System.out.println("PESKY INTERRUPTED?");
            }

            System.out.println("interrupting other (isAlive="
                + mOther.isAlive() + ")");
            mOther.interrupt();
            mOther.otherDone = true;
        }
    }
}
