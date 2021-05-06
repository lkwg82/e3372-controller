package de.lgohlke.hilink;

import de.lgohlke.hilink.api.Error;
import lombok.Getter;

@Getter
public class APIErrorException extends RuntimeException {
    private final Error error;

    public APIErrorException(Error error) {
        this.error = error;
    }
}
