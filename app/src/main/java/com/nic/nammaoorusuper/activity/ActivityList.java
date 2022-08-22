package com.nic.nammaoorusuper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompatSideChannelService;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ActivityList extends AppCompatActivity implements Api.ServerResponseListener{
    private com.nic.nammaoorusuper.databinding.ActivityListBinding activityListBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String hab_code;
    private String campaign_id;
    private String activity_id;
    private String campaign_activity_id;

    private List<NOS> Habitation = new ArrayList<>();
    ArrayList<NOS> activityList=new ArrayList<>();
    ArrayList<NOS> activitySubList=new ArrayList<>();
    ActivityListAdapter activityListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityListBinding = DataBindingUtil.setContentView(this, R.layout.activity_list);
        activityListBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        campaign_id = getIntent().getStringExtra("campaign_id");

        activityListBinding.activityRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if(Utils.isOnline()){
            getHabList();
        }
        else {
            Utils.showAlert(ActivityList.this,"No Internet");
        }

        activityListBinding.habitationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    hab_code = Habitation.get(position).getHabCode();
                    if(Utils.isOnline()){
                        getActivityList();
                    }
                    else {
                        Utils.showAlert(ActivityList.this,"No Internet");
                    }
                }
                else {
                    hab_code="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Utils.statuscolor(ActivityList.this);
    }

    public void getHabList() {
        try {
            new ApiService(this).makeJSONObjectRequest("HabitationList", Api.Method.POST, UrlGenerator.getServicesListUrl(), habitationListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject habitationListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.HabitationListDistrictBlockVillageWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("HabitationList", "" + authKey);
        return dataSet;
    }

    public void getActivityList() {
        try {
            new ApiService(this).makeJSONObjectRequest("activity_details", Api.Method.POST, UrlGenerator.getPMAYListUrl(), activity_details_en_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject activity_details_en_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), activity_details_no_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("activty", "" + authKey);
        return dataSet;
    }
    private JSONObject activity_details_no_JsonParams() {
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_SERVICE_ID, "campaign_activity_list");
            dataSet.put("hab_code",hab_code);
            dataSet.put("campaign_id", campaign_id);
            Log.d("activty", "" + dataSet);
        }
        catch (Exception e){

        }

        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;

            if ("HabitationList".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertHabTask().execute(jsonObject);
                }
                Log.d("HabitationList", "" + responseDecryptedBlockKey);
            }
            else if ("activity_details".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertActivityList().execute(jsonObject);
                }
                else {
                    activityListBinding.noDataIcon.setVisibility(View.VISIBLE);
                    activityListBinding.activityRecycler.setVisibility(View.GONE);
                    activityListBinding.activityRecycler.setAdapter(null);
                }
                Log.d("activity_details", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void activityListItemClicked(int pos){
        activity_id = activityList.get(pos).getActivity_id();
        campaign_activity_id = activityList.get(pos).getCampaign_activity_id();
        if(Utils.isOnline()){
            Intent intent = new Intent(ActivityList.this,SubActivityList.class);
            intent.putExtra("activity_id",activity_id);
            intent.putExtra("campaign_activity_id",campaign_activity_id);
            intent.putExtra("hab_code",hab_code);
            intent.putExtra("campaign_id",campaign_id);
            intent.putExtra("no_of_images",activityList.get(pos).getNo_of_images());
            startActivity(intent);
        }
        else {
            Utils.showAlert(ActivityList.this,"No Internet");
        }

    }

    public class InsertHabTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<NOS> hablist_count = dbData.getAll_Habitation(prefManager.getDistrictCode(),prefManager.getBlockCode());
            if (hablist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        NOS habListValue = new NOS();
                        try {
                            habListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            habListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            habListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            habListValue.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HABB_CODE));
                            habListValue.setHabitationName(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME));

                            dbData.insertHabitation(habListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            habitationFilterSpinner(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode());
        }
    }
    public void habitationFilterSpinner(String dcode, String bcode, String pvcode) {
        Cursor HABList = null;
        HABList = db.rawQuery("SELECT * FROM " + DBHelper.HABITATION_TABLE_NAME + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' order by habitation_name asc", null);

        Habitation.clear();
        NOS habitationListValue = new NOS();
        habitationListValue.setHabitationName("Select Habitation");
        Habitation.add(habitationListValue);
        if (HABList.getCount() > 0) {
            if (HABList.moveToFirst()) {
                do {
                    NOS habList = new NOS();
                    String districtCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String habCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABB_CODE));
                    String habName = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABITATION_NAME));

                    habList.setDistictCode(districtCode);
                    habList.setBlockCode(blockCode);
                    habList.setPvCode(pvCode);
                    habList.setHabCode(habCode);
                    habList.setHabitationName(habName);

                    Habitation.add(habList);
                    Log.d("spinnersize", "" + Habitation.size());
                } while (HABList.moveToNext());
            }
        }
        if(Habitation.size()>0){
            activityListBinding.habitationSpinner.setAdapter(new CommonAdapter(ActivityList.this,Habitation,"HabitationList"));
        }
    }

    public class InsertActivityList extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    activityList = new ArrayList<>();
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
                            activity_details_item.setNo_of_items(jsonArray.getJSONObject(i).getString("no_of_items"));
                            activity_details_item.setActivity_name(jsonArray.getJSONObject(i).getString("activity_name"));
                            activity_details_item.setNo_of_images(jsonArray.getJSONObject(i).getString("no_of_images"));
                            activity_details_item.setActivity_from_date(jsonArray.getJSONObject(i).getString("activity_from_date"));
                            activity_details_item.setActivity_to_date(jsonArray.getJSONObject(i).getString("activity_to_date"));
                            activity_id = jsonArray.getJSONObject(i).getString("activity_id");
                            campaign_activity_id = jsonArray.getJSONObject(i).getString("campaign_activity_id");
                            activityList.add(activity_details_item);

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
            if(activityList.size()>0){
                activityListBinding.noDataIcon.setVisibility(View.GONE);
                activityListBinding.activityRecycler.setVisibility(View.VISIBLE);
                activityListAdapter =new ActivityListAdapter(ActivityList.this,activityList,dbData);
                activityListBinding.activityRecycler.setAdapter(activityListAdapter);
            }
            else {
                activityListBinding.noDataIcon.setVisibility(View.VISIBLE);
                activityListBinding.activityRecycler.setVisibility(View.GONE);
                activityListBinding.activityRecycler.setAdapter(null);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getHabList();
        getActivityList();
    }
}
