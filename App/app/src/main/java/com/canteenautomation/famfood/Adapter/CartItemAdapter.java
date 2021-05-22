package com.canteenautomation.famfood.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Activity.CartActivity;
import com.canteenautomation.famfood.Activity.ItemActivity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.OnItemQuantityChange;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.OrderUtility;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.canteenautomation.famfood.Utilities.UserUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder>{
    private final List<ItemEntity> Mvalues;
    Context context;
    private SharedData sharedData;
    private Helper helper;
    private OnItemQuantityChange onItemQuantityChange;

    public CartItemAdapter() {
        Mvalues = null;

    }

    public CartItemAdapter(List<ItemEntity> mvalues, Context c, OnItemQuantityChange onItemQuantityChange) {
        Mvalues = mvalues;
        context = c;
        sharedData = new SharedData(context);
        helper = new Helper(context);
        this.onItemQuantityChange = onItemQuantityChange;
    }

    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cart_item, parent, false);
        return new CartItemAdapter.ViewHolder(view);

    }
    @Override
    public void onBindViewHolder(final CartItemAdapter.ViewHolder holder, final int position) {

        holder.itemName.setTypeface(helper.comic);
        holder.itemPrice.setTypeface(helper.comic);
        holder.itemQuantity.setTypeface(helper.comic);
        holder.cartJain.setTypeface(helper.cambria);

        final ItemEntity itemEntity = Mvalues.get(position);
        final String itemId = itemEntity.getItemId();

        if(itemEntity.getJain()) {
            holder.cartJain.setVisibility(View.VISIBLE);
        }
        else {
            holder.cartJain.setVisibility(View.INVISIBLE);
        }

        holder.itemName.setText(itemEntity.getItemName());
        holder.itemPrice.setText("₹ "+itemEntity.getPrice());

        HashMap<String,Integer> cartItems = sharedData.getCart();
        List<String> items = sharedData.getJainItems();
        if(items==null) {
            items = new ArrayList<String>();
            sharedData.setJainItems(items);
        }
        else{
            if(items.contains(itemEntity.getItemId())){
                holder.cartJain.setBackground(context.getResources().getDrawable(R.drawable.solid_rectangle_jain_filled));
            }
            else{
                holder.cartJain.setBackground(context.getResources().getDrawable(R.drawable.solid_rectangle_jain));
            }
        }

        if(cartItems.containsKey(itemId)){
            int quantity = cartItems.get(itemId);
            holder.itemQuantity.setText(Integer.toString(quantity));
        }

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
                    onItemQuantityChange.onQuantityChange();
                }
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
                        new MaterialDialog.Builder(holder.itemView.getContext())
                                .title("Confirm")
                                .content("Remove item:"+itemEntity.getItemName()+"\nfrom the cart?")
                                .positiveText("Yes")
                                .negativeText("No")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        HashMap<String,Integer> cartItems = sharedData.getCart();
                                        cartItems.remove(itemId);
                                        sharedData.setCart(cartItems);
                                        //removeItem(position);
                                        List<String> items = sharedData.getJainItems();
                                        if(items.contains(itemEntity.getItemId())){
                                            items.remove(itemId);
                                            sharedData.setJainItems(items);
                                        }
                                        removeAt(holder.getAdapterPosition());
                                        onItemQuantityChange.onQuantityChange();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        //
                                    }
                                })
                                .canceledOnTouchOutside(false)
                                .cancelable(false)
                                .show();
                    }
                    else {
                        holder.itemQuantity.setText(Integer.toString(quantity));
                        cartItems.put(itemId, quantity);
                        sharedData.setCart(cartItems);
                        onItemQuantityChange.onQuantityChange();
                    }

                }
            }
        });


        holder.cartJain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> items = sharedData.getJainItems();
                if(items.contains(itemEntity.getItemId())){
                    items.remove(itemId);
                    sharedData.setJainItems(items);
                    holder.cartJain.setBackground(context.getResources().getDrawable(R.drawable.solid_rectangle_jain));
                }
                else{
                    items.add(itemId);
                    sharedData.setJainItems(items);
                    holder.cartJain.setBackground(context.getResources().getDrawable(R.drawable.solid_rectangle_jain_filled));
                }
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
        ImageView vegIcon;
        TextView itemName, itemPrice, itemQuantity;
        ImageButton quantitySub, quantityAdd;
        Button cartJain;

        public ViewHolder(View itemView) {
            super(itemView);
            vegIcon = (ImageView) itemView.findViewById(R.id.cart_veg);
            itemName = (TextView) itemView.findViewById(R.id.cart_food_text);
            itemPrice = (TextView) itemView.findViewById(R.id.cart_price);
            itemQuantity = (TextView) itemView.findViewById(R.id.quantity);
            quantitySub = (ImageButton) itemView.findViewById(R.id.quantity_sub);
            quantityAdd = (ImageButton) itemView.findViewById(R.id.quantity_add);
            cartJain = (Button) itemView.findViewById(R.id.cart_jain);
        }
    }
}