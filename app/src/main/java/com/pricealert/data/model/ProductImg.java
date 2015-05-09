package com.pricealert.data.model;

import java.util.Arrays;

public class ProductImg {

    private Long product_id;
    private String imgUrl;
    private byte[] img;

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    @Override
    public String toString() {
        return "ProductImg{" +
                "img=" + Arrays.toString(img) +
                ", product_id=" + product_id +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
