package org.nbu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nbu.repository.NbuDataRepository;
import org.nbu.service.CurrencyService;
import org.nbu.service.impl.MockCurrencyService;
import org.nbu.service.impl.NbuService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;

/**
 * @author Nikolay Boyko
 */

@Configuration
public class AppConfig {

    @Bean
    @Primary
    @Profile("!mock")
    @Qualifier("nbuCurrencyService")
    public CurrencyService<LocalDate> nbuCurrencyService(NbuDataRepository repository, ObjectMapper mapper) {
        return new NbuService(mapper, repository);
    }

    @Bean
    @Profile("mock")
    @Qualifier("mockedCurrencyService")
    public CurrencyService<LocalDate> mockedCurrencyService() {
        return new MockCurrencyService();
    }
}
