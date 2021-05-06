package de.lgohlke.hilink.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class Error {
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
