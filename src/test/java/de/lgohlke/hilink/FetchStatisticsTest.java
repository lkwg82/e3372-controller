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

        var fetchStatistics = new FetchStatistics(telegrafTransmitter);

        var max = 1;
        for (int i = 0; i < max; i++) {
            fetchStatistics.doTask();
        }
        assertThat(outputs).hasSize(3);
    }

}
