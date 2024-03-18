package org.nbu.nbucurrencycollector.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.nbu.controller.CurrencyController;
import org.nbu.dto.CurrencyDto;
import org.nbu.handler.CurrencyRequestHandler;
import org.nbu.models.NbuDataModel;
import org.nbu.models.ServiceResponse;
import org.nbu.service.CurrencyService;
import org.nbu.utils.DataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@WebMvcTest(CurrencyController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CurrencyRequestHandler requestHandler;

    private final Map<LocalDate, List<NbuDataModel>> listOfData = new HashMap<>();

    // actual data mocked as date of 2024-03-18
    private final List<CurrencyDto> actualData = new ArrayList<>();

    // data by date of 2024-01-01
    private final List<CurrencyDto> dataByDate = new ArrayList<>();

    private String actualDataStringJson = null;
    private String dataByDateStringJson = null;

    @BeforeAll
    public void initMockedData() throws Exception {
        mapper.findAndRegisterModules();
        NbuDataModel[] models = mapper.readValue(getClass().getResource("/static/mock-currency.json"), NbuDataModel[].class);
        for (NbuDataModel model : models) {
            if (model != null) {
                if (!listOfData.containsKey(model.getExchangeDate())) {
                    List<NbuDataModel> list = new ArrayList<>();
                    list.add(model);
                    listOfData.put(model.getExchangeDate(), list);
                } else {
                    listOfData.get(model.getExchangeDate()).add(model);
                }
            }
        }
        LocalDate actual = LocalDate.of(2024, 3, 18);
        LocalDateTime now = LocalDateTime.now();
        actualData.addAll(
                listOfData.get(actual)
                        .stream()
                        .map(model -> DataMapper.nbuModelToDto(model, now))
                        .toList()
        );
        dataByDate.addAll(
                listOfData.get(LocalDate.of(2024, 1, 1))
                        .stream()
                        .map(model -> DataMapper.nbuModelToDto(model, now))
                        .toList()
        );
        dataByDateStringJson = mapper.writeValueAsString(this.dataByDate);
        actualDataStringJson = mapper.writeValueAsString(this.actualData);
    }

    @Test
    @DisplayName("Test method \"getActualCurrency\" - response ok")
    public void getActualCurrencyOkResponse() throws Exception {
        mvc.perform(get("/api/v1/currency/get"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test method \"getActualCurrency\" - response ok with data inside body")
    public void getActualCurrencyGetJsonResult() throws Exception {
        ResponseEntity<List<CurrencyDto>> response = ResponseEntity.status(HttpStatus.OK).body(this.actualData);
        Mockito.when(this.requestHandler.getAllCurrency()).thenReturn(response);
        mvc.perform(get("/api/v1/currency/get"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(this.actualDataStringJson));
    }

    @Test
    @DisplayName("Test method \"getActualCurrency\" - response ok with empty body")
    public void getActualCurrencyExpectNoData() throws Exception {
        ResponseEntity<List<CurrencyDto>> response = ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        Mockito.when(this.requestHandler.getAllCurrency()).thenReturn(response);
        mvc.perform(get("/api/v1/currency/get"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    @DisplayName("Test method \"getAllCurrencyByDate\" - response ok with empty body")
    public void getCurrencyByDateIsOkButEmpty() throws Exception {
        mvc.perform(get("/api/v1/currency/get/by-date/1995-01-01"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test method \"getAllCurrencyByDate\" - response ok with data inside body")
    public void getCurrencyByDateWithDataOk() throws Exception {
        ResponseEntity<List<CurrencyDto>> response = ResponseEntity.status(HttpStatus.OK).body(this.dataByDate);
        Mockito.when(this.requestHandler.getAllCurrencyByDate(LocalDate.of(2024, 1, 1))).thenReturn(response);
        mvc.perform(get("/api/v1/currency/get/by-date/2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(this.dataByDateStringJson));
    }

    @Test
    @DisplayName("Test method \"getAllCurrencyByDate\" - expecting exception because of incorrect date")
    public void getCurrencyByDateIncorrectDateProvided() throws Exception {
        mvc.perform(get("/api/v1/currency/get/by-date/112312323"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Can't parse provided URL parameter")));
    }

    @Test
    @DisplayName("Test method \"deleteCurrencyByDate\" - expecting OK status response")
    public void deleteCurrencyByDateCorrectExpectOkStatus() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatus("OK");
        serviceResponse.setMessage(String.format("Data deletion by provided date %s successful", date));
        ResponseEntity<ServiceResponse> response = ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
        Mockito.when(this.requestHandler.deleteCurrencyByDate(date)).thenReturn(response);
        mvc.perform(post("/api/v1/currency/delete/by-date/2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", CoreMatchers.is("OK")));
    }

    @Test
    @DisplayName("Test method \"deleteCurrencyByDate\" - expecting successful message")
    public void deleteCurrencyByDateCorrectExpectSuccessfulMessage() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatus("OK");
        serviceResponse.setMessage(String.format("Data deletion by provided date %s successful", date));
        ResponseEntity<ServiceResponse> response = ResponseEntity.status(HttpStatus.OK).body(serviceResponse);
        Mockito.when(this.requestHandler.deleteCurrencyByDate(date)).thenReturn(response);
        mvc.perform(post("/api/v1/currency/delete/by-date/2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Data deletion by provided date 2024-01-01 successful")));
    }

    @Test
    @DisplayName("Test method \"deleteCurrencyByDate\" - expecting exception because of incorrect date")
    public void deleteCurrencyByDateCorrectExceptionExpected() throws Exception {
        mvc.perform(get("/api/v1/currency/get/by-date/112312323"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Can't parse provided URL parameter")));
    }
}
