package de.lgohlke.hilink;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;


public class FetchStatisticsTest {
    @Test
    void fetch() {
        var outputs = new ArrayList<String>();
        var telegrafTransmitter = new TelegrafTransmitter() {
            @Override
            public void transferPayload(String json) {
                System.out.println("transfer " + json);
                outputs.add(json);
            }
        };

        new FetchStatistics(telegrafTransmitter).doTask();
        assertThat(outputs).hasSize(1);
    }

}
