package com.pricealert.app.service;

import com.pricealert.app.service.event.ImageEvent;
import com.pricealert.app.service.event.PriceEvent;
import com.pricealert.data.RecentPricesDb;
import com.pricealert.data.dto.ProductInfoDto;
import com.pricealert.data.model.ProductImg;
import com.pricealert.data.model.ProductPriceHistory;
import com.pricealert.scraping.jsoup.AmazonScraper;
import com.pricealert.scraping.jsoup.AmazonScraperResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.DecimalFormat;

public class ProductUpdater implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ProductUpdater.class);
    private final ProductInfoDto product;
    private final ScraperService context;

    public ProductUpdater(ProductInfoDto product, ScraperService context) {
        this.product = product;
        this.context = context;
    }

    @Override
    public void run() {
        if(product.getUrl() == null || product.getUrl().isEmpty()) {
            return;
        }

        try {
            AmazonScraperResult scraperResult = AmazonScraper.scrape(product.getUrl());
            String price = scraperResult.getPrice();
            String imgUrl = scraperResult.getImageThumbnail();
            RecentPricesDb db = new RecentPricesDb(context);

            if(price != null) {
                LOG.info("Price found: {}", price);
                updatePrice(price, db);
            }

            if(imgUrl != null) {
                LOG.info("Found image for {}, {}.", product.getProductId(), imgUrl);
                updateImage(imgUrl, db);
            }
        }
        catch(InterruptedIOException iioe) {
            // swallow
        }
        catch(Exception e) {
            LOG.error("Error updating product: {}, retrying in 60 seconds", product.getProductId(), e);
            context.quickRetry(product, this, 60);
        }
    }

    private boolean shouldNotify(Double price) {
        Integer targetPercent = product.getTargets().getTargetPercent();
        Double targetValue = product.getTargets().getTargetValue();

        if(targetValue != null && targetPercent != null && targetPercent > 0) {
            return (targetValue * (double) targetPercent / 100.0) > price;
        }
        else if(targetValue != null) {
            return targetValue > price;
        }

        return false;
    }

    private void updatePrice(String price, RecentPricesDb db) {
        try {
            ProductPriceHistory priceHistory = new ProductPriceHistory();
            priceHistory.setProductId(product.getProductId());
            priceHistory.setDate(new Date(System.currentTimeMillis()));

            DecimalFormat format = new DecimalFormat("$#,##0.00");
            priceHistory.setPrice(format.parse(price).doubleValue());

            db.newHistory(priceHistory);

            context.notifyListeners(new PriceEvent(priceHistory.getPrice(), product.getProductId()));

            if(shouldNotify(priceHistory.getPrice())) {
                LOG.info("New low price detected!");
                context.sendNotification(product, price);
            }
        }
        catch(Exception e) {
            LOG.error("Failed to save price: ", e);
        }
    }

    private void updateImage(String imgUrl, RecentPricesDb db) {
        byte[] img = null;
        try {
            img = downloadImg(imgUrl);
            context.notifyListeners(new ImageEvent(product.getProductId(), img));
        }
        catch(Exception e) {
            LOG.error("Failed to download image: ", e);
        }

        if(img != null) {
            ProductImg productImg = new ProductImg();
            productImg.setProduct_id(product.getProductId());
            productImg.setImgUrl(imgUrl);
            productImg.setImg(img);
            db.saveProductImage(productImg);
        }
    }

    private static byte[] downloadImg(String imgUrl) throws IOException {
        LOG.info("Downloading image from: {}", imgUrl);
        URL url = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            int responseCode = conn.getResponseCode();
            if(responseCode == 200) {
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int read;
                while((read = bis.read()) != -1) {
                    baos.write(read);
                }

                return baos.toByteArray();
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
