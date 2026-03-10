package com.Universal_bot_jobs.dto;

import lombok.Data;

@Data
public class JobScrapeRequest {

    private String site;
    private String keyword;
    private String location;
}
