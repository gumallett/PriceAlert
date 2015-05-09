package com.pricealert.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pricealert.data.RecentPricesDb;
import com.pricealert.data.dto.ProductInfoDto;
import com.pricealert.data.model.ProductImg;
import com.pricealert.scraping.yql.YQLTemplate;
import com.pricealert.scraping.yql.model.YQLCSSQuery;
import com.pricealert.scraping.yql.model.YQLResponse;
import com.pricealert.scraping.yql.model.YQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class ImageUpdater implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ImageUpdater.class);

    private final ProductInfoDto productInfo;
    private final ScraperService scraperService;

    public ImageUpdater(ProductInfoDto productInfo, ScraperService context) {
        this.productInfo = productInfo;
        this.scraperService = context;
    }

    @Override
    public void run() {
        for(int i = 0; i < 5; i++) {
            if(Thread.currentThread().isInterrupted()) {
                return;
            }

            try {
                final Long theProductId = productInfo.getProductId();
                YQLTemplate template = scraperService.getYqlTemplate();
                YQLCSSQuery query = new YQLCSSQuery();
                query.setCssSelector(Arrays.asList("#thumbs-image img", "#imageBlock img"));
                query.setUrl(productInfo.getUrl());
                YQLResponse response = template.cssQuery(query);

                YQueryResponse.YQueryResultsContainer resultsContainer = response.getQuery().getResults();
                if(resultsContainer != null) {
                    JsonNode imgNode = resultsContainer.getResults().findValue("img");

                    if(imgNode != null && imgNode.isArray()) {
                        String imgUrl = imgNode.get(0).get("src").asText();
                        LOG.info("Found image for {}, {}.", theProductId, imgUrl);
                        byte[] img = null;
                        try {
                            img = downloadImg(imgUrl);
                        }
                        catch(Exception e) {
                            LOG.error("Failed to download image: ", e);
                        }

                        if(img != null) {
                            RecentPricesDb db = new RecentPricesDb(scraperService);
                            ProductImg productImg = new ProductImg();
                            productImg.setProduct_id(theProductId);
                            productImg.setImgUrl(imgUrl);
                            productImg.setImg(img);
                            db.saveProductImage(productImg);
                        }

                        return;
                    }
                }
            }
            catch(Exception e) {
                LOG.error(e.getMessage(), e);
            }

            LOG.info("Images not found for product {}, retrying...", productInfo.getName());

            try {
                Thread.sleep(5000);
            }
            catch(InterruptedException e) {
                return;
            }
        }

        LOG.warn("No images found in 5 attempts, retrying in 60 seconds.");
        scraperService.quickRetry(productInfo, this, 60);
    }

    private static byte[] downloadImg(String imgUrl) throws IOException {
        LOG.info("Downloading image from: {}", imgUrl);
        URL url = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            int responseCode = conn.getResponseCode();
            if(responseCode == 200) {
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Integer contentLen = Integer.valueOf(conn.getHeaderField("Content-Length"));

                byte[] buffer = new byte[contentLen];
                int read = bis.read(buffer);

                if(read != contentLen) {
                    LOG.warn("Did not get whole image? read {}, expected: {}", read, contentLen);
                }

                return buffer;
            }
            else {
                LOG.error("Response not OK: {}", responseCode);
            }
        }
        finally {
            conn.disconnect();
        }

        return null;
    }
}
