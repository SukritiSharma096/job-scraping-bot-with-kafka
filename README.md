# Kafka Job Scraping System

## 📌 Project Overview
This project is a Kafka-based job scraping system built using Spring Boot. It collects job data from multiple platforms and processes it using a producer-consumer architecture.

## 🚀 Features
- Scrapes job data from multiple platforms (e.g., Naukri, Indeed)
- Uses Apache Kafka for real-time data streaming
- Producer-Consumer architecture for efficient processing
- Scalable and high-performance system
- REST APIs for managing job data

## 🛠️ Tech Stack
- Java
- Spring Boot
- Apache Kafka
- REST APIs
- MySQL 

## ⚙️ How It Works
1. Job data is scraped from external platforms
2. Producer sends data to Kafka topics
3. Consumer reads and processes the data
4. Data is stored in the database

## ▶️ How to Run
1. Clone the repository
2. Start Kafka and Zookeeper locally
3. Run the Spring Boot application
4. Test APIs using Postman

## 📷 API Example
http://localhost:8081/api/jobs/scrape?site=naukri&keyword=java(for naukri.com)
http://localhost:8081/api/jobs/scrape?site=indeed&keyword=java(for indeed.com)

## 📌 Future Improvements
- Add more job platforms
- Improve error handling
- Add frontend UI (Angular)

## 👩‍💻 Author
Sukriti Sharma
