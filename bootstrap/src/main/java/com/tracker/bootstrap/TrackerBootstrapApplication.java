package com.tracker.bootstrap;


import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {
        "com.tracker.bootstrap",
        "com.tracker.main",
        "com.tracker.workflow",
        "com.tracker.shared",
        "com.tracker.dashboard",
        "com.tracker.email",
        "com.tracker.audit"
})
@EnableJpaRepositories(basePackages = {
        "com.tracker.workflow.repository"
})
@EntityScan(basePackages = {
        "com.tracker.workflow.model"
})


@Log4j2
public class TrackerBootstrapApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(TrackerBootstrapApplication.class, args);
        log.info("starting TrackerBootstrapApplication application");
    }

    @Bean
    public CommandLineRunner runFlyway(Flyway flyway) {
        return args -> {
            log.info("Running Flyway migrations from bootstrap...");
            flyway.migrate();
            log.info("Flyway migrations completed successfully from bootstrap.");

        };
    }
}
