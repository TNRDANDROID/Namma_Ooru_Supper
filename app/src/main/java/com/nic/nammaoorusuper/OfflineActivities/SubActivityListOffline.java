package com.nic.nammaoorusuper.OfflineActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.LocationSaveActivity;
import com.nic.nammaoorusuper.activity.PhotoCategoryList;
import com.nic.nammaoorusuper.activity.SubActivityList;
import com.nic.nammaoorusuper.adapter.ActivityListAdapter;
import com.nic.nammaoorusuper.adapter.ActivitySubListAdapter;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.Utils;

import java.util.ArrayList;

public class SubActivityListOffline extends AppCompatActivity{
    private com.nic.nammaoorusuper.databinding.ActivitySubListOfflineBinding subListBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private String hab_code;
    private String hab_name;
    private String campaign_id;
    private String activity_id;
    private String activity_name;
    private String campaign_activity_id;
    private String is_taken;
    private String item_no;
    private String no_of_images;
    private String go_flag;
    private int position;

    ArrayList<NOS> activitySubList=new ArrayList<>();
    ActivitySubListAdapter activitySubListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subListBinding = DataBindingUtil.setContentView(this, R.layout.activity_sub_list_offline);
        subListBinding.setActivity(this);

        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeUI();

        subListBinding.entryForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(SubActivityListOffline.this, LocationSaveOffline.class);
                    intent.putExtra("activity_id",activity_id);
                    intent.putExtra("activity_name",activity_name);
                    intent.putExtra("campaign_activity_id",campaign_activity_id);
                    intent.putExtra("hab_code",hab_code);
                    intent.putExtra("hab_name",hab_name);
                    intent.putExtra("campaign_id",campaign_id);
                    intent.putExtra("item_no",item_no );
                    intent.putExtra("no_of_images", no_of_images);

