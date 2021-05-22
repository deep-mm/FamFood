package com.canteenautomation.famfood.Listener;

import com.canteenautomation.famfood.Entities.CanteenEntity;

import java.util.List;

public interface OnCanteenEntityChangeListener {
    void onDataChanged(CanteenEntity canteenEntity);
    void onErrorOccured();
}
