package com.canteenautomation.famfood.Entities;

import java.util.HashMap;

public class FeedbackEntity {

    private String feedbackId;
    private String userId;
    private String userName;
    private String userGender;
    private HashMap<String,Float> itemReviews;
    private String review;
    private String orderId;
    private Float overallReview;
    private String dateTime;

    public FeedbackEntity() {
    }

    public FeedbackEntity(String feedbackId, String userId, HashMap<String, Float> itemReviews, String review, String orderId, Float overallReview, String dateTime) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.itemReviews = itemReviews;
        this.review = review;
        this.orderId = orderId;
        this.overallReview = overallReview;
        this.dateTime = dateTime;
    }

    public FeedbackEntity(String feedbackId, String userId, String userName, String userGender, String dateTime) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.userName = userName;
        this.userGender = userGender;
        this.dateTime = dateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public HashMap<String, Float> getItemReviews() {
        return itemReviews;
    }

    public void setItemReviews(HashMap<String, Float> itemReviews) {
        this.itemReviews = itemReviews;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Float getOverallReview() {
        return overallReview;
    }

    public void setOverallReview(Float overallReview) {
        this.overallReview = overallReview;
    }
}
