package com.pricealert.data.dto;

import com.pricealert.data.model.ProductTarget;

/**
 * Immutable ProductTarget class for communicating between threads.
 */
public final class ProductTargetInfoDto {

    private final Double targetPrice;
    private final Integer targetPct;

    public ProductTargetInfoDto(Integer targetPct, Double targetPrice) {
        this.targetPct = targetPct;
        this.targetPrice = targetPrice;
    }

    public Integer getTargetPercent() {
        return targetPct;
    }

    public Double getTargetValue() {
        return targetPrice;
    }

    public static ProductTargetInfoDto fromProductTarget(ProductTarget target) {
        return target == null ? null : new ProductTargetInfoDto(target.getTargetPercent(), target.getTargetValue());
    }
}
