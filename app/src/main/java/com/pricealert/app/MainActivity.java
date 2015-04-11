package com.pricealert.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.pricealert.data.RecentPricesDb;
import com.pricealert.data.model.Product;
import com.pricealert.data.model.ProductTarget;
import com.pricealert.scraping.Scraper;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    private Scraper scraper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter(ScraperService.BROADCAST);
        intentFilter.addDataScheme("http");

        ScraperReceiver scraperReceiver = new ScraperReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(scraperReceiver, intentFilter);

        Uri testingUri = Uri.parse("http://www.amazon.com/XFX-Double-947MHz-Graphics-R9290AEDFD/dp/B00HHIPM5Q/ref=sr_1_4?ie=UTF8&qid=1424101836&sr=8-4&keywords=R290");
        /*Intent intent = new Intent(this, ScraperService.class);
        intent.setData(testingUri);

        startService(intent);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAlarm(Uri uri) {
        Intent newIntent = new Intent(this, ScraperReceiver.class);
        newIntent.setData(uri);
        long scTime = 60*1000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, newIntent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + scTime, pendingIntent);
    }

    private void testQueries() {
        Product product = new Product();
        product.setName("XFX Double D");
        product.setUrl("http://www.amazon.com/XFX-Double-947MHz-Graphics-R9290AEDFD/dp/B00HHIPM5Q/");

        ProductTarget target = new ProductTarget();
        target.setTargetValue(250.99);
        product.setTargets(target);

        RecentPricesDb recentPricesDb = new RecentPricesDb(this);
        recentPricesDb.saveProduct(product);

        List<Product> productList = recentPricesDb.selectProductDetails();
        Log.d(MainActivity.class.getSimpleName(), productList.toString());
    }
}
