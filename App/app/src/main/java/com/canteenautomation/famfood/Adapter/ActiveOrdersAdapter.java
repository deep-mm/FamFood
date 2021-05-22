package com.canteenautomation.famfood.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.canteenautomation.famfood.Activity.OrderHistoryDetail;
import com.canteenautomation.famfood.Activity.TokenActivity;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;

import java.util.List;

public class ActiveOrdersAdapter extends RecyclerView.Adapter<ActiveOrdersAdapter.MyViewHolder> {
    private final List<OrderEntity> fvalues ;
    Context context;
    private SharedData sharedData;
    private Helper helper;
    private OrderEntity orderEntity;

    public ActiveOrdersAdapter() {
        fvalues = null;

    }
    public ActiveOrdersAdapter(List<OrderEntity> fvalues, Context c) {
        this.fvalues = fvalues;
        context=c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_active_orders, parent, false);
        return new ActiveOrdersAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        sharedData = new SharedData(context);
        helper = new Helper(context);

        holder.canteenName.setTypeface(helper.comicBold);
        holder.dateTime.setTypeface(helper.comic);
        holder.trackOrder.setTypeface(helper.comic);

        orderEntity = fvalues.get(position);

        StringBuffer stringBuffer1 = new StringBuffer(orderEntity.getOrder_id());
        String canteenId = orderEntity.getCanteen_id();
        List<CanteenEntity> canteenEntityList = sharedData.getCanteenEntitities();
        String collegeName = getName(canteenId,canteenEntityList);
        holder.canteenName.setText(collegeName);
        holder.dateTime.setText(stringBuffer1.substring(17,22));
        final OrderEntity current = orderEntity;

        holder.trackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderHistoryDetail.class);
                sharedData.setOrderEntity(current);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public String getName(String id, List<CanteenEntity> canteenEntities){
        for(int i=0; i<canteenEntities.size();i++){
            CanteenEntity canteenEntity = canteenEntities.get(i);
            if(canteenEntity.getId().equals(id))
                return canteenEntity.getName();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return fvalues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView canteenName, dateTime;
        Button trackOrder;

        public MyViewHolder(View itemView) {
            super(itemView);
            canteenName = (TextView) itemView.findViewById(R.id.college_name);
            dateTime = (TextView) itemView.findViewById(R.id.dateTime);
            trackOrder = (Button) itemView.findViewById(R.id.track_order);
        }
    }
}