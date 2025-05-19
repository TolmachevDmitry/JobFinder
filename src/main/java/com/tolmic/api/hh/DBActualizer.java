package com.tolmic.api.hh;

import java.time.LocalTime;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tolmic.entity.Vacancy;

import com.tolmic.service.impl.VacanciesService;

@Component
public class DBActualizer implements CommandLineRunner {

    @Autowired
    private VacanciesService vacanciesService;

    private boolean compareTime(LocalTime now) {
        LocalTime start = LocalTime.of(22,30);
        LocalTime end   = LocalTime.of(23, 30);

        return now.isAfter(start) && now.isBefore(end);
    }

    private void actualizeDB() {
        vacanciesService.updateVacancyStorage();
    }

    @Override
    public void run(String... args) {
        boolean isActive = true;

        while(isActive) {
            LocalTime localTime = LocalTime.now();

            if (compareTime(localTime)) {
                actualizeDB();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isActive = false;
            }
        } 
    }

}
