package edu.fish.blinddate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BlindDateRecordPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlindDateRecordPlatformApplication.class, args);
    }

}
