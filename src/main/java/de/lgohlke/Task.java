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
        TimeUnit.SECONDS.sleep(3);
    }

    final void signalException(Exception e) {
        e.printStackTrace();
    }

    public abstract void doTask();
}
