package com.Universal_bot_jobs.connector;

import com.Universal_bot_jobs.entity.Job;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class NaukriConnector implements JobConnector {

    @Override
    public String getSiteName() {
        return "naukri";
    }

    @Override
    public List<Job> scrape(String url) {

        List<Job> jobs = new ArrayList<>();

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        //options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        WebDriver driver = new ChromeDriver(options);

        try {

            driver.get(url);

            Thread.sleep(8000);

            List<WebElement> jobCards =
                    driver.findElements(By.cssSelector("div.srp-jobtuple-wrapper"));
            System.out.println("Jobs found: " + jobCards.size());

            for (WebElement card : jobCards) {

                try {

                    String title = card
                            .findElement(By.cssSelector("a.title"))
                            .getText();

                    String company = getSafe(card,"a.comp-name");

                    String location = getSafe(card,"span.locWdth");

                    String link = card
                            .findElement(By.cssSelector("a.title"))
                            .getAttribute("href");

                    String description = getSafe(card,"span.job-desc");

                    Job job = Job.builder()
                            .title(title)
                            .company(company)
                            .location(location)
                            .jobUrl(link)
                            .applyLink(link)
                            .description(description)
                            .source("naukri")
                            .build();

                    jobs.add(job);

                } catch (Exception ignored) {}
            }

        } catch (Exception e) {

            System.out.println("Naukri scrape error " + e.getMessage());

        } finally {

            driver.quit();
        }

        return jobs;
    }

    private String getSafe(WebElement parent,String selector){

        try{
            return parent.findElement(By.cssSelector(selector)).getText();
        }
        catch(Exception e){
            return "";
        }
    }
}