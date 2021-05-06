package de.lgohlke;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.lgohlke.hilink.APIErrorException;
import de.lgohlke.hilink.XMLProcessor;
import de.lgohlke.hilink.api.SMS;
import de.lgohlke.hilink.api.SessionToken;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class API {
    private static final String DEFAULT_BASE_PATH = "http://192.168.8.1";

    @SneakyThrows
    void demo() {
        var redirectTo = System.getenv("REDIRECT_PHONE_NUMBER");
        Objects.requireNonNull(redirectTo, "missing number to redirect: REDIRECT_PHONE_NUMBER");
        try {
            SMS.Actions.list(SMS.BOXTYPE.INBOX)
                       .forEach(message -> {
                           log.info(message.toString());

                           handleSMS(redirectTo, message);

                           var status = SMS.Actions.delete(message);
                           if (status.isOk()) {
                               log.info("removed {}", message.getIndex());
                           } else {
                               log.error(status.getStatus());
                           }
                       });

            SMS.Actions.list(SMS.BOXTYPE.OUTBOX)
                       .forEach(message -> {
                           var status = SMS.Actions.delete(message);
                           if (status.isOk()) {
                               log.info("removed {}", message.getIndex());
                           } else {
                               log.error(status.getStatus());
                           }
                       });
        } catch (Exception e) {
            if (e instanceof HttpConnectTimeoutException || e instanceof ConnectException) {
                log.error("http: " + e.getMessage());
                e.printStackTrace();
            } else if (e instanceof IOException) {
                log.warn(e.getMessage());
            } else if (e instanceof APIErrorException) {
                var apiErrorException = (APIErrorException) e;
                var error = apiErrorException.getError();
                log.warn("code {} name '{}' message '{}'", error.getCode(), error.getError_code(), error.getMessage());
                TimeUnit.SECONDS.sleep(5);
            } else {
                throw e;
            }
        }
    }

    private void handleSMS(String redirectTo, SMS.Response.List.Message message) {
        if (message.getType() == SMS.Response.List.Message.TYPE.DATA_VOLUME_EXCEEDED || message
                .getType() == SMS.Response.List.Message.TYPE.INCOMING) {
            var content = "received from " + message.getPhone() + "\n" +
//                        "at " + message.getDate() + "\n" +
                    message.getContent();
            SMS.Actions.send(redirectTo, content);
            if (message.getType() == SMS.Response.List.Message.TYPE.DATA_VOLUME_EXCEEDED) {
                if (message.getContent()
                           .contains("noch kein Upgrade buchen")) {
                    log.info("no upgrade yet");
                } else {
                    SMS.Actions.send(message.getPhone(), "2");
                    SMS.Actions.send(redirectTo, "extends data volume");
                }
            }
        } else {
            try {
                log.info("skip: {}", writeXml(message));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private static HttpClient createClient() {
        return HttpClient.newBuilder()
                         .connectTimeout(Duration.ofMillis(1500))
                         .build();
    }

    public static String post(String path, String payload) throws URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {
        HttpClient client = createClient();
        var uri = new URI(DEFAULT_BASE_PATH + path);

        var sessionToken = fetchSessionToken();
        var request = HttpRequest.newBuilder()
                                 .uri(uri)
                                 .POST(HttpRequest.BodyPublishers.ofString(payload))
                                 .header("__RequestVerificationToken", sessionToken.getTokenInfo())
                                 .header("Cookie", "SessionId=" + sessionToken.getSessionInfo())
                                 .header("Content-Type", "text/xml")
                                 .build();
        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        try {
            log.info("posting {} ... ", path);
//            log.debug(payload);
            var response = client.send(request, responseBodyHandler);
            return response.body();
        } catch (IOException e) {
            log.warn("retrying post");
            TimeUnit.MILLISECONDS.sleep(1500);
            var response = client.send(request, responseBodyHandler);
            return response.body();
        }
    }

    private static String get(String path) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = createClient();
        var uri = new URI(DEFAULT_BASE_PATH + path);
        var request = HttpRequest.newBuilder()
                                 .uri(uri)
                                 .GET()
                                 .build();
        try {
            log.info("getting {} ... ", path);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException e) {
            log.warn("retrying get");
            TimeUnit.MILLISECONDS.sleep(1500);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
    }


    @SneakyThrows
    private static SessionToken fetchSessionToken() {
        var response = get("/api/webserver/SesTokInfo");
        return readXml(response, SessionToken.class);
    }

    public static <T> T readXml(String response, Class<T> clazz) throws JsonProcessingException, APIErrorException {
        return new XMLProcessor().readXml(response, clazz);
    }

    public static String writeXml(Object o) throws JsonProcessingException {
        return writeXml(o, true);
    }

    public static String writeXml(Object o, boolean indent) throws JsonProcessingException {
        return new XMLProcessor().writeXml(o, indent);
    }
}
