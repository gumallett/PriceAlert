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
import android.widget.AdapterView;
import android.widget.ListView;
import com.pricealert.app.service.ScraperService;
import com.pricealert.data.RecentPricesDb;
import com.pricealert.data.adapter.ProductListAdapter;
import com.pricealert.data.dto.ProductInfoDto;
import com.pricealert.data.model.Product;
import com.pricealert.data.model.ProductTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class MainActivity extends ActionBarActivity {

    private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

    private boolean mBound = false;
    private ScraperService scraperService;

    private final ServiceConnection mConnection = new ServiceConnection() {

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

        final ListView productsListView = (ListView) findViewById(R.id.productsList);

        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startProductActivity(id);
            }
        });

        LOG.info("MainActivity created...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        startService(new Intent(this, ScraperService.class));
        bindService(new Intent(this, ScraperService.class), mConnection, Context.BIND_AUTO_CREATE);
        loadProductList();

        LOG.info("MainActivity started...");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mBound) {
            bindService(new Intent(this, ScraperService.class), mConnection, Context.BIND_AUTO_CREATE);
        }

        LOG.info("MainActivity resuming...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mBound) {
            mBound = false;
            unbindService(mConnection);
        }

        LOG.info("MainActivity paused...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBound) {
            mBound = false;
            unbindService(mConnection);
        }

        LOG.info("MainActivity destroyed...");
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

        Product productList = recentPricesDb.selectProductDetails(product.getId());
        Log.d(MainActivity.class.getSimpleName(), productList.toString());
    }

    public void testService(View view) {
        if(mBound) {
            RecentPricesDb recentPricesDb = new RecentPricesDb(this);

            Product product = new Product();
            product.setName("TEST NOTIFICATION " + new Random().nextInt(10000));
            product.setUrl("http://www.amazon.com/XFX-Double-947MHz-Graphics-R9290AEDFD/dp/B00HHIPM5Q/");

            ProductTarget target = new ProductTarget();
            target.setTargetValue(2270.99);
            product.setTargets(target);

            recentPricesDb.saveProduct(product);

            scraperService.updatePrice(ProductInfoDto.fromProduct(product));
        }
    }

    public void trackOnClick(View view) {
        startProductActivity(null);
    }

    public void startProductActivity(Long productId) {
        Intent resultIntent = new Intent(this, ProductActivity.class);

        if(productId != null) {
            resultIntent.putExtra("PRODUCT_ID", productId);
        }

        startActivity(resultIntent);
    }

    private void loadProductList() {
        RecentPricesDb db = new RecentPricesDb(this);
        final ListView productsListView = (ListView) findViewById(R.id.productsList);
        productsListView.setAdapter(new ProductListAdapter(db.selectProducts()));
    }
}
