package com.example.do_i_need_it.model;

import java.io.Serializable;

public class Product implements Serializable {

    private String prodId, prodTitle, prodWebsite, imageUrl, dateAdded, prodStatus;
    double prodPrice;

    public Product() {}

    public Product(String prodId, String prodTitle, String prodWebsite, String imageUrl, String dateAdded, double prodPrice) {
        this.prodId = prodId;
        this.prodTitle = prodTitle;
        this.prodWebsite = prodWebsite;
        this.imageUrl = imageUrl;
        this.dateAdded = dateAdded;
        this.prodPrice = prodPrice;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProdTitle() {
        return prodTitle;
    }

    public void setProdTitle(String prodTitle) {
        this.prodTitle = prodTitle;
    }

    public String getProdWebsite() {
        return prodWebsite;
    }

    public void setProdWebsite(String prodWebsite) {
        this.prodWebsite = prodWebsite;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public double getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(double prodPrice) {
        this.prodPrice = prodPrice;
    }

    public String getProdStatus() {
        return prodStatus;
    }

    public void setProdStatus(String prodStatus) {
        this.prodStatus = prodStatus;
    }
}
