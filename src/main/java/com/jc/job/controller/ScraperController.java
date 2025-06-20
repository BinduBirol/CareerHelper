package com.jc.job.controller;


import com.jc.job.service.ScraperManagerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scrape")
public class ScraperController {

    private final ScraperManagerService scraperManagerService;

    public ScraperController(ScraperManagerService scraperManagerService) {
        this.scraperManagerService = scraperManagerService;
    }

    @GetMapping("/run")
    public String scrape() {
        scraperManagerService.scrapeAll();
        return "Scraping started!";
    }
}
