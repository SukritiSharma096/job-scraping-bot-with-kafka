//package com.Universal_bot_jobs.connector;
//
//import com.Universal_bot_jobs.entity.Job;
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class GlassdoorConnector implements JobConnector {
//
//    @Override
//    public String getSiteName() {
//        return "glassdoor";
//    }
//
//    @Override
//    public List<Job> scrape(String keyword, String location) {
//
//        List<Job> jobs = new ArrayList<>();
//
//        WebDriverManager.chromedriver().setup();
//
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--start-maximized");
//        options.addArguments("--disable-notifications");
//
//        WebDriver driver = new ChromeDriver(options);
//
//        try {
//
//            String url = "https://www.glassdoor.co.in/Job/jobs.htm?sc.keyword="
//                    + keyword.replace(" ", "%20")
//                    + "&locKeyword="
//                    + location.replace(" ", "%20");
//
//            System.out.println("Opening Glassdoor: " + url);
//
//            driver.get(url);
//
//            Thread.sleep(8000);
//
//            List<WebElement> jobCards = driver.findElements(By.cssSelector("li[data-test='jobListing']"));
//
//            System.out.println("Glassdoor jobs found: " + jobCards.size());
//
//            for (WebElement card : jobCards) {
//
//                try {
//
//                    String title = "";
//                    String company = "";
//                    String locationText = "";
//                    String jobUrl = "";
//
//                    try {
//                        WebElement titleEl = card.findElement(By.cssSelector("a.JobCard_jobTitle__GLyJ1"));
//                        title = titleEl.getText();
//                        jobUrl = titleEl.getAttribute("href");
//                    } catch (Exception ignored) {}
//
//                    try {
//                        company = card.findElement(By.cssSelector("span.EmployerProfile_compactEmployerName__9MGcV")).getText();
//                    } catch (Exception ignored) {}
//
//                    try {
//                        locationText = card.findElement(By.cssSelector("div.JobCard_location__Ds1fM")).getText();
//                    } catch (Exception ignored) {}
//
//                    // LOCATION FILTER
//                    if (locationText != null &&
//                            !locationText.toLowerCase().contains(location.toLowerCase()) &&
//                            !locationText.toLowerCase().contains("india") &&
//                            !locationText.toLowerCase().contains("remote")) {
//                        continue;
//                    }
//
//                    // KEYWORD FILTER (SMART)
//                    String t = title.toLowerCase();
//
//                    if (!(t.contains(keyword.toLowerCase())
//                            || t.contains("frontend")
//                            || t.contains("javascript")
//                            || t.contains("ui"))) {
//                        continue;
//                    }
//
//                    Job job = new Job();
//
//                    job.setTitle(title);
//                    job.setCompany(company);
//                    job.setLocation(locationText);
//                    job.setJobUrl(jobUrl);
//                    job.setApplyLink(jobUrl);
//                    job.setDescription("");
//                    job.setSource("glassdoor");
//
//                    jobs.add(job);
//
//                    System.out.println("Saved job: " + title);
//
//                } catch (Exception e) {
//                    System.out.println("Skipping job");
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            driver.quit();
//        }
//
//        return jobs;
//    }
//}