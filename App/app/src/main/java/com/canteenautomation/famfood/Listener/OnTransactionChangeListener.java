package com.canteenautomation.famfood.Listener;

import com.canteenautomation.famfood.Entities.ItemCategoryEntity;
import com.canteenautomation.famfood.Entities.TransactionEntity;

import java.util.List;

public interface OnTransactionChangeListener {
    void onDataChanged(TransactionEntity transactionEntity);
    void onErrorOccured();
}
