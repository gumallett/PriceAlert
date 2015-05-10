package com.pricealert.data.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pricealert.app.R;
import com.pricealert.data.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductListPagerAdapter extends FragmentStatePagerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ProductListPagerAdapter.class);

    private final Product product;

    public ProductListPagerAdapter(FragmentManager fm, Product product) {
        super(fm);
        this.product = product;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Fragment getItem(int position) {
        LOG.info("getItem({})", position);

        Fragment fragment = new PagerFragment();
        Bundle bundle = new Bundle();

        bundle.putString("productName", product.getName());

        if(product.getProductImg() != null) {
            bundle.putByteArray("productImg", product.getProductImg().getImg());
        }

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return product.getName();
    }


    public static class PagerFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.product_list_item_layout, container, false);
            Bundle args = getArguments();
            LOG.info("onCreateView({}, {})", args, container);
            final String productName = args.getString("productName");
            final byte[] imageBytes = args.getByteArray("productImg");

            TextView textView = (TextView) view.findViewById(R.id.productListItem);
            textView.setText(productName);

            /*if(imageBytes != null) {
                ImageView imageView = (ImageView) view.findViewById(R.id.productListItemImg);
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imageView.setImageBitmap(imageBitmap);
            }*/

            return view;
        }
    }

}
