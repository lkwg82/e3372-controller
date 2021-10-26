package de.lgohlke.hilink;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

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
            fetchStatistics.getFetchTasks()
                           .forEach(TimerTask::run);
        }
        assertThat(outputs).hasSize(3);
    }

    @Test
    void signal_fetch_should_be_a_timerTask() {
        var fetchStatistics = new FetchStatistics(new TelegrafTransmitter() {
            @Override
            public void transferPayload(String json) throws IOException, InterruptedException {
                // ok
            }
        });
        assertThat(fetchStatistics.fetchSignal()).isInstanceOf(TimerTask.class);
    }

    @Test
    void status_fetch_should_be_a_timerTask() {
        var fetchStatistics = new FetchStatistics(new TelegrafTransmitter() {
            @Override
            public void transferPayload(String json) throws IOException, InterruptedException {
                // ok
            }
        });
        assertThat(fetchStatistics.fetchStatus()).isInstanceOf(TimerTask.class);
    }

    @Test
    void trafficStats_fetch_should_be_a_timerTask() {
        var fetchStatistics = new FetchStatistics(new TelegrafTransmitter() {
            @Override
            public void transferPayload(String json) throws IOException, InterruptedException {
                // ok
            }
        });
        assertThat(fetchStatistics.fetchTrafficStats()).isInstanceOf(TimerTask.class);
    }
}
