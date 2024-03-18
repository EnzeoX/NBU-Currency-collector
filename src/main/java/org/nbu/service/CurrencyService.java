package org.nbu.service;

import org.nbu.dto.CurrencyDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Nikolay Boyko
 */
@Transactional
public interface CurrencyService<R> {

    List<CurrencyDto> getAllCurrency();

    List<CurrencyDto> getCurrencyByDate(R date);

    void deleteCurrencyDataByDate(R date);

}
