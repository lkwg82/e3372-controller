package de.lgohlke;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Executors;

@Log4j2
public class Main {
    public static void main(String... args) throws Exception {
        var executorService = Executors.newCachedThreadPool();
        executorService.submit(new DataLimitExceededTask());
    }
}


