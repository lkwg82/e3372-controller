package de.lgohlke.hilink.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;

public class Device {
    public static class Response {
        @JacksonXmlRootElement(localName = "response")
        private static class Base {
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Signal extends Base {

            private final int pci;
            private final int cellId;
            private final int tac;
            private final int band;
            private final int plmn;
            private final float rsrq;
            private final int rsrp;
            private final int rssi;
            private final int mode;
            private final int ulbandwidth;
            private final int dlbandwidth;
            private final Integer sinr;

            public Signal(
                    @JsonProperty("pci") int pci,
                    @JsonProperty("cell_id") int cellId,
                    @JsonProperty("tac") int tac,
                    @JsonProperty("band") int band,
                    @JsonProperty("plmn") int plmn,
                    @JsonProperty("lteulfreq") int lteulfreq,
                    @JsonProperty("ltedlfreq") int ltedlfreq,
                    @JsonProperty("ulbandwidth") String ulbandwidth,
                    @JsonProperty("dlbandwidth") String dlbandwidth,

                    @JsonProperty("rsrq") String rsrq,
                    @JsonProperty("rsrp") String rsrp,
                    @JsonProperty("rssi") String rssi,
                    @JsonProperty("sinr") String sinr,
                    @JsonProperty("mode") int mode
            ) {
                this.pci = pci;
                this.cellId = cellId;
                this.tac = tac;
                this.band = band;
                this.plmn = plmn;
                this.rsrq = Float.parseFloat(rsrq.replace("dB", ""));
                this.sinr = Integer.parseInt(sinr.replace("dB", ""));
                this.rsrp = Integer.parseInt(rsrp.replace("dBm", ""));
                this.rssi = Integer.parseInt(rssi.replace("dBm", ""));

                this.ulbandwidth = Integer.parseInt(ulbandwidth.replace("MHz", ""));
                this.dlbandwidth = Integer.parseInt(dlbandwidth.replace("MHz", ""));
                this.mode = mode;
            }
        }
    }
}
