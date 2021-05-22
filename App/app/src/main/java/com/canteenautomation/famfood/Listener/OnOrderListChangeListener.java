package com.canteenautomation.famfood.Listener;

import com.canteenautomation.famfood.Entities.OrderEntity;

import java.util.List;

public interface OnOrderListChangeListener {
    void onCompleteTask(List<OrderEntity> orderEntityList);
    void onErrorOccured();
}
