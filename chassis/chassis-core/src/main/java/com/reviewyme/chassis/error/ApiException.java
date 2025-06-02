package com.reviewyme.chassis.error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends RuntimeException {
    private final String code;
    private final String reason;

    public ApiException(String code, String message, String reason) {
        super(message);
        this.code = code;
        this.reason = reason;
    }

}
