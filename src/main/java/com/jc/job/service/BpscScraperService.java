package com.jc.job.service;

import com.jc.job.dto.JobCircularEntity;
import com.jc.job.repo.JobCircularRepo;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BpscScraperService implements ScraperService {

	private final JobCircularRepo repo;

	public BpscScraperService(JobCircularRepo repo) {
		this.repo = repo;
	}

	@Override
	public void scrapeAndSave() {
		WebDriverManager.chromedriver().setup(); // auto-download chromedriver

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new"); // headless mode (no GUI)
		options.addArguments("--disable-gpu");
		options.addArguments("--window-size=1920,1080");

		WebDriver driver = new ChromeDriver(options);

		try {
			String url = "https://bpsc.gov.bd/site/view/psc_exam/BCS_Examination/বিসিএস-পরীক্ষা";
			driver.get(url);

			// Wait for page to fully load (if needed)
			Thread.sleep(5000); // replace with WebDriverWait for production use

			List<WebElement> pdfLinks = driver.findElements(By.xpath("//a[contains(@href, '.pdf')]"));

			System.out.println("🔍 Found PDF links: " + pdfLinks.size());

			for (WebElement link : pdfLinks) {
				String title = link.getText().trim();
				String href = link.getAttribute("href");

				if (href == null || href.isEmpty())
					continue;

				String sourceId = String.valueOf(href.hashCode());

				Optional<JobCircularEntity> existing = repo.findBySourceId(sourceId);
				if (existing.isEmpty()) {
					JobCircularEntity circular = new JobCircularEntity();
					circular.setTitle(title);
					circular.setDepartment("BPSC");
					circular.setLink(href);
					circular.setSourceId(sourceId);
					circular.setPublishDate(LocalDate.now());

					repo.save(circular);
					System.out.println("✅ Saved: " + title);
				} else {
					System.out.println("⏩ Skipped (already exists): " + title);
				}
			}

		} catch (Exception e) {
			System.err.println("❌ Scraping failed: " + e.getMessage());
		} finally {
			driver.quit();
		}
	}
}
