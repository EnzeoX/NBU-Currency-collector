package org.nbu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nbu.dto.CurrencyDto;
import org.nbu.entity.NbuDataEntity;
import org.nbu.models.NbuDataModel;
import org.nbu.repository.NbuDataRepository;
import org.nbu.service.CurrencyService;
import org.nbu.utils.DataMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@AllArgsConstructor
public class NbuService implements CurrencyService<LocalDate> {

    private final ObjectMapper mapper;
    private final NbuDataRepository repository;

    private final Set<LocalDate> isCurrencyByDateInDB = ConcurrentHashMap.newKeySet();
    private final Map<LocalDate, List<NbuDataModel>> updatingData = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final DateTimeFormatter nbuFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @PostConstruct
    private void init() {
        List<NbuDataEntity> availableDates = this.repository.getAvailableDates();
        if (availableDates.size() == 0) {
            log.warn("No data in database");
            return;
        }
        availableDates.forEach(date -> isCurrencyByDateInDB.add(date.getExchangeDate()));
        log.info("Added available dates to database. List size: {}", this.isCurrencyByDateInDB.size());
    }

    @Override
    public List<CurrencyDto> getAllCurrency() {
        LocalDate now = LocalDate.now();
        return getCurrencyByDate(validateDate(now));
    }

    @Override
    public List<CurrencyDto> getCurrencyByDate(LocalDate date) {
        Objects.requireNonNull(date, "Provided date is empty or null");
        if (isCurrencyByDateInDB.contains(date)) {
            log.info("Getting data from Database for date of {}", date);
            List<CurrencyDto> result = getLocalDataDto(date);
            if (result.size() == 0) {
                log.warn("No data collected from database for date {}, is it still there?", date);
                if (!repository.existsByExchangeDate(date)) {
                    return processFromNbu(date);
                }
            }
            return result;
        } else if (repository.existsByExchangeDate(date)) {
            log.info("Data exists in Database for date {}, but not marked", date);
            isCurrencyByDateInDB.add(date);
            List<CurrencyDto> result = getLocalDataDto(date);
            if (result.size() == 0) {
                log.warn("No data collected from database for date {}, is it still there?", date);
            }
            return result;
        } else {
            return processFromNbu(date);
        }
    }

    @Override
    public void deleteCurrencyDataByDate(LocalDate date) {
        Objects.requireNonNull(date, "Provided date is null or empty");
        date = validateDate(date);
        repository.deleteAllByExchangeDate(date);
        isCurrencyByDateInDB.remove(date);
    }

    private List<CurrencyDto> processFromNbu(LocalDate date) {
        if (updatingData.containsKey(date)) {
            LocalDateTime now = LocalDateTime.now();
            return updatingData.get(date)
                    .stream()
                    .map(model -> DataMapper.nbuModelToDto(model, now))
                    .toList();
        } else {
            log.info("Requested currency data for date of {} is not in database", date);
            List<NbuDataModel> dataModels = getDataFromNbu(date);
            if (dataModels.size() == 0) {
                log.warn("Nothing was collected from NBU");
                return new ArrayList<>();
            }
            updatingData.put(date, dataModels);
            LocalDateTime now = LocalDateTime.now();
            executorService.submit(() -> {
                List<NbuDataEntity> entityList = dataModels
                        .stream()
                        .map(model -> DataMapper.nbuModelToEntity(model, now))
                        .toList();
                repository.saveAll(entityList);
                isCurrencyByDateInDB.add(date);
                updatingData.remove(date);
            });
            return dataModels.stream()
                    .map(model -> DataMapper.nbuModelToDto(model, now))
                    .toList();
        }
    }

    private List<NbuDataModel> getDataFromNbu(LocalDate date) {
        ResponseEntity<Object[]> listOfData = new RestTemplate()
                .getForEntity(
                        String.format("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=%s&json", date.format(nbuFormatter)),
                        Object[].class);
        if (listOfData.getBody() == null) {
            log.warn("Request to NBU returned nothing");
            return new ArrayList<>();
        }
        return Arrays.stream(listOfData.getBody())
                .map(object -> mapper.convertValue(object, NbuDataModel.class))
                .toList();
    }

    private List<NbuDataEntity> getLocalData(LocalDate date) {
        return repository.getAllCurrencyByDate(date)
                .orElseThrow(() -> new NullPointerException(String.format("No data by date %s found in database", date)));
    }

    private List<CurrencyDto> getLocalDataDto(LocalDate date) {
        return getLocalData(date)
                .stream()
                .map(DataMapper::nbuEntityToDto)
                .toList();
    }

    private LocalDate validateDate(LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek.getValue() >= 5 && dayOfWeek.getValue() <= 7) {
            date = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        } else {
            // checking if current time is after 15:30 cuz after that time currency data updates and sets as for tomorrow
            if (now.isAfter(LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 15, 30))) {
               date = adjustDate(date);
            }
        }
        return date;
    }

    private LocalDate adjustDate(LocalDate date) {
        return date.plusDays(1);
    }
}
