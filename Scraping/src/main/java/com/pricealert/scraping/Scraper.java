package com.pricealert.scraping;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Scraper {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0";

    private final String url;

    public Scraper(String url) {
        this.url = url;
    }

    public ScraperResponse connect() throws IOException {
        Document html = getDocument(url);
        return new ScraperResponse(html);
    }

    private static Document getDocument(String url) throws IOException {
        return getConnection(url).get();
    }

    private static Connection getConnection(String url) {
        return Jsoup.connect(url).maxBodySize(0).userAgent(USER_AGENT);
    }

    public static class ScraperResponse {
        private Document html;

        public ScraperResponse(Document html) {
            this.html = html;
        }

        public String getPrice() {
            return html.select("#priceblock_ourprice").text();
        }

        public String getProductTitle() {
            return html.select("#productTitle").text();
        }
    }
}
