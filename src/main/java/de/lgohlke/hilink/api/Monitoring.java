package de.lgohlke.hilink.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import de.lgohlke.API;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

public class Monitoring {

    public static class Response {
        @JacksonXmlRootElement(localName = "response")
        private static class Base {
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @ToString
        public static class Status extends Base {
            @JsonProperty("SignalIcon")
            int signalIcon;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @ToString
        public static class TrafficStatistics extends Base {
            @JsonProperty("TotalUpload")
            long totalUpload;
            @JsonProperty("TotalDownload")
            long totalDownload;
        }
    }

    @Slf4j
    public static class Actions {
        @SneakyThrows
        public static Response.Status status() {
            var response = API.get("/api/monitoring/status");
            return API.readXml(response, Response.Status.class);
        }

        @SneakyThrows
        public static Response.TrafficStatistics trafficStatistics() {
            var response = API.get("/api/monitoring/traffic-statistics");
            return API.readXml(response, Response.TrafficStatistics.class);
        }
    }
}
