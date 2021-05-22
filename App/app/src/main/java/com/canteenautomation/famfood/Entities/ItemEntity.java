package com.canteenautomation.famfood.Entities;

public class ItemEntity {
    private String itemId;
    private String itemName;
    private String itemPhoto;
    private String price;
    private String expectedTime;
    private Boolean jain;
    private Integer filterCategory;

    public ItemEntity(String itemId, String itemName, String itemPhoto, String price, String expectedTime, Boolean jain, Integer filterCategory) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPhoto = itemPhoto;
        this.price = price;
        this.expectedTime = expectedTime;
        this.jain = jain;
        this.filterCategory = filterCategory;
    }

    public ItemEntity() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPhoto() {
        return itemPhoto;
    }

    public void setItemPhoto(String itemPhoto) {
        this.itemPhoto = itemPhoto;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public Boolean getJain() {
        return jain;
    }

    public void setJain(Boolean jain) {
        this.jain = jain;
    }

    public Integer getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(Integer filterCategory) {
        this.filterCategory = filterCategory;
    }
}
