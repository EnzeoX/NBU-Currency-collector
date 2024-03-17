package org.nbu.controller;

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
    public ResponseEntity<List<NbuDto>> getAllCurrency() {
        return requestHandler.getAllCurrency();
    }

    @GetMapping("/get/by-date/{date}")
    public ResponseEntity<?> getAllCurrencyByDate(@PathVariable("date") String date) {
        List<NbuDto> collectedData = requestHandler.getAllCurrencyByDate(date);
        return ResponseEntity.status(HttpStatus.OK).body(collectedData);
    }

    @PostMapping("/delete/by-date/{date}")
    public ResponseEntity<?> deleteCurrencyByDate(@PathVariable("date") String date) {
        requestHandler.deleteCurrencyByDate(date);
        return ResponseEntity.status(HttpStatus.OK).body("Deletion by date" + date + " ok"); //TODO get better response after deletion
    }
}
