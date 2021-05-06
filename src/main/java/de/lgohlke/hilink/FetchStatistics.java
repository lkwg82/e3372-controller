package de.lgohlke.hilink;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.lgohlke.Task;
import de.lgohlke.hilink.api.Device;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class FetchStatistics extends Task {
    private final TelegrafTransmitter telegrafTransmitter;

    @SneakyThrows
    @Override
    public void doTask() {
        var signal = getSignal();
        var json = new ObjectMapper().writeValueAsString(signal);

        telegrafTransmitter.transferPayload(json);
    }

    private void transferPayload(String json) throws java.io.IOException, InterruptedException {
        log.debug("transfer: {}", json);
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:8080/telegraf"))
                                 .POST(HttpRequest.BodyPublishers.ofString(json))
                                 .build();

        var bodyHandler = HttpResponse.BodyHandlers.ofString();
        var response = client.send(request, bodyHandler);
        if (response.statusCode() == 204) {
            log.debug("signal stats transfered");
        } else {
            log.error("{} {}", response.statusCode(), response.body());
        }
    }

    @SneakyThrows
    private Device.Response.Signal getSignal() {
        int retryMax = 4;
        for (int i = 0; i < retryMax; i++) {
            try {
                return Device.Actions.fetchSignal();
            } catch (Exception e) {
                log.warn("ioexception {}", e.getMessage());
                TimeUnit.SECONDS.sleep(i);
            }
        }
        throw new IllegalStateException("could not get signal");
    }
}
