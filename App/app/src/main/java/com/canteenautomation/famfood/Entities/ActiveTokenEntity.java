package com.canteenautomation.famfood.Entities;

public class ActiveTokenEntity {

    private int activeToken;

    public ActiveTokenEntity(int activeToken) {
        this.activeToken = activeToken;
    }

    public ActiveTokenEntity() {
    }

    public int getActiveToken() {
        return activeToken;
    }

    public void setActiveToken(int activeToken) {
        this.activeToken = activeToken;
    }
}
