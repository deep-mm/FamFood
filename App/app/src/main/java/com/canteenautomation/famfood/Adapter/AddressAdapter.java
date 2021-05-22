package com.canteenautomation.famfood.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.canteenautomation.famfood.Activity.AddNewAddress;
import com.canteenautomation.famfood.Activity.Address;
import com.canteenautomation.famfood.Activity.ItemActivity;
import com.canteenautomation.famfood.Activity.PaytmPayment;
import com.canteenautomation.famfood.Entities.AddressEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.OrderUtility;
import com.canteenautomation.famfood.Utilities.SharedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {
    private final List<AddressEntity> dvalues ;
    Context context;
    SharedData sharedData;
    Helper helper;
    OrderUtility orderUtility;

    public AddressAdapter()
    {
        dvalues=null;
    }

    public AddressAdapter(List<AddressEntity> dvalues, Context c) {
        this.dvalues = dvalues;
        context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_address,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position){
        sharedData = new SharedData(context);
        helper = new Helper(context);

        holder.address.setTypeface(helper.comic);
        final AddressEntity add= dvalues.get(position);
        holder.address.setText(add.getCollege_name()+ "\nBuilding: " + add.getBuilding_name()+ "\nFloor: " + add.getFloor_no() + "\nRoom: " + add.getRoom_no());
        int currentPos = position;
        holder.rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedData.setAddressEntity(add);
                int flag = sharedData.getAddrFlag();
                final String addr = add.getCollege_name()+ "\nBuilding: " + add.getBuilding_name()+ "\nFloor: " + add.getFloor_no() + "\nRoom: " + add.getRoom_no();
                if(flag==1){
                    new MaterialDialog.Builder(holder.itemView.getContext())
                            .title("Confirm Address")
                            .content(addr)
                            .positiveText("Yes")
                            .negativeText("No")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    OrderEntity orderEntity = sharedData.getOrderEntity();
                                    orderEntity.setDelivery_add(addr);
                                    sharedData.setOrderEntity(orderEntity);
                                    Intent intent = new Intent(context, PaytmPayment.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
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
                    Intent intent = new Intent(context, AddNewAddress.class);
                    intent.putExtra("default_check",false);
                    intent.putExtra("type",false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return dvalues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView address;
            RippleView rippleView;

            public  MyViewHolder(View itemView){
                super(itemView);
                address=itemView.findViewById(R.id.address);
                rippleView = (RippleView) itemView.findViewById(R.id.ripple);
            }
    }
}
