package com.tracker.dashboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service for handling dashboards.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class DashboardService {

    /**
     * Gets dashboard data.
     *
     * @param userId the user ID
     * @param dashboardId the dashboard ID
     * @return a string representation of the dashboard data
     */
    public String getDashboardData(String userId, String dashboardId) {
        log.info("Getting dashboard data for user {}, dashboard {}", userId, dashboardId);
        // Implementation will be added later
        return "Dashboard data for " + dashboardId;
    }
}