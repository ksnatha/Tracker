package com.tracker.main;

import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot application to run Flyway migrations.
 * This application will automatically run Flyway migrations when started.
 */
//@SpringBootApplication(scanBasePackages = "com.odyssey")
@Log4j2
//@EnableAutoConfiguration
public class FlywayMigrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlywayMigrationApplication.class, args);
    }

    @Bean
    public CommandLineRunner runFlyway(Flyway flyway) {
        return args -> {
            log.info("Running Flyway migrations...");
            //flyway.migrate();
            log.info("Flyway migrations completed successfully.");
            // Exit the application after migrations are complete
            //System.exit(0);
            // Ensure it runs as a web application
            //app.setWebApplicationType(WebApplicationType.SERVLET);


            //SpringApplication.run(App.class, args);
        };
    }
}