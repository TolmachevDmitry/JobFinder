package com.tolmic.service;

import java.util.List;

import com.tolmic.entity.Vacancy;

public interface VacanciesServiceInterface {

    public void updateVacancyStorage();

    public List<Object[]> findVacanciesByCosinus(float[] vector);

}
