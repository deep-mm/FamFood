package com.canteenautomation.famfood.Entities;

public class OrderHistoryItemEntity {
    private OrderItemEntitty orderItemEntitty;
    private ItemEntity itemEntity;

    public OrderHistoryItemEntity(OrderItemEntitty orderItemEntitty, ItemEntity itemEntity) {
        this.orderItemEntitty = orderItemEntitty;
        this.itemEntity = itemEntity;
    }

    public OrderItemEntitty getOrderItemEntitty() {
        return orderItemEntitty;
    }

    public void setOrderItemEntitty(OrderItemEntitty orderItemEntitty) {
        this.orderItemEntitty = orderItemEntitty;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    public void setItemEntity(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }
}
