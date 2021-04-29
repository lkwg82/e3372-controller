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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class API {
    private static final String DEFAULT_BASE_PATH = "http://192.168.8.1";

    @RequiredArgsConstructor
    @Getter
    public enum ERROR_CODE {
        ERROR_BUSY(100004),
        ERROR_CHECK_SIM_CARD_CAN_UNUSEABLE(101004),
        ERROR_CHECK_SIM_CARD_PIN_LOCK(101002),
        ERROR_CHECK_SIM_CARD_PUN_LOCK(101003),
        ERROR_COMPRESS_LOG_FILE_FAILED(103102),
        ERROR_CRADLE_CODING_FAILED(118005),
        ERROR_CRADLE_GET_CRURRENT_CONNECTED_USER_IP_FAILED(118001),
        ERROR_CRADLE_GET_CRURRENT_CONNECTED_USER_MAC_FAILED(118002),
        ERROR_CRADLE_GET_WAN_INFORMATION_FAILED(118004),
        ERROR_CRADLE_SET_MAC_FAILED(118003),
        ERROR_CRADLE_UPDATE_PROFILE_FAILED(118006),
        ERROR_DEFAULT(-1),
        ERROR_DEVICE_AT_EXECUTE_FAILED(103001),
        ERROR_DEVICE_COMPRESS_LOG_FILE_FAILED(103015),
        ERROR_DEVICE_GET_API_VERSION_FAILED(103006),
        ERROR_DEVICE_GET_AUTORUN_VERSION_FAILED(103005),
        ERROR_DEVICE_GET_LOG_INFORMATON_LEVEL_FAILED(103014),
        ERROR_DEVICE_GET_PC_AISSST_INFORMATION_FAILED(103012),
        ERROR_DEVICE_GET_PRODUCT_INFORMATON_FAILED(103007),
        ERROR_DEVICE_NOT_SUPPORT_REMOTE_OPERATE(103010),
        ERROR_DEVICE_PIN_MODIFFY_FAILED(103003),
        ERROR_DEVICE_PIN_VALIDATE_FAILED(103002),
        ERROR_DEVICE_PUK_DEAD_LOCK(103011),
        ERROR_DEVICE_PUK_MODIFFY_FAILED(103004),
        ERROR_DEVICE_RESTORE_FILE_DECRYPT_FAILED(103016),
        ERROR_DEVICE_RESTORE_FILE_FAILED(103018),
        ERROR_DEVICE_RESTORE_FILE_VERSION_MATCH_FAILED(103017),
        ERROR_DEVICE_SET_LOG_INFORMATON_LEVEL_FAILED(103013),
        ERROR_DEVICE_SET_TIME_FAILED(103101),
        ERROR_DEVICE_SIM_CARD_BUSY(103008),
        ERROR_DEVICE_SIM_LOCK_INPUT_ERROR(103009),
        ERROR_DHCP_ERROR(104001),
        ERROR_DIALUP_ADD_PRORILE_ERROR(107724),
        ERROR_DIALUP_DIALUP_MANAGMENT_PARSE_ERROR(107722),
        ERROR_DIALUP_GET_AUTO_APN_MATCH_ERROR(107728),
        ERROR_DIALUP_GET_CONNECT_FILE_ERROR(107720),
        ERROR_DIALUP_GET_PRORILE_LIST_ERROR(107727),
        ERROR_DIALUP_MODIFY_PRORILE_ERROR(107725),
        ERROR_DIALUP_SET_AUTO_APN_MATCH_ERROR(107729),
        ERROR_DIALUP_SET_CONNECT_FILE_ERROR(107721),
        ERROR_DIALUP_SET_DEFAULT_PRORILE_ERROR(107726),
        ERROR_DISABLE_AUTO_PIN_FAILED(101008),
        ERROR_DISABLE_PIN_FAILED(101006),
        ERROR_ENABLE_AUTO_PIN_FAILED(101009),
        ERROR_ENABLE_PIN_FAILED(101005),
        ERROR_FIRST_SEND(1),
        ERROR_FORMAT_ERROR(100005),
        ERROR_GET_CONFIG_FILE_ERROR(100008),
        ERROR_GET_CONNECT_STATUS_FAILED(102004),
        ERROR_GET_NET_TYPE_FAILED(102001),
        ERROR_GET_ROAM_STATUS_FAILED(102003),
        ERROR_GET_SERVICE_STATUS_FAILED(102002),
        ERROR_LANGUAGE_GET_FAILED(109001),
        ERROR_LANGUAGE_SET_FAILED(109002),
        ERROR_LOGIN_ALREADY_LOGINED(108003),
        ERROR_LOGIN_MODIFY_PASSWORD_FAILED(108004),
        ERROR_LOGIN_NO_EXIST_USER(108001),
        ERROR_LOGIN_PASSWORD_ERROR(108002),
        ERROR_LOGIN_TOO_MANY_TIMES(108007),
        ERROR_LOGIN_TOO_MANY_USERS_LOGINED(108005),
        ERROR_LOGIN_USERNAME_OR_PASSWORD_ERROR(108006),
        ERROR_NET_CURRENT_NET_MODE_NOT_SUPPORT(112007),
        ERROR_NET_MEMORY_ALLOC_FAILED(112009),
        ERROR_NET_NET_CONNECTED_ORDER_NOT_MATCH(112006),
        ERROR_NET_REGISTER_NET_FAILED(112005),
        ERROR_NET_SIM_CARD_NOT_READY_STATUS(112008),
        ERROR_NOT_SUPPORT(100002),
        ERROR_NO_DEVICE(-2),
        ERROR_NO_RIGHT(100003),
        ERROR_NO_SIM_CARD_OR_INVALID_SIM_CARD(101001),
        ERROR_ONLINE_UPDATE_ALREADY_BOOTED(110002),
        ERROR_ONLINE_UPDATE_CANCEL_DOWNLODING(110007),
        ERROR_ONLINE_UPDATE_CONNECT_ERROR(110009),
        ERROR_ONLINE_UPDATE_GET_DEVICE_INFORMATION_FAILED(110003),
        ERROR_ONLINE_UPDATE_GET_LOCAL_GROUP_COMMPONENT_INFORMATION_FAILED(110004),
        ERROR_ONLINE_UPDATE_INVALID_URL_LIST(110021),
        ERROR_ONLINE_UPDATE_LOW_BATTERY(110024),
        ERROR_ONLINE_UPDATE_NEED_RECONNECT_SERVER(110006),
        ERROR_ONLINE_UPDATE_NOT_BOOT(110023),
        ERROR_ONLINE_UPDATE_NOT_FIND_FILE_ON_SERVER(110005),
        ERROR_ONLINE_UPDATE_NOT_SUPPORT_URL_LIST(110022),
        ERROR_ONLINE_UPDATE_SAME_FILE_LIST(110008),
        ERROR_ONLINE_UPDATE_SERVER_NOT_ACCESSED(110001),
        ERROR_PARAMETER_ERROR(100006),
        ERROR_PB_CALL_SYSTEM_FUCNTION_ERROR(115003),
        ERROR_PB_LOCAL_TELEPHONE_FULL_ERROR(115199),
        ERROR_PB_NULL_ARGUMENT_OR_ILLEGAL_ARGUMENT(115001),
        ERROR_PB_OVERTIME(115002),
        ERROR_PB_READ_FILE_ERROR(115005),
        ERROR_PB_WRITE_FILE_ERROR(115004),
        ERROR_SAFE_ERROR(106001),
        ERROR_SAVE_CONFIG_FILE_ERROR(100007),
        ERROR_SD_DIRECTORY_EXIST(114002),
        ERROR_SD_FILE_EXIST(114001),
        ERROR_SD_FILE_IS_UPLOADING(114007),
        ERROR_SD_FILE_NAME_TOO_LONG(114005),
        ERROR_SD_FILE_OR_DIRECTORY_NOT_EXIST(114004),
        ERROR_SD_IS_OPERTED_BY_OTHER_USER(114004),
        ERROR_SD_NO_RIGHT(114006),
        ERROR_SET_NET_MODE_AND_BAND_FAILED(112003),
        ERROR_SET_NET_MODE_AND_BAND_WHEN_DAILUP_FAILED(112001),
        ERROR_SET_NET_SEARCH_MODE_FAILED(112004),
        ERROR_SET_NET_SEARCH_MODE_WHEN_DAILUP_FAILED(112002),
        ERROR_SMS_DELETE_SMS_FAILED(113036),
        ERROR_SMS_LOCAL_SPACE_NOT_ENOUGH(113053),
        ERROR_SMS_NULL_ARGUMENT_OR_ILLEGAL_ARGUMENT(113017),
        ERROR_SMS_OVERTIME(113018),
        ERROR_SMS_QUERY_SMS_INDEX_LIST_ERROR(113020),
        ERROR_SMS_SAVE_CONFIG_FILE_FAILED(113047),
        ERROR_SMS_SET_SMS_CENTER_NUMBER_FAILED(113031),
        ERROR_SMS_TELEPHONE_NUMBER_TOO_LONG(113054),
        ERROR_STK_CALL_SYSTEM_FUCNTION_ERROR(116003),
        ERROR_STK_NULL_ARGUMENT_OR_ILLEGAL_ARGUMENT(116001),
        ERROR_STK_OVERTIME(116002),
        ERROR_STK_READ_FILE_ERROR(116005),
        ERROR_STK_WRITE_FILE_ERROR(116004),
        ERROR_UNKNOWN(100001),
        ERROR_UNLOCK_PIN_FAILED(101007),
        ERROR_USSD_AT_SEND_FAILED(111018),
        ERROR_USSD_CODING_ERROR(111017),
        ERROR_USSD_EMPTY_COMMAND(111016),
        ERROR_USSD_ERROR(111001),
        ERROR_USSD_FUCNTION_RETURN_ERROR(111012),
        ERROR_USSD_IN_USSD_SESSION(111013),
        ERROR_USSD_NET_NOT_SUPPORT_USSD(111022),
        ERROR_USSD_NET_NO_RETURN(11019),
        ERROR_USSD_NET_OVERTIME(111020),
        ERROR_USSD_TOO_LONG_CONTENT(111014),
        ERROR_USSD_XML_SPECIAL_CHARACTER_TRANSFER_FAILED(111021),
        ERROR_WIFI_PBC_CONNECT_FAILED(117003),
        ERROR_WIFI_STATION_CONNECT_AP_PASSWORD_ERROR(117001),
        ERROR_WIFI_STATION_CONNECT_AP_WISPR_PASSWORD_ERROR(117004),
        ERROR_WIFI_WEB_PASSWORD_OR_DHCP_OVERTIME_ERROR(117002),

        // revealed
        ERROR_TOO_MANY_SMS(113004),
        E_125003(125003),
        ;

        private final int code;

        public static ERROR_CODE valueof(int code) {
            for (var value : ERROR_CODE.values()) {
                if (value.code == code) {
                    return value;
                }
            }
            throw new IllegalArgumentException("could not map " + ERROR_CODE.class + ": " + code);
        }
    }

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
    public static final class Error {
        private final int code;
        private final String message;
        private final ERROR_CODE error_code;

        @JsonCreator
        public Error(@JsonProperty("code") int code,
                     @JsonProperty("message") String message) {
            this.code = code;
            this.message = message;
            error_code = ERROR_CODE.valueof(code);
        }
    }

    public static class SMS {
        private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

        @RequiredArgsConstructor
        @Getter
        public enum BOXTYPE {
            INBOX(1),
            OUTBOX(2);

            private final int type;

            public static BOXTYPE valueof(int type) {
                for (var value : BOXTYPE.values()) {
                    if (value.type == type) {
                        return value;
                    }
                }
                throw new IllegalArgumentException("could not map " + BOXTYPE.class + ": " + type);
            }
        }

        public static class Request {

            @JacksonXmlRootElement(localName = "request")
            private static class Base {
            }

            public static class List extends Base {
                @JsonProperty("PageIndex")
                private final int PageIndex = 1;
                @JsonProperty("ReadCount")
                private final int readCount;
                @JsonProperty("BoxType")
                private final int boxtype;
                @JsonProperty("SortType")
                private final int SortType = 1;
                @JsonProperty("Ascending")
                private final int Ascending = 0;
                @JsonProperty("UnreadPreferred")
                private final int UnreadPreferred = 1;

                public List(SMS.BOXTYPE boxtype, int readcount) {
                    readCount = readcount;
                    this.boxtype = boxtype.getType();
                }

                public List(SMS.BOXTYPE boxtype) {
                    this(boxtype, 1);
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
                        UNREAD(1),
                        SENT(3);

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

        @Slf4j
        static class Actions {
            @SneakyThrows
            private static SMS.Response.Status delete(Response.List.Message message) {
                var payload = writeXml(new SMS.Request.Delete(message.getIndex()));
                var response = post("/api/sms/delete-sms", payload);
                return readXml(response, SMS.Response.Status.class);
            }

            @SneakyThrows
            static List<Response.List.Message> list(BOXTYPE boxtype) {
                var payload = writeXml(new Request.List(boxtype, 20));
                var response = post("/api/sms/sms-list", payload);
                return readXml(response, Response.List.class).messages;
            }

            @SneakyThrows
            private static SMS.Response.Status send(String phone, String content) {
                log.info("send to '{}' text: '{}'", phone, content);
                var request = new Request.Send(phone, content);
                var payload = writeXml(request, false);

                var response = post("/api/sms/send-sms", payload);
                TimeUnit.SECONDS.sleep(2); // else error 113004, throttling seems good idea

                return readXml(response, SMS.Response.Status.class);
            }
        }
    }

    @SneakyThrows
    void demo() {
        var redirectTo = System.getenv("REDIRECT_PHONE_NUMBER");
        Objects.requireNonNull(redirectTo, "missing number to redirect: REDIRECT_PHONE_NUMBER");
        try {
            SMS.Actions.list(SMS.BOXTYPE.INBOX).forEach(message -> {
                log.info(message.toString());

                handleSMS(redirectTo, message);

                var status = SMS.Actions.delete(message);
                if (status.isOk()) {
                    log.info("removed {}", message.getIndex());
                } else {
                    log.error(status.getStatus());
                }
            });

            SMS.Actions.list(SMS.BOXTYPE.OUTBOX).forEach(message -> {
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
                if (message.getContent().contains("noch kein Upgrade buchen")) {
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
        var request = HttpRequest.newBuilder().uri(uri).GET().build();
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
