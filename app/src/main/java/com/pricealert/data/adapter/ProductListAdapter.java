package com.pricealert.data.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pricealert.app.R;
import com.pricealert.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends BaseAdapter {

    private List<Product> productList = new ArrayList<Product>();

    public ProductListAdapter(List<Product> products) {
        this.productList = products;
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
        TextView textView = new TextView(parent.getContext());
        textView.setText(getProduct(position).getName());
        textView.setTextAppearance(parent.getContext(), R.style.TextAppearance_AppCompat_Medium);
        return textView;
    }
}
