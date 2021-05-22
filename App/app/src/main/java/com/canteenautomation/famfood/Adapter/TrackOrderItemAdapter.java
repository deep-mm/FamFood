package com.canteenautomation.famfood.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.canteenautomation.famfood.Activity.TrackOrder;
import com.canteenautomation.famfood.Entities.ItemCategoryEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.OrderHistoryItemEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class TrackOrderItemAdapter extends RecyclerView.Adapter<TrackOrderItemAdapter.ViewHolder>{
    private final List<OrderItemEntitty> Mvalues;
    Context context;
    private SharedData sharedData;
    private Helper helper;
    private Date startDate, endDate;

    public TrackOrderItemAdapter() {
        Mvalues = null;
    }

    public TrackOrderItemAdapter(List<OrderItemEntitty> mvalues, Context c) {
        Mvalues = mvalues;
        context = c;
        sharedData = new SharedData(context);
        helper = new Helper(context);
    }

    @Override
    public TrackOrderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_track_order_item, parent, false);
        return new TrackOrderItemAdapter.ViewHolder(view);

    }
    @Override
    public void onBindViewHolder(final TrackOrderItemAdapter.ViewHolder holder, final int position) {

        OrderItemEntitty orderItemEntitty = Mvalues.get(position);
        String id = orderItemEntitty.getItemId();
        List<ItemEntity> itemEntities = sharedData.getItemEntities();
        ItemEntity itemEntity = getItem(id,itemEntities);
        if(itemEntity!=null) {
            if(orderItemEntitty.getQuantity()>1)
                holder.itemName.setText(itemEntity.getItemName()+"x "+orderItemEntitty.getQuantity());
            else
                holder.itemName.setText(itemEntity.getItemName());

            double price = Double.parseDouble(itemEntity.getPrice());
            price = price * orderItemEntitty.getQuantity();
            holder.itemPrice.setText("₹ "+price);
            holder.serialNo.setText(Integer.toString(position+1)+")");

            if(orderItemEntitty.isJain())
                holder.jainIcon.setVisibility(View.VISIBLE);

            else
                holder.jainIcon.setVisibility(View.INVISIBLE);
        }

        List<String> statusUpdateTime = orderItemEntitty.getStatusUpdateTime();
        int status = orderItemEntitty.getStatus();
        changeUI(status,holder,orderItemEntitty);

        String dtStart = statusUpdateTime.get(0);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            startDate = df.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.MINUTE,Integer.parseInt(itemEntity.getExpectedTime()));
        endDate = c.getTime();

        System.out.println("Start: "+startDate.toString());
        System.out.println("End: "+endDate.toString());

        Timer updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            public void run() {
                try {
                    Date current = Calendar.getInstance().getTime();
                    long mills = endDate.getTime() - current.getTime();
                    long mins = (mills/(1000*60)) % 60;

                    holder.expectedTime.setText(mins+"m");
                    if(mins<=0){
                        holder.expectedTime.setVisibility(View.INVISIBLE);
                        holder.expectedTimeIcon.setVisibility(View.INVISIBLE);
                    }
                    else{
                        holder.expectedTime.setVisibility(View.VISIBLE);
                        holder.expectedTimeIcon.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, 0, 1000*60); // here 1000 means 1000 mills i.e. 1 second

        holder.serialNo.setTypeface(helper.comic);
        holder.itemName.setTypeface(helper.comicBold);
        holder.itemPrice.setTypeface(helper.comic);
        holder.expectedTime.setTypeface(helper.comic);
        holder.statusButton.setTypeface(helper.cambria);
        holder.updateTime1.setTypeface(helper.comic);
        holder.updateTime2.setTypeface(helper.comic);
        holder.updateTime3.setTypeface(helper.comic);
        holder.updateTime4.setTypeface(helper.comic);
    }

    public void changeUI(int status,final TrackOrderItemAdapter.ViewHolder holder, OrderItemEntitty orderItemEntitty){

        switch(status){
            case 0: firstUpdate(holder, orderItemEntitty.getOrderPlaced());
                    break;

            case 1: firstUpdate(holder, orderItemEntitty.getOrderPlaced());
                    secondUpdate(holder, orderItemEntitty.getConfirmed());
                    break;

            case 2: firstUpdate(holder, orderItemEntitty.getOrderPlaced());
                    secondUpdate(holder, orderItemEntitty.getConfirmed());
                    break;

            case 3: firstUpdate(holder, orderItemEntitty.getOrderPlaced());
                    secondUpdate(holder, orderItemEntitty.getConfirmed());
                    thirdUpdate(holder, orderItemEntitty.getPrepared());
                    break;

            case 4: firstUpdate(holder, orderItemEntitty.getOrderPlaced());
                    secondUpdate(holder, orderItemEntitty.getConfirmed());
                    thirdUpdate(holder, orderItemEntitty.getPrepared());
                    fourthUpdate(holder, orderItemEntitty.getDelivered());
                    break;

            case -1:rejectedUpdate(holder, orderItemEntitty.getConfirmed());
                    break;

            default: firstUpdate(holder, orderItemEntitty.getOrderPlaced());
        }
    }

    public void firstUpdate(final TrackOrderItemAdapter.ViewHolder holder, String time){
        holder.bigYellowCircle1.setVisibility(View.VISIBLE);
        holder.greyCircle2.setVisibility(View.VISIBLE);
        holder.greyCircle3.setVisibility(View.VISIBLE);
        holder.greyCircle4.setVisibility(View.VISIBLE);
        holder.greyLine1.setVisibility(View.VISIBLE);
        holder.greyLine2.setVisibility(View.VISIBLE);
        holder.greyLine3.setVisibility(View.VISIBLE);

        holder.bigRedCircle.setVisibility(View.INVISIBLE);
        holder.bigYellowCircle2.setVisibility(View.INVISIBLE);
        holder.bigYellowCircle3.setVisibility(View.INVISIBLE);
        holder.bigYellowCircle4.setVisibility(View.INVISIBLE);
        holder.smallYelloCircle1.setVisibility(View.INVISIBLE);
        holder.smallYelloCircle2.setVisibility(View.INVISIBLE);
        holder.smallYelloCircle3.setVisibility(View.INVISIBLE);
        holder.smallYelloCircle4.setVisibility(View.INVISIBLE);
        holder.yellowLine1.setVisibility(View.INVISIBLE);
        holder.yellowLine2.setVisibility(View.INVISIBLE);
        holder.yellowLine3.setVisibility(View.INVISIBLE);
        holder.updateTime2.setVisibility(View.INVISIBLE);
        holder.updateTime3.setVisibility(View.INVISIBLE);
        holder.updateTime4.setVisibility(View.INVISIBLE);

        holder.updateTime1.setText(time);
        holder.statusButton.setText("Placed");
        holder.statusButton.setBackgroundColor(context.getResources().getColor(R.color.redShade));
    }

    public void secondUpdate(final TrackOrderItemAdapter.ViewHolder holder, String time){
        holder.bigYellowCircle2.setVisibility(View.VISIBLE);
        holder.smallYelloCircle1.setVisibility(View.VISIBLE);
        holder.greyCircle3.setVisibility(View.VISIBLE);
        holder.greyCircle4.setVisibility(View.VISIBLE);
        holder.yellowLine1.setVisibility(View.VISIBLE);
        holder.greyLine2.setVisibility(View.VISIBLE);
        holder.greyLine3.setVisibility(View.VISIBLE);
        holder.updateTime2.setVisibility(View.VISIBLE);

        holder.bigYellowCircle1.setVisibility(View.INVISIBLE);
        holder.updateTime3.setVisibility(View.INVISIBLE);
        holder.updateTime4.setVisibility(View.INVISIBLE);

        holder.updateTime2.setText(time);
        holder.statusButton.setText("Confirmed");
        holder.statusButton.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
    }

    public void thirdUpdate(final TrackOrderItemAdapter.ViewHolder holder, String time){
        holder.bigYellowCircle3.setVisibility(View.VISIBLE);
        holder.smallYelloCircle1.setVisibility(View.VISIBLE);
        holder.smallYelloCircle2.setVisibility(View.VISIBLE);
        holder.greyCircle4.setVisibility(View.VISIBLE);
        holder.yellowLine1.setVisibility(View.VISIBLE);
        holder.yellowLine2.setVisibility(View.VISIBLE);
        holder.greyLine3.setVisibility(View.VISIBLE);
        holder.updateTime3.setVisibility(View.VISIBLE);

        holder.bigYellowCircle2.setVisibility(View.INVISIBLE);
        holder.updateTime4.setVisibility(View.INVISIBLE);

        holder.updateTime3.setText(time);
        if(sharedData.getUserEntity().getUserType().equals("Faculty")){
            holder.statusButton.setText("In Delivery");
        }
        else {
            holder.statusButton.setText("Prepared");
        }
        holder.statusButton.setBackgroundColor(context.getResources().getColor(R.color.nav_blue));
    }

    public void fourthUpdate(final TrackOrderItemAdapter.ViewHolder holder, String time){
        holder.bigYellowCircle4.setVisibility(View.VISIBLE);
        holder.smallYelloCircle1.setVisibility(View.VISIBLE);
        holder.smallYelloCircle2.setVisibility(View.VISIBLE);
        holder.smallYelloCircle3.setVisibility(View.VISIBLE);
        holder.yellowLine1.setVisibility(View.VISIBLE);
        holder.yellowLine2.setVisibility(View.VISIBLE);
        holder.yellowLine3.setVisibility(View.VISIBLE);
        holder.updateTime4.setVisibility(View.VISIBLE);

        holder.bigYellowCircle3.setVisibility(View.INVISIBLE);

        holder.updateTime4.setText(time);
        holder.expectedTime.setVisibility(View.INVISIBLE);
        holder.expectedTimeIcon.setVisibility(View.INVISIBLE);
        holder.statusButton.setText("Delivered");
        holder.statusButton.setBackgroundColor(context.getResources().getColor(R.color.purple));
    }

    public void rejectedUpdate(final TrackOrderItemAdapter.ViewHolder holder, String time){

        holder.bigRedCircle.setVisibility(View.VISIBLE);
        holder.greyCircle2.setVisibility(View.VISIBLE);
        holder.greyCircle3.setVisibility(View.VISIBLE);
        holder.greyCircle4.setVisibility(View.VISIBLE);
        holder.greyLine1.setVisibility(View.VISIBLE);
        holder.greyLine2.setVisibility(View.VISIBLE);
        holder.greyLine3.setVisibility(View.VISIBLE);

        holder.bigYellowCircle1.setVisibility(View.INVISIBLE);
        holder.bigYellowCircle2.setVisibility(View.INVISIBLE);
        holder.bigYellowCircle3.setVisibility(View.INVISIBLE);
        holder.bigYellowCircle4.setVisibility(View.INVISIBLE);
        holder.smallYelloCircle1.setVisibility(View.INVISIBLE);
        holder.smallYelloCircle2.setVisibility(View.INVISIBLE);
        holder.smallYelloCircle3.setVisibility(View.INVISIBLE);
        holder.smallYelloCircle4.setVisibility(View.INVISIBLE);
        holder.yellowLine1.setVisibility(View.INVISIBLE);
        holder.yellowLine2.setVisibility(View.INVISIBLE);
        holder.yellowLine3.setVisibility(View.INVISIBLE);
        holder.updateTime2.setVisibility(View.INVISIBLE);
        holder.updateTime3.setVisibility(View.INVISIBLE);
        holder.updateTime4.setVisibility(View.INVISIBLE);

        holder.updateTime1.setText(time);
        holder.expectedTime.setVisibility(View.INVISIBLE);
        holder.expectedTimeIcon.setVisibility(View.INVISIBLE);
        holder.statusButton.setText("Rejected");
        holder.statusButton.setBackgroundColor(context.getResources().getColor(R.color.redShade));
    }

    public ItemEntity getItem(String id, List<ItemEntity> itemEntityList){

        for(int i=0;i<itemEntityList.size();i++){
            ItemEntity itemEntity = itemEntityList.get(i);
            if(id.equals(itemEntity.getItemId()))
                return itemEntity;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return Mvalues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView serialNo, itemName, itemPrice, expectedTime;
        Button statusButton;
        ImageView jainIcon, greyCircle1, smallYelloCircle1, bigYellowCircle1, greyCircle2, smallYelloCircle2, bigYellowCircle2, greyCircle3, smallYelloCircle3, bigYellowCircle3, greyCircle4, smallYelloCircle4, bigYellowCircle4;
        ImageView greyLine1, yellowLine1, greyLine2, yellowLine2,greyLine3, yellowLine3, bigRedCircle, expectedTimeIcon;
        TextView updateTime1, updateTime2, updateTime3, updateTime4;

        public ViewHolder(View itemView) {
            super(itemView);
            serialNo = (TextView) itemView.findViewById(R.id.serialNo);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemPrice = (TextView) itemView.findViewById(R.id.item_price);
            expectedTime = (TextView) itemView.findViewById(R.id.expected_time);

            statusButton = (Button) itemView.findViewById(R.id.status_button);
            jainIcon = (ImageView) itemView.findViewById(R.id.jain_icon);

            greyCircle1 = (ImageView) itemView.findViewById(R.id.greyCircle1);
            smallYelloCircle1 = (ImageView) itemView.findViewById(R.id.smallYellowCircle1);
            bigYellowCircle1 = (ImageView) itemView.findViewById(R.id.bigYellowCircle1);
            bigRedCircle = (ImageView) itemView.findViewById(R.id.bigRedCircle);

            greyCircle2 = (ImageView) itemView.findViewById(R.id.greyCircle2);
            smallYelloCircle2 = (ImageView) itemView.findViewById(R.id.smallYellowCircle2);
            bigYellowCircle2 = (ImageView) itemView.findViewById(R.id.bigYellowCircle2);

            greyCircle3 = (ImageView) itemView.findViewById(R.id.greyCircle3);
            smallYelloCircle3 = (ImageView) itemView.findViewById(R.id.smallYellowCircle3);
            bigYellowCircle3 = (ImageView) itemView.findViewById(R.id.bigYellowCircle3);

            greyCircle4 = (ImageView) itemView.findViewById(R.id.greyCircle4);
            smallYelloCircle4 = (ImageView) itemView.findViewById(R.id.smallYellowCircle4);
            bigYellowCircle4 = (ImageView) itemView.findViewById(R.id.bigYellowCircle4);

            greyLine1 = (ImageView) itemView.findViewById(R.id.greyLine1);
            yellowLine1 = (ImageView) itemView.findViewById(R.id.yellowLine1);

            greyLine2 = (ImageView) itemView.findViewById(R.id.greyLine2);
            yellowLine2 = (ImageView) itemView.findViewById(R.id.yellowLine2);

            greyLine3 = (ImageView) itemView.findViewById(R.id.greyLine3);
            yellowLine3 = (ImageView) itemView.findViewById(R.id.yellowLine3);

            updateTime1 = (TextView) itemView.findViewById(R.id.update_time_1);
            updateTime2 = (TextView) itemView.findViewById(R.id.update_time_2);
            updateTime3 = (TextView) itemView.findViewById(R.id.update_time_3);
            updateTime4 = (TextView) itemView.findViewById(R.id.update_time_4);

            expectedTimeIcon = (ImageView) itemView.findViewById(R.id.expected_time_icon);
        }
    }
}