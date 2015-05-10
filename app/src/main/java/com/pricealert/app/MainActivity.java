package com.pricealert.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.pricealert.app.service.ScraperService;
import com.pricealert.data.RecentPricesDb;
import com.pricealert.data.adapter.ProductListAdapter;
import com.pricealert.data.dto.ProductInfoDto;
import com.pricealert.data.model.Product;
import com.pricealert.data.model.ProductTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
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

        LOG.info("MainActivity created...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        startService(new Intent(this, ScraperService.class));
        bindService(new Intent(this, ScraperService.class), mConnection, Context.BIND_AUTO_CREATE);

        LOG.info("MainActivity started...");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mBound) {
            bindService(new Intent(this, ScraperService.class), mConnection, Context.BIND_AUTO_CREATE);
        }

        loadProductList();
        final ListView productsListView = (ListView) findViewById(R.id.productsList);

        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //startProductActivity(id);
            }
        });

        productsListView.setOnTouchListener(new SwipeTouchListener());

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

    public void deleteProduct(View view, Product product) {
        LOG.info("DELETE CALLED");
        if(product == null || product.getId() < 0) {
            return;
        }

        RecentPricesDb db = new RecentPricesDb(this);
        db.deleteProduct(product);
        final ListView productsListView = (ListView) findViewById(R.id.productsList);
        productsListView.setAdapter(new ProductListAdapter(db.selectProducts(), this));
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
        productsListView.setAdapter(new ProductListAdapter(db.selectProducts(), this));
    }

    public class SwipeTouchListener implements View.OnTouchListener {

        // MINIMUM distance user has to swipe before view is switched
        private static final int SWIPE_THRESHOLD = 250;
        // MINIMUM distance user has to swipe on x-axis before swipe animation is shown
        private static final int MIN_X_THRESHOLD = 45;
        // MINIMUM distance user has to swipe on y-axis to pass thru (to allow scrolling)
        private static final int MIN_Y_THRESHOLD = 20;

        private float x;
        private float y;
        private float deltaX;
        private float deltaY;
        private boolean moving;
        private View child;
        private View active;
        LinearLayout mainView;
        LinearLayout deleteView;
        private int childPos;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            LOG.info("raw X: {}, raw Y: {}", event.getRawX(), event.getRawY());
            LOG.info("X: {}, Y: {}", event.getX(), event.getY());
            LOG.info("view: " + view);

            if(view instanceof ListView) {
                ListView listView = (ListView) view;

                switch (MotionEventCompat.getActionMasked(event)) {
                    case MotionEvent.ACTION_DOWN:
                        LOG.info("DOWN");
                        x = event.getX();
                        y = event.getY();

                        childPos = getPositionByTouch(listView, event);
                        child = childPos == ListView.INVALID_POSITION ? null : listView.getChildAt(childPos - listView.getFirstVisiblePosition());
                        LOG.info("child: {}", child);

                        mainView = child == null ? null : (LinearLayout) child.findViewById(R.id.mainView);
                        deleteView = child == null ? null : (LinearLayout) child.findViewById(R.id.swipeView);
                        if(mainView != null) {
                            active = mainView.getVisibility() == View.VISIBLE ? mainView : deleteView;
                        }

                        return true;
                    case MotionEvent.ACTION_UP:
                        LOG.info("UP");

                        // handle clicks
                        if(child != null && !moving && mainView == active) {
                            LOG.info("child at {} clicked!", childPos);
                            startProductActivity(listView.getItemIdAtPosition(childPos));
                        }
                        else if(child != null && !moving && deleteView == active) {
                            LOG.info("child at {} clicked!", childPos);
                            deleteProduct(child, (Product)listView.getItemAtPosition(childPos));
                        }

                        // swipe logic begins
                        deltaX = x - event.getX();
                        if(mainView != null && deleteView != null && Math.abs(deltaX) > SWIPE_THRESHOLD) {
                            if(active == mainView) {
                                switchView(mainView, deleteView);
                            }
                            else {
                                switchView(deleteView, mainView);
                            }
                        }
                        else {
                            move(active, 0);
                        }

                        reset();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        LOG.info("MOVE");
                        moving = true;
                        deltaX = x - event.getX();
                        deltaY = y - event.getY();
                        LOG.info("deltaX: {}", deltaX);
                        LOG.info("deltaY: {}", deltaY);

                        if(Math.abs(deltaY) > MIN_Y_THRESHOLD && Math.abs(deltaX) < MIN_X_THRESHOLD) {
                            move(active, 0);
                        }

                        if(Math.abs(deltaX) > 30) {
                            move(active, -deltaX);
                            return true;
                        }

                        return false;
                    case MotionEvent.ACTION_CANCEL:
                        return false;
                }
            }

            return false;
        }

        private void switchView(View current, View target) {
            target.setVisibility(View.VISIBLE);
            current.setVisibility(View.GONE);
            move(target, 0);
        }

        private void move(View view, float deltaX) {
            if(view == null) {
                return;
            }

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.leftMargin = (int) deltaX;
            view.setLayoutParams(params);
        }

        private void reset() {
            moving = false;
            deltaX = 0;
            deltaY = 0;
            child = null;
            active = null;
            mainView = null;
            deleteView = null;
            childPos = ListView.INVALID_POSITION;
        }

        private int getPositionByTouch(ListView listView, MotionEvent event) {
            int[] listViewCoords = new int[2];
            listView.getLocationOnScreen(listViewCoords);
            LOG.info("listView location: {}", listViewCoords);

            int scrollX = listView.getScrollX();
            int scrollY = listView.getScrollY();
            LOG.info("scrollX: {}, scrollY: {}", scrollX, scrollY);

            int x = (int) event.getRawX() - listViewCoords[0];
            int y = (int) event.getRawY() - listViewCoords[1];

            LOG.info("x, y: {} {}", x, y);
            LOG.info("first visible: {}", listView.getFirstVisiblePosition());
            int position = listView.pointToPosition((int) event.getX(), (int)event.getY());
            LOG.info("pointToPosition: {}", position);
            return position;
        }
    }
}
