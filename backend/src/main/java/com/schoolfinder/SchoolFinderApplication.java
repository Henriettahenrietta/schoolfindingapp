package com.schoolfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SchoolFinderApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolFinderApplication.class, args);
    }
}
