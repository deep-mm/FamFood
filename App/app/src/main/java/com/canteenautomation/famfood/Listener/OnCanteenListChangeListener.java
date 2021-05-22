package com.canteenautomation.famfood.Listener;

import com.canteenautomation.famfood.Entities.CanteenEntity;

import java.util.List;

public interface OnCanteenListChangeListener {
    void onDataChanged(List<CanteenEntity> canteenEntityList);
    void onErrorOccured();
}
