package com.pricealert.data.dto;

import com.pricealert.data.model.Product;

/**
 * Immutable Product class for communicating between threads.
 */
public final class ProductInfoDto {

    private final Long productId;
    private final String name;
    private final String url;

    private final ProductTargetInfoDto targets;

    public ProductInfoDto(Long productId, String name, String url, ProductTargetInfoDto targets) {
        this.productId = productId;
        this.name = name;
        this.targets = targets;
        this.url = url;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public ProductTargetInfoDto getTargets() {
        return targets;
    }

    public static ProductInfoDto fromProduct(Product product) {
        return new ProductInfoDto(
                product.getId(),
                product.getName(),
                product.getUrl(),
                ProductTargetInfoDto.fromProductTarget(product.getTargets()));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        ProductInfoDto that = (ProductInfoDto) o;

        if(productId != null ? !productId.equals(that.productId) : that.productId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return productId != null ? productId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ProductInfoDto{" +
                "name='" + name + '\'' +
                ", productId=" + productId +
                ", targets=" + targets +
                '}';
    }
}
