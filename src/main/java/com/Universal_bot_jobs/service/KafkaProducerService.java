package com.Universal_bot_jobs.service;

import com.Universal_bot_jobs.entity.Job;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${job.kafka.topic}")
    private String topic;

    public void sendJob(Job job) {
        try {
            String message = objectMapper.writeValueAsString(job);
            kafkaTemplate.send(topic, message);
            logger.info("Job sent to Kafka: {} | Company: {}", job.getTitle(), job.getCompany());
        } catch (Exception e) {
            logger.error("Kafka send error for job: {} | Error: {}", job.getTitle(), e.getMessage(), e);
        }
    }
}