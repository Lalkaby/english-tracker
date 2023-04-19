package by.temniakov.english.tracker.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TranslationServiceException extends ExternalServiceException {
    public TranslationServiceException(String message) {
        super(message);
    }

    public TranslationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}