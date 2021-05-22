package com.canteenautomation.famfood.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.canteenautomation.famfood.Entities.FeedbackEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.HashMap;
import java.util.List;

public class FeedbackItemAdapter extends RecyclerView.Adapter<FeedbackItemAdapter.MyViewHolder> {
    private final HashMap<String, Float> dvalues ;
    Context context;
    SharedData sharedData;
    Helper helper;
    Integer flag;

    public FeedbackItemAdapter()
    {
        dvalues=null;
    }

    public FeedbackItemAdapter(HashMap<String, Float> dvalues, Context c, Integer flag) {
        this.dvalues = dvalues;
        context = c;
        this.flag = flag;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feedback,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position){
        sharedData = new SharedData(context);
        helper = new Helper(context);

        Object[] keys = dvalues.keySet().toArray();
        Float rating = dvalues.get(keys[position].toString());
        holder.ratingBar.setRating(rating);
        String item = getItemName(keys[position].toString());
        holder.itemName.setText(item);
        holder.itemName.setTypeface(helper.comicBold);
        final int current = position;

        if(flag==1){
            holder.ratingBar.setEnabled(false);
            holder.buttonOverlay.setVisibility(View.VISIBLE);
        }
        else{
            holder.ratingBar.setEnabled(true);
            holder.buttonOverlay.setVisibility(View.INVISIBLE);
        }

        holder.ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar baseRatingBar, float v) {
                if(flag!=1) {
                    List<Float> feedback = sharedData.getFeedbackValues();
                    feedback.set(current, v);
                    sharedData.setFeedbackValues(feedback);
                }
            }
        });
    }

    public String getItemName(String itemId){

        List<ItemEntity> itemEntities = sharedData.getItemEntities();
        for(int i=0;i<itemEntities.size();i++){
            ItemEntity itemEntity = itemEntities.get(i);
            if(itemId.equals(itemEntity.getItemId())){
                return itemEntity.getItemName();
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return dvalues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

            TextView itemName;
            ScaleRatingBar ratingBar;
            Button buttonOverlay;

            public  MyViewHolder(View itemView){
                super(itemView);
                itemName = (TextView) itemView.findViewById(R.id.itemName);
                ratingBar = (ScaleRatingBar) itemView.findViewById(R.id.simpleRatingBar);
                buttonOverlay = (Button) itemView.findViewById(R.id.buttonOverlay);
            }
    }
}
