package com.nic.nammaoorusuper.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.ActivityList;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.support.MyCustomTextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CampaignListAdapter extends RecyclerView.Adapter<CampaignListAdapter.MyViewHolder>{

    private final dbData dbData;
    private Context context;
    private List<NOS> campaignListValues;
    private PrefManager prefManager;

    public CampaignListAdapter(Context context, List<NOS> campaignListValues, dbData dbData) {
        this.context = context;
        this.campaignListValues = campaignListValues;
        this.dbData = dbData;
       prefManager = new PrefManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.campaign_list, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MyCustomTextView schedule_tv,from_date_tv,to_date_tv,campaign_description_tv;
        private LinearLayout schedule, vertical_tv;
        private CardView district_card;
        int[] androidColors;

        public MyViewHolder(View itemView) {
            super(itemView);
            androidColors = itemView.getResources().getIntArray(R.array.androidcolors);
            schedule_tv = (MyCustomTextView)itemView.findViewById(R.id.schedule_tv);
            from_date_tv = (MyCustomTextView)itemView.findViewById(R.id.from_date_tv);
            to_date_tv = (MyCustomTextView)itemView.findViewById(R.id.to_date_tv);
            campaign_description_tv = (MyCustomTextView)itemView.findViewById(R.id.campaign_description_tv);
            schedule = (LinearLayout)itemView.findViewById(R.id.schedule);
            vertical_tv = (LinearLayout) itemView.findViewById(R.id.vertical_tv);
            district_card = (CardView) itemView.findViewById(R.id.district_card);
            schedule_tv.setVisibility(View.GONE);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (position % 2 == 1) {
            holder.vertical_tv.setBackgroundResource(R.drawable.gradient_diff);
            holder.district_card.setBackgroundResource(R.drawable.gradient_blue);


        } else {
            holder.vertical_tv.setBackgroundResource(R.drawable.gradient_pink);
            holder.district_card.setBackgroundResource(R.drawable.gradient);

        }

        holder.from_date_tv.setText(campaignListValues.get(position).getCampaign_from_date());
        holder.to_date_tv.setText(campaignListValues.get(position).getCampaign_to_date());
        holder.campaign_description_tv.setText(campaignListValues.get(position).getCampaign_name());

        holder.schedule.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                openActivityClass(position);
            }
        });
    }

    private void openActivityClass(int pos){
        Intent intent = new Intent(context, ActivityList.class);
        intent.putExtra("campaign_id",campaignListValues.get(pos).getCampaign_id());
        context.startActivity(intent);
    }
    public void showAlert( String msg){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            @SuppressLint("WrongViewCast") MyCustomTextView text = (MyCustomTextView) dialog.findViewById(R.id.tv_message);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    @Override
    public int getItemCount() {
        return campaignListValues.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean CompareScheduleDateVsCurrentDate(String schedule_date){
        String sDate1=schedule_date;
        try {
            Date schedule_date1=new SimpleDateFormat("yyyy-MM-dd").parse(sDate1);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Calendar obj = Calendar.getInstance();
            String str = formatter.format(obj.getTime());
            Date current_date1=new SimpleDateFormat("yyyy-MM-dd").parse(str);
            if(schedule_date1.compareTo(current_date1)>=0){
                return true;
            }
            else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
