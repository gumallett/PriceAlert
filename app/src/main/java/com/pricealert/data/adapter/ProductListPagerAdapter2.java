package com.pricealert.data.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pricealert.app.R;
import com.pricealert.data.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.zip.Inflater;

public class ProductListPagerAdapter2 extends PagerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ProductListPagerAdapter2.class);

    private final Product product;
    private final Context context;

    public ProductListPagerAdapter2(Context context, Product product) {
        this.product = product;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        LOG.info("isViewFromObject({}, {})", view, object);
        return true;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view = inflater.inflate(R.layout.product_list_item_layout, null);
        LOG.info("onCreateView({}, {})", position, container);
        final String productName = product.getName();
        final byte[] imageBytes = product.getProductImg() != null ? product.getProductImg().getImg() : null;

        TextView textView = (TextView) view.findViewById(R.id.productListItem);
        textView.setText(productName);

        if(imageBytes != null) {
            //ImageView imageView = (ImageView) view.findViewById(R.id.productListItemImg);
            //Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            //imageView.setImageBitmap(imageBitmap);
        }

        container.addView(view);

        return view;
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return product.getName();
    }

}