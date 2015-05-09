package com.pricealert.scraping.jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public final class AmazonScraperResult {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonScraperResult.class);
    private final Document document;

    public AmazonScraperResult(Document document) {
        this.document = document;
    }

    public String getPrice() {
        List<String> css = Arrays.asList("#priceblock_ourprice", "#priceblock_saleprice", "#buyingPriceValue", "#actualPriceValue");
        for(String cssQuery : css) {
            LOG.info("Checking {}", cssQuery);
            Elements elements = document.select(cssQuery);

            if(!elements.isEmpty()) {
                String price = elements.text();
                LOG.info("Found price: {}", price);
                return price;
            }
        }

        return null;
    }

    public String getImageThumbnail() {
        List<String> css = Arrays.asList("#thumbs-image img", "#imageBlock img");

        for(String cssQuery : css) {
            LOG.info("Checking {}", cssQuery);
            Elements elements = document.select(cssQuery);

            if(!elements.isEmpty()) {
                String imgSrc = elements.attr("src");
                LOG.info("Found image src: {}", imgSrc);
                return imgSrc;
            }
        }

        return null;
    }
}
