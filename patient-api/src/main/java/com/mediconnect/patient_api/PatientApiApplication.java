package com.mediconnect.patient_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class PatientApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientApiApplication.class, args);
    }
}