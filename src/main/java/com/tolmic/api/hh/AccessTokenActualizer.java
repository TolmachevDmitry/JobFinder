package com.tolmic.api.hh;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


public class AccessTokenActualizer implements CommandLineRunner {

    private void actualAccessToken() {
        for (int i = 0; i < 20; i++) {
            System.out.println("actual token");
        }
    }

    @Override
    public void run(String... args) {
        boolean isActive = true;

        while(isActive) {

            actualAccessToken();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isActive = false;
            }
        }

    }

    // public AccessTokenActualizer() {
    //     run();
    // }

}
