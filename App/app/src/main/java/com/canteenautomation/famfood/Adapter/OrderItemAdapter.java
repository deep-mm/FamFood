package com.canteenautomation.famfood.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.canteenautomation.famfood.Activity.TrackOrder;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderHistoryItemEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.Listener.OnItemQuantityChange;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder>{
    private final List<OrderHistoryItemEntity> Mvalues;
    Context context;
    private SharedData sharedData;
    private Helper helper;
    private OrderItemEntitty orderItemEntitty;
    private OrderHistoryItemEntity orderHistoryItemEntity;
    private ItemEntity itemEntity;

    public OrderItemAdapter() {
        Mvalues = null;
    }

    public OrderItemAdapter(List<OrderHistoryItemEntity> mvalues, Context c) {
        Mvalues = mvalues;
        context = c;
        sharedData = new SharedData(context);
        helper = new Helper(context);
    }

    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order_history_item, parent, false);
        return new OrderItemAdapter.ViewHolder(view);

    }
    @Override
    public void onBindViewHolder(final OrderItemAdapter.ViewHolder holder, final int position) {

        holder.itemName.setTypeface(helper.comic);
        holder.itemPrice.setTypeface(helper.comic);
        holder.itemQuantity.setTypeface(helper.comic);
        holder.totalItemPrice.setTypeface(helper.comic);
        holder.trackOrder.setTypeface(helper.cambria);

        orderHistoryItemEntity = Mvalues.get(position);
        orderItemEntitty = orderHistoryItemEntity.getOrderItemEntitty();
        itemEntity = orderHistoryItemEntity.getItemEntity();
        if(itemEntity!=null) {
            final String itemId = itemEntity.getItemId();

        if(orderItemEntitty.isJain()) {
            holder.jainIcon.setVisibility(View.VISIBLE);
        }
        else {
            holder.jainIcon.setVisibility(View.INVISIBLE);
        }

            holder.itemName.setText(itemEntity.getItemName());
            holder.itemPrice.setText("₹ "+itemEntity.getPrice());
            holder.itemQuantity.setText("x " + orderItemEntitty.getQuantity());

            float price = Float.valueOf(itemEntity.getPrice());
            int quantity = orderItemEntitty.getQuantity();
            float total = price * quantity;

            holder.totalItemPrice.setText("₹ "+String.valueOf(total));
        }

        final OrderItemEntitty current = orderItemEntitty;

        holder.trackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TrackOrder.class);
                intent.putExtra("id",current.getItemId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Mvalues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView vegIcon, jainIcon;
        TextView itemName, itemPrice, itemQuantity, totalItemPrice;
        Button trackOrder;

        public ViewHolder(View itemView) {
            super(itemView);
            vegIcon = (ImageView) itemView.findViewById(R.id.vegIcon);
            jainIcon = (ImageView) itemView.findViewById(R.id.jainIcon);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            itemQuantity = (TextView) itemView.findViewById(R.id.itemQuantity);
            totalItemPrice = (TextView) itemView.findViewById(R.id.total_item_price);
            trackOrder = (Button) itemView.findViewById(R.id.track_item);
        }
    }
}