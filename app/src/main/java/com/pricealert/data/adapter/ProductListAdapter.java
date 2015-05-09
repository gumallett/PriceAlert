package com.pricealert.data.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.pricealert.app.R;
import com.pricealert.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends BaseAdapter {

    private final Context context;
    private List<Product> productList = new ArrayList<Product>();

    public ProductListAdapter(List<Product> products, Context context) {
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

    //image from amazon


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_list_item_layout, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.productListItem);
        Product product = getProduct(position);
        textView.setText(product.getName());

        ImageView imageView = (ImageView) view.findViewById(R.id.productListItemImg);
        byte[] imageBytes = product.getProductImg().getImg();
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(imageBitmap);

        return view;
    }
}
