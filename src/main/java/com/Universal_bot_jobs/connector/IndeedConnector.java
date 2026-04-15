package com.Universal_bot_jobs.connector;

import com.Universal_bot_jobs.entity.Job;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
public class IndeedConnector implements JobConnector {

    @Override
    public String getSiteName() {
        return "indeed";
    }

    @Override
    public List<Job> scrape(String baseUrl) {

        List<Job> jobs = new ArrayList<>();
        Set<String> visitedLinks = new HashSet<>();

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--start-maximized");

        WebDriver driver = new ChromeDriver(options);

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));

        try {

            int maxPages = 3;

            for(int page=0; page<maxPages; page++) {

                String url = baseUrl + "&start=" + (page*10);
                System.out.println("Opening page: " + url);

                driver.get(url);

                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div.job_seen_beacon")));

                List<WebElement> jobCards =
                        driver.findElements(By.cssSelector("div.job_seen_beacon"));

                System.out.println("Jobs found: " + jobCards.size());

                for (WebElement card : jobCards) {

                    try {

                        WebElement titleElement =
                                card.findElement(By.cssSelector("a.jcs-JobTitle"));

                        String title = titleElement.getText();
                        String link = titleElement.getAttribute("href");

                        if(link == null || visitedLinks.contains(link)) continue;
                        if(link.contains("pagead")) continue;

                        visitedLinks.add(link);

                        String company =
                                getSafe(card,"span[data-testid='company-name']");

                        String location =
                                getSafe(card,"div[data-testid='text-location']");

                        String description =
                                getSafe(card,"div.job-snippet");

                        if(description == null || description.isBlank()) {
                            description = "Job opportunity for " + title +
                                    " at " + company +
                                    ". Click link for full details.";
                        }

                        Job job = Job.builder()
                                .title(title)
                                .company(company)
                                .location(location)
                                .description(description)
                                .jobUrl(link)
                                .applyLink(link)
                                .source("indeed")
                                .build();

                        jobs.add(job);

                    } catch (Exception ignored) {}
                }
            }

        } catch (Exception e) {

            System.out.println("Indeed scraping error " + e.getMessage());

        } finally {

            driver.quit();
        }

        return jobs;
    }

    private String getSafe(WebElement parent,String selector){
        try{
            return parent.findElement(By.cssSelector(selector)).getText();
        }catch(Exception e){
            return "";
        }
    }
}