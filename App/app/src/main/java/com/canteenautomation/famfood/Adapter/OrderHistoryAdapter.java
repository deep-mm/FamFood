package com.canteenautomation.famfood.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.canteenautomation.famfood.Activity.CartActivity;
import com.canteenautomation.famfood.Activity.FeedbackActivity;
import com.canteenautomation.famfood.Activity.OrderHistoryDetail;
import com.canteenautomation.famfood.Activity.TokenActivity;
import com.canteenautomation.famfood.Activity.TrackOrder;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {
    private final List<OrderEntity> fvalues ;
    Context context;
    private SharedData sharedData;
    private Helper helper;
    private OrderEntity orderEntity;

    public OrderHistoryAdapter() {
        fvalues = null;

    }
    public OrderHistoryAdapter(List<OrderEntity> fvalues, Context c) {
        this.fvalues = fvalues;
        context=c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order_history, parent, false);
        return new OrderHistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        orderEntity = fvalues.get(position);

        sharedData = new SharedData(context);
        helper = new Helper(context);

        holder.mamounttext.setTypeface(helper.comicBold);
        holder.mdatetime.setTypeface(helper.comic);
        holder.mcollegename.setTypeface(helper.comicBold);
        holder.mamount.setTypeface(helper.comic);
        holder.mitemlist.setTypeface(helper.comic);
        holder.mitemtext.setTypeface(helper.comicBold);
        holder.track.setTypeface(helper.cambria);
        holder.reorder.setTypeface(helper.cambria);
        holder.mfeedback.setTypeface(helper.comic);
        holder.failedTransaction.setTypeface(helper.cambria);

        StringBuffer stringBuffer1 = new StringBuffer(orderEntity.getOrder_time());
        String canteenId = orderEntity.getCanteen_id();
        List<CanteenEntity> canteenEntityList = sharedData.getCanteenEntitities();
        String collegeName = getName(canteenId,canteenEntityList);
        List<OrderItemEntitty> item_list = orderEntity.getOrderItemEntitty();
        String item_names = getItemList(item_list);

        holder.mcollegename.setText(collegeName);
        String orderDate = "Ordered on <b>"+stringBuffer1.substring(0,10)+"</b> at <b>"+stringBuffer1.substring(10)+"</b>";
        holder.mdatetime.setText(Html.fromHtml(orderDate));
        holder.mamount.setText("₹ "+orderEntity.getAmount());
        holder.mitemlist.setText(item_names);

        final OrderEntity current = orderEntity;
        if(orderEntity.getTransactionStatus()==null){
            holder.reorder_layout.setVisibility(View.INVISIBLE);
            holder.feedback_layout.setVisibility(View.INVISIBLE);
            holder.track.setVisibility(View.INVISIBLE);
            holder.failed_layout.setVisibility(View.VISIBLE);
            holder.mainBox.setBackgroundResource(R.drawable.rectangle);
        }
        else if(orderEntity.getTransactionStatus().equals("TXN_FAILURE")){
            holder.reorder_layout.setVisibility(View.INVISIBLE);
            holder.feedback_layout.setVisibility(View.INVISIBLE);
            holder.track.setVisibility(View.INVISIBLE);
            holder.failed_layout.setVisibility(View.VISIBLE);
            holder.mainBox.setBackgroundResource(R.drawable.rectangle);
        }
        else if(checkOrderStatus(orderEntity)) {
            holder.reorder_layout.setVisibility(View.VISIBLE);
            holder.feedback_layout.setVisibility(View.VISIBLE);
            holder.track.setVisibility(View.INVISIBLE);
            holder.failed_layout.setVisibility(View.INVISIBLE);
            holder.mainBox.setBackgroundResource(R.drawable.rectangle);
        }
        else {
            holder.reorder_layout.setVisibility(View.INVISIBLE);
            holder.track.setVisibility(View.VISIBLE);
            holder.feedback_layout.setVisibility(View.GONE);
            holder.failed_layout.setVisibility(View.INVISIBLE);
            holder.mainBox.setBackgroundResource(R.drawable.active_order_rectangle);
        }

        holder.track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(context, TrackOrder.class);
                    sharedData.setOrderEntity(current);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }


        });

        holder.reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Integer> cart = new HashMap<>();
                List<OrderItemEntitty> orderItemEntitties = current.getOrderItemEntitty();
                List<String> items = new ArrayList<>();
                for(int i=0;i<orderItemEntitties.size();i++){
                    OrderItemEntitty orderItemEntitty = orderItemEntitties.get(i);
                    cart.put(orderItemEntitty.getItemId(),orderItemEntitty.getQuantity());
                    if(orderItemEntitty.isJain())
                        items.add(orderItemEntitty.getItemId());
                }
                sharedData.setCart(cart);
                sharedData.setJainItems(items);
                Intent intent = new Intent(context, CartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.feedback_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedbackActivity.class);
                sharedData.setOrderEntity(current);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    public String getItemList(List<OrderItemEntitty> items){
        String itemList = "";

        for(int i=0;i<items.size();i++){
            OrderItemEntitty orderItemEntitty = items.get(i);
            if(orderItemEntitty.getQuantity()>1)
                itemList = itemList + orderItemEntitty.getItemName() + "x" +orderItemEntitty.getQuantity();

            else
                itemList = itemList + orderItemEntitty.getItemName();

            if(orderItemEntitty.isJain())
                itemList = itemList + "(J)";

            if(i!=(items.size()-1)){
                itemList = itemList + ", ";
            }

        }

        return itemList;
    }
    public String getName(String id, List<CanteenEntity> canteenEntities){
        for(int i=0; i<canteenEntities.size();i++){
            CanteenEntity canteenEntity = canteenEntities.get(i);
            if(canteenEntity.getId().equals(id))
                return canteenEntity.getName();
        }
        return "";
    }

    public boolean checkOrderStatus(OrderEntity order){
        List<OrderItemEntitty> orderItemEntitties = order.getOrderItemEntitty();
        for(int i=0;i<orderItemEntitties.size();i++){
            OrderItemEntitty orderItemEntitty = orderItemEntitties.get(i);
            if(orderItemEntitty.getStatus()==4)
                continue;
            else
                return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return fvalues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mcollegename, mdatetime, mamount, mamounttext,mitemtext,mfeedback, mitemlist;
        LinearLayout feedback_layout, reorder_layout, failed_layout;
        Button reorder, track, failedTransaction;
        RelativeLayout mainBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            mcollegename = (TextView) itemView.findViewById(R.id.college_name) ;
            mdatetime = (TextView) itemView.findViewById(R.id.order_dateTime);
            mamount = (TextView) itemView.findViewById(R.id.total_price);
            mamounttext= (TextView) itemView.findViewById(R.id.totslprice_heading);
            mfeedback = (TextView) itemView.findViewById(R.id.feedback_text);
            mitemtext = (TextView) itemView.findViewById(R.id.itemlist_heading );
            reorder = (Button) itemView.findViewById(R.id.re_order_button);
            track = (Button) itemView.findViewById(R.id.track_button);
            mitemlist = (TextView) itemView.findViewById(R.id.item_list);
            feedback_layout = (LinearLayout) itemView.findViewById(R.id.feedback);
            reorder_layout = (LinearLayout) itemView.findViewById(R.id.reorder);
            mainBox = (RelativeLayout) itemView.findViewById(R.id.main_box);
            failedTransaction = (Button) itemView.findViewById(R.id.failedTransaction_button);
            failed_layout = (LinearLayout) itemView.findViewById(R.id.failedTransaction);
        }
    }
}