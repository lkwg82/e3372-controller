package de.lgohlke.hilink.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import de.lgohlke.API;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SMS {
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

        public static class List extends Request.Base {
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

            public List(BOXTYPE boxtype, int readcount) {
                readCount = readcount;
                this.boxtype = boxtype.getType();
            }

            public List(BOXTYPE boxtype) {
                this(boxtype, 1);
            }

        }

        @RequiredArgsConstructor
        public static class Delete extends Request.Base {
            @JsonProperty("Index")
            private final int index;
        }

        public static class Send extends Request.Base {
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
            java.util.List<Response.List.Message> messages;

            @JacksonXmlRootElement(localName = "message")
            @JsonIgnoreProperties(ignoreUnknown = true)
            @Getter
            @ToString
            public static class Message {
                private final Response.List.Message.SMSTAT status;
                private final Response.List.Message.TYPE type;
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
                    status = Response.List.Message.SMSTAT.valueof(smsStat);
                    this.index = index;
                    this.content = content;
                    this.type = Response.List.Message.TYPE.valueof(type);
                    this.date = date;
                    this.phone = phone;
                }

                @RequiredArgsConstructor
                @Getter
                public enum SMSTAT {
                    READ(0),
                    UNREAD(1),
                    SENT(3);

                    private final int status;

                    public static Response.List.Message.SMSTAT valueof(int status) {
                        for (var value : Response.List.Message.SMSTAT.values()) {
                            if (value.status == status) {
                                return value;
                            }
                        }
                        throw new IllegalArgumentException("could not map " + Response.List.Message.SMSTAT.class + ": " + status);
                    }
                }

                @RequiredArgsConstructor
                @Getter
                public
                enum TYPE {
                    TEXT(0),
                    INCOMING(1),
                    DATA_VOLUME_EXCEEDED(2),
                    TYPE_5(5),
                    STATUS(7);

                    private final int status;

                    public static Response.List.Message.TYPE valueof(int type) {
                        for (var value : Response.List.Message.TYPE.values()) {
                            if (value.status == type) {
                                return value;
                            }
                        }
                        throw new IllegalArgumentException("could not map " + Response.List.Message.TYPE.class + ": " + type);
                    }
                }
            }
        }

        @Getter
        @ToString
        public static class Status extends Response.Base {

            private final String status;
            private final boolean isOk;

            public Status(@JacksonXmlText String status) {
                this.status = status;
                isOk = "OK".equals(status);
            }
        }
    }

    @Slf4j
    public static class Actions {
        @SneakyThrows
        public static Response.Status delete(Response.List.Message message) {
            var payload = API.writeXml(new Request.Delete(message.getIndex()));
            var response = API.post("/api/sms/delete-sms", payload);
            return API.readXml(response, Response.Status.class);
        }

        @SneakyThrows
        public static List<Response.List.Message> list(BOXTYPE boxtype) {
            var payload = API.writeXml(new Request.List(boxtype, 20));
            var response = API.post("/api/sms/sms-list", payload);
            return API.readXml(response, Response.List.class).messages;
        }

        @SneakyThrows
        public static Response.Status send(String phone, String content) {
            log.info("send to '{}' text: '{}'", phone, content);
            var request = new Request.Send(phone, content);
            var payload = API.writeXml(request, false);

            var response = API.post("/api/sms/send-sms", payload);
            TimeUnit.SECONDS.sleep(2); // else error 113004, throttling seems good idea

            return API.readXml(response, Response.Status.class);
        }
    }
}
