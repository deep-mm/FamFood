package com.canteenautomation.famfood.Listener;

import com.canteenautomation.famfood.Entities.UserEntity;

public interface GetUserListener {
    void onCompleteTask(UserEntity userEntity);
    void onErrorOccured();
}
