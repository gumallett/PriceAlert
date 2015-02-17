package com.pricealert.scraping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ScraperTest {

    @Test
    public void canDownloadAndParseHtmlPage() {
        Scraper scraper = new Scraper("http://www.amazon.com/XFX-Double-947MHz-Graphics-R9290AEDFD/dp/B00HHIPM5Q/ref=sr_1_4?ie=UTF8&qid=1424101836&sr=8-4&keywords=R290");

        assertEquals(scraper.getPrice(), "$299.99");
    }
}
