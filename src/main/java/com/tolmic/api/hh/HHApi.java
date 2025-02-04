package com.tolmic.api.hh;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.NotNull;


@PropertySource("keys.yml")
@Component
public class HHApi {

    @Value("${access-tocken}")
    private String accessToken;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate = new RestTemplate();

    public List<Vacancy> getSimpleData(String string) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange("https://api.hh.ru/vacancies?text=" + string,
                HttpMethod.GET, entity, String.class);

        List<Vacancy> vacancies = new ArrayList<>();
        String body = response.getBody();

        if (Objects.nonNull(body)) {
            body = body.replace("{\"items\":", "");
        }
        
        try {
            vacancies = objectMapper.readValue(body, new TypeReference<List<Vacancy>>(){});
        } catch (JsonProcessingException e) {
            e.addSuppressed(e);
        }

        return vacancies;
    }

}
