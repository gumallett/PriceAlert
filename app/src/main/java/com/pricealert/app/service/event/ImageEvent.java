package com.pricealert.app.service.event;

public final class ImageEvent implements ProductEvent {

    private final Long productId;
    private final byte[] imageBytes;

    public ImageEvent(Long productId, byte[] imageBytes) {
        this.productId = productId;
        this.imageBytes = imageBytes;
    }

    @Override
    public Long getProductId() {
        return productId;
    }

    public byte[] getImage() {
        return imageBytes;
    }
}
