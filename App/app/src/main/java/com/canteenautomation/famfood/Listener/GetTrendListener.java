package com.canteenautomation.famfood.Listener;

import com.canteenautomation.famfood.Entities.TrendEntity;

import java.util.List;

public interface GetTrendListener {
    void onCompleteTask(List<TrendEntity> trendEntityList);
    void onErrorOccured();
}
