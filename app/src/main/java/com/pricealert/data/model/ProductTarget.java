package com.pricealert.data.model;

import java.io.Serializable;

public class ProductTarget implements Serializable {

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

    @Override
    public String toString() {
        return "ProductTarget{" +
                "targetPercent=" + targetPercent +
                ", targetValue=" + targetValue +
                '}';
    }
}
