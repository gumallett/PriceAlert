package com.pricealert.app.service;

import com.pricealert.data.RecentPricesDb;
import com.pricealert.data.model.Product;
import com.pricealert.data.model.ProductPriceHistory;
import com.pricealert.scraping.yql.YQLTemplate;
import com.pricealert.scraping.yql.model.YQLCSSQuery;
import com.pricealert.scraping.yql.model.YQLResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.Arrays;

public final class PriceUpdater implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PriceUpdater.class);

    private final ScraperService context;
    private final YQLTemplate template;
    private final Product product;

    public PriceUpdater(ScraperService context, Product product) {
        this.context = context;
        this.template = context.getYqlTemplate();
        this.product = product;
    }

    @Override
    public void run() {
        YQLCSSQuery yqlcssQuery = new YQLCSSQuery();
        yqlcssQuery.setUrl(product.getUrl());
        yqlcssQuery.setCssSelector(Arrays.asList("#priceblock_ourprice", "#priceblock_saleprice"));

        YQLResponse response = template.cssQuery(yqlcssQuery);
        final String price = response.getQuery().getResults().getText("priceblock_ourprice", "priceblock_saleprice");

        if(price != null) {
            LOG.info("Price found: {}", price);

            try {
                ProductPriceHistory priceHistory = new ProductPriceHistory();
                priceHistory.setProductId(product.getId());
                priceHistory.setDate(new Date(System.currentTimeMillis()));

                DecimalFormat format = new DecimalFormat("$#,##0.00");
                priceHistory.setPrice(format.parse(price).doubleValue());
                product.getPriceHistory().add(priceHistory);

                RecentPricesDb db = new RecentPricesDb(context);
                db.newHistory(priceHistory);

                if(shouldNotify(priceHistory.getPrice())) {
                    LOG.info("New low price detected!");
                    context.sendNotification(product, price);
                }
            }
            catch(Exception e) {
                LOG.error("Failed to save price: ", e);
            }
        }
        else {
            LOG.info("Price not found for product {}, retrying...", product.getName());
            context.quickRetry(this);
        }
    }

    public boolean shouldNotify(Double price) {
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
}
