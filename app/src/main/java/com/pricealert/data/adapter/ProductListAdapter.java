package com.pricealert.data.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.*;
import android.widget.*;
import com.pricealert.app.MainActivity;
import com.pricealert.app.R;
import com.pricealert.data.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
        LOG.info("getView({}, {})", position, convertView);
        View view;

        final Product product = getProduct(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.product_list_item_layout, null);

        final String productName = product.getName();
        final byte[] imageBytes = product.getProductImg() != null ? product.getProductImg().getImg() : null;

        TextView textView = (TextView) view.findViewById(R.id.productListItem);
        textView.setText(productName);

        TextView priceTextView = (TextView) view.findViewById(R.id.productListItemPrice);
        DecimalFormat decimalFormat = new DecimalFormat("$#,##0.00");
        Double price = product.getMostRecentPrice() == null ? 0.0 : product.getMostRecentPrice().getPrice();
        priceTextView.setText(decimalFormat.format(price));

        if(imageBytes != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.productListItemImg);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(imageBitmap);
        }

        return view;
    }
}
