package com.nic.nammaoorusuper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.adapter.CampaignListAdapter;
import com.nic.nammaoorusuper.api.Api;
import com.nic.nammaoorusuper.api.ApiService;
import com.nic.nammaoorusuper.api.ServerResponse;
import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.databinding.HomeScreenBinding;
import com.nic.nammaoorusuper.dialog.MyDialog;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.UrlGenerator;
import com.nic.nammaoorusuper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private HomeScreenBinding homeScreenBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String isHome;
    private String On_Off_Type;

    private List<NOS> Habitation = new ArrayList<>();
    ArrayList<NOS>  campaign_details;
    CampaignListAdapter campaignListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.home_screen);
        homeScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.statuscolor(HomePage.this);
        Bundle bundle = this.getIntent().getExtras();
        On_Off_Type = getIntent().getStringExtra("On_Off_Type");
        if (bundle != null) {
            isHome = bundle.getString("Home");
        }
        homeScreenBinding.campaignRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        homeScreenBinding.noDataIcon.setVisibility(View.GONE);

        if(On_Off_Type.equals("Online")){
            getcampaign_details();
        }
        else {
            new fetch_Campaign_list().execute();
        }



    }


    public void getcampaign_details() {
        try {
            new ApiService(this).makeJSONObjectRequest("campaign_details", Api.Method.POST, UrlGenerator.getPMAYListUrl(), campaign_detailsJsonParamsJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject campaign_detailsJsonParamsJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.campaign_detailsJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("campaign_details", "" + authKey);
        return dataSet;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }



    @Override
    protected void onResume() {
        super.onResume();

    }






    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("campaign_details".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insertcampaign_details().execute(jsonObject);
                }
                Log.d("campaign_details", "" + responseDecryptedBlockKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }


    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        } else {

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }

    public class Insertcampaign_details extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                dbData.open();
                dbData.delete_campaign_list_table();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                campaign_details = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    NOS campaign_details_item = new NOS();
                    try {
                        campaign_details_item.setCampaign_id(jsonArray.getJSONObject(i).getString("campaign_id"));
                        campaign_details_item.setCampaign_name(jsonArray.getJSONObject(i).getString("campaign_name"));
                        campaign_details_item.setCampaign_from_date(jsonArray.getJSONObject(i).getString("campaign_from_date"));
                        campaign_details_item.setCampaign_to_date(jsonArray.getJSONObject(i).getString("campaign_to_date"));

                        dbData.insertCampaignList(campaign_details_item);
                        campaign_details.add(campaign_details_item);
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
            if(campaign_details.size()>0){
                homeScreenBinding.campaignRecycler.setVisibility(View.VISIBLE);
                homeScreenBinding.noDataIcon.setVisibility(View.GONE);
                campaignListAdapter = new CampaignListAdapter(HomePage.this,campaign_details,dbData,On_Off_Type);
                homeScreenBinding.campaignRecycler.setAdapter(campaignListAdapter);
            }
            else {
                homeScreenBinding.noDataIcon.setVisibility(View.VISIBLE);
                homeScreenBinding.campaignRecycler.setVisibility(View.GONE);
                homeScreenBinding.campaignRecycler.setAdapter(null);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class fetch_Campaign_list extends AsyncTask<Void, Void,ArrayList<NOS>> {
        @Override
        protected ArrayList<NOS> doInBackground(Void... params) {
            dbData.open();
            campaign_details = new ArrayList<>();
            campaign_details = dbData.getAll_Campaign();
            Log.d("campaign_list_count", String.valueOf(campaign_details.size()));
            return campaign_details;
        }

        @Override
        protected void onPostExecute(ArrayList<NOS> campaign_details) {
            super.onPostExecute(campaign_details);
            if(!Utils.isOnline()) {
                if (campaign_details.size() == 0) {
                    Utils.showAlert(HomePage.this, "No Data Available in Local Database. Please, Turn On mobile data");
                }
            }
            else {
                getcampaign_details();
            }
            if(campaign_details.size()>0){
                homeScreenBinding.campaignRecycler.setVisibility(View.VISIBLE);
                homeScreenBinding.noDataIcon.setVisibility(View.GONE);
                campaignListAdapter = new CampaignListAdapter(HomePage.this,campaign_details,dbData,On_Off_Type);
                homeScreenBinding.campaignRecycler.setAdapter(campaignListAdapter);
            }
            else {
                homeScreenBinding.noDataIcon.setVisibility(View.VISIBLE);
                homeScreenBinding.campaignRecycler.setVisibility(View.GONE);
                homeScreenBinding.campaignRecycler.setAdapter(null);
            }


        }
    }

}
