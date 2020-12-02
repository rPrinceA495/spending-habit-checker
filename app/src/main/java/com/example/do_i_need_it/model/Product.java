package com.example.do_i_need_it.model;

public class Product {

    private String prodId, prodTitle, prodWebsite, imageUrl, dateAdded, prodPrice;

    public Product() {}

    public Product(String prodId, String prodTitle, String prodWebsite, String imageUrl, String dateAdded, String prodPrice) {
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

    public String getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(String prodPrice) {
        this.prodPrice = prodPrice;
    }
}
