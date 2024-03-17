package org.nbu.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.nbu.models.ServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@ControllerAdvice
public class DataControllerExceptionAdvice {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> handleException(Exception e) {
        String errorName = e.getClass().getSimpleName();
        log.error("Error occurred, catching in Controller Advice, message: {}", e.getMessage());
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatus("ERROR");
        serviceResponse.setMessage(e.getMessage());
        switch (errorName) {
            case "NullPointerException":
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(serviceResponse);
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceResponse);
        }
    }
}
