package com.Universal_bot_jobs.repository;

import com.Universal_bot_jobs.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    boolean existsByTitleAndCompanyAndLocation(
            String title,
            String company,
            String location
    );
    List<Job> findByLocationIgnoreCase(String location);

    List<Job> findBySourceIgnoreCase(String source);

    List<Job> findByLocationIgnoreCaseAndSourceIgnoreCase(
            String location,
            String source
    );
}
