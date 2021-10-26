package de.lgohlke;

import java.util.TimerTask;

public abstract class Task extends TimerTask {
    @Override
    public final void run() {
        Thread.currentThread()
              .setName(getClass().getSimpleName());
        doTask();
    }

    public abstract void doTask();
}
