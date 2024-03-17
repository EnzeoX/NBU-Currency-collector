package org.nbu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Nikolay Boyko
 */

@Getter
@Setter
@Builder
public class NbuDto {

    @JsonProperty("r030")
    private String r030;

    @JsonProperty("txt")
    private String txt;

    @JsonProperty("rate")
    private BigDecimal rate;

    @JsonProperty("cc")
    private String cc;

    @JsonProperty("exchangedate")
    private LocalDate exchangeDate;

    @JsonProperty("date_add")
    private LocalDateTime dateAdd;

}
