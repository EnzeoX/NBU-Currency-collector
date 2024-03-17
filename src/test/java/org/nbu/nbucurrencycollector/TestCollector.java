package org.nbu.nbucurrencycollector;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.nbu.models.NbuDataModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCollector {

    @Test
    public void testCollectingNbuDta() {

        ResponseEntity<List<NbuDataModel>> listOfData = new RestTemplate().exchange(
                "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        log.info(String.valueOf(listOfData.getBody()));
    }
}
