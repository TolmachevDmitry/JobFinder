package com.tolmic.api.hh;

import java.util.ArrayList;
import java.util.List;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@PropertySource("keys.yml")
@Component
public class HHApi {

    @Value("${access-tocken}")
    private String accessToken;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate = new RestTemplate();

    private JsonNode getResponseEntity(String text, int page) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            String.format("https://api.hh.ru/vacancies?text=%s&page=%s", text, page),
                HttpMethod.GET, entity, String.class);

        return objectMapper.readTree(response.getBody());
    }

    private JsonNode getFirstPageResponse(String text) throws JsonProcessingException {
        return getResponseEntity(text, 0);
    }

    private JsonNode getResponseByPage(String text, int page) throws JsonProcessingException {
        return getResponseEntity(text, page);
    }

    private List<Vacancy> getVacanciesFromJsonNode(JsonNode jsonNode) throws JsonProcessingException {
        JsonNode jn = jsonNode.get("items");
        return objectMapper.readValue(jn.toString(), new TypeReference<List<Vacancy>>(){});
    }

    /**
     * Search relevant vacancies by API of Head Hunter.
     * 
     * @param vacancyName - little description of vacancy.
     * @return list of found relevant vacancies.
     * @throws JsonProcessingException if hh api server will work incorrect.
     */
    public List<Vacancy> getSimpleData(String vacancyName) throws JsonProcessingException {
        JsonNode jsonNode = getFirstPageResponse(vacancyName);

        int pageNumber = jsonNode.get("pages").asInt();
        int perPage = jsonNode.get("per_page").asInt();

        List<Vacancy> vacancies = new ArrayList<>(pageNumber * perPage);
        vacancies.addAll(getVacanciesFromJsonNode(jsonNode));

        // takes quite a long time to complete
        for (int i = 1; i < pageNumber; i++) {
            jsonNode = getResponseByPage(vacancyName, i);

            List<Vacancy> listPart = getVacanciesFromJsonNode(jsonNode);
            vacancies.addAll(listPart);
        }

        return vacancies;
    }

}
