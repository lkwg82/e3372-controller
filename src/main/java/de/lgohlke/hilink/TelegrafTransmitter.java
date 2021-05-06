package de.lgohlke.hilink;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class TelegrafTransmitter {

    public void transferPayload(String json) throws java.io.IOException, InterruptedException {
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
}
