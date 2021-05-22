package com.canteenautomation.famfood.Listener;

import com.canteenautomation.famfood.Entities.OrderEntity;

public interface GetOrderListener {
    void onCompleteTask(OrderEntity orderEntity);
    void onErrorOccured();
}
