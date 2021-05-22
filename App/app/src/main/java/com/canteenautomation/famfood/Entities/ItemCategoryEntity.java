package com.canteenautomation.famfood.Entities;

import java.util.List;

public class ItemCategoryEntity {
    private String categoryName;
    private String categoryPhoto;
    private List<ItemEntity> itemEntities;

    public ItemCategoryEntity() {

    }

    public ItemCategoryEntity(String categoryName, String categoryPhoto, List<ItemEntity> itemEntities) {
        this.categoryName = categoryName;
        this.categoryPhoto = categoryPhoto;
        this.itemEntities = itemEntities;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<ItemEntity> getItemEntities() {
        return itemEntities;
    }

    public void setItemEntities(List<ItemEntity> itemEntities) {
        this.itemEntities = itemEntities;
    }

    public String getCategoryPhoto() {
        return categoryPhoto;
    }

    public void setCategoryPhoto(String categoryPhoto) {
        this.categoryPhoto = categoryPhoto;
    }
}
