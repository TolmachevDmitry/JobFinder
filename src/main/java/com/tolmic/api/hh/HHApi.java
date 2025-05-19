package com.tolmic.api.hh;

import java.util.ArrayList;
import java.util.LinkedList;
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
import com.tolmic.entity.Vacancy;

@PropertySource("keys.yml")
@Component
public class HHApi {

    @Value("${access-tocken}")
    private String accessToken;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate = new RestTemplate();

    private final String httpLink = "https://api.hh.ru/vacancies";

    private final String[] vacancyNames = new String[] {"Бухгалтер", "Системный администратор", "Разработчик", "Графический дизайнер",
                    "Повар", "Врач", "Кассир", "Продавец", "Уборщик", "Программист", "Инженер", "Учитель", "Преподаватель",
                    "Грузчик", "Сварщик", "Контроллер", "Делопроизводитель", "Менеджер", "Охранник", "Аналитик", "Экономист",
                    "Водитель", "Оператор", "Администратор"};

    private JsonNode getResponseEntityMain(String parameters, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            String.format(httpLink + parameters),
                HttpMethod.GET, entity, JsonNode.class);

        return response.getBody();
    }

    //#region get vacancies by name
    private JsonNode getResponseEntityByName(String parameters) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        return getResponseEntityMain(parameters, headers);
    }

    private JsonNode getFirstPageResponse(String text) {
        return getResponseEntityByName(String.format("?text=%s&page=%s", text, 0));
    }

    private JsonNode getResponseByPage(String text, int page) {
        return getResponseEntityByName(String.format("?text=%s&page=%s", text, page));
    }
    //#endregion

    //#region clean data
    private String deleteExtraSpace(String text) {
        StringBuilder sb = new StringBuilder("");

        int n = text.length();
        boolean prevIsSpace = false;
        for (int i = 0; i < n; i++) {
            String symb = String.valueOf(text.charAt(i));

            if (symb.equals(" ") && prevIsSpace) {
                continue;
            }

            sb.append(symb);

            prevIsSpace = symb.equals(" ");
        }

        return sb.toString();
    }

    private String deleteTegs(String text) {
        String text1 = text;

        int ind1 = text1.indexOf("<");
        int ind2 = text1.indexOf(">");

        while (ind1 > -1) {
            text1 = text1.substring(0, ind1) + text1.substring(ind2 + 1, text1.length());
            
            ind1 = text1.indexOf("<");
            ind2 = text1.indexOf(">");
        }

        return text1;
    }

    private void cleanFromTegs(Vacancy vacancy) {
        String description = deleteTegs(vacancy.getDescription());
        description = deleteExtraSpace(description);

        vacancy.setDescription(description);
    }
    //#endregion

    //#region find vacancy by id
    private JsonNode getResponseEntityById(String parameters) {
        return getResponseEntityMain(parameters, new HttpHeaders());
    }

    private LinkedList<String> getVacanciesIdentifiers(JsonNode jsonNode) {
        JsonNode jn = jsonNode.get("items");
        
        LinkedList<String> identifiers = new LinkedList<>();
        for (JsonNode node : jn) {
            identifiers.add(node.get("id").asText());
        }

        return identifiers;
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException inter) {

        }
    }

    private boolean isForbidden(String exceptionMessege) {
        return exceptionMessege.contains("Forbidden");
    }

    private JsonNode iterativeResponseEntityGeneration(String id) {
        boolean f = true;

        JsonNode jsonNode = null;
        while (f) {
            f = false;

            try {
                jsonNode = getResponseEntityById("/" + id);
            } catch (Exception e) {
                f = isForbidden(e.getMessage());
            }

            if (f) {
                sleep();
            }
        }

        return jsonNode;
    }

    private Vacancy getVacancyFromJson(JsonNode jsonNode) {
        Vacancy vacancy = null;
        
        try {
            vacancy = objectMapper.readValue(jsonNode.toString(), Vacancy.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return vacancy;
    }

    private Vacancy getVacnacyByIdentifiers(String id) {
        JsonNode jsonNode = iterativeResponseEntityGeneration(id);
        Vacancy vacancy = getVacancyFromJson(jsonNode);;

        return vacancy;
    }

    private void getAndPutVacanciesByIdentifiers(LinkedList<String> identifiers, LinkedList<Vacancy> vacancies) {
        while (!identifiers.isEmpty()) {
            Vacancy vacancy = getVacnacyByIdentifiers(identifiers.pollFirst());

            if (vacancy != null) {
                cleanFromTegs(vacancy);
                
                vacancies.add(vacancy);
            }
        }
    }

    private void getAndPutNVacanciesPage(LinkedList<Vacancy> vacancies, int pageNumber, String vacancyName) {
        for (int i = 1; i < pageNumber; i++) {
            JsonNode jsonNode = getResponseByPage(vacancyName, i);
            LinkedList<String> ides = getVacanciesIdentifiers(jsonNode);

            getAndPutVacanciesByIdentifiers(ides, vacancies);
        }
    }

    private void getAndPutVacancies(String vacancyName, LinkedList<Vacancy> vacancies)  {
        JsonNode firstNode = getFirstPageResponse(vacancyName);
        int pageNumber = firstNode.get("pages").asInt();

        getAndPutVacanciesByIdentifiers(getVacanciesIdentifiers(firstNode), vacancies);

        getAndPutNVacanciesPage(vacancies, pageNumber, vacancyName);
    }

    /**
     * Search relevant vacancies by API of Head Hunter.
     * 
     * @param vacancyName - little description of vacancy.
     * @return list of found relevant vacancies.
     * @throws JsonProcessingException if hh api server will work incorrect.
     */
    public LinkedList<Vacancy> getVacancies() {
        LinkedList<Vacancy> vacancies = new LinkedList<>();

        for (String vacancyName : vacancyNames) {
            getAndPutVacancies(vacancyName, vacancies);
        }

        return vacancies;
    }

}
