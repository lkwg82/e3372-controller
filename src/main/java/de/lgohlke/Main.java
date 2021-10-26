package de.lgohlke;

import de.lgohlke.hilink.FetchStatistics;
import de.lgohlke.hilink.TelegrafTransmitter;
import lombok.extern.log4j.Log4j2;

import java.util.Timer;

@Log4j2
public class Main {
    public static void main(String... args) throws InterruptedException {
        var timer = new Timer();

        var dataLimitExceededTask = new DataLimitExceededTask();
        timer.scheduleAtFixedRate(dataLimitExceededTask, 3000, 3000);

        var fetchStatistics = new FetchStatistics(new TelegrafTransmitter());
        fetchStatistics.getFetchTasks()
                       .forEach(task -> {
                           timer.scheduleAtFixedRate(task, 5000, 5000);
                       });
    }
}


