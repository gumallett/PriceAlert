package com.pricealert.app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.pricealert.app.R;
import com.pricealert.app.ScraperService;
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

    public PriceUpdater(ScraperService context, YQLTemplate template, Product product) {
        this.context = context;
        this.template = template;
        this.product = product;
    }

    @Override
    public void run() {
        YQLCSSQuery yqlcssQuery = new YQLCSSQuery();
        yqlcssQuery.setUrl(product.getUrl());
        yqlcssQuery.setCssSelector(Arrays.asList("#priceblock_ourprice"));

        YQLResponse response = template.cssQuery(yqlcssQuery);
        final String price = response.getQuery().getResults().getText("priceblock_ourprice");

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

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
                notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
                notificationBuilder.setContentTitle("New Price!");
                notificationBuilder.setContentText("New Price found for " + product.getName() + ": " + price);
                Notification notification = notificationBuilder.build();
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, notification);
            }
            catch(Exception e) {
                LOG.error("Failed to save price: ", e);
            }
        }
        else {
            LOG.info("Price not found for product {}, retrying...", product.getName());
            context.updatePrice(product);
        }
    }
}
