package com.Universal_bot_jobs.connector;

import com.Universal_bot_jobs.entity.Job;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
    public List<Job> scrape(String keyword, String location) {

        List<Job> jobs = new ArrayList<>();
        Set<String> visitedLinks = new HashSet<>();

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-notifications");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {

            for (int page = 0; page < 3; page++) {

                String url = "https://in.indeed.com/jobs?q="
                        + keyword.replace(" ", "+")
                        + "&l="
                        + location.replace(" ", "+")
                        + "&start=" + (page * 10);

                System.out.println("Opening page: " + url);

                driver.get(url);

                Thread.sleep(3000);

                dynamicScroll(driver);

                List<WebElement> jobCards =
                        driver.findElements(By.cssSelector("div.job_seen_beacon"));

                System.out.println("Jobs found: " + jobCards.size());

                for (WebElement card : jobCards) {

                    try {

                        WebElement titleElement = safeFind(card, "a.jcs-JobTitle");

                        if (titleElement == null || titleElement.getText().trim().isEmpty()) {
                            continue;
                        }

                        String title = titleElement.getText().trim();
                        String jobLink = titleElement.getAttribute("href");

                        if (jobLink.contains("pagead")) {
                            continue; 
                        }

                        if (jobLink == null || visitedLinks.contains(jobLink)) {
                            continue;
                        }

                        visitedLinks.add(jobLink);

                        String company = firstNonBlank(
                                getSafeText(card, "span[data-testid='company-name']"),
                                getSafeText(card, ".companyName")
                        );

                        String loc = firstNonBlank(
                                getSafeText(card, "div[data-testid='text-location']"),
                                getSafeText(card, ".companyLocation"),
                                getSafeText(card, ".locationsContainer"),
                                location
                        );
                        ((JavascriptExecutor) driver).executeScript(
                                "window.open(arguments[0])", jobLink);

                        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());

                        driver.switchTo().window(tabs.get(1));

                        Thread.sleep(2000);

                        String description = "N/A";

                        try {
                            WebElement desc = wait.until(
                                    ExpectedConditions.presenceOfElementLocated(
                                            By.cssSelector("#jobDescriptionText")));

                            description = desc.getText();

                        } catch (Exception e) {
                            try {

                                WebElement desc = driver.findElement(
                                        By.cssSelector(".jobsearch-jobDescriptionText"));

                                description = desc.getText();

                            } catch (Exception ignored) {}

                        }
                        driver.close();
                        driver.switchTo().window(tabs.get(0));

                        jobs.add(
                                Job.builder()
                                        .title(title)
                                        .company(company)
                                        .location(loc)
                                        .description(description)
                                        .jobUrl(jobLink)
                                        .applyLink(jobLink)
                                        .source("indeed")
                                        .build()
                        );

                        System.out.println("Saved job: " + title);

                    } catch (Exception e) {

                        System.out.println("Skipping broken card...");
                    }
                }
            }

        } catch (Exception e) {

            System.out.println("Error scraping Indeed: " + e.getMessage());

        } finally {

            driver.quit();
        }

        return jobs;
    }

    private void dynamicScroll(WebDriver driver) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (int i = 0; i < 3; i++) {

            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

            try {
                Thread.sleep(1500);
            } catch (Exception ignored) {}
        }
    }

    private WebElement safeFind(WebElement parent, String selector) {

        try {
            return parent.findElement(By.cssSelector(selector));
        } catch (Exception e) {
            return null;
        }
    }

    private String getSafeText(WebElement parent, String selector) {

        try {
            return parent.findElement(By.cssSelector(selector)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private String firstNonBlank(String... values) {

        for (String v : values) {

            if (v != null && !v.isBlank()) {
                return v;
            }
        }

        return "N/A";
    }
}