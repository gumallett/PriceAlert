package com.pricealert.app;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import com.pricealert.app.service.PriceUpdater;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScraperService extends Service {

    private static final Logger LOG = LoggerFactory.getLogger(ScraperService.class);

    private final LocalBinder binder = new LocalBinder();
    private final YQLTemplate template = new YQLTemplate();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ScraperService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    public void updatePrice(final Product product) {
        executor.submit(new PriceUpdater(this, template, product));
    }

    public void sendNotification(Product product, String newPrice) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        notificationBuilder.setContentTitle("New Price!");
        notificationBuilder.setContentText("New Price found for " + product.getName() + ": " + newPrice);
        notificationBuilder.setVibrate(new long[]{1, 500});
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("PRODUCT_ID", product.getId());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notificationBuilder.build());
    }

    public class LocalBinder extends Binder {

        public ScraperService getService() {
            return ScraperService.this;
        }
    }
}
