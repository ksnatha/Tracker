package com.tracker.main.util;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;

/**
 * Utility class to run Flyway migrations programmatically.
 * This can be used to run migrations without starting the entire Spring Boot application.
 */
public class FlywayMigrationRunner {

    public static void main(String[] args) {
        // Configure Flyway with the same properties as in application.properties
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://localhost:5432/tracker_db",
                        "tracker",
                        "tracker_123")
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .schemas("public")
                .load();

        // Print the pending migrations
        MigrationInfoService migrationInfoService = flyway.info();
        System.out.println("Pending migrations:");
        for (MigrationInfo info : migrationInfoService.pending()) {
            System.out.println(info.getVersion() + " - " + info.getDescription());
        }

        // Run the migrations
        System.out.println("Running migrations...");
        var migrateResult = flyway.migrate();
        System.out.println("Successfully applied " + migrateResult.migrationsExecuted + " migrations.");
    }
}