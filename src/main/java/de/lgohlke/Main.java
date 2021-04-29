package de.lgohlke;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

@Log4j2
public class Main {
    public static void main(String... args) throws Exception {
        var api = new API();
        while (true) {
            log.info(" checking  ...");
            api.demo();
            TimeUnit.SECONDS.sleep(2);
        }
    }
}


