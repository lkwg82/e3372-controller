package de.lgohlke.hilink;

import de.lgohlke.API;
import lombok.Getter;

@Getter
public class APIErrorException extends Exception {
    private final API.Error error;

    public APIErrorException() {
        this(new API.Error(-1, "leer"));
    }

    public APIErrorException(API.Error error) {
        this.error = error;
    }
}
