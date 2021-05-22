package com.canteenautomation.famfood.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.canteenautomation.famfood.Entities.AddressEntity;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.FeedbackEntity;
import com.canteenautomation.famfood.Entities.ItemCategoryEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.Entities.TrendEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SharedData {

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public Gson gson;
    public Context context;

    public SharedData(Context c) {
        this.context = c;
        pref = c.getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        gson = new Gson();
    }

    public Boolean isLoggedIn(){
        Boolean check = pref.getBoolean("login", false);
        return check;
    }

    public void isLoggedIn(Boolean check){
        editor.putBoolean("login", check).commit();
    }

    public Boolean isSignedUp(){
        Boolean check = pref.getBoolean("sign", false);
        return check;
    }

    public void isSignedUp(Boolean check){
        editor.putBoolean("sign", check).commit();
    }

    public void inputSignUpDetails(List<String> details){
        String signupdetail = gson.toJson(details);
        editor.putString("signup",signupdetail).commit();
    }

    public List<String> outputSignUpDetails(){
        String json = pref.getString("signup", "");
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> signupDetails = gson.fromJson(json, listType);
        return signupDetails;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser){
        String type = gson.toJson(firebaseUser);
        editor.putString("firebaseUser",type).commit();
    }

    public FirebaseUser getFirebaseUsers(){
        String json = pref.getString("firebaseUser", "");
        Type listType = new TypeToken<FirebaseUser>() {}.getType();
        FirebaseUser firebaseUser = gson.fromJson(json, listType);
        return firebaseUser;
    }

    public void setUserEntity(UserEntity userEntity){
        String type = gson.toJson(userEntity);
        editor.putString("user",type).commit();
    }

    public UserEntity getUserEntity(){
        String json = pref.getString("user", "");
        Type listType = new TypeToken<UserEntity>() {}.getType();
        UserEntity userEntity = gson.fromJson(json, listType);
        return userEntity;
    }

    public void setCanteenEntities(List<CanteenEntity> canteenEntityList){
        String canteenEntitityList = gson.toJson(canteenEntityList);
        editor.putString("canteenEntitityList",canteenEntitityList).commit();
    }

    public List<CanteenEntity> getCanteenEntitities(){
        String json = pref.getString("canteenEntitityList", "");
        Type listType = new TypeToken<List<CanteenEntity>>() {}.getType();
        List<CanteenEntity> canteenEntityList = gson.fromJson(json, listType);
        return canteenEntityList;
    }

    public void setItemEntities(List<ItemEntity> itemEntities){
        String itemEntityList = gson.toJson(itemEntities);
        editor.putString("itemEntityList",itemEntityList).commit();
    }

    public List<ItemEntity> getItemEntities(){
        String json = pref.getString("itemEntityList", "");
        Type listType = new TypeToken<List<ItemEntity>>() {}.getType();
        List<ItemEntity> itemEntities = gson.fromJson(json, listType);
        return itemEntities;
    }

    public void setCart(HashMap<String,Integer> cartItems){
        String cartItemList = gson.toJson(cartItems);
        editor.putString("cartItemList",cartItemList).commit();
    }
    public HashMap<String,Integer> getCart(){
        String json = pref.getString("cartItemList", "");
        Type listType = new TypeToken<HashMap<String,Integer>>() {}.getType();
        HashMap<String,Integer> cartItemList = gson.fromJson(json, listType);
        return cartItemList;
    }

    public void setCanteenEntity(CanteenEntity canteenEntity){
        String type = gson.toJson(canteenEntity);
        editor.putString("canteenEntity",type).commit();
    }

    public CanteenEntity getCanteenEntity(){
        String json = pref.getString("canteenEntity", "");
        Type listType = new TypeToken<CanteenEntity>() {}.getType();
        CanteenEntity canteenEntity = gson.fromJson(json, listType);
        return canteenEntity;
    }

    public void setJainItems(List<String> items){
        String jainItems = gson.toJson(items);
        editor.putString("jainItems",jainItems).commit();
    }

    public List<String> getJainItems(){
        String json = pref.getString("jainItems", "");
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> jainItems = gson.fromJson(json, listType);
        return jainItems;
    }

    public void setOrderEntity(OrderEntity orderEntity){
        String type = gson.toJson(orderEntity);
        editor.putString("orderEntity",type).commit();
    }

    public OrderEntity getOrderEntity(){
        String json = pref.getString("orderEntity", "");
        Type listType = new TypeToken<OrderEntity>() {}.getType();
        OrderEntity orderEntity = gson.fromJson(json, listType);
        return orderEntity;
    }

    public void setItemCategoryEntity(List<ItemCategoryEntity> itemCategoryEntity){
        String itemCategoryEntities = gson.toJson(itemCategoryEntity);
        editor.putString("itemCategoryEntities",itemCategoryEntities).commit();
    }

    public List<ItemCategoryEntity> getItemCategoryEntity(){
        String json = pref.getString("itemCategoryEntities", "");
        Type listType = new TypeToken<List<ItemCategoryEntity>>() {}.getType();
        List<ItemCategoryEntity> itemCategoryEntities = gson.fromJson(json, listType);
        return itemCategoryEntities;
    }

    public void setOrderItemEntity(OrderItemEntitty orderItemEntity){
        String type = gson.toJson(orderItemEntity);
        editor.putString("orderItemEntity",type).commit();
    }

    public OrderItemEntitty getOrderItemEntity(){
        String json = pref.getString("orderItemEntity", "");
        Type listType = new TypeToken<OrderItemEntitty>() {}.getType();
        OrderItemEntitty orderItemEntitty = gson.fromJson(json, listType);
        return orderItemEntitty;
    }

    public void setOrderEntityList(List<OrderEntity> orderEntities){
        String orderEntitiyList = gson.toJson(orderEntities);
        editor.putString("orderEntities",orderEntitiyList).commit();
    }

    public List<OrderEntity> getOrderEntityList(){
        String json = pref.getString("orderEntities", "");
        Type listType = new TypeToken<List<OrderEntity>>() {}.getType();
        List<OrderEntity> orderEntities = gson.fromJson(json, listType);
        return orderEntities;
    }

    public Boolean isJainChecked(){
        Boolean check = pref.getBoolean("jainCheck", false);
        return check;
    }

    public void isJainChecked(Boolean check){
        editor.putBoolean("jainCheck", check).commit();
    }

    public int isPriceSort(){
        int check = pref.getInt("priceCheck", 0);
        return check;
    }

    public void isPriceSort(int check){
        editor.putInt("priceCheck", check).commit();
    }

    public int isTimeSort(){
        int check = pref.getInt("timeCheck", 0);
        return check;
    }

    public void isTimeSort(int check){
        editor.putInt("timeCheck", check).commit();
    }

    public void clear(){
        editor.clear();
        editor.commit();
    }

    public void setAddressEntity(AddressEntity addressEntity){
        String type = gson.toJson(addressEntity);
        editor.putString("addressEntity",type).commit();
    }

    public AddressEntity getAddressEntity(){
        String json = pref.getString("addressEntity", "");
        Type listType = new TypeToken<AddressEntity>() {}.getType();
        AddressEntity addressEntity = gson.fromJson(json, listType);
        return addressEntity;
    }

    public void setAddrFlag(int flag){
        editor.putInt("addrFlag", flag).commit();
    }

    public int getAddrFlag(){
        int flag = pref.getInt("addrFlag", 0);
        return flag;
    }

    public void setActiveOrderEntityList(List<OrderEntity> orderEntities){
        String orderEntitiyList = gson.toJson(orderEntities);
        editor.putString("activeOrderEntities",orderEntitiyList).commit();
    }

    public List<OrderEntity> getActiveOrderEntityList(){
        String json = pref.getString("activeOrderEntities", "");
        Type listType = new TypeToken<List<OrderEntity>>() {}.getType();
        List<OrderEntity> orderEntities = gson.fromJson(json, listType);
        return orderEntities;
    }

    public void setFeedbackEntityList(List<FeedbackEntity> feedbackEntities){
        String feedbackEntityList = gson.toJson(feedbackEntities);
        editor.putString("feedbackEntities",feedbackEntityList).commit();
    }

    public List<FeedbackEntity> getFeedbackEntityList(){
        String json = pref.getString("feedbackEntities", "");
        Type listType = new TypeToken<List<FeedbackEntity>>() {}.getType();
        List<FeedbackEntity> feedbackEntities = gson.fromJson(json, listType);
        return feedbackEntities;
    }

    public void setFeedbackValues(List<Float> items){
        String feedbacks = gson.toJson(items);
        editor.putString("feedbackValues",feedbacks).commit();
    }

    public List<Float> getFeedbackValues(){
        String json = pref.getString("feedbackValues", "");
        Type listType = new TypeToken<List<Float>>() {}.getType();
        List<Float> feedbacks = gson.fromJson(json, listType);
        return feedbacks;
    }

    public void setTrendList(List<TrendEntity> trendEntities){
        String trendEntityList = gson.toJson(trendEntities);
        editor.putString("trendEntities",trendEntityList).commit();
    }

    public List<TrendEntity> getTrendList(){
        String json = pref.getString("trendEntities", "");
        Type listType = new TypeToken<List<TrendEntity>>() {}.getType();
        List<TrendEntity> trendEntities = gson.fromJson(json, listType);
        return trendEntities;
    }

    public void setTrendingItems(List<String> trendEntities){
        String trendEntityList = gson.toJson(trendEntities);
        editor.putString("trendingItems",trendEntityList).commit();
    }

    public List<String> getTrendingItems(){
        String json = pref.getString("trendingItems", "");
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> trendEntities = gson.fromJson(json, listType);
        return trendEntities;
    }

}
