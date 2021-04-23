package de.lgohlke;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import de.lgohlke.hilink.APIErrorException;
import de.lgohlke.hilink.XMLProcessor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class API {
    private static final String DEFAULT_BASE_PATH = "http://192.168.8.1";

    @JsonTypeName("response")
    @Data
    public static class SessionToken {
        private final String sessionInfo;
        private final String tokenInfo;

        @JsonCreator
        public SessionToken(@JsonProperty("SesInfo") String sessionInfo,
                            @JsonProperty("TokInfo") String tokenInfo) {
            this.sessionInfo = sessionInfo;
            this.tokenInfo = tokenInfo;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static final class Error {
        private final int code;
        private final String message;
    }

    public static class SMS {
        private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

        public static class Request {

            @JacksonXmlRootElement(localName = "request")
            private static class Base {
            }

            public static class List extends Base {
                @JsonProperty("PageIndex")
                private final int PageIndex = 1;
                @JsonProperty("ReadCount")
                private final int ReadCount;
                @JsonProperty("BoxType")
                private final int BoxType = 1;
                @JsonProperty("SortType")
                private final int SortType = 1;
                @JsonProperty("Ascending")
                private final int Ascending = 0;
                @JsonProperty("UnreadPreferred")
                private final int UnreadPreferred = 1;

                public List(int readcount) {
                    ReadCount = readcount;
                }

                public List() {
                    this(1);
                }
            }

            @RequiredArgsConstructor
            public static class Delete extends Base {
                @JsonProperty("Index")
                private final int index;
            }

            public static class Send extends Base {
                @JsonProperty("Index")
                private final int index = -1;

                @JsonProperty("Sca")
                private final String sca = null;

                @JacksonXmlElementWrapper(localName = "Phones")
                @JacksonXmlProperty(localName = "Phone")
                private final java.util.List<String> phones;
                @JsonProperty("Content")
                private final String content;
                @JsonProperty("Length")
                private final int length;
                @JsonProperty("Reserved")
                private final int reserved = 1;
                @JsonProperty("Date")
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
                private final Date date = new Date();

                Send(String phone, String content) {
                    phones = java.util.List.of(phone);
                    this.content = content;
                    length = content.length();
                }
            }
        }

        public static class Response {
            @JacksonXmlRootElement(localName = "response")
            private static class Base {
            }

            public static class List extends Request.Base {
                @JsonProperty("Count")
                int count;

                @JacksonXmlElementWrapper(localName = "Messages")
                @JacksonXmlProperty(localName = "Message")
                java.util.List<Message> messages;

                @JacksonXmlRootElement(localName = "message")
                @JsonIgnoreProperties(ignoreUnknown = true)
                @Getter
                @ToString
                static class Message {
                    private final SMSTAT status;
                    private final TYPE type;
                    private final int index;
                    @JsonProperty("Date")
                    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
                    private final Date date;
                    private final String phone;
                    private final String content;

                    @JsonCreator
                    Message(
                            @JsonProperty("Smstat") int smsStat,
                            @JsonProperty("Index") int index,
                            @JsonProperty("Content") String content,
                            @JsonProperty("SmsType") int type,
                            @JsonProperty("Date")
                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN) Date date,
                            @JsonProperty("Phone") String phone
                    ) {
                        status = SMSTAT.valueof(smsStat);
                        this.index = index;
                        this.content = content;
                        this.type = TYPE.valueof(type);
                        this.date = date;
                        this.phone = phone;
                    }

                    @RequiredArgsConstructor
                    @Getter
                    enum SMSTAT {
                        READ(0),
                        UNREAD(1);

                        private final int status;

                        public static SMSTAT valueof(int status) {
                            for (var value : SMSTAT.values()) {
                                if (value.status == status) {
                                    return value;
                                }
                            }
                            throw new IllegalArgumentException("could not map " + SMSTAT.class + ": " + status);
                        }
                    }

                    @RequiredArgsConstructor
                    @Getter
                    enum TYPE {
                        TEXT(0),
                        INCOMING(1),
                        DATA_VOLUME_EXCEEDED(2),
                        TYPE_5(5),
                        STATUS(7);

                        private final int status;

                        public static TYPE valueof(int type) {
                            for (var value : TYPE.values()) {
                                if (value.status == type) {
                                    return value;
                                }
                            }
                            throw new IllegalArgumentException("could not map " + TYPE.class + ": " + type);
                        }
                    }
                }
            }

            @Getter
            @ToString
            public static class Status extends Base {

                private final String status;
                private final boolean isOk;

                public Status(@JacksonXmlText String status) {
                    this.status = status;
                    isOk = "OK".equals(status);
                }
            }
        }

        static class Actions {
            @SneakyThrows
            private static SMS.Response.Status delete(Response.List.Message message) {
                var payload = writeXml(new SMS.Request.Delete(message.getIndex()));
                var response = post("/api/sms/delete-sms", payload);
                return readXml(response, SMS.Response.Status.class);
            }

            @SneakyThrows
            static List<Response.List.Message> list() {
                var payload = writeXml(new Request.List(20));
                var response = post("/api/sms/sms-list", payload);
                return readXml(response, Response.List.class).messages;
            }

            @SneakyThrows
            private static SMS.Response.Status send(String phone, String content) {
                var request = new Request.Send(phone, content);
                var payload = writeXml(request, false);
                var response = post("/api/sms/send-sms", payload);
                try {
                    return readXml(response, SMS.Response.Status.class);
                } finally {
                    TimeUnit.SECONDS.sleep(2); // else error 113004, throttling seems good idea
                }
            }
        }
    }

    void demo() {
        var redirectTo = System.getenv("REDIRECT_PHONE_NUMBER");
        Objects.requireNonNull(redirectTo, "missing number to redirect: REDIRECT_PHONE_NUMBER");
        try {
            SMS.Actions.list().forEach(message -> {
                System.out.println(message);

                handleSMS(redirectTo, message);

                var status = SMS.Actions.delete(message);
                if (status.isOk()) {
                    System.out.println("removed " + message.getIndex());
                } else {
                    System.err.println(status.getStatus());
                }
            });
        } catch (Exception e) {
            if (e instanceof HttpConnectTimeoutException || e instanceof ConnectException || e instanceof IOException) {
                System.err.println("http: " + e.getMessage());
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
                if (message.getContent().contains("noch kein Upgrade buchen")) {
                    System.out.println("no upgrade yet");
                } else {
                    SMS.Actions.send(message.getPhone(), "2");
                    SMS.Actions.send(redirectTo, "extends data volume");
                }
            }
        } else {
            try {
                System.out.println("skip: " + writeXml(message));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }


    private static String post(String path, String payload) throws URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {
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
        var response = client.send(request, responseBodyHandler);
        return response.body();
    }

    private static HttpClient createClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(200))
                .build();
    }

    private static String get(String path) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = createClient();
        var uri = new URI(DEFAULT_BASE_PATH + path);
        var request = HttpRequest.newBuilder().uri(uri).GET().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }


    @SneakyThrows
    private static SessionToken fetchSessionToken() {
        var response = get("/api/webserver/SesTokInfo");
        return readXml(response, SessionToken.class);
    }

    private static <T> T readXml(String response, Class<T> clazz) throws JsonProcessingException, APIErrorException {
        return new XMLProcessor().readXml(response, clazz);
    }

    private static String writeXml(Object o) throws JsonProcessingException {
        return writeXml(o, true);
    }

    private static String writeXml(Object o, boolean indent) throws JsonProcessingException {
        return new XMLProcessor().writeXml(o, indent);
    }
}
