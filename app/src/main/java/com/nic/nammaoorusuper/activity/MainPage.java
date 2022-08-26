package com.nic.nammaoorusuper.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.adapter.CategoryListeNameAdapter;
import com.nic.nammaoorusuper.api.Api;
import com.nic.nammaoorusuper.api.ApiService;
import com.nic.nammaoorusuper.api.ServerResponse;
import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.databinding.MainPageBinding;
import com.nic.nammaoorusuper.dialog.MyDialog;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.UrlGenerator;
import com.nic.nammaoorusuper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity implements Api.ServerResponseListener ,MyDialog.myOnClickListener{

    public MainPageBinding mainPageBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainPageBinding = DataBindingUtil.setContentView(this, R.layout.main_page);
        mainPageBinding.setActivity(this);

        prefManager = new PrefManager(this);
        Utils.statuscolor(MainPage.this);
        mainPageBinding.tvName.setText(prefManager.getDistrictName());
        mainPageBinding.designation.setText(prefManager.getBlockName());
        mainPageBinding.pvName.setText(prefManager.getVillageListPvName());

        getHabList();
        get_category_list();
        mainPageBinding.goWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHomeScreen("Online");
            }
        });
        mainPageBinding.goOfflineWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHomeScreen("Offline");
            }
        });
        mainPageBinding.syncLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pendingScreen();
            }
        });
        syncButtonVisibility();
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


    @Override
    protected void onResume() {
        super.onResume();
        syncButtonVisibility();
    }




    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();

            if ("HabitationList".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertHabTask().execute(jsonObject);
                }
                Log.d("HabitationList", "" + responseDecryptedBlockKey);
            }
            if ("get_category".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertCategoryList().execute(jsonObject);
                }
                Log.d("get_category", "" + responseDecryptedBlockKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

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

        }
    }

    private void showHomeScreen(String type) {
        Intent intent = new Intent(MainPage.this, HomePage.class);
        intent.putExtra("On_Off_Type",type);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    private void pendingScreen() {
        Intent intent = new Intent(MainPage.this, PendingScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }





    public void logout() {
        dbData.open();
        ArrayList<NOS> image_count = dbData.get_Particular_Save_Image_Details_List("","","","","","","","","","","All");
        ArrayList<NOS> work_count = dbData.get_Particular_Location_Save_List("","","","","","","","","All");
        if (!Utils.isOnline()) {
            Utils.showAlert(this, "Logging out while offline may leads to loss of data!");
        } else {
            if (!(work_count.size() > 0)&&!(image_count.size()>0)) {
                closeApplication();
            } else {
                Utils.showAlert(this, "Sync all the data before logout!");
            }
        }
    }

    public void closeApplication() {
        new MyDialog(this).exitDialog(this, "Are you sure you want to Logout?", "Logout");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, "Are you sure you want to exit ?", "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
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

    public void get_category_list() {
        try {
            new ApiService(this).makeJSONObjectRequest("get_category", Api.Method.POST, UrlGenerator.getPMAYListUrl(), get_category_en_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject get_category_en_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), get_category_no_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("get_category", "" + authKey);
        return dataSet;
    }
    private JSONObject get_category_no_JsonParams() {
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_SERVICE_ID, "image_category_details");
            Log.d("get_category", "" + dataSet);
        }
        catch (Exception e){

        }

        return dataSet;
    }

    public class InsertCategoryList extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                dbData.open();
                dbData.delete_get_photo_category_table();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    NOS activity_details_item = new NOS();
                    try {
                        activity_details_item.setImage_category_id(jsonArray.getJSONObject(i).getString("image_category_id"));
                        activity_details_item.setImage_category_name(jsonArray.getJSONObject(i).getString("image_category_name"));

                        dbData.insertPhotoCategoryList(activity_details_item);

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

        }
    }



    private void syncButtonVisibility(){
        dbData.open();
        ArrayList<NOS> work_count = dbData.get_Particular_Location_Save_List("","","","","","","","","All");
        ArrayList<NOS> image_count = dbData.get_Particular_Save_Image_Details_List("","","","","","","","","","","All");
        if(work_count.size()>0 || image_count.size()>0){
            mainPageBinding.syncLayout.setVisibility(View.VISIBLE);
            //mainPageBinding.pendingCount.setText(""+work_count);
        }
        else {
            mainPageBinding.syncLayout.setVisibility(View.GONE);
        }
    }
}
