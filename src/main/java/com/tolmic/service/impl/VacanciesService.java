package com.tolmic.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tolmic.api.hh.HHApi;
import com.tolmic.entity.Vacancy;
import com.tolmic.repository.VacancyRepository;
import com.tolmic.service.VacanciesServiceInterface;

@Service
public class VacanciesService implements VacanciesServiceInterface {

    @Autowired
    private VacancyRepository vacancyRepository;

    @Autowired
    private HHApi HHApi;

    private final double threshold = 98.34;

    // 1 элемент списка ~ 660 символов и 86 слов
    @Autowired
    private EmbeddingModel embeddingModel;

    @Override
    public List<Object[]> findVacanciesByCosinus(float[] vector) {
        List<Object[]> vacancies = vacancyRepository.findCosinus(new float[] {1, 2}, threshold);

        fromObject((Object[]) vacancies.get(0)[0]);

        return vacancies;
    }

    private Vacancy findVacancyById(Long id) {
        return vacancyRepository.findById(id).get();
    }

    private void saveVacancyMain(Vacancy vacancy) {
        vacancyRepository.save(vacancy);
    }

    @Override
    public void saveVacancy(Vacancy vacancy) {
        saveVacancyMain(vacancy);
    }

    public Vacancy fromObject(Object[] obj) {
        Vacancy v = new Vacancy();

        return v;
    }

    private void updateVacancyStorage(LinkedList<Vacancy> vacancies) {
        while (!vacancies.isEmpty()) {
            Vacancy vacancy = vacancies.pollFirst();

            if (vacancy != null) {
                saveVacancyMain(vacancy);
            }
        }
    }

    @Override
    public void updateVacancyStorage() {
        updateVacancyStorage(HHApi.getVacancies());

    }

    private float[] turnToEmbedding(String text) {
        EmbeddingResponse vector = embeddingModel.embedForResponse(List.of(text));
        float[] embedding = vector.getResults().get(0).getOutput();

        return embedding;
    }

    private int getIndexOfPropEnd(String str) {
        int index = str.indexOf(".");

        if (index == -1) {
            index = str.indexOf("!");
        }

        return index;
    }
    
}
