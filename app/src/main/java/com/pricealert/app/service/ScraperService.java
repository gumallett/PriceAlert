package com.pricealert.app.service;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.pricealert.app.ProductActivity;
import com.pricealert.app.R;
import com.pricealert.data.model.Product;
import com.pricealert.scraping.yql.YQLTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScraperService extends Service {

    private static final Logger LOG = LoggerFactory.getLogger(ScraperService.class);

    private final LocalBinder binder = new LocalBinder();
    private final YQLTemplate template = new YQLTemplate();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    private final Set<Product> trackedItems = new HashSet<Product>();

    public ScraperService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LOG.info("Scraper service unbound.");
        return false;
    }

    public void track(Product product) {
        if(trackedItems.contains(product)) {
            return;
        }

        LOG.info("Tracking new product: {} ", product);

        trackedItems.add(product);

        PriceUpdater priceUpdater = new PriceUpdater(this, product);
        scheduledExecutor.scheduleAtFixedRate(priceUpdater, 0, 10, TimeUnit.MINUTES);
    }

    public void updatePrice(final Product product) {
        executor.submit(new PriceUpdater(this, product));
    }

    public Set<Product> getTrackedItems() {
        return Collections.unmodifiableSet(trackedItems);
    }

    public void sendNotification(Product product, String newPrice) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        notificationBuilder.setContentTitle("New Price!");
        notificationBuilder.setContentText("New Price found for " + product.getName() + ": " + newPrice);
        notificationBuilder.setVibrate(new long[]{1, 500});
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent resultIntent = new Intent(this, ProductActivity.class);
        resultIntent.putExtra("PRODUCT_ID", product.getId());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ProductActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notificationBuilder.build());
    }

    public YQLTemplate getYqlTemplate() {
        return template;
    }

    void quickRetry(PriceUpdater updater) {
        scheduledExecutor.schedule(updater, 5, TimeUnit.SECONDS);
    }

    public class LocalBinder extends Binder {

        public ScraperService getService() {
            return ScraperService.this;
        }
    }
}