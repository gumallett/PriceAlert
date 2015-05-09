package com.pricealert.scraping;

import com.pricealert.scraping.jsoup.AmazonScraper;
import com.pricealert.scraping.jsoup.AmazonScraperResult;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AmazonScraperTest {

    @Test
    public void testGetPrice() throws Exception {
        AmazonScraperResult result = AmazonScraper.scrape("http://www.amazon.com/Raymond-Weil-4830-PC5-05658-Gold-Tone-Stainless/dp/B00GCQXT04");
        assertNotNull(result);
        assertNotNull(result.getPrice());
    }

    @Test
    public void testGetImgSrc() throws Exception {
        AmazonScraperResult result = AmazonScraper.scrape("http://www.amazon.com/Raymond-Weil-4830-PC5-05658-Gold-Tone-Stainless/dp/B00GCQXT04");
        assertNotNull(result);
        assertNotNull(result.getImageThumbnail());
    }
}
