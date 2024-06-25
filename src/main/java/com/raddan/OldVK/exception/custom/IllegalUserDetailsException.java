package com.raddan.OldVK.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IllegalUserDetailsException extends RuntimeException {
    public IllegalUserDetailsException(String message) {
        super(message);
    }
}
