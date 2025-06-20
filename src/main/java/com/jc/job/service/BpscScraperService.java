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
import java.util.Map;
import java.util.Optional;

@Service
public class BpscScraperService implements ScraperService {

	private final JobCircularRepo repo;

	public BpscScraperService(JobCircularRepo repo) {
		this.repo = repo;
	}

	@Override
	public void scrapeAndSave() {
		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new"); // headless mode
		options.addArguments("--disable-gpu");
		options.addArguments("--window-size=1920,1080");

		WebDriver driver = new ChromeDriver(options);

		try {
			// Map of URLs and their categories
			Map<String, String> examPages = Map.of(
					"https://bpsc.gov.bd/site/view/psc_exam/BCS_Examination/বিসিএস-পরীক্ষা", "বিসিএস-পরীক্ষা",
					"https://bpsc.gov.bd/site/view/psc_exam/Senior_Scale_Examination/সিনিয়র-স্কেল-পরীক্ষা",
					"সিনিয়র-স্কেল-পরীক্ষা"
			// Add more pages and categories as needed
			);

			for (Map.Entry<String, String> entry : examPages.entrySet()) {
				String url = entry.getKey();
				String category = entry.getValue();

				System.out.println("📄 Visiting: " + url);
				driver.get(url);
				Thread.sleep(4000); // wait for page to load JS content

				List<WebElement> pdfLinks = driver.findElements(By.xpath("//a[contains(@href, '.pdf')]"));
				System.out.println("🔍 Found " + pdfLinks.size() + " PDFs on category: " + category);

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
						circular.setCategory(category); // save category here
						circular.setLink(href);
						circular.setSourceId(sourceId);
						circular.setPublishDate(LocalDate.now()); // or extract real date if possible

						repo.save(circular);
						System.out.println("✅ Saved: " + title + " (" + category + ")");
					} else {
						System.out.println("⏩ Skipped (duplicate): " + title);
					}
				}
			}

		} catch (Exception e) {
			System.err.println("❌ Scraping failed: " + e.getMessage());
		} finally {
			driver.quit();
		}
	}

}
