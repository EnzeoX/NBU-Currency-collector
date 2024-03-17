package org.nbu.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
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
        log.error("Error occurred, catching in Controller Advice, message: {}", e.getMessage());
        return null;
    }
}
