package com.pricealert.app.service.event;

public interface PriceEventListener {

    void onPriceChange(PriceEvent priceEvent);
}
