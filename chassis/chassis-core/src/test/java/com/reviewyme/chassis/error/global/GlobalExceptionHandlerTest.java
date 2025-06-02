package com.reviewyme.chassis.error.global;

import com.reviewyme.chassis.constant.CoreConstant;
import com.reviewyme.chassis.error.ApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleCustomRuntimeException_HappyPath_BadRequest() {
        ApiException ex = new ApiException(CoreConstant.ERROR_CODE_4, "Invalid Payment", "Payment details are incorrect");

        ResponseEntity<ErrorRepresentation> response = globalExceptionHandler.handleCustomRuntimeException(ex);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(CoreConstant.ERROR_CODE_4, response.getBody().getCode());
        assertEquals("Invalid Payment", response.getBody().getMessage());
        assertEquals("Payment details are incorrect", response.getBody().getReason());
    }

    @Test
    void testHandleCustomRuntimeException_HappyPath_InternalServerError() {
        ApiException ex = new ApiException(CoreConstant.ERROR_CODE_5, "Server Error", "Payment processing failed due to server issue");

        ResponseEntity<ErrorRepresentation> response = globalExceptionHandler.handleCustomRuntimeException(ex);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(CoreConstant.ERROR_CODE_5, response.getBody().getCode());
        assertEquals("Server Error", response.getBody().getMessage());
        assertEquals("Payment processing failed due to server issue", response.getBody().getReason());
    }

    @Test
    void testHandleGeneralException_UnhappyPath() {
        Exception ex = new Exception("Unexpected error occurred");

        ResponseEntity<ErrorRepresentation> response = globalExceptionHandler.handleGeneralException(ex);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(CoreConstant.ERROR_CODE_5, response.getBody().getCode());
        assertEquals(CoreConstant.INTERNAL_ERROR, response.getBody().getMessage());
        assertEquals(CoreConstant.AN_UNEXPECTED_ERROR_OCCURRED, response.getBody().getReason());
    }
}
