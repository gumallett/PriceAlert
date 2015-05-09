package com.pricealert.app.service.event;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.TextView;

public final class PriceViewUpdater implements ProductEventListener {

    private final TextView view;

    public PriceViewUpdater(TextView view) {
        this.view = view;
    }

    @Override
    public void onChange(final ProductEvent e) {
        if(e instanceof PriceEvent) {
            final PriceEvent priceEvent = (PriceEvent) e;

            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setText("" + priceEvent.getNewPrice());
                    flashView(view);
                }
            });
        }
    }

    private static void flashView(final View view) {
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), 0xff52ffde, 0x0052ffde);
        anim.setDuration(3000);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });

        anim.start();
    }
}
