package de.lgohlke;

import java.util.concurrent.TimeUnit;

public abstract class Task implements Runnable {
    @Override
    public final void run() {
        try {
            Thread.currentThread()
                  .setName(getClass().getSimpleName());
            while (true) {
                doTask();
                sleep_time();
            }
        } catch (Exception e) {
            signalException(e);
        }
    }

    protected void sleep_time() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
    }

    final void signalException(Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

    public abstract void doTask();
}
