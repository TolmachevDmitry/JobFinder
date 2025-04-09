package com.tolmic.api.hh;

import org.springframework.boot.CommandLineRunner;

public class AccessTokenActualizer implements CommandLineRunner {

    private void actualAccessToken() {
        
    }

    @Override
    public void run(String... args) {
        boolean isActive = true;

        while(isActive) {

            
            // actualAccessToken();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isActive = false;
            }
        }

    }

}
