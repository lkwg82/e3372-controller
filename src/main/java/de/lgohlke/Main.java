package de.lgohlke;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String... args) throws Exception {
        var api = new API();
        while (true) {
            System.out.println(new Date() + " checking  ...");
            api.demo();
            TimeUnit.SECONDS.sleep(2);
        }
    }
}


