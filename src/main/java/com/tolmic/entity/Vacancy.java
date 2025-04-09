package com.tolmic.entity;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "vacancy")
public class Vacancy {

    @Id
    private Long id;

    @Column(name = "vacancy_name", columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text")
    private String requirement;

    @Column(columnDefinition = "text")
    private String responsibility;

    @Column(columnDefinition = "text")
    private String experience;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vacancy")
    private List<VectorView> vectorViews;

    @JsonIgnore
    private boolean isOpen;

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
