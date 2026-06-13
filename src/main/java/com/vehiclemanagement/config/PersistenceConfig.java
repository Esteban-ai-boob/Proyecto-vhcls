package com.vehiclemanagement.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"com.vehiclemanagement.entity", "com.PPOOII.Laboratorio.Entities"})
@EnableJpaRepositories(basePackages = {"com.vehiclemanagement.repository", "com.PPOOII.Laboratorio.Repository"})
public class PersistenceConfig {
}
