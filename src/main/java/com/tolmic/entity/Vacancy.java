package com.tolmic.entity;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "vacancy")
public class Vacancy {

    @Id
    private Long id;

    @Column(name = "vacancy_name", columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private String city;

    private String employer;

    private String experience;

    @JsonIgnore
    private Date closureDate;

    @JsonIgnore
    private float[] vectorView;

    @JsonProperty("experience")
    private void unpackExperience(Map<String, Object> experience) {
        this.experience = (String) experience.get("name");
    }

    @JsonProperty("area")
    private void unpackArea(Map<String, Object> area) {
        this.city = (String) area.get("name");
    }

    @JsonProperty("employer")
    private void unpackEmployer(Map<String, Object> employer) {
        this.employer = (String) employer.get("name");
    }
}
