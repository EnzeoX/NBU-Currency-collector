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

    // in future add custom exceptions and handle them here if service
    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> handleException(Exception e) {
        String errorName = e.getClass().getSimpleName();
        log.error("Error occurred, catching in Controller Advice, Exception type: {}, message: {}",
                errorName,
                e.getMessage());
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatus("ERROR");
        switch (errorName) {
            case "MethodArgumentTypeMismatchException" -> {
                serviceResponse.setMessage("Can't parse provided URL parameter");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceResponse);
            }
            case "NullPointerException" -> {
                serviceResponse.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(serviceResponse);
            }
            default -> {
                serviceResponse.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceResponse);
            }
        }
    }
}
