package com.reviewyme.chassis.error.global;

import com.reviewyme.chassis.error.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static com.reviewyme.chassis.constant.CoreConstant.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorRepresentation> handleCustomRuntimeException(ApiException ex) {
        log.error("Api exception: {}", ex.getMessage());
        ErrorRepresentation error = new ErrorRepresentation(
                ex.getCode(),
                ex.getMessage(),
                ex.getReason()
        );
        if (error.getCode().equals(ERROR_CODE_5))
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRepresentation> handleGeneralException(Exception ex) {
        log.error("Exception: {}", ex.getMessage());
        ErrorRepresentation error = new ErrorRepresentation(
                ERROR_CODE_5,
                INTERNAL_ERROR,
                AN_UNEXPECTED_ERROR_OCCURRED
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRepresentation> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> reasons = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorRepresentation errorResponse = new ErrorRepresentation(
                ERROR_CODE_4,
                BAD_REQUEST,
                String.join("; ", reasons)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
