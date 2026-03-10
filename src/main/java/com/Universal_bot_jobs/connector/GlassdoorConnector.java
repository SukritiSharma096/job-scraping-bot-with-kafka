package com.Universal_bot_jobs.connector;

import com.Universal_bot_jobs.entity.Job;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GlassdoorConnector implements JobConnector {

    @Override
    public String getSiteName() {
        return "glassdoor";
    }

    @Override
    public List<Job> scrape(String keyword, String location) {

        List<Job> jobs = new ArrayList<>();

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);

        try {

            String url = "https://www.glassdoor.co.in/Job/jobs.htm?sc.keyword="
                    + keyword.replace(" ", "%20")
                    + "&locKeyword="
                    + location.replace(" ", "%20");

            System.out.println("Opening Glassdoor: " + url);

            driver.get(url);

            Thread.sleep(8000);

            List<WebElement> jobCards = driver.findElements(By.cssSelector("li[data-test='jobListing']"));

            System.out.println("Glassdoor jobs found: " + jobCards.size());

            for (WebElement card : jobCards) {

                try {

                    WebElement titleElement = card.findElement(By.cssSelector("a.jobLink"));
                    String title = titleElement.getText().trim();
                    String link = titleElement.getAttribute("href");

                    String company = getSafeText(card, ".employer-name");
                    String locationText = getSafeText(card, ".location");
                    String description = getSafeText(card, "div.job-snippet");

                    Job job = Job.builder()
                            .title(title)
                            .company(company)
                            .location(locationText)
                            .jobUrl(link)
                            .applyLink(link)
                            .description(description)
                            .source("glassdoor")
                            .build();

                    jobs.add(job);

                    System.out.println("Saved job: " + title);

                } catch (Exception e) {
                    System.out.println("Skipping broken job");
                }
            }

        } catch (Exception e) {

            System.out.println("Glassdoor scraping error: " + e.getMessage());

        } finally {

            driver.quit();
        }

        return jobs;
    }

    private String getSafeText(WebElement element, String css) {
        try {
            return element.findElement(By.cssSelector(css)).getText();
        } catch (Exception e) {
            return "N/A";
        }
    }
}