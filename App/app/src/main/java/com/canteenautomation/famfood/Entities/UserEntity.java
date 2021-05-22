package com.canteenautomation.famfood.Entities;

import java.util.HashMap;
import java.util.List;

public class UserEntity {

    private String firebaseID;
    private String lastLogin;
    private String userType;
    private List<String> recentCanteens;
    private String gender;
    private String DOB;
    private String userMobile;
    private String userEmail;
    private Integer points;
    private List<String> favourites;
    private HashMap<String,String> orders;
    private String name;
    private List<AddressEntity> addresses;

    public UserEntity() {

    }

    public UserEntity(String name, String firebaseID, String lastLogin, String gender, String DOB, String userMobile, String userEmail) {
        this.name = name;
        this.firebaseID = firebaseID;
        this.lastLogin = lastLogin;
        this.gender = gender;
        this.DOB = DOB;
        this.userMobile = userMobile;
        this.userEmail = userEmail;
    }

    public UserEntity(String firebaseID, String lastLogin, String userType, String gender, String DOB, String userMobile, String userEmail, String name) {
        this.firebaseID = firebaseID;
        this.lastLogin = lastLogin;
        this.userType = userType;
        this.gender = gender;
        this.DOB = DOB;
        this.userMobile = userMobile;
        this.userEmail = userEmail;
        this.name = name;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<String> getRecentCanteens() {
        return recentCanteens;
    }

    public void setRecentCanteens(List<String> recentCanteens) {
        this.recentCanteens = recentCanteens;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public List<String> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<String> favourites) {
        this.favourites = favourites;
    }

    public HashMap<String, String> getOrders() {
        return orders;
    }

    public void setOrders(HashMap<String, String> orders) {
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressEntity> addresses) {
        this.addresses = addresses;
    }
}
