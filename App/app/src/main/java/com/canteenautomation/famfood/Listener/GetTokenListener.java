package com.canteenautomation.famfood.Listener;

import com.canteenautomation.famfood.Entities.OrderEntity;

public interface GetTokenListener {
    void Auth(boolean check);
    void onErrorOccured();
}