                    subListBinding.recyclerViewsLayout.setVisibility(View.VISIBLE);
                    subListBinding.dashBoardLayout.setVisibility(View.GONE);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }
        });
        subListBinding.takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NOS> old_items = new ArrayList<>(dbData.get_Particular_Location_Save_List(campaign_id,activity_id,campaign_activity_id,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code,item_no,""));
                if(old_items.size()>0) {
                    for(int i=0;i<old_items.size();i++){
                        String row_id = old_items.get(i).getLocation_save_details_primary_id();
                        gotoPhotoCategoryOffline(row_id);
                    }
                }
                else {

                }
               /* Intent intent = new Intent(SubActivityListOffline.this, PhotoCategoryOffline.class);
                intent.putExtra("activity_id",activity_id);
                intent.putExtra("campaign_activity_id",campaign_activity_id);
                intent.putExtra("hab_code",hab_code);
                intent.putExtra("campaign_id",campaign_id);
                intent.putExtra("hab_name",hab_name);
                intent.putExtra("activity_name",activity_name);
                intent.putExtra("campaign_id",campaign_id);
                intent.putExtra("item_no", item_no);
                intent.putExtra("no_of_images", no_of_images);
                subListBinding.recyclerViewsLayout.setVisibility(View.VISIBLE);
                subListBinding.dashBoardLayout.setVisibility(View.GONE);
                startActivity(intent);
                //finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/

            }
        });


    }

    private void initializeUI() {
        Utils.statuscolor(SubActivityListOffline.this);
        campaign_id = getIntent().getStringExtra("campaign_id");
        campaign_activity_id = getIntent().getStringExtra("campaign_activity_id");
        activity_id = getIntent().getStringExtra("activity_id");
        activity_name = getIntent().getStringExtra("activity_name");
        hab_code = getIntent().getStringExtra("hab_code");
        hab_name = getIntent().getStringExtra("hab_name");
        no_of_images = getIntent().getStringExtra("no_of_images");

        subListBinding.activityRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        subListBinding.recyclerViewsLayout.setVisibility(View.VISIBLE);
        subListBinding.dashBoardLayout.setVisibility(View.GONE);

        new fetch_sub_Activity_list().execute();
    }

    @SuppressLint("StaticFieldLeak")
    public class fetch_sub_Activity_list extends AsyncTask<Void, Void,ArrayList<NOS>> {
        @Override
        protected ArrayList<NOS> doInBackground(Void... params) {
            dbData.open();
            activitySubList = new ArrayList<>();
            activitySubList = dbData.get_Particular_Campaign_Activity_Sub_List(campaign_id,hab_code,activity_id,campaign_activity_id);
            Log.d("sub_list_count", String.valueOf(activitySubList.size()));
            return activitySubList;
        }

        @Override
        protected void onPostExecute(ArrayList<NOS> sub_activity_list) {
            super.onPostExecute(sub_activity_list);
            if(!Utils.isOnline()) {
                if (sub_activity_list.size() == 0) {
                    Utils.showAlert(SubActivityListOffline.this, "No Data Available in Local Database. Please, Turn On mobile data");
                }
            }
            if(sub_activity_list.size()>0){
                subListBinding.noDataIcon.setVisibility(View.GONE);
                subListBinding.activityRecycler.setVisibility(View.VISIBLE);
                activitySubListAdapter =new ActivitySubListAdapter(SubActivityListOffline.this,sub_activity_list,dbData,"Offline");
                subListBinding.activityRecycler.setAdapter(activitySubListAdapter);
            }
            else {
                subListBinding.noDataIcon.setVisibility(View.VISIBLE);
                subListBinding.activityRecycler.setVisibility(View.GONE);
                subListBinding.activityRecycler.setAdapter(null);
            }

        }
    }

    public void activityListItemClicked(int pos){
        position= pos;
        activity_id = activitySubList.get(pos).getActivity_id();
        campaign_activity_id = activitySubList.get(pos).getCampaign_activity_id();
        is_taken =activitySubList.get(pos).getIs_taken();
        item_no =activitySubList.get(pos).getItem_no();
        subListBinding.recyclerViewsLayout.setVisibility(View.GONE);
        subListBinding.dashBoardLayout.setVisibility(View.VISIBLE);

       /* if(is_taken.equals("Y")){
            subListBinding.entryForm.setEnabled(true);
            subListBinding.takePhoto.setEnabled(true);
            subListBinding.takePhoto.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner_strong_rect_bg_color));
        }
        else {
            subListBinding.entryForm.setEnabled(true);
            subListBinding.takePhoto.setEnabled(false);
            subListBinding.takePhoto.setBackgroundDrawable(getResources().getDrawable(R.drawable.blured_shadow));

        }*/
    }


    @Override
    public void onBackPressed() {
        if(subListBinding.dashBoardLayout.getVisibility()==View.VISIBLE){
            subListBinding.dashBoardLayout.setVisibility(View.GONE);
            subListBinding.recyclerViewsLayout.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
        }

    }
    private void gotoPhotoCategoryOffline(String row_id){
        Intent intent = new Intent(SubActivityListOffline.this, PhotoCategoryOffline.class);
        intent.putExtra("campaign_id",campaign_id);
        intent.putExtra("activity_id",activity_id);
        intent.putExtra("activity_name",activity_name);
        intent.putExtra("location_save_details_primary_id",row_id);
        intent.putExtra("campaign_activity_id",campaign_activity_id);
        intent.putExtra("campaign_activity_id",campaign_activity_id);
        intent.putExtra("dcode",prefManager.getDistrictCode());
        intent.putExtra("bcode",prefManager.getBlockCode());
        intent.putExtra("pvcode",prefManager.getPvCode());
        intent.putExtra("hab_code",hab_code);
        intent.putExtra("hab_name",hab_name);
        intent.putExtra("item_no",item_no);
        intent.putExtra("no_of_images",no_of_images);
        subListBinding.recyclerViewsLayout.setVisibility(View.VISIBLE);
        subListBinding.dashBoardLayout.setVisibility(View.GONE);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

}
