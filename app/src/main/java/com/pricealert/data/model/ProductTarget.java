package com.pricealert.data.model;

public class ProductTarget {

    private Double targetValue;
    private Integer targetPercent;

    public Integer getTargetPercent() {
        return targetPercent;
    }

    public void setTargetPercent(Integer targetPercent) {
        this.targetPercent = targetPercent;
    }

    public Double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }
}
