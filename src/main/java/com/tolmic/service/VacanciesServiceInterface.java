package com.tolmic.service;

import java.util.List;

import com.tolmic.entity.Vacancy;

public interface VacanciesServiceInterface {

    public void saveVacancy(List<Vacancy> vacancies);

    public List<Vacancy> findVacanciesFromHH(String name);

    public List<Object[]> findVacanciesFromDB(float[] vector);

}
