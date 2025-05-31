package com.example.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class RegistrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistrationApplication.class, args);
    }

}
