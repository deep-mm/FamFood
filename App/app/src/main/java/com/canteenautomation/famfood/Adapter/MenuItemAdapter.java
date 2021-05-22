package com.canteenautomation.famfood.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canteenautomation.famfood.Activity.ItemActivity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.OnItemQuantityChange;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.canteenautomation.famfood.Utilities.UserUtility;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder>{
    private  List<ItemEntity> Mvalues;
    Context context;
    private SharedData sharedData;
    private OnItemQuantityChange onItemQuantityChange;
    private Helper helper;
    private String category;

    public MenuItemAdapter() {
        Mvalues = null;

    }

    public MenuItemAdapter(String category, List<ItemEntity> mvalues, Context c, OnItemQuantityChange onItemQuantityChange) {
        Mvalues = mvalues;
        context = c;
        sharedData = new SharedData(context);
        helper = new Helper(context);
        this.onItemQuantityChange = onItemQuantityChange;
        this.category = category;
    }

    @Override
    public MenuItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new MenuItemAdapter.ViewHolder(view);

    }
    @Override
    public void onBindViewHolder(final MenuItemAdapter.ViewHolder holder, final int position) {

        holder.itemName.setTypeface(helper.comic);
        holder.itemPrice.setTypeface(helper.comic);
        holder.itemQuantity.setTypeface(helper.comic);
        holder.cartAdd.setTypeface(helper.cambria);
        holder.expectedTime.setTypeface(helper.comic);

        final ItemEntity itemEntity = Mvalues.get(position);
        final UserUtility userUtility = new UserUtility();
        final String itemId = itemEntity.getItemId();

        if(itemEntity.getJain())
            holder.jainIcon.setVisibility(View.VISIBLE);
        else
            holder.jainIcon.setVisibility(View.INVISIBLE);

        holder.itemName.setText(itemEntity.getItemName());
        holder.itemPrice.setText("₹ "+itemEntity.getPrice());
        holder.expectedTime.setText(itemEntity.getExpectedTime()+"m");

        final int pos = position;

        if(!sharedData.isLoggedIn())
            holder.favIcon.setVisibility(View.INVISIBLE);

        final UserEntity userEntity = sharedData.getUserEntity();
        List<String> favItems;
        if(userEntity==null){
            favItems = new ArrayList<>();
        }
        else {
            favItems = userEntity.getFavourites();
        }
        if(favItems==null){
            favItems = new ArrayList<>();
            userEntity.setFavourites(favItems);
            sharedData.setUserEntity(userEntity);
            userUtility.addUser(userEntity);
        }
        if(favItems.contains(itemId)) {
            holder.favIcon.setBackgroundDrawable(holder.itemView.getResources().getDrawable(R.mipmap.fav_icon_filled));
        }
        else{
            holder.favIcon.setBackgroundDrawable(holder.itemView.getResources().getDrawable(R.mipmap.fav_icon_empty));
        }

        holder.cartAdd.setVisibility(View.VISIBLE);
        holder.quantityBar.setVisibility(View.GONE);
        HashMap<String,Integer> cartItems = sharedData.getCart();
        if(cartItems.containsKey(itemId)){
            int quantity = cartItems.get(itemId);
            holder.itemQuantity.setText(Integer.toString(quantity));
            if(quantity>0) {
                holder.cartAdd.setVisibility(View.GONE);
                holder.quantityBar.setVisibility(View.VISIBLE);
            }
        }

        holder.favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userEntity == null) {
                    helper.printToast("Log in first to access this feature", 1);
                } else {
                    UserEntity userEntity = sharedData.getUserEntity();
                    List<String> favItems = userEntity.getFavourites();
                    if (favItems == null) {
                        favItems = new ArrayList<>();
                        userEntity.setFavourites(favItems);
                        sharedData.setUserEntity(userEntity);
                        userUtility.addUser(userEntity);
                    }
                    if (!favItems.contains(itemId)) {
                        holder.favIcon.setBackgroundDrawable(view.getResources().getDrawable(R.mipmap.fav_icon_filled));
                        favItems.add(itemId);
                        userEntity.setFavourites(favItems);
                        sharedData.setUserEntity(userEntity);
                        userUtility.addUser(userEntity);
                    } else {
                        holder.favIcon.setBackgroundDrawable(view.getResources().getDrawable(R.mipmap.fav_icon_empty));
                        favItems.remove(itemId);
                        userEntity.setFavourites(favItems);
                        sharedData.setUserEntity(userEntity);
                        userUtility.addUser(userEntity);
                        if(category.equals("Favourites")) {
                            removeAt(holder.getAdapterPosition());
                        }
                    }
                }
            }
        });

        holder.cartAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.cartAdd.setVisibility(View.GONE);
                holder.quantityBar.setVisibility(View.VISIBLE);
                HashMap<String,Integer> cartItems = sharedData.getCart();
                if(cartItems.containsKey(itemId)){
                    int quantity = cartItems.get(itemId);
                    quantity++;
                    holder.itemQuantity.setText(Integer.toString(quantity));
                    cartItems.put(itemId,quantity);
                    sharedData.setCart(cartItems);
                }
                else{
                    int quantity = 1;
                    holder.itemQuantity.setText(Integer.toString(quantity));
                    cartItems.put(itemId,quantity);
                    sharedData.setCart(cartItems);
                }
                onItemQuantityChange.onQuantityChange();
            }
        });

        holder.quantityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Integer> cartItems = sharedData.getCart();
                if(cartItems.containsKey(itemId)){
                    int quantity = cartItems.get(itemId);
                    if(quantity==9)
                        helper.printToast("You can select a max of 9 items",0);
                    else {
                        quantity++;
                        holder.itemQuantity.setText(Integer.toString(quantity));
                        cartItems.put(itemId, quantity);
                        sharedData.setCart(cartItems);
                    }
                }
                onItemQuantityChange.onQuantityChange();
            }
        });

        holder.quantitySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Integer> cartItems = sharedData.getCart();
                if(cartItems.containsKey(itemId)){
                    int quantity = cartItems.get(itemId);
                    quantity--;
                    if(quantity==0) {
                        holder.cartAdd.setVisibility(View.VISIBLE);
                        holder.quantityBar.setVisibility(View.GONE);
                        cartItems.remove(itemId);
                        holder.itemQuantity.setText(Integer.toString(quantity));
                        sharedData.setCart(cartItems);
                    }
                    else {
                        holder.itemQuantity.setText(Integer.toString(quantity));
                        cartItems.put(itemId, quantity);
                        sharedData.setCart(cartItems);
                    }
                }
                onItemQuantityChange.onQuantityChange();
            }
        });

    }

    public void removeAt(int position) {
        Mvalues.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, Mvalues.size());
    }

    @Override
    public int getItemCount() {
        return Mvalues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView vegIcon, jainIcon;
        TextView itemName, itemPrice, itemQuantity, expectedTime;
        ImageButton favIcon, quantitySub, quantityAdd;
        Button cartAdd;
        LinearLayout quantityBar;

        public ViewHolder(View itemView) {
            super(itemView);
            vegIcon = (ImageView) itemView.findViewById(R.id.vegIcon);
            jainIcon = (ImageView) itemView.findViewById(R.id.jainIcon);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            itemQuantity = (TextView) itemView.findViewById(R.id.quantity);
            favIcon = (ImageButton) itemView.findViewById(R.id.fav_icon);
            quantitySub = (ImageButton) itemView.findViewById(R.id.quantity_sub);
            quantityAdd = (ImageButton) itemView.findViewById(R.id.quantity_add);
            cartAdd = (Button) itemView.findViewById(R.id.cart_add_button);
            quantityBar = (LinearLayout) itemView.findViewById(R.id.quantity_bar);
            expectedTime = (TextView) itemView.findViewById(R.id.expectedTime);
        }
    }
    public void filterList(ArrayList<ItemEntity> filteredList) {
        Mvalues = filteredList;
        notifyDataSetChanged();
    }

}