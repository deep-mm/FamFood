package com.canteenautomation.famfood.Entities;

import java.util.List;

public class CanteenEntity {
    private String id;
    private String photo;
    private String name;
    private String rating;
    private String location;
    private String owner;
    private List<String> workers;
    private List<String> categories;
    private List<String> collegeDelivers;
    private int status;
    private List<String> peakHours;
    private ActiveTokenEntity activeTokenEntity;
    private PaytmAPIEntity paytmAPIEntity;

    public CanteenEntity() {
    }

    public CanteenEntity(String id, String photo, String name, String rating, String location, String owner, List<String> workers, List<String> categories) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.rating = rating;
        this.location = location;
        this.owner = owner;
        this.workers = workers;
        this.categories = categories;
    }

    public CanteenEntity(String id, String photo, String name, String rating, String location) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.rating = rating;
        this.location = location;
    }

    public CanteenEntity(String id, String photo, String name, String rating, String location, List<String> categories) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.rating = rating;
        this.location = location;
        this.categories = categories;
    }

    public CanteenEntity(String id, String photo, String name, String rating, String location, String owner, List<String> workers, List<String> categories, List<String> collegeDelivers) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.rating = rating;
        this.location = location;
        this.owner = owner;
        this.workers = workers;
        this.categories = categories;
        this.collegeDelivers = collegeDelivers;
    }

    public List<String> getCollegeDelivers() {
        return collegeDelivers;
    }

    public void setCollegeDelivers(List<String> collegeDelivers) {
        this.collegeDelivers = collegeDelivers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getWorkers() {
        return workers;
    }

    public void setWorkers(List<String> workers) {
        this.workers = workers;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getPeakHours() {
        return peakHours;
    }

    public void setPeakHours(List<String> peakHours) {
        this.peakHours = peakHours;
    }

    public ActiveTokenEntity getActiveTokenEntity() {
        return activeTokenEntity;
    }

    public void setActiveTokenEntity(ActiveTokenEntity activeTokenEntity) {
        this.activeTokenEntity = activeTokenEntity;
    }

    public PaytmAPIEntity getPaytmAPIEntity() {
        return paytmAPIEntity;
    }

    public void setPaytmAPIEntity(PaytmAPIEntity paytmAPIEntity) {
        this.paytmAPIEntity = paytmAPIEntity;
    }
}
