package com.Universal_bot_jobs.service;

import com.Universal_bot_jobs.entity.Job;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(JobConsumerService.class);

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${job.kafka.topic}", groupId = "job-consumer-group")
    public void consumeJob(ConsumerRecord<String, String> record) {
        String message = record.value();
        try {
            Job job = objectMapper.readValue(message, Job.class);
     
            logger.info("Consumed Job: {} | Title: {} | Company: {} | Location: {}",
                    job.getId(), job.getTitle(), job.getCompany(), job.getLocation());
        } catch (Exception e) {
            logger.error("Failed to parse Job message: {} | Error: {}", message, e.getMessage(), e);
        }
    }
}