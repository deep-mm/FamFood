package com.canteenautomation.famfood.Entities;

public class TrendEntity {
    private String itemId;
    private Integer quantityOrdered;
    private Double avgRating;
    private Integer quantityRated;

    public TrendEntity(String itemId, Integer quantityOrdered, Double avgRating, Integer quantityRated) {
        this.itemId = itemId;
        this.quantityOrdered = quantityOrdered;
        this.avgRating = avgRating;
        this.quantityRated = quantityRated;
    }

    public TrendEntity() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getQuantityRated() {
        return quantityRated;
    }

    public void setQuantityRated(Integer quantityRated) {
        this.quantityRated = quantityRated;
    }
}
