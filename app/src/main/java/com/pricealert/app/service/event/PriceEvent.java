package com.pricealert.app.service.event;

public final class PriceEvent implements ProductEvent {

    private final Long productId;
    private final Double newPrice;

    public PriceEvent(Double newPrice, Long productId) {
        this.newPrice = newPrice;
        this.productId = productId;
    }

    public Double getNewPrice() {
        return newPrice;
    }

    @Override
    public Long getProductId() {
        return productId;
    }
}
