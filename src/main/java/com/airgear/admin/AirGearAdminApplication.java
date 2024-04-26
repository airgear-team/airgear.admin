package com.airgear.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.airgear.admin", "com.airgear.model"})
public class AirGearAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirGearAdminApplication.class, args);
    }

}
