package de.lgohlke;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

abstract class Task implements Runnable {
    @Override
    @SneakyThrows
    public final void run() {
        while (true) {
            doTask();
            TimeUnit.SECONDS.sleep(2);
        }
    }

    abstract void doTask();
}
