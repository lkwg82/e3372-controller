package de.lgohlke;

import de.lgohlke.hilink.FetchStatistics;
import de.lgohlke.hilink.TelegrafTransmitter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Executors;

@Log4j2
public class Main {
    public static void main(String... args) {
        var executorService = Executors.newCachedThreadPool();
        var dataLimitExceededTask = new DataLimitExceededTask();
        executorService.submit(dataLimitExceededTask);
        executorService.submit(new UploadCheckTask(dataLimitExceededTask));
        executorService.submit(new FetchStatistics(new TelegrafTransmitter()));
    }
}


