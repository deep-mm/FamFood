package com.canteenautomation.famfood.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.canteenautomation.famfood.Adapter.MenuItemAdapter;
import com.canteenautomation.famfood.Entities.ItemCategoryEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.TrendEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.OnItemQuantityChange;
import com.canteenautomation.famfood.Listener.OnItemQuantityChangeListener;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.MenuUtility;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.canteenautomation.famfood.Activity.ItemActivity.sortIcon;
import static com.facebook.FacebookSdk.getApplicationContext;

public class CategoryFragment extends android.support.v4.app.Fragment {

    // Store instance variables
    private String id, name, photo;
    private SharedData sharedData;
    private Helper helper;
    private List<ItemEntity> itemEntities;
    private OnItemQuantityChangeListener onItemQuantityChangeListener;
    private List<ItemEntity> allItemEntities;
    private MenuUtility menuUtility;
    private List<ItemCategoryEntity> itemCategoryEntities;
    private ImageView categoryImage;
    private GifImageView gifImage;
    private RelativeLayout gifLayout;

    // newInstance constructor for creating fragment with arguments
    public static CategoryFragment newInstance(String id, String name, String photo, OnItemQuantityChangeListener onItemQuantityChangeListener) {
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("id",id);
        args.putString("photo",photo);
        categoryFragment.setArguments(args);
        return categoryFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = getArguments().getString("name");
        id = getArguments().getString("id");
        photo = getArguments().getString("photo");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category,
                container, false);

        initialize();
        itemCategoryEntities = sharedData.getItemCategoryEntity();

        categoryImage = (ImageView) view.findViewById(R.id.category_image);
        gifImage = (GifImageView) view.findViewById(R.id.gif_image);
        gifLayout = (RelativeLayout) view.findViewById(R.id.gifLayout);
        if(name.equals("Favourites")){
            try {
                /*GifDrawable gifFromResource = new GifDrawable( getResources(), R.drawable.fav_gif2 );
                gifImage.setBackground(gifFromResource);*/
                categoryImage.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            gifLayout.setVisibility(View.INVISIBLE);
            gifImage.setVisibility(View.INVISIBLE);
            Picasso.get()
                    .load(photo)
                    .into(categoryImage);
        }
        if(itemCategoryEntities==null) {
            itemCategoryEntities = new ArrayList<>();
            /*ItemCategoryEntity itemCategoryEntity = new ItemCategoryEntity(name,itemEntities);
            menuUtility.addMenuItem(name, itemCategoryEntity);*/
        }

        if(name.equals("Favourites")){
            UserEntity userEntity = sharedData.getUserEntity();
            if(userEntity!=null){
                List<String> fav = userEntity.getFavourites();
                if(fav==null)
                    fav = new ArrayList<>();

                for (int i = 0; i < itemCategoryEntities.size(); i++) {
                    ItemCategoryEntity itemCategoryEntity = itemCategoryEntities.get(i);
                    allItemEntities.addAll(itemCategoryEntity.getItemEntities());
                }

                for(int i=0;i<allItemEntities.size();i++){
                    ItemEntity itemEntity = allItemEntities.get(i);
                    if(fav.contains(itemEntity.getItemId()))
                        itemEntities.add(itemEntity);
                }
            }
        }
        else if(name.equals("Recommended")){
            categoryImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            List<String> trendItems = sharedData.getTrendingItems();
            if(trendItems!=null){

                for (int i = 0; i < itemCategoryEntities.size(); i++) {
                    ItemCategoryEntity itemCategoryEntity = itemCategoryEntities.get(i);
                    allItemEntities.addAll(itemCategoryEntity.getItemEntities());
                }

                for(int i=0;i<allItemEntities.size();i++){
                    ItemEntity itemEntity = allItemEntities.get(i);
                    if(trendItems.contains(itemEntity.getItemId()))
                        if(itemEntities.size()<10)
                            itemEntities.add(itemEntity);
                        else
                            break;
                }
            }
        }
        else {
            for (int i = 0; i < itemCategoryEntities.size(); i++) {
                ItemCategoryEntity itemCategoryEntity = itemCategoryEntities.get(i);
                allItemEntities.addAll(itemCategoryEntity.getItemEntities());
                String catName = itemCategoryEntity.getCategoryName();
                if (catName.equals(name))
                    itemEntities = itemCategoryEntity.getItemEntities();
            }
        }

        filter();

        RecyclerView itemList = (RecyclerView) view.findViewById(R.id.menu_items);
        RecyclerView.LayoutManager mlayoutmanager = new LinearLayoutManager(getApplicationContext());
        itemList.setLayoutManager(mlayoutmanager);
        itemList.setItemAnimator(new DefaultItemAnimator());

        calcCartValue();
        MenuItemAdapter menuItemAdapter = new MenuItemAdapter(name,itemEntities, getApplicationContext(), new OnItemQuantityChange() {
            @Override
            public void onQuantityChange() {
                calcCartValue();
            }
        });
        itemList.setAdapter(menuItemAdapter);
        /*TextView category = (TextView) view.findViewById(R.id.category);
        category.setText(name);*/
        return view;
    }

