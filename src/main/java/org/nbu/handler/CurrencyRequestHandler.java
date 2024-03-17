package org.nbu.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nbu.dto.NbuDto;
import org.nbu.models.NbuDataModel;
import org.nbu.models.ServiceResponse;
import org.nbu.service.NbuService;
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
    
    private final NbuService nbuService;

    public ResponseEntity<List<NbuDto>> getAllCurrency() {
        List<NbuDto> collectedData = nbuService.getAllCurrency();
        return ResponseEntity.status(HttpStatus.OK).body(collectedData);
    }

    public ResponseEntity<List<NbuDto>> getAllCurrencyByDate(String date) {
        LocalDate providedDate = LocalDate.parse(date);
        List<NbuDto> collectedData = nbuService.getCurrencyByDate(providedDate);
        return ResponseEntity.status(HttpStatus.OK).body(collectedData);
    }

    public ResponseEntity<ServiceResponse> deleteCurrencyByDate(String date) {
        nbuService.deleteCurrencyDataByDate(date);
        ServiceResponse response = new ServiceResponse();
        response.setStatus("OK");
        response.setMessage(String.format("Data deletion by provided date %s successful", date));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
