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
    private String no_of_images;

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

    }

    public void activityListItemClicked(int pos){
        activity_id = activitySubList.get(pos).getActivity_id();
        campaign_activity_id = activitySubList.get(pos).getCampaign_activity_id();
        if(Utils.isOnline()){
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
                activityListAdapter =new ActivitySubListAdapter(SubActivityList.this,activitySubList,dbData);
                subListBinding.activityRecycler.setAdapter(activityListAdapter);
            }
            else {
                subListBinding.noDataIcon.setVisibility(View.VISIBLE);
                subListBinding.activityRecycler.setVisibility(View.GONE);
                subListBinding.activityRecycler.setAdapter(null);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSubActivityList();
    }

}
