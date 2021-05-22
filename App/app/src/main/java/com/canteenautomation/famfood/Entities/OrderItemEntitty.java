package com.canteenautomation.famfood.Entities;

import org.w3c.dom.ls.LSException;

import java.util.List;

public class OrderItemEntitty {

    private String itemId;
    private String itemName;
    private int quantity;
    private int status;
    private boolean jain;
    private List<String> statusUpdateTime;
    private String orderPlaced;
    private String confirmed;
    private String inKitchen;
    private String prepared;
    private String delivered;


    public OrderItemEntitty(String itemId, String itemName, int quantity, int status, boolean jain) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.status = status;
        this.jain = jain;
    }

    public OrderItemEntitty(String itemId, String itemName, int quantity, int status, boolean jain, List<String> statusUpdateTime, String orderPlaced) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.status = status;
        this.jain = jain;
        this.statusUpdateTime = statusUpdateTime;
        this.orderPlaced = orderPlaced;
    }

    public String getOrderPlaced() {
        return orderPlaced;
    }

    public void setOrderPlaced(String orderPlaced) {
        this.orderPlaced = orderPlaced;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getInKitchen() {
        return inKitchen;
    }

    public void setInKitchen(String inKitchen) {
        this.inKitchen = inKitchen;
    }

    public String getPrepared() {
        return prepared;
    }

    public void setPrepared(String prepared) {
        this.prepared = prepared;
    }

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    public List<String> getStatusUpdateTime() {
        return statusUpdateTime;
    }

    public void setStatusUpdateTime(List<String> statusUpdateTime) {
        this.statusUpdateTime = statusUpdateTime;
    }

    public OrderItemEntitty() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isJain() {
        return jain;
    }

    public void setJain(boolean jain) {
        this.jain = jain;
    }
}
