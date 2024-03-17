package org.nbu.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nbu.dto.CurrencyDto;
import org.nbu.models.ServiceResponse;
import org.nbu.service.ICurrencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Component
@AllArgsConstructor
public class CurrencyRequestHandler {

    private final ICurrencyService<List<CurrencyDto>, LocalDate> currencyService;

    public ResponseEntity<List<CurrencyDto>> getAllCurrency() {
        List<CurrencyDto> collectedData = currencyService.getAllCurrency();
        return ResponseEntity.status(HttpStatus.OK).body(collectedData);
    }

    public ResponseEntity<List<CurrencyDto>> getAllCurrencyByDate(String date) {
        LocalDate providedDate = LocalDate.parse(date);
        List<CurrencyDto> collectedData = currencyService.getCurrencyByDate(providedDate);
        return ResponseEntity.status(HttpStatus.OK).body(collectedData);
    }

    public ResponseEntity<ServiceResponse> deleteCurrencyByDate(String date) {
        LocalDate providedDate = LocalDate.parse(date);
        currencyService.deleteCurrencyDataByDate(providedDate);
        ServiceResponse response = new ServiceResponse();
        response.setStatus("OK");
        response.setMessage(String.format("Data deletion by provided date %s successful", date));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
