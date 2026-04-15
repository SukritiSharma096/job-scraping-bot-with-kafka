package com.Universal_bot_jobs.controller;

import com.Universal_bot_jobs.dto.JobResponse;
import com.Universal_bot_jobs.entity.Job;
import com.Universal_bot_jobs.service.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Job APIs", description = "Operations related to job scraping")
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService service;

    @GetMapping("/scrape")
    public List<JobResponse> scrape(
            @RequestParam String site,
            @RequestParam String keyword
    ) {

        List<Job> jobs =
                service.scrapeAndSave(site,keyword);

        return jobs.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .jobUrl(job.getJobUrl())
                .applyLink(job.getApplyLink())
                .description(job.getDescription())
                .source(job.getSource())
                .build();
    }

    @GetMapping("getAll")
    public List<JobResponse> getAllJobs() {

        return service.getAllJobs().stream()
                .map(job -> JobResponse.builder()
                        .title(job.getTitle())
                        .company(job.getCompany())
                        .location(job.getLocation())
                        .jobUrl(job.getJobUrl())
                        .applyLink(job.getApplyLink())
                        .description(job.getDescription())
                        .source(job.getSource())
                        .build())
                .toList();
    }


//    @GetMapping("/search")
//    public List<JobResponse> searchJobs(
//            @RequestParam(required = false) String location,
//            @RequestParam(required = false) String source
//    ) {
//
//        return service.searchJobs(location, source)
//                .stream()
//                .map(job -> JobResponse.builder()
//                        .title(job.getTitle())
//                        .company(job.getCompany())
//                        .location(job.getLocation())
//                        .jobUrl(job.getJobUrl())
//                        .applyLink(job.getApplyLink())
//                        .description(job.getDescription())
//                        .source(job.getSource())
//                        .build())
//                .toList();
//    }
}
