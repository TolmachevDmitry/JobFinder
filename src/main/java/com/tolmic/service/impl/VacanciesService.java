package com.tolmic.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tolmic.api.hh.HHApi;
import com.tolmic.entity.Vacancy;
import com.tolmic.entity.VectorView;
import com.tolmic.repository.VacancyRepository;
import com.tolmic.service.VacanciesServiceInterface;

@Service
public class VacanciesService implements VacanciesServiceInterface {

    @Autowired
    private HHApi HHApi;

    @Autowired
    private VacancyRepository vacancyRepository;

    // 1 элемент списка ~ 660 символов и 86 слов
    @Autowired
    private EmbeddingModel embeddingModel;

    @Override
    public List<Object[]> findVacanciesFromDB(float[] vector) {
        List<Object[]> vacancies = vacancyRepository.findCosinus(new float[] {1, 2});

        fromObject((Object[]) vacancies.get(0)[0]);

        return vacancies;
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

    // rename method !
    private void createEmbeddingByText(List<VectorView> vectorViews, String text) {
        int endPropInd = getIndexOfPropEnd(text);
        int startPropInd = 0;

        while (endPropInd != -1) {
            String proposal = text.substring(startPropInd, endPropInd + 1);

            VectorView vectorView = new VectorView();
            vectorView.setView(turnToEmbedding(proposal));

            vectorViews.add(vectorView);

            startPropInd = endPropInd + 1;
            endPropInd = getIndexOfPropEnd(text.substring(startPropInd));
        }
    }

    private void createVectorView(Vacancy vacancy) {
        List<VectorView> vectorViews = new ArrayList<>();

        createEmbeddingByText(vectorViews, vacancy.getExperience());
        createEmbeddingByText(vectorViews, vacancy.getName());
        createEmbeddingByText(vectorViews, vacancy.getRequirement());
        createEmbeddingByText(vectorViews, vacancy.getRequirement());

        vacancy.setVectorViews(vectorViews);
    }

    @Override
    public List<Vacancy> findVacanciesFromHH(String name) {
        List<Vacancy> vacancies = new ArrayList<>();

        try {
            vacancies = HHApi.getSimpleData(name);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return vacancies;
    }

    public void createVectorView(List<Vacancy> vacancies) {
        for (Vacancy vacancy : vacancies) {
            createVectorView(vacancy);
        }
    }

    @Override
    public void saveVacancy(List<Vacancy> vacancies) {
        saveVacancy(vacancies);
    }

    public Vacancy fromObject(Object[] obj) {
        Vacancy v = new Vacancy();

        v.setId(Long.valueOf((String) obj[0]));
        v.setName((String) obj[1]);    
        v.setOpen(true);     
        v.setRequirement((String) obj[3]);
        v.setResponsibility((String) obj[4]);
        v.setRequirement((String) obj[5]);

        return v;
    }
    
}
