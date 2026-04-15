package com.Universal_bot_jobs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
public class JobSitesConfig {

    private Map<String, JobSite> sites;

    @PostConstruct
    public void load() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("job-sites.json");

        sites = mapper.readValue(
                is,
                mapper.getTypeFactory()
                        .constructMapType(Map.class, String.class, JobSite.class)
        );
    }

    @Getter
    @Setter
    public static class JobSite {

        private List<String> urls;   // multiple URLs
    }
}