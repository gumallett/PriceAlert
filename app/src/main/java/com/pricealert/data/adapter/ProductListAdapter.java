package com.pricealert.data.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.pricealert.app.MainActivity;
import com.pricealert.app.R;
import com.pricealert.data.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductListAdapter extends BaseAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ProductListAdapter.class);
    private final MainActivity context;
    private List<Product> productList = new ArrayList<Product>();

    public ProductListAdapter(List<Product> products, MainActivity context) {
        this.productList = products;
        this.context = context;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return getProduct(position);
    }

    public Product getProduct(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getProduct(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if(convertView == null) {
            final Product product = getProduct(position);
            view = View.inflate(context, R.layout.product_list_item_layout, null);

            final String productName = product.getName();
            final byte[] imageBytes = product.getProductImg() != null ? product.getProductImg().getImg() : null;

            TextView textView = (TextView) view.findViewById(R.id.productListItem);
            textView.setText(productName);

            if(imageBytes != null) {
                ImageView imageView = (ImageView) view.findViewById(R.id.productListItemImg);
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imageView.setImageBitmap(imageBitmap);
            }

            Button button = (Button) view.findViewById(R.id.deleteBtn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.deleteProduct(v, product);
                }
            });

            final GestureDetectorCompat mDetector = new GestureDetectorCompat(context, new ScrollDetector(view));

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    LOG.info("onTouch: {}", event);
                    mDetector.onTouchEvent(event);
                    return false;
                }
            });

            return view;
        }
        else {
            view = convertView;
        }

        return view;
    }

    public class ScrollListener implements AbsListView.OnScrollListener {


        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            LOG.info("onScrollStateChanged: {}, {}", view, scrollState);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            LOG.info("onScroll: {}, {}", view, firstVisibleItem);
        }
    }

    public class ScrollDetector extends GestureDetector.SimpleOnGestureListener {

        private final View view;
        private int startX = 0;

        public ScrollDetector(View view) {
            this.view = view;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            LOG.info("DOWN");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LOG.info("FLING");
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            LOG.info("SINGLE TAP UP");
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            LOG.info("SINGLE TAP CONFIRMED");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LOG.info("SCROLL: {} {}", distanceX, distanceY);
            startX += (int) distanceX;
            if(startX > 0) {
                startX = 0;
            }

            LOG.info("X: {}", startX);
            view.setScrollX(startX);

            int[] coords = new int[2];
            View button = view.findViewById(R.id.deleteBtn);
            View firstChild = button.getVisibility() == View.GONE ? view.findViewById(R.id.productListItemImg) : button;
            firstChild.getLocationOnScreen(coords);
            LOG.info("coords: {}", Arrays.toString(coords));

            Rect rect = new Rect();
            view.getHitRect(rect);
            LOG.info("hit rect: {}", rect);

            int hitX = coords[0];

            if(hitX > (rect.width() / 2)) {
                LOG.info("Passed threshold");
                button.setVisibility(View.VISIBLE);
            }
            else {
                button.setVisibility(View.GONE);
            }

            return false;
        }
    }
}
