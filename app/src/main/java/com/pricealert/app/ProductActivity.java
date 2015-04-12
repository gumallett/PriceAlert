package com.pricealert.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import com.pricealert.data.RecentPricesDb;
import com.pricealert.data.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProductActivity extends ActionBarActivity {

    private static final Logger LOG = LoggerFactory.getLogger(ProductActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Intent intent = getIntent();

        if(intent != null) {
            RecentPricesDb db = new RecentPricesDb(this);
            Product product = db.selectProductDetails(intent.getLongExtra("PRODUCT_ID", -1L));
            LOG.info("Activity product: {}", product);

            if(product != null) {
                EditText urlText = (EditText) findViewById(R.id.productUrl);
                urlText.setText(product.getUrl());

                EditText nameText = (EditText) findViewById(R.id.productName);
                nameText.setText(product.getName());

                TextView priceView = (TextView) findViewById(R.id.lastPriceText);
                priceView.setText(String.valueOf(product.getMostRecentPrice().getPrice()));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu);
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
}
