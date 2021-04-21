package de.lgohlke.hilink.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;


@JsonTypeName("response")
public record SessionToken(
        @JsonProperty("SesInfo")
        String sessionInfo,
        @JsonProperty("TokInfo")
        String tokenInfo) {
}
