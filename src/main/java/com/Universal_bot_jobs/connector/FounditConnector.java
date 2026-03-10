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
public class FounditConnector implements JobConnector {

    @Override
    public String getSiteName() {
        return "foundit";
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

            String url = "https://www.foundit.in/srp/results?query="
                    + keyword.replace(" ", "%20")
                    + "&locations="
                    + location.replace(" ", "%20");

            System.out.println("Opening Foundit: " + url);

            driver.get(url);

            Thread.sleep(6000);

            // Scroll to load jobs
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(4000);

            List<WebElement> jobLinks = driver.findElements(By.cssSelector("a[href*='/job/']"));

            System.out.println("Jobs found: " + jobLinks.size());

            for (WebElement linkElement : jobLinks) {

                try {

                    String link = linkElement.getAttribute("href");
                    String title = linkElement.getText();

                    if (title == null || title.isEmpty()) continue;

                    Job job = Job.builder()
                            .title(title)
                            .company("N/A")
                            .location(location)
                            .jobUrl(link)
                            .applyLink(link)
                            .description("N/A")
                            .source("foundit")
                            .build();

                    jobs.add(job);

                    System.out.println("Saved job: " + title);

                } catch (Exception ignored) {}
            }

        } catch (Exception e) {

            System.out.println("Foundit scraping error: " + e.getMessage());

        } finally {

            driver.quit();
        }

        return jobs;
    }
}