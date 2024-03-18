package org.nbu.utils;

import org.nbu.dto.CurrencyDto;
import org.nbu.entity.NbuDataEntity;
import org.nbu.models.NbuDataModel;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Nikolay Boyko
 */
public class DataMapper {

    public static NbuDataEntity nbuModelToEntity(NbuDataModel model, LocalDateTime now) {
        Objects.requireNonNull(model, "Provided model is null or empty");
        return NbuDataEntity.builder()
                .r030(model.getR030())
                .txt(model.getTxt())
                .rate(model.getRate())
                .cc(model.getCc())
                .exchangeDate(model.getExchangeDate())
                .dateAdd(now)
                .build();
    }

    public static CurrencyDto nbuEntityToDto(NbuDataEntity entity) {
        Objects.requireNonNull(entity, "Provided entity is null or empty");
        return CurrencyDto.builder()
                .r030(entity.getR030())
                .txt(entity.getTxt())
                .rate(entity.getRate())
                .cc(entity.getCc())
                .dateAdd(entity.getDateAdd())
                .exchangeDate(entity.getExchangeDate())
                .build();
    }

    public static NbuDataEntity nbuDtoToEntity(CurrencyDto dto) {
        Objects.requireNonNull(dto, "Provided dto is null or empty");
        return NbuDataEntity.builder()
                .r030(dto.getR030())
                .txt(dto.getTxt())
                .rate(dto.getRate())
                .cc(dto.getCc())
                .exchangeDate(dto.getExchangeDate())
                .dateAdd(dto.getDateAdd())
                .build();
    }

    public static CurrencyDto nbuModelToDto(NbuDataModel model, LocalDateTime now) {
        Objects.requireNonNull(model, "Provided model is null or empty");
        return CurrencyDto.builder()
                .r030(model.getR030())
                .txt(model.getTxt())
                .rate(model.getRate())
                .cc(model.getCc())
                .exchangeDate(model.getExchangeDate())
                .dateAdd(now)
                .build();
    }
}
