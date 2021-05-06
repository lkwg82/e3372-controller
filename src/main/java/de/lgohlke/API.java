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
import java.util.concurrent.TimeUnit;

@Slf4j
public class API {
    private static final String DEFAULT_BASE_PATH = "http://192.168.8.1";

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
