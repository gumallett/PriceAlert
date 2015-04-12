package com.pricealert.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
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

    public void updatePrice(final Product product) {
        executor.submit(new PriceUpdater(this, template, product));
    }

    public class LocalBinder extends Binder {

        public ScraperService getService() {
            return ScraperService.this;
        }
    }
}
