package com.pricealert.scraping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ScraperTest {

    private static final Logger LOG = LoggerFactory.getLogger(ScraperTest.class);

    @Test
    public void canDownloadAndParseHtmlPage() throws IOException {
        Scraper scraper = new Scraper("http://www.amazon.com/XFX-Double-947MHz-Graphics-R9290AEDFD/dp/B00HHIPM5Q");

        long start = System.currentTimeMillis();
        LOG.info("Starting..{}", 0);
        Scraper.ScraperResponse response = scraper.connect();
        LOG.info("parsed response, {}", (System.currentTimeMillis()-start));
        assertEquals("$279.99", response.getPrice());
        LOG.info("got price, {}", (System.currentTimeMillis()-start));
        assertEquals("XFX Double D R9 290 947MHz 4GB DDR5 DP HDMI 2XDVI Graphics Cards R9290AEDFD", response.getProductTitle());
        LOG.info("got title, {}", (System.currentTimeMillis()-start));
    }
}
