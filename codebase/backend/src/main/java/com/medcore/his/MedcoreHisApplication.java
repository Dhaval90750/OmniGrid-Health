package com.medcore.his;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.medcore.his.repository", 
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.medcore.his.repository.search.*"))
@EnableElasticsearchRepositories(basePackages = "com.medcore.his.repository.search")
public class MedcoreHisApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedcoreHisApplication.class, args);
    }

}
