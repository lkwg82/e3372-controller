package de.lgohlke;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String... args) throws Exception {
        var api = new API();
        while (true) {
            System.out.println("checking ...");
            api.demo();
            TimeUnit.SECONDS.sleep(5);
        }
    }
}


