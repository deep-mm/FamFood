package com.canteenautomation.famfood.Listener;

import com.canteenautomation.famfood.Entities.ItemCategoryEntity;

import java.util.List;

public interface OnMenuChangeListener {
    void onDataChanged(List<ItemCategoryEntity> itemCategoryEntities);
    void onErrorOccured();
}
