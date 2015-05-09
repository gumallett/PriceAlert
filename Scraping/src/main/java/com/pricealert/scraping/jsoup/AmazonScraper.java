package com.pricealert.scraping.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class AmazonScraper {

    public static AmazonScraperResult scrape(String url) throws Exception {
        Document page = Jsoup.connect(url).get();
        return new AmazonScraperResult(page);
    }
}
