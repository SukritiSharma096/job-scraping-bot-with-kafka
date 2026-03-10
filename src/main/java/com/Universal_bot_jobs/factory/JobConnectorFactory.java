package com.Universal_bot_jobs.factory;

import com.Universal_bot_jobs.connector.JobConnector;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobConnectorFactory {

    private final List<JobConnector> connectors;

    public JobConnectorFactory(List<JobConnector> connectors) {
        this.connectors = connectors;
    }

    public JobConnector getConnector(String site) {

        return connectors.stream()
                .filter(c -> c.getSiteName().equalsIgnoreCase(site))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No connector found for " + site));
    }
}