    public void initialize(){
        sharedData = new SharedData(getApplicationContext());
        helper = new Helper(getApplicationContext());
        menuUtility = new MenuUtility(sharedData.getCanteenEntity().getId());
        allItemEntities = new ArrayList<>();
        itemCategoryEntities = new ArrayList<>();
        itemEntities = new ArrayList<>();
    }

    public void filter(){
        for(int i=0;i<itemEntities.size();i++){
            ItemEntity itemEntity = itemEntities.get(i);
            if(sharedData.isJainChecked()){
                if(itemEntity.getJain()==false) {
                    itemEntities.remove(i);
                    i--;
                }
            }
        }

        if(sharedData.isPriceSort()==1) {
            Collections.sort(itemEntities, new Comparator<ItemEntity>() {
                public int compare(ItemEntity obj1, ItemEntity obj2) {
                    return obj1.getPrice().compareToIgnoreCase(obj2.getPrice()); // To compare string values
                }
            });
        }

        if(sharedData.isPriceSort()==2){
            Collections.sort(itemEntities, new Comparator<ItemEntity>() {
                public int compare(ItemEntity obj1, ItemEntity obj2) {
                     return obj2.getPrice().compareToIgnoreCase(obj1.getPrice());
                }
            });
        }

        if(sharedData.isTimeSort()==1) {
            Collections.sort(itemEntities, new Comparator<ItemEntity>() {
                public int compare(ItemEntity obj1, ItemEntity obj2) {
                    return obj1.getExpectedTime().compareToIgnoreCase(obj2.getExpectedTime()); // To compare string values
                }
            });
        }

        if(sharedData.isTimeSort()==2){
            Collections.sort(itemEntities, new Comparator<ItemEntity>() {
                public int compare(ItemEntity obj1, ItemEntity obj2) {
                    return obj2.getExpectedTime().compareToIgnoreCase(obj1.getExpectedTime());
                }
            });
        }
    }

    public void calcCartValue(){
            List<ItemEntity> cartItems = getCartItems();
            Double cartTotalVal = calcTotal(cartItems);
            ItemActivity.totalPrice.setText("₹ "+String.valueOf(cartTotalVal));
    }

    public List<ItemEntity> getCartItems(){
        HashMap<String,Integer> items = sharedData.getCart();
        if(items==null){
            items = new HashMap<>();
            sharedData.setCart(items);
        }

        List<ItemEntity> itemEntityList = new ArrayList<ItemEntity>();
        for(int i=0;i<allItemEntities.size();i++){
            ItemEntity itemEntity = allItemEntities.get(i);
            if(items.containsKey(itemEntity.getItemId()))
                itemEntityList.add(itemEntity);
        }
        return itemEntityList;
    }

    public Double calcTotal(List<ItemEntity> itemEntityList){
        HashMap<String,Integer> cartItems = sharedData.getCart();
        Double total = 0.0;
        Integer quantity = 0;
        for(int i=0;i<itemEntityList.size();i++){
            ItemEntity itemEntity = itemEntityList.get(i);
            Double price = Double.valueOf(itemEntity.getPrice());
            int quant = cartItems.get(itemEntity.getItemId());
            Double totalPr = price*quant;
            total = total + totalPr;
            quantity = quantity + quant;
        }
        ItemActivity.itemQuantity.setText(String.valueOf(quantity));
        ItemActivity.itemQuantityCartTop.setText(String.valueOf(quantity));
        if(quantity<=0)
            ItemActivity.cartPopup.setVisibility(View.GONE);
        else
            ItemActivity.cartPopup.setVisibility(View.VISIBLE);
        return total;
    }
}
