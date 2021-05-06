package de.lgohlke.hilink.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@JsonTypeName("response")
@Data
public class SessionToken {
    private final String sessionInfo;
    private final String tokenInfo;

    @JsonCreator
    public SessionToken(@JsonProperty("SesInfo") String sessionInfo,
                        @JsonProperty("TokInfo") String tokenInfo) {
        this.sessionInfo = sessionInfo;
        this.tokenInfo = tokenInfo;
    }
}
