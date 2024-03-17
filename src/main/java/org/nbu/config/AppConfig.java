package org.nbu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nbu.dto.CurrencyDto;
import org.nbu.repository.NbuDataRepository;
import org.nbu.service.ICurrencyService;
import org.nbu.service.impl.MockCurrencyService;
import org.nbu.service.impl.NbuService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Nikolay Boyko
 */

@Configuration
public class AppConfig {

    @Bean
    @Primary
    @Profile("dev")
    @Qualifier("nbuCurrencyService")
    public ICurrencyService<List<CurrencyDto>, LocalDate> nbuCurrencyService(NbuDataRepository repository, ObjectMapper mapper) {
        return new NbuService(mapper, repository);
    }

    @Bean
    @Profile("mock")
    @Qualifier("mockedCurrencyService")
    public ICurrencyService<List<CurrencyDto>, LocalDate> mockedCurrencyService() {
        return new MockCurrencyService();
    }
}
