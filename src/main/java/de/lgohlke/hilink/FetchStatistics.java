package de.lgohlke.hilink;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.lgohlke.hilink.api.Device;
import de.lgohlke.hilink.api.Monitoring;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class FetchStatistics {
    private final TelegrafTransmitter telegrafTransmitter;

    private Monitoring.Response.Status getStatus() {
        return fetchWithRetry(Monitoring.Actions::status, "status");
    }

    private Monitoring.Response.TrafficStatistics getTrafficStatistics() {
        return fetchWithRetry(Monitoring.Actions::trafficStatistics, "traffic statistics");
    }

    private Device.Response.Signal getSignal() {
        return fetchWithRetry(Device.Actions::fetchSignal, "signal");
    }

    private TimerTask collect(Callable<Object> collector, String name) {

        return new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.currentThread()
                          .setName(name);
                    var result = collector.call();
                    var json = new ObjectMapper().writeValueAsString(result);
                    telegrafTransmitter.transferPayload(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    // synchronize to throttle requests LTE stick
    @SneakyThrows
    private synchronized <T> T fetchWithRetry(Callable<T> fetch, String hint) {
        int retryMax = 4;
        for (int i = 0; i < retryMax; i++) {
            try {
                return fetch.call();
            } catch (Exception e) {
                log.warn("exception {}", e.getMessage());
                TimeUnit.SECONDS.sleep(i);
            }
        }
        throw new IllegalStateException("could not get " + hint);
    }

    TimerTask fetchSignal() {
        return collect(this::getSignal, "fetchSignal");
    }

    TimerTask fetchStatus() {
        return collect(this::getStatus, "fetchStatus");
    }

    TimerTask fetchTrafficStats() {
        return collect(this::getTrafficStatistics, "getTrafficStatistics");
    }

    public List<TimerTask> getFetchTasks() {
        return List.of(fetchSignal(), fetchStatus(), fetchTrafficStats());
    }
}
