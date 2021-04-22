package de.lgohlke.hilink;

import de.lgohlke.API;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public
class APIErrorException extends Exception {
    private final API.Error error;
}
