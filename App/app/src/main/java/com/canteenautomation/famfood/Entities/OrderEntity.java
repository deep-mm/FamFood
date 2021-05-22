package com.canteenautomation.famfood.Entities;

import java.util.List;

public class OrderEntity {
    private String canteen_id;
    private String order_id;
    private List<OrderItemEntitty> orderItemEntitty;
    private String amount;
    private String special_inst;
    private String coupon_app;
    private String delivery_add;
    private String order_time;
    private String user_id;
    private int delivery_time;
    private int expected_time;
    private String order_rating;
    private int order_token;
    private String order_review;
    private DeliveryManEntity delivery_guy;
    private String transactionStatus;
    private TransactionEntity transactionEntity;

    public OrderEntity(String canteen_id, String order_id, List<OrderItemEntitty> orderItemEntitty, String amount, String special_inst, String coupon_app, String delivery_add, String order_time, String user_id, int delivery_time, int expected_time, String order_rating, int order_token, String order_review, DeliveryManEntity delivery_guy, String transactionStatus, TransactionEntity transactionEntity) {
        this.canteen_id = canteen_id;
        this.order_id = order_id;
        this.orderItemEntitty = orderItemEntitty;
        this.amount = amount;
        this.special_inst = special_inst;
        this.coupon_app = coupon_app;
        this.delivery_add = delivery_add;
        this.order_time = order_time;
        this.user_id = user_id;
        this.delivery_time = delivery_time;
        this.expected_time = expected_time;
        this.order_rating = order_rating;
        this.order_token = order_token;
        this.order_review = order_review;
        this.delivery_guy = delivery_guy;
        this.transactionStatus = transactionStatus;
        this.transactionEntity = transactionEntity;
    }

    public OrderEntity(String canteen_id, String order_id, List<OrderItemEntitty> orderItemEntitty, String amount, String order_time, String user_id) {
        this.canteen_id = canteen_id;
        this.order_id = order_id;
        this.orderItemEntitty = orderItemEntitty;
        this.amount = amount;
        this.order_time = order_time;
        this.user_id = user_id;
    }

    public OrderEntity() {
    }

    public TransactionEntity getTransactionEntity() {
        return transactionEntity;
    }

    public void setTransactionEntity(TransactionEntity transactionEntity) {
        this.transactionEntity = transactionEntity;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getCanteen_id() {
        return canteen_id;
    }

    public void setCanteen_id(String canteen_id) {
        this.canteen_id = canteen_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSpecial_inst() {
        return special_inst;
    }

    public void setSpecial_inst(String special_inst) {
        this.special_inst = special_inst;
    }

    public String getCoupon_app() {
        return coupon_app;
    }

    public void setCoupon_app(String coupon_app) {
        this.coupon_app = coupon_app;
    }

    public String getDelivery_add() {
        return delivery_add;
    }

    public void setDelivery_add(String delivery_add) {
        this.delivery_add = delivery_add;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(int delivery_time) {
        this.delivery_time = delivery_time;
    }

    public int getExpected_time() {
        return expected_time;
    }

    public void setExpected_time(int expected_time) {
        this.expected_time = expected_time;
    }

    public String getOrder_rating() {
        return order_rating;
    }

    public void setOrder_rating(String order_rating) {
        this.order_rating = order_rating;
    }

    public int getOrder_token() {
        return order_token;
    }

    public void setOrder_token(int order_token) {
        this.order_token = order_token;
    }

    public String getOrder_review() {
        return order_review;
    }

    public void setOrder_review(String order_review) {
        this.order_review = order_review;
    }

    public DeliveryManEntity getDelivery_guy() {
        return delivery_guy;
    }

    public void setDelivery_guy(DeliveryManEntity delivery_guy) {
        this.delivery_guy = delivery_guy;
    }

    public List<OrderItemEntitty> getOrderItemEntitty() {
        return orderItemEntitty;
    }

    public void setOrderItemEntitty(List<OrderItemEntitty> orderItemEntitty) {
        this.orderItemEntitty = orderItemEntitty;
    }
}


