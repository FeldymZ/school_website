package com.school.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync   // ✅ OBLIGATOIRE
@SpringBootApplication
public class SchoolApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SchoolApiApplication.class, args);
  }

}
