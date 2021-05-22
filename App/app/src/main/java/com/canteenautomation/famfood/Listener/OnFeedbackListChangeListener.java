package com.canteenautomation.famfood.Listener;

import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.FeedbackEntity;

import java.util.List;

public interface OnFeedbackListChangeListener {
    void onDataChanged(List<FeedbackEntity> feedbackEntityList);
    void onErrorOccured();
}
