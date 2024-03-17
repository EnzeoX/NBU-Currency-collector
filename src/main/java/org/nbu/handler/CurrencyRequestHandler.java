package org.nbu.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nbu.dto.NbuDto;
import org.nbu.models.NbuDataModel;
import org.nbu.service.NbuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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

    public List<NbuDto> getAllCurrencyByDate(String date) {

        return null;
    }

    public void deleteCurrencyByDate(String date) {

    }
}
