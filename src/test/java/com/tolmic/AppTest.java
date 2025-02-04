package com.tolmic;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tolmic.api.hh.Vacancy;


@SpringBootTest
public class AppTest 
{
    
    // @Autowired
    // ObjectMapper objectMapper;

    @Test
    public void shouldAnswerWithTrue()
    {
        // String jsonCarArray = 
        //     "[{ \"name\" : \"Teacher\", \"organization\" : \"School № 1\", \"datePublished\" : \"11.2.2024\", \"city\" : \"Voronezh\", \"requirements\" : \"bla-bla-bla\", \"salary\" : \"50 000\" }]";

        // List<Vacancy> vacancies = new ArrayList<>();
        // try {
        //     objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //     vacancies = objectMapper.readValue(jsonCarArray, new TypeReference<List<Vacancy>>(){});
        // } catch (JsonProcessingException e) {
        //     e.addSuppressed(e);
        // }
        
        // assertEquals(vacancies.size(), 1);

        // Vacancy vacancy = vacancies.get(0);

        // assertEquals(vacancy.getCity(), "School № 1");
    }
}
