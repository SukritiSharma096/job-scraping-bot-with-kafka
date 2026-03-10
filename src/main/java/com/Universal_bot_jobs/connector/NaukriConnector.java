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
public class NaukriConnector implements JobConnector {

    @Override
    public String getSiteName() {
        return "naukri";
    }

    @Override
    public List<Job> scrape(String keyword, String location) {

        List<Job> jobs = new ArrayList<>();

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);

        try {

            String url = "https://www.naukri.com/"
                    + keyword.replace(" ", "-").toLowerCase()
                    + "-jobs-in-"
                    + location.replace(" ", "-").toLowerCase();

            System.out.println("Opening Naukri: " + url);

            driver.get(url);

            Thread.sleep(7000);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            for (int i = 0; i < 5; i++) {
                js.executeScript("window.scrollBy(0,800)");
                Thread.sleep(2000);
            }

            List<WebElement> jobCards =
                    driver.findElements(By.cssSelector("div.srp-jobtuple-wrapper"));

            System.out.println("Naukri jobs found: " + jobCards.size());

            String mainWindow = driver.getWindowHandle();

            for (WebElement card : jobCards) {

                try {

                    String title = getSafeText(card, "a.title");
                    String company = getSafeText(card, "a.comp-name");
                    String locationText = getSafeText(card, "span.locWdth");

                    String link = getSafeAttr(card, "a.title", "href");

                    if (link == null || link.isEmpty()) {
                        continue;
                    }

                    String description = "N/A";

                    try {

                        driver.switchTo().newWindow(WindowType.TAB);
                        driver.get(link);

                        Thread.sleep(5000);

                        try {

                            WebElement descElement = driver.findElement(
                                    By.xpath("//div[contains(@class,'dang-inner-html')]")
                            );

                            description = descElement.getText();

                        } catch (Exception e) {
                            description = "N/A";
                        }

                        driver.close();

                        driver.switchTo().window(mainWindow);

                    } catch (Exception e) {
                        description = "N/A";
                    }
                    Job job = Job.builder()
                            .title(title)
                            .company(company)
                            .location(locationText)
                            .jobUrl(link)
                            .applyLink(link)
                            .description(description)
                            .source("naukri")
                            .build();

                    jobs.add(job);

                    System.out.println("Saved job: " + title);

                } catch (Exception ignored) {}
            }

        } catch (Exception e) {

            System.out.println("Naukri scraping error: " + e.getMessage());

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

    private String getSafeAttr(WebElement element, String css, String attr) {
        try {
            return element.findElement(By.cssSelector(css)).getAttribute(attr);
        } catch (Exception e) {
            return null;
        }
    }
}