package org.spms.spmsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartPackageManagementSystemBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartPackageManagementSystemBackEndApplication.class, args);
    }
}
