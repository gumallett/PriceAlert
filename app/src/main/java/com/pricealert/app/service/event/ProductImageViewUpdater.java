package com.pricealert.app.service.event;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ProductImageViewUpdater implements ProductEventListener {

    private final ImageView imageView;

    public ProductImageViewUpdater(ImageView textView) {
        this.imageView = textView;
    }

    @Override
    public void onChange(final ProductEvent event) {
        if(event instanceof ImageEvent) {
            final ImageEvent imageEvent = (ImageEvent) event;

            imageView.post(new Runnable() {
                @Override
                public void run() {
                    byte[] imageBytes = imageEvent.getImage();
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imageView.setImageBitmap(imageBitmap);
                }
            });
        }
    }
}
