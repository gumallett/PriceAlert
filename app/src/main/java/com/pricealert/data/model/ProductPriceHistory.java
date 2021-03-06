package com.pricealert.data.model;

import java.io.Serializable;
import java.sql.Date;

public class ProductPriceHistory implements Serializable {

    private Long productId;
    private Double price;
    private Date date;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ProductPriceHistory{" +
                "date=" + date +
                ", productId=" + productId +
                ", price=" + price +
                '}';
    }
}
