package org.nbu.repository;

import org.nbu.entity.NbuDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Nikolay Boyko
 */

@Repository
public interface NbuDataRepository extends JpaRepository<NbuDataEntity, Long> {

    @Query(value = "SELECT * FROM nbu_data_table WHERE exchange_date=:date", nativeQuery = true)
    Optional<List<NbuDataEntity>> getAllCurrencyByDate(LocalDate date);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM nbu_data_table WHERE exchange_date=:date", nativeQuery = true)
    boolean existsByExchangeDate(LocalDate date);
}
