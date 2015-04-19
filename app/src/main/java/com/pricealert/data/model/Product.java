package com.pricealert.data.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    private Long id;
    private String url;
    private String name;
    private Date createDate;

    private ProductPriceHistory mostRecentPrice;
    private ProductTarget targets;
    private List<ProductPriceHistory> priceHistory = new ArrayList<ProductPriceHistory>();

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ProductPriceHistory getMostRecentPrice() {
        return mostRecentPrice;
    }

    public void setMostRecentPrice(ProductPriceHistory mostRecentPrice) {
        this.mostRecentPrice = mostRecentPrice;
    }

    public List<ProductPriceHistory> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<ProductPriceHistory> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public ProductTarget getTargets() {
        return targets;
    }

    public void setTargets(ProductTarget targets) {
        this.targets = targets;
    }

    @Override
    public String toString() {
        return "Product{" +
                "createDate=" + createDate +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", mostRecentPrice=" + mostRecentPrice +
                ", targets=" + targets +
                ", priceHistory=" + priceHistory +
                '}';
    }
}
