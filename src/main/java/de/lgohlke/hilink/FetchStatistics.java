package de.lgohlke.hilink;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.lgohlke.Task;
import de.lgohlke.hilink.api.Device;
import de.lgohlke.hilink.api.Monitoring;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class FetchStatistics extends Task {
    private final TelegrafTransmitter telegrafTransmitter;

    @SneakyThrows
    @Override
    public void doTask() {
        collect(this::getSignal);
        collect(this::getStatus);
        collect(this::getTrafficStatistics);
    }

    private Monitoring.Response.Status getStatus() {
        return fetchWithRetry(Monitoring.Actions::status, "status");
    }

    private Monitoring.Response.TrafficStatistics getTrafficStatistics() {
        return fetchWithRetry(Monitoring.Actions::trafficStatistics, "traffic statistics");
    }

    private Device.Response.Signal getSignal() {
        return fetchWithRetry(Device.Actions::fetchSignal, "signal");
    }

    private void collect(Callable<Object> collector) throws Exception {
        var result = collector.call();
        var json = new ObjectMapper().writeValueAsString(result);
        telegrafTransmitter.transferPayload(json);
    }

    @SneakyThrows
    private <T> T fetchWithRetry(Callable<T> fetch, String hint) {
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
}
