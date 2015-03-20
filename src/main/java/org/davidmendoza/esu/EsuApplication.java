package org.davidmendoza.esu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableJpaRepositories("org.davidmendoza.esu.dao")
@EnableSpringDataWebSupport
public class EsuApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsuApplication.class, args);
    }
}
