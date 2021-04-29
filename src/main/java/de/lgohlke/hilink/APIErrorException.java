package de.lgohlke.hilink;

import de.lgohlke.API;
import lombok.Getter;

@Getter
public class APIErrorException extends RuntimeException {
    private final API.Error error;

    public APIErrorException(API.Error error) {
        this.error = error;
    }
}
