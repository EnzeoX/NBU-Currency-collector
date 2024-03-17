package org.nbu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Nikolay Boyko
 */

@Getter
@Setter
@Entity
@Builder
@Table(name = "nbu_data_table")
@AllArgsConstructor
@NoArgsConstructor
public class NbuDataEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "r030", nullable = false)
    private String r030;

    @Column(name = "txt", nullable = false)
    private String txt;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    @Column(name = "cc", nullable = false)
    private String cc;

    @Column(name = "exchange_date", nullable = false)
    private LocalDate exchangeDate;

    @Column(name = "date_add", nullable = false)
    private LocalDateTime dateAdd;
}
