package com.Universal_bot_jobs.service;


import com.Universal_bot_jobs.entity.Job;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${job.kafka.topic}", groupId = "job-consumer-group")
    public void consume(String message) {

        try {
            Job job = objectMapper.readValue(message, Job.class);

            System.out.println(" Received Job from Kafka:");
            System.out.println("Title: " + job.getTitle());
            System.out.println("Company: " + job.getCompany());

        } catch (Exception e) {
            System.out.println("Error consuming message: " + e.getMessage());
        }
    }
}
