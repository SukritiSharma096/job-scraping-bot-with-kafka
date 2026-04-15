package com.Universal_bot_jobs.connector;

import com.Universal_bot_jobs.entity.Job;
import java.util.List;

public interface JobConnector {

    String getSiteName();

    List<Job> scrape(String url);
}