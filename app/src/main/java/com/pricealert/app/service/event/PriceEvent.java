package com.pricealert.app.service.event;

public final class PriceEvent {

    private final Long productId;
    private final Double newPrice;

    public PriceEvent(Double newPrice, Long productId) {
        this.newPrice = newPrice;
        this.productId = productId;
    }

    public Double getNewPrice() {
        return newPrice;
    }

    public Long getProductId() {
        return productId;
    }
}
