package com.pricealert.data.model;

import java.sql.Date;

public class ProductPriceHistory {

    private Double price;
    private Date date;

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
}
