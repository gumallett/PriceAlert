package com.pricealert.app;

import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.pricealert.app.service.ScraperService;
import com.pricealert.app.service.event.PriceViewUpdater;
import com.pricealert.app.service.event.ProductImageViewUpdater;
import com.pricealert.data.RecentPricesDb;
import com.pricealert.data.dto.ProductInfoDto;
import com.pricealert.data.model.Product;
import com.pricealert.data.model.ProductTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


public class ProductActivity extends ActionBarActivity {

    private static final Logger LOG = LoggerFactory.getLogger(ProductActivity.class);

    private volatile boolean mBound = false;
    private ScraperService scraperService;
    private final DecimalFormat targetPriceFmt = new DecimalFormat("#,##0.00");

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ScraperService.LocalBinder binder = (ScraperService.LocalBinder) service;
            scraperService = binder.getService();
            mBound = true;

            if(productId != -1) {
                scraperService.registerProductUpdateListener(productId, new PriceViewUpdater((TextView) findViewById(R.id.lastPriceText)));
                scraperService.registerProductUpdateListener(productId, new ProductImageViewUpdater((ImageView) findViewById(R.id.productImg)));
            }

            Log.d(MainActivity.class.getSimpleName(), "ScraperService bound.");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private long productId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        findViewById(R.id.productUrl).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                saveProduct(v);
            }
        });

        findViewById(R.id.productName).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                saveProduct(v);
            }
        });

        findViewById(R.id.productTargetPct).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                saveProduct(v);
            }
        });

        findViewById(R.id.productTargetPrice).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                saveProduct(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mBound) {
            bindService(new Intent(this, ScraperService.class), mConnection, Context.BIND_AUTO_CREATE);
        }

        loadProduct();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mBound) {
            cleanup();
        }

        saveProduct(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound) {
            cleanup();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBound) {
            cleanup();
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

        if (productId != -1) {
            product.setId(productId);
        }

        EditText urlText = (EditText) findViewById(R.id.productUrl);
        EditText nameText = (EditText) findViewById(R.id.productName);
        product.setUrl(urlText.getText().toString());
        product.setName(nameText.getText().toString());

        if(product.getName().isEmpty()) {
            product.setName("Not Named");
        }

        ProductTarget targets = new ProductTarget();
        EditText targetText = (EditText) findViewById(R.id.productTargetPrice);
        EditText targetPctText = (EditText) findViewById(R.id.productTargetPct);
        String targetVal = targetText.getText().toString();

        if(!targetVal.isEmpty()) {
            try {
                targets.setTargetValue(targetPriceFmt.parse(targetVal).doubleValue());
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

        if(product.getId() != null) {
            productId = product.getId();
            View deleteBtn = findViewById(R.id.deleteBtn);
            deleteBtn.setEnabled(true);
        }

        if(product.getId() != null && product.getUrl() != null && mBound) {
            scraperService.registerProductUpdateListener(product.getId(), new PriceViewUpdater((TextView) findViewById(R.id.lastPriceText)));
            scraperService.registerProductUpdateListener(product.getId(), new ProductImageViewUpdater((ImageView) findViewById(R.id.productImg)));
            final ProductInfoDto productInfoDto = ProductInfoDto.fromProduct(product);
            scraperService.track(productInfoDto);
        }
    }

    public void deleteProduct(View view) {
        if(productId == -1L) {
            return;
        }

        Product product = new Product();
        product.setId(productId);

        scraperService.unTrack(ProductInfoDto.fromProduct(product));

        RecentPricesDb db = new RecentPricesDb(this);
        db.deleteProduct(product);

        startActivity(new Intent(this, MainActivity.class));
    }

    private void loadProduct() {
        Intent intent = getIntent();

        if(intent != null) {
            RecentPricesDb db = new RecentPricesDb(this);
            long id = intent.getLongExtra("PRODUCT_ID", -1L);
            Product product = db.selectProductDetails(id);
            LOG.info("Activity product: {}", product);

            if(product != null) {
                productId = product.getId();
                EditText urlText = (EditText) findViewById(R.id.productUrl);
                urlText.setText(product.getUrl());

                EditText nameText = (EditText) findViewById(R.id.productName);
                nameText.setText(product.getName());

                TextView priceView = (TextView) findViewById(R.id.lastPriceText);
                priceView.setText(String.valueOf(product.getMostRecentPrice().getPrice()));

                TextView lastUpdatedView = (TextView) findViewById(R.id.lastPriceUpdateText);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                lastUpdatedView.setText(sdf.format(product.getMostRecentPrice().getDate()));

                if(product.getTargets().getTargetValue() != null) {
                    EditText targetText = (EditText) findViewById(R.id.productTargetPrice);
                    targetText.setText(targetPriceFmt.format(product.getTargets().getTargetValue()));
                }

                if(product.getTargets().getTargetPercent() != null) {
                    EditText targetPctText = (EditText) findViewById(R.id.productTargetPct);
                    targetPctText.setText(String.valueOf(product.getTargets().getTargetPercent()));
                }

                if(product.getProductImg() != null && product.getProductImg().getImg() != null) {
                    ImageView imageView = (ImageView) findViewById(R.id.productImg);
                    byte[] imageBytes = product.getProductImg().getImg();
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imageView.setImageBitmap(imageBitmap);
                }

                View deleteBtn = findViewById(R.id.deleteBtn);
                deleteBtn.setEnabled(true);
            }
            else {
                View deleteBtn = findViewById(R.id.deleteBtn);
                deleteBtn.setEnabled(false);
            }
        }
    }

    public void onPaste(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if(clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            TextView urlText = (TextView) findViewById(R.id.productUrl);
            urlText.setText(clipboardManager.getPrimaryClip().getItemAt(0).getText());
            urlText.requestFocus();
        }
    }

    public void onAmazon(View view) {
        TextView urlText = (TextView) findViewById(R.id.productUrl);
        CharSequence url = urlText.getText();

        if(url != null && url.length() > 0) {
            Intent startBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
            startActivity(startBrowser);
        }
    }

    private void cleanup() {
        mBound = false;
        scraperService.unRegisterProductUpdateListener(productId);
        unbindService(mConnection);
    }
}
