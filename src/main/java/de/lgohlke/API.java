package de.lgohlke;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.lgohlke.hilink.APIErrorException;
import de.lgohlke.hilink.XMLProcessor;
import de.lgohlke.hilink.api.SessionToken;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public class API {
    private static final String DEFAULT_BASE_PATH = "http://192.168.8.1";
    private final static Semaphore MUTEX = new Semaphore(1);

    private static HttpClient createClient() {
        return HttpClient.newBuilder()
                         .version(HttpClient.Version.HTTP_1_1)
                         .connectTimeout(Duration.ofMillis(15000))
                         .build();
    }

    @SneakyThrows
    private static <T> T mutex(Callable<T> callable) {
        MUTEX.acquire();
        TimeUnit.MILLISECONDS.sleep(200);
        try {
            return callable.call();
        } finally {
            MUTEX.release();
        }
    }

    public static String post(String path, String payload) throws URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {
        return mutex(() -> inner_post(path, payload));
    }

    public static String get(String path) throws URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {
        return mutex(() -> inner_get_plain(path));
    }

    public static String get_authenticated(String path) throws URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {
        return mutex(() -> inner_get(path));
    }

    private static String inner_post(String path, String payload) throws URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {
        HttpClient client = createClient();
        HttpRequest.Builder requestBuilder = getRequestBuilder(path);

        var request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(payload))
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

    private static HttpRequest.Builder getRequestBuilder(String path) throws URISyntaxException {
        var uri = new URI(DEFAULT_BASE_PATH + path);

        var sessionToken = fetchSessionToken();
        var requestBuilder = HttpRequest.newBuilder()
                                        .uri(uri)
                                        .header("__RequestVerificationToken", sessionToken.getTokenInfo())
                                        .header("Cookie", "SessionId=" + sessionToken.getSessionInfo())
                                        .header("Content-Type", "text/xml");
        return requestBuilder;
    }

    private static String inner_get(String path) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = createClient();
        var requestBuilder = getRequestBuilder(path);
        var request = requestBuilder.GET()
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

    private static String inner_get_plain(String path) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = createClient();
        var uri = new URI(DEFAULT_BASE_PATH + path);

        var request = HttpRequest.newBuilder()
                                 .uri(uri)
                                 .header("Content-Type", "text/xml")
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
        var response = inner_get_plain("/api/webserver/SesTokInfo");
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

