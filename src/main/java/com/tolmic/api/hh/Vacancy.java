package com.tolmic.api.hh;

import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Vacancy {

    private Long id;

    private String name;

    private String requirement;

    private String responsibility;

    private String experience;

    @JsonProperty("snippet")
    private void unpackSnippet(Map<String, Object> snippet) {
        this.requirement = (String) snippet.get("requirement");
        this.responsibility = (String) snippet.get("responsibility");
    }

    @JsonProperty("experience")
    private void unpackExperience(Map<String, Object> experience) {
        this.experience = (String) experience.get("name");
    }

    @Override
    public String toString() {
        return "";
    }
}
