package com.tracker.main;

import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot Application
 */
/*@SpringBootApplication(scanBasePackages = "com.tracker")*/
@Log4j2
//@EnableAutoConfiguration
/*@ComponentScan(basePackages = {
        "com.odyssey"
})*/
public class App 
{
    public static void main( String[] args )
    {
        log.info("Starting Tracker Application");
        //SpringApplication.run(App.class, args);
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
