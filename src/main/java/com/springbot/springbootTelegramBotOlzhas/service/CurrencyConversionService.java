package com.springbot.springbootTelegramBotOlzhas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.springframework.stereotype.Service;
@Slf4j
@Service
public class CurrencyConversionService {
    private static final String API_URL = "http://api.freecurrencyapi.com/v1/latest?apikey=nmQZWnL7NDw57OMF4JAR7VQzf4cGuRgbOR42b7dD";
//    private static final String API_KEY = "nmQZWnL7NDw57OMF4JAR7VQzf4cGuRgbOR42b7dD";

    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        RestTemplate restTemplate = new RestTemplate();
        String url = API_URL + "&base_currency=" + fromCurrency.toUpperCase() + "&currencies=" + toCurrency.toUpperCase();
        log.info("Making request to " + url);
        try {
            ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity(url, ExchangeRateResponse.class);
            log.info("Received response: " + response);
            Double toCurrencyRate = response.getBody().getData().get(toCurrency.toUpperCase());
            if (toCurrencyRate == null) {
                throw new RuntimeException("Currency code not supported: " + toCurrency);
            }

            return amount * toCurrencyRate;
        } catch (RestClientException e) {
            log.error("HTTP call failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
