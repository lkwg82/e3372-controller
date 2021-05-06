package de.lgohlke;

import java.util.concurrent.TimeUnit;

abstract class Task implements Runnable {
    @Override
    public final void run() {
        try {
            Thread.currentThread()
                  .setName(getClass().getSimpleName());
            while (true) {
                doTask();
                TimeUnit.SECONDS.sleep(2);
            }
        } catch (Exception e) {
            signalException(e);
        }
    }

    final void signalException(Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

    abstract void doTask();
}
