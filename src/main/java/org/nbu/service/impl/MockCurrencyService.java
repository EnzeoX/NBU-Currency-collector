package org.nbu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.nbu.dto.CurrencyDto;
import org.nbu.models.NbuDataModel;
import org.nbu.service.ICurrencyService;
import org.nbu.utils.DataMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Service
@Profile("{mock}")
public class MockCurrencyService implements ICurrencyService<List<CurrencyDto>, LocalDate> {

    private final Map<LocalDate, List<NbuDataModel>> data = new ConcurrentHashMap<>();

    public MockCurrencyService() {
        initMocks();
    }

    private void initMocks() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Mocking data. Trying to read from file");
            NbuDataModel[] models = mapper.readValue(getClass().getResource("/static/mock-currency.json"), NbuDataModel[].class);
            if (models.length > 0) {
                log.info("File read successful. Total size: {}", models.length);
                List<NbuDataModel> list = Arrays.asList(models);
                data.put(list.get(0).getExchangeDate(), list);
                log.info("Data successfully added to map");
            }
        } catch (IOException e) {
            log.error("Error occurred while trying to read mocked data. Message: {}", e.getMessage());
        }
    }

    @Override
    public List<CurrencyDto> getAllCurrency() {
        return getCurrencyByDate(LocalDate.now());
    }

    @Override
    public List<CurrencyDto> getCurrencyByDate(LocalDate date) {
        Objects.requireNonNull(date, "Provided date is empty or null");
        date = validateDate(date);
        List<NbuDataModel> models = data.getOrDefault(date, null);
        if (models == null || models.size() == 0) {
            log.warn("Data in map not found by date {}", date);
            return new ArrayList<>();
        }
        return models.stream()
                .map(model -> DataMapper.nbuModelToDto(model,LocalDateTime.now()))
                .toList();
    }

    @Override
    public void deleteCurrencyDataByDate(LocalDate date) {
        Objects.requireNonNull(date, "Provided date is empty or null");
        data.remove(date);
    }

    private LocalDate validateDate(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek.getValue() >= 5 && dayOfWeek.getValue() <= 7) {
            date = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }
        return date;
    }
}
