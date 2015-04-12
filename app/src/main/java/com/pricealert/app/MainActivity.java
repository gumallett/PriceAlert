package com.pricealert.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.pricealert.data.RecentPricesDb;
import com.pricealert.data.model.Product;
import com.pricealert.data.model.ProductTarget;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    private boolean mBound = false;
    private ScraperService scraperService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ScraperService.LocalBinder binder = (ScraperService.LocalBinder) service;
            scraperService = binder.getService();
            mBound = true;

            Log.d(MainActivity.class.getSimpleName(), "ScraperService bound.");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, ScraperService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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

    public void testService(View view) {
        if(mBound) {
            Product product = new Product();
            product.setName("XFX Double D");
            product.setUrl("http://www.amazon.com/XFX-Double-947MHz-Graphics-R9290AEDFD/dp/B00HHIPM5Q/");

            ProductTarget target = new ProductTarget();
            target.setTargetValue(250.99);
            product.setTargets(target);

            RecentPricesDb recentPricesDb = new RecentPricesDb(this);
            recentPricesDb.saveProduct(product);

            scraperService.updatePrice(product);
        }
    }
}
