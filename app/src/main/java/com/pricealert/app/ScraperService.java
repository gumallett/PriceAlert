package com.pricealert.app;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.pricealert.scraping.Scraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ScraperService extends IntentService {

    public static final String BROADCAST = "SCRAPER_SERVICE_PRICE";

    public ScraperService() {
        this("Scraper Service");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ScraperService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri url = intent.getData();

        Log.d(ScraperService.class.getSimpleName(), "Scraping url: " + url);
        Scraper scraper = new Scraper(url.toString());
        try {
            Log.d(ScraperReceiver.class.getSimpleName(), "Scraping complete: " + scraper.connect().getPrice());
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        //database code here

        /*Intent localIntent = new Intent(BROADCAST);
        localIntent.setData(url);
        localIntent.putExtra("com.pricealert.scraper.PRICE", scraper.getPrice());
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);*/
    }
}
