package org.nbu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nbu.dto.NbuDto;
import org.nbu.entity.NbuDataEntity;
import org.nbu.models.NbuDataModel;
import org.nbu.repository.NbuDataRepository;
import org.nbu.utils.DataMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Service
@AllArgsConstructor
public class NbuService {

    private final ObjectMapper mapper;
    private final NbuDataRepository repository;

    private final Set<LocalDate> isCurrencyByDateInDB = ConcurrentHashMap.newKeySet();
    private final Map<LocalDate, List<NbuDataModel>> updatingData = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public List<NbuDto> getAllCurrency() {
        LocalDate dateNow = LocalDate.now();
        DayOfWeek dayOfWeek = dateNow.getDayOfWeek();
        if (dayOfWeek.getValue() >= 5 && dayOfWeek.getValue() <= 7) {
            dateNow = dateNow.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }
        return getCurrencyByDate(dateNow);
    }

    public List<NbuDto> getCurrencyByDate(LocalDate date) {
        Objects.requireNonNull(date, "Provided date is empty or null");
        if (isCurrencyByDateInDB.contains(date)) {
            log.info("Getting data from Database for date of {}", date);
            List<NbuDto> result = getLocalDataDto(date);
            if (result.size() == 0) {
                log.warn("No data collected from database for date {}, is it still there?", date);
            }
            return result;
        } else if (repository.existsByExchangeDate(date)) {
            log.info("Data exists in Database for date {}, but not marked", date);
            isCurrencyByDateInDB.add(date);
            List<NbuDto> result = getLocalDataDto(date);
            if (result.size() == 0) {
                log.warn("No data collected from database for date {}, is it still there?", date);
            }
            return result;
        } else {
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
                    updatingData.remove(date);
                    isCurrencyByDateInDB.add(date);
                });
                return dataModels.stream()
                        .map(model -> DataMapper.nbuModelToDto(model, now))
                        .toList();
            }
        }
    }

    private List<NbuDataModel> getDataFromNbu(LocalDate date) {
        ResponseEntity<Object[]> listOfData = new RestTemplate()
                .getForEntity("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json", Object[].class);
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

    private List<NbuDto> getLocalDataDto(LocalDate date) {
        return getLocalData(date)
                .stream()
                .map(DataMapper::nbuEntityToDto)
                .toList();
    }

}
