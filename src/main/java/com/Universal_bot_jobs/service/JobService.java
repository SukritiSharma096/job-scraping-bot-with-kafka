package com.Universal_bot_jobs.service;

import com.Universal_bot_jobs.entity.Job;
import com.Universal_bot_jobs.factory.JobConnectorFactory;
import com.Universal_bot_jobs.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobConnectorFactory factory;
    private final JobRepository repository;
    private final KafkaProducerService kafkaProducerService;

    public List<Job> scrapeAndSave(String site, String keyword, String location) {

        List<Job> scraped = factory
                .getConnector(site)
                .scrape(keyword, location);

        List<Job> newJobs = scraped.stream()
                .filter(job -> !repository.existsByTitleAndCompanyAndLocation(
                        job.getTitle(),
                        job.getCompany(),
                        job.getLocation()
                ))
                .toList();

        List<Job> savedJobs = repository.saveAll(newJobs);

        savedJobs.forEach(kafkaProducerService::sendJob);

        return savedJobs;
    }

    public List<Job> getAllJobs() {
        return repository.findAll();
    }

    public List<Job> searchJobs(String location, String source) {

        if (location != null && source != null) {
            return repository.findByLocationIgnoreCaseAndSourceIgnoreCase(location, source);
        }

        if (location != null) {
            return repository.findByLocationIgnoreCase(location);
        }

        if (source != null) {
            return repository.findBySourceIgnoreCase(source);
        }

        return repository.findAll();
    }
}
