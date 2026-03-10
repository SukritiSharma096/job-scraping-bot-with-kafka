package com.Universal_bot_jobs.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobResponse {

    private String title;
    private String company;
    private String location;
    private String jobUrl;
    private String applyLink;
    private String description;
    private String source;
}
