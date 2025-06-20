package com.jc.job.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ScraperManagerService {

	private final List<ScraperService> scrapers;

	public ScraperManagerService(List<ScraperService> scrapers) {
		this.scrapers = scrapers;
	}

	public void scrapeAll() {
		for (ScraperService scraper : scrapers) {
			scraper.scrapeAndSave();
		}
	}
}
