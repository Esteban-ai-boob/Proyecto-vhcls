package com.PPOOII.Laboratorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
    scanBasePackages = {"com.PPOOII.Laboratorio", "com.vehiclemanagement"},
    exclude = {UserDetailsServiceAutoConfiguration.class}
)
public class Laboratorio3Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Laboratorio3Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Laboratorio3Application.class);
    }
}
