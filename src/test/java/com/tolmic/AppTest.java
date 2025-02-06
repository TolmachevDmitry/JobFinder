package com.tolmic;


import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class AppTest 
{
    private static class Person {
        private String name;
        private int age;
        
        // Геттеры и сеттеры
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    @Test void test() {
        assertTrue(true);
    }

    // public static void main(String[] args) throws JsonProcessingException {
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    //     String text = "{\"items\": [{\"name\":\"Иван\",\"age\":\"30\"}], \"pages\": \"20\"}";

    //     JsonNode jsonNode = objectMapper.readTree(text);
    //     JsonNode jn = jsonNode.get("items");

    //     List<Person> p = objectMapper.readValue(jn.toString(), new TypeReference<List<Person>>(){});
    // }

    public static void main(String[] args) throws JsonProcessingException {
        String text = "Кассир";
        Object page = 10;

        String a = String.format("https://api.hh.ru/vacancies?text=%s&pages=%s", text, page);
    } 
}
