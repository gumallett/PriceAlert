package com.pricealert.data.model;

import java.sql.Date;
import java.util.List;

public class Product {

    private Long id;
    private String url;
    private String name;
    private Date createDate;

    private ProductTarget targets;
    private List<ProductPriceHistory> priceHistory;

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

    public ProductPriceHistory getMostRecentPriceHistory() {
        if(priceHistory != null) {
            return priceHistory.get(0);
        }

        return null;
    }

    @Override
    public String toString() {
        return "Product{" +
                "createDate=" + createDate +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", targets=" + targets +
                ", priceHistory=" + priceHistory +
                '}';
    }
}
