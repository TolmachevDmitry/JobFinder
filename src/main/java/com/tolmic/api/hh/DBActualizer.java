package com.tolmic.api.hh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tolmic.entity.Vacancy;

import com.tolmic.service.impl.VacanciesService;

@Component
public class DBActualizer implements CommandLineRunner {

    @Autowired
    private VacanciesService vacanciesService;

    String[] professions = new String[] {"Бухглатер", "Java-программист", "Python-программист", "QA-инженер", 
                                        "1С-программист", "Кассир", "Продавец", "Водитель", "Врач", "Кладовщик", "Электрик",
                                        "Python-программист", "Тестировщик", "Аптекарь", "Сварщик", "Тракторист", "Сантехник", "Директор",
                                        "Учитель", "Курьер", "Преподаватель", "Программист на C#", "Программист на Go", "Монтажник",
                                        "Повар", "Кандитер", "Охранник", "Строитель"};

    private void actualizeDB() {
        for (String profession : professions) {
            List<Vacancy> vacancies = vacanciesService.findVacanciesFromHH(profession);
            vacanciesService.saveVacancy(vacancies);
        }
    }

    @Override
    public void run(String... args) {
        boolean isActive = true;

        while(isActive) {
            // actualizeDB();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isActive = false;
            }
        }
    }

}
