package org.example.translateapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class LanguagesController {

    @Getter
    private static final Map<String, String> languages = fetchLanguages();

    @GetMapping("/languages")
    public Map<String, String> languages() {
        return languages;
    }

    private static Map<String, String> fetchLanguages() {
        final String GET_LANGUAGES_URI = "https://ftapi.pythonanywhere.com/languages";
        ObjectMapper objectMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(GET_LANGUAGES_URI, String.class);
        try {
            return objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
