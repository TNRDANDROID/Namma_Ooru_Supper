package com.nic.nammaoorusuper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.adapter.ActivityListAdapter;
import com.nic.nammaoorusuper.adapter.ActivitySubListAdapter;
import com.nic.nammaoorusuper.adapter.CommonAdapter;
import com.nic.nammaoorusuper.api.Api;
import com.nic.nammaoorusuper.api.ApiService;
import com.nic.nammaoorusuper.api.ServerResponse;
import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.UrlGenerator;
import com.nic.nammaoorusuper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubActivityList extends AppCompatActivity implements Api.ServerResponseListener{
    private com.nic.nammaoorusuper.databinding.ActivitySubListBinding subListBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String hab_code;
    private String campaign_id;
    private String activity_id;
    private String campaign_activity_id;
    private String is_taken;
    private String item_no;
    private String no_of_images;
    private String go_flag;
    private int position;

    ArrayList<NOS> activitySubList=new ArrayList<>();
    ActivitySubListAdapter activityListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subListBinding = DataBindingUtil.setContentView(this, R.layout.activity_sub_list);
        subListBinding.setActivity(this);
        /*ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.background));*/
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.statuscolor(SubActivityList.this);
        campaign_id = getIntent().getStringExtra("campaign_id");
        campaign_activity_id = getIntent().getStringExtra("campaign_activity_id");
        activity_id = getIntent().getStringExtra("activity_id");
        hab_code = getIntent().getStringExtra("hab_code");
        no_of_images = getIntent().getStringExtra("no_of_images");

        subListBinding.activityRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if(Utils.isOnline()){
            getSubActivityList();
        }

        subListBinding.recyclerViewsLayout.setVisibility(View.VISIBLE);
        subListBinding.dashBoardLayout.setVisibility(View.GONE);

        subListBinding.entryForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline()){
                    Intent intent = new Intent(SubActivityList.this,LocationSaveActivity.class);
                    intent.putExtra("activity_id",activity_id);
                    intent.putExtra("campaign_activity_id",campaign_activity_id);
                    intent.putExtra("hab_code",hab_code);
                    intent.putExtra("campaign_id",campaign_id);
                    intent.putExtra("item_no",item_no );
                    intent.putExtra("no_of_images", no_of_images);
                    intent.putExtra("On_Off_Type", "");
                    subListBinding.recyclerViewsLayout.setVisibility(View.VISIBLE);
                    subListBinding.dashBoardLayout.setVisibility(View.GONE);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
                else {
                    Utils.showAlert(SubActivityList.this,"No Internet");
                }
            }
        });

        subListBinding.takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline()){
                    go_flag = "take_photo";
                    getActivityEntryId();
                }
                else {
                    Utils.showAlert(SubActivityList.this,"No Internet");
                }
            }
        });
        subListBinding.viewForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline()){
                    go_flag = "view_form";
                    getActivityEntryId();
                }
                else {
                    Utils.showAlert(SubActivityList.this,"No Internet");
                }
            }
        });
    }

    public void activityListItemClicked(int pos){
        position= pos;
        activity_id = activitySubList.get(pos).getActivity_id();
        campaign_activity_id = activitySubList.get(pos).getCampaign_activity_id();
        is_taken =activitySubList.get(pos).getIs_taken();
        item_no =activitySubList.get(pos).getItem_no();
        subListBinding.recyclerViewsLayout.setVisibility(View.GONE);
        subListBinding.dashBoardLayout.setVisibility(View.VISIBLE);
       /* if(Utils.isOnline()){
            Intent intent = new Intent(SubActivityList.this,SubListDashBoard.class);
            intent.putExtra("activity_id",activity_id);
            intent.putExtra("campaign_activity_id",campaign_activity_id);
            intent.putExtra("hab_code",hab_code);
            intent.putExtra("campaign_id",campaign_id);
            intent.putExtra("item_no", activitySubList.get(pos).getItem_no());
            intent.putExtra("no_of_images", no_of_images);
            intent.putExtra("is_taken", activitySubList.get(pos).getIs_taken());
            startActivity(intent);
        }
        else {
            Utils.showAlert(SubActivityList.this,"No Internet");
        }*/
        if(is_taken.equals("Y")){
            subListBinding.entryForm.setEnabled(true);
            subListBinding.viewForm.setEnabled(true);
            subListBinding.takePhoto.setEnabled(true);
            subListBinding.takePhoto.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner_strong_rect_bg_color));
            subListBinding.viewForm.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner_strong_rect_bg_color));
        }
        else {
            subListBinding.entryForm.setEnabled(true);
            subListBinding.viewForm.setEnabled(false);
            subListBinding.takePhoto.setEnabled(false);
            subListBinding.takePhoto.setBackgroundDrawable(getResources().getDrawable(R.drawable.blured_shadow));
            subListBinding.viewForm.setBackgroundDrawable(getResources().getDrawable(R.drawable.blured_shadow));
        }

    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;

            if ("sub_activity_details".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertSubActivityList().execute(jsonObject);
                }
                else {
                    subListBinding.noDataIcon.setVisibility(View.VISIBLE);
                    subListBinding.activityRecycler.setVisibility(View.GONE);
                    subListBinding.activityRecycler.setAdapter(null);
                }
                Log.d("sub_activity_details", "" + responseDecryptedBlockKey);
            }
            if ("get_activity_entry_details".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertViewFormList().execute(jsonObject);

                }
                else {
                    Utils.showAlert(SubActivityList.this,jsonObject.getString("MESSAGE"));
                }
                Log.d("activity_entry_details", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
    public void getSubActivityList() {
        try {
            new ApiService(this).makeJSONObjectRequest("sub_activity_details", Api.Method.POST, UrlGenerator.getPMAYListUrl(), sub_activity_details_en_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject sub_activity_details_en_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), sub_activity_details_no_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("sub_activty", "" + authKey);
        return dataSet;
    }
    private JSONObject sub_activity_details_no_JsonParams() {
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_SERVICE_ID, "campaign_activity_sub_list");
            dataSet.put("hab_code",hab_code);
            dataSet.put("campaign_id", campaign_id);
            dataSet.put("activity_id", activity_id);
            dataSet.put("campaign_activity_id", campaign_activity_id);
            Log.d("sub_activty", "" + dataSet);
        }
        catch (Exception e){

        }

        return dataSet;
    }


    public class InsertSubActivityList extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                activitySubList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    NOS activity_details_item = new NOS();
                    try {
                        activity_details_item.setCampaign_activity_details_id(jsonArray.getJSONObject(i).getString("campaign_activity_details_id"));
                        activity_details_item.setCampaign_id(jsonArray.getJSONObject(i).getString("campaign_id"));
                        activity_details_item.setActivity_id(jsonArray.getJSONObject(i).getString("activity_id"));
                        activity_details_item.setCampaign_activity_id(jsonArray.getJSONObject(i).getString("campaign_activity_id"));
                        activity_details_item.setDistictCode(jsonArray.getJSONObject(i).getString("dcode"));
                        activity_details_item.setBlockCode(jsonArray.getJSONObject(i).getString("bcode"));
                        activity_details_item.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                        activity_details_item.setHabCode(jsonArray.getJSONObject(i).getString("hab_code"));
                        activity_details_item.setActivity_sub_list(jsonArray.getJSONObject(i).getString("activity_sub_list"));
                        activity_details_item.setItem_no(jsonArray.getJSONObject(i).getString("item_no"));
                        activity_details_item.setIs_taken(jsonArray.getJSONObject(i).getString("is_taken"));

                        activitySubList.add(activity_details_item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }


            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(activitySubList.size()>0){
                subListBinding.noDataIcon.setVisibility(View.GONE);
                subListBinding.activityRecycler.setVisibility(View.VISIBLE);
                activityListAdapter =new ActivitySubListAdapter(SubActivityList.this,activitySubList,dbData,"");
                subListBinding.activityRecycler.setAdapter(activityListAdapter);
            }
            else {
                subListBinding.noDataIcon.setVisibility(View.VISIBLE);
                subListBinding.activityRecycler.setVisibility(View.GONE);
                subListBinding.activityRecycler.setAdapter(null);
            }
        }
    }
    public class InsertViewFormList extends AsyncTask<JSONObject, Void, Void> {
        String hab_code;
        String activity_id;
        String campaign_id;
        String item_no;
        String campaign_activity_id;
        String campaign_activity_entry_id;
        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = params[0].getJSONObject(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(jsonObject.length()>0){
                    JSONObject activity_entry_list = new JSONObject();
                    JSONArray activity_entry_label_list = new JSONArray();
                    //label_list = new ArrayList<>();
                    try {
                        activity_entry_list = jsonObject.getJSONObject("activity_entry_list");
                        hab_code= activity_entry_list.getString("hab_code");
                        activity_id= activity_entry_list.getString("activity_id");
                        campaign_id= activity_entry_list.getString("campaign_id");
                        item_no= activity_entry_list.getString("item_no");
                        campaign_activity_id= activity_entry_list.getString("campaign_activity_id");
                        campaign_activity_entry_id= activity_entry_list.getString("campaign_activity_entry_id");


                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }


            }


            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(go_flag.equals("take_photo")){
                Intent intent = new Intent(SubActivityList.this, PhotoCategoryList.class);
                intent.putExtra("activity_id",activity_id);
                intent.putExtra("campaign_activity_id",campaign_activity_id);
                intent.putExtra("hab_code",hab_code);
                intent.putExtra("campaign_id",campaign_id);
                intent.putExtra("campaign_activity_entry_id",campaign_activity_entry_id);
                intent.putExtra("item_no", item_no);
                intent.putExtra("no_of_images", no_of_images);
                subListBinding.recyclerViewsLayout.setVisibility(View.VISIBLE);
                subListBinding.dashBoardLayout.setVisibility(View.GONE);
                startActivity(intent);
                //finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }

            else {
                Intent intent = new Intent(SubActivityList.this, ViewFormActivity.class);
                intent.putExtra("activity_id",activity_id);
                intent.putExtra("campaign_activity_id",campaign_activity_id);
                intent.putExtra("hab_code",hab_code);
                intent.putExtra("campaign_id",campaign_id);
                intent.putExtra("campaign_activity_entry_id",campaign_activity_entry_id);
                intent.putExtra("item_no", item_no);
                intent.putExtra("no_of_images", no_of_images);
                subListBinding.recyclerViewsLayout.setVisibility(View.VISIBLE);
                subListBinding.dashBoardLayout.setVisibility(View.GONE);
                startActivity(intent);
                //finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }
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

    public void getActivityEntryId() {
        try {
            new ApiService(this).makeJSONObjectRequest("get_activity_entry_details", Api.Method.POST, UrlGenerator.getPMAYListUrl(), activity_entry_id_en_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject activity_entry_id_en_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), activity_entry_id_no_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("save_data", "" + authKey);
        return dataSet;
    }

    private JSONObject activity_entry_id_no_JsonParams(){
        JSONObject data_set = new JSONObject();
        try {
            data_set.put(AppConstant.KEY_SERVICE_ID,"get_activity_entry_details");
            data_set.put("campaign_id",campaign_id);
            data_set.put("hab_code",hab_code);
            data_set.put("activity_id",activity_id);
            data_set.put("campaign_activity_id",campaign_activity_id);
            data_set.put("item_no",item_no);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return data_set;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSubActivityList();
    }

}
