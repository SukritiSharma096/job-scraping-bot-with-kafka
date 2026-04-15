package com.Universal_bot_jobs.service;

import com.Universal_bot_jobs.config.JobSitesConfig;
import com.Universal_bot_jobs.entity.Job;
import com.Universal_bot_jobs.factory.JobConnectorFactory;
import com.Universal_bot_jobs.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobConnectorFactory factory;
    private final JobRepository repository;
    private final KafkaProducerService kafkaProducerService;
    private final JobSitesConfig jobSitesConfig;

    public List<Job> scrapeAndSave(String site,String keyword){

        List<String> urls =
                jobSitesConfig.getSites()
                        .get(site)
                        .getUrls();

        List<Job> allJobs = new ArrayList<>();

        for(String url : urls){

            String finalUrl =
                    url.replace("{keyword}",
                            keyword.toLowerCase().replace(" ","-"));

            List<Job> scraped =
                    factory.getConnector(site).scrape(finalUrl);

            allJobs.addAll(scraped);
        }

        List<Job> newJobs = allJobs.stream()
                .filter(job -> !repository.existsByTitleAndCompanyAndLocation(
                        job.getTitle(),
                        job.getCompany(),
                        job.getLocation()))
                .toList();

        List<Job> savedJobs = repository.saveAll(newJobs);

        savedJobs.forEach(kafkaProducerService::sendJob);

        return savedJobs;
    }

    public List<Job> getAllJobs(){
        return repository.findAll();
    }
}