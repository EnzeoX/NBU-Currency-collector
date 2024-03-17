package org.nbu.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nbu.dto.NbuDto;
import org.nbu.handler.CurrencyRequestHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/currency")
public class CurrencyController {

    private final CurrencyRequestHandler requestHandler;

    @GetMapping("/get")
    public ResponseEntity<List<NbuDto>> getActualCurrency(HttpServletRequest request) {
        log.info("Request to get actual currency info by date. Request from {}", request.getRemoteAddr());
        return requestHandler.getAllCurrency();
    }

    @GetMapping("/get/by-date/{date}")
    public ResponseEntity<?> getAllCurrencyByDate(@PathVariable("date") String date,
                                                  HttpServletRequest request) {
        log.info("Request to get currency info by date. Request from {}", request.getRemoteAddr());
        return requestHandler.getAllCurrencyByDate(date);
    }

    @PostMapping("/delete/by-date/{date}")
    public ResponseEntity<?> deleteCurrencyByDate(@PathVariable("date") String date,
                                                  HttpServletRequest request) {
        log.info("Request to delete currency info by date. Request from {}", request.getRemoteAddr());
        return requestHandler.deleteCurrencyByDate(date);
    }
}
