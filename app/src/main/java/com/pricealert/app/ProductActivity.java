package com.pricealert.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.pricealert.data.RecentPricesDb;
import com.pricealert.data.model.Product;
import com.pricealert.data.model.ProductTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProductActivity extends ActionBarActivity {

    private static final Logger LOG = LoggerFactory.getLogger(ProductActivity.class);
    Long productId = null;

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
                productId = product.getId();
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

    public void saveProduct(View view) {
        RecentPricesDb db = new RecentPricesDb(this);
        Product product = new Product();
        product.setId(productId);

        EditText urlText = (EditText) findViewById(R.id.productUrl);
        EditText nameText = (EditText) findViewById(R.id.productName);
        product.setUrl(urlText.getText().toString());
        product.setName(nameText.getText().toString());

        ProductTarget targets = new ProductTarget();
        EditText targetText = (EditText) findViewById(R.id.productTargetPrice);
        EditText targetPctText = (EditText) findViewById(R.id.productTargetPct);
        String targetVal = targetText.getText().toString();

        if(!targetVal.isEmpty()) {
            try {
                targets.setTargetValue(Double.valueOf(targetVal));
            }
            catch(Exception e) {
                LOG.error("Error parsing value: ", e);
            }
        }

        String targetPctVal = targetPctText.getText().toString();

        if(!targetPctVal.isEmpty()) {
            try {
                targets.setTargetPercent(Integer.valueOf(targetPctVal));
            }
            catch(Exception e) {
                LOG.error("Error parsing value: ", e);
            }
        }

        product.setTargets(targets);

        db.saveProduct(product);
    }
}
