package com.nic.nammaoorusuper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.adapter.CategoryListAdapter;
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

public class SubListDashBoard extends AppCompatActivity implements Api.ServerResponseListener{
    private com.nic.nammaoorusuper.databinding.ActivitySubListDashBoardBinding subListDashBoardBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String hab_code;
    private String campaign_id;
    private String activity_id;
    private String campaign_activity_id;
    private String no_of_images;
    private String item_no;
    private String is_taken;
    private float setalpha = 7;

    ArrayList<NOS> label_list;

    String go_flag="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subListDashBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_sub_list_dash_board);
        subListDashBoardBinding.setActivity(this);
        /*ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.background));*/
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.statuscolor(SubListDashBoard.this);
        campaign_id = getIntent().getStringExtra("campaign_id");
        campaign_activity_id = getIntent().getStringExtra("campaign_activity_id");
        activity_id = getIntent().getStringExtra("activity_id");
        hab_code = getIntent().getStringExtra("hab_code");
        no_of_images = getIntent().getStringExtra("no_of_images");
        item_no = getIntent().getStringExtra("item_no");
        is_taken = getIntent().getStringExtra("is_taken");

        if(is_taken.equals("Y")){
            subListDashBoardBinding.entryForm.setEnabled(true);
            subListDashBoardBinding.viewForm.setEnabled(true);
            subListDashBoardBinding.takePhoto.setEnabled(true);
            subListDashBoardBinding.takePhoto.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner_strong_rect_bg_color));
            subListDashBoardBinding.viewForm.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner_strong_rect_bg_color));
        }
        else {
            subListDashBoardBinding.entryForm.setEnabled(true);
            subListDashBoardBinding.viewForm.setEnabled(false);
            subListDashBoardBinding.takePhoto.setEnabled(false);
            subListDashBoardBinding.takePhoto.setBackgroundDrawable(getResources().getDrawable(R.drawable.blured_shadow));
            subListDashBoardBinding.viewForm.setBackgroundDrawable(getResources().getDrawable(R.drawable.blured_shadow));
        }
        subListDashBoardBinding.entryForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline()){
                    Intent intent = new Intent(SubListDashBoard.this,LocationSaveActivity.class);
                    intent.putExtra("activity_id",activity_id);
                    intent.putExtra("campaign_activity_id",campaign_activity_id);
                    intent.putExtra("hab_code",hab_code);
                    intent.putExtra("campaign_id",campaign_id);
                    intent.putExtra("item_no",item_no );
                    intent.putExtra("no_of_images", no_of_images);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
                else {
                    Utils.showAlert(SubListDashBoard.this,"No Internet");
                }
            }
        });

        subListDashBoardBinding.takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline()){
                    go_flag = "take_photo";
                    getActivityEntryId();
                }
                else {
                    Utils.showAlert(SubListDashBoard.this,"No Internet");
                }
            }
        });
        subListDashBoardBinding.viewForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline()){
                    go_flag = "view_form";
                    getActivityEntryId();
                }
                else {
                    Utils.showAlert(SubListDashBoard.this,"No Internet");
                }
            }
        });
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
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            if ("get_activity_entry_details".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertViewFormList().execute(jsonObject);

                }
                else {
                    Utils.showAlert(SubListDashBoard.this,jsonObject.getString("MESSAGE"));
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
                       /* activity_entry_label_list = activity_entry_list.getJSONArray("activity_entry_label_list");

                        for (int i=0;i<activity_entry_label_list.length();i++){
                            NOS activity_details_item = new NOS();
                            activity_details_item.setCampaign_activity_data(activity_entry_label_list.getJSONObject(i).getString("campaign_activity_data"));
                            activity_details_item.setCampaign_id(activity_entry_label_list.getJSONObject(i).getString("campaign_id"));
                            activity_details_item.setActivity_id(activity_entry_label_list.getJSONObject(i).getString("activity_id"));
                            activity_details_item.setCampaign_activity_id(activity_entry_label_list.getJSONObject(i).getString("campaign_activity_id"));
                            activity_details_item.setCampaign_data_label(activity_entry_label_list.getJSONObject(i).getString("campaign_data_label"));
                            activity_details_item.setCampaign_data_type(activity_entry_label_list.getJSONObject(i).getString("campaign_data_type"));
                            activity_details_item.setItem_no(activity_entry_label_list.getJSONObject(i).getString("item_no"));
                            activity_details_item.setLabel_value(activity_entry_label_list.getJSONObject(i).getString("label_value"));
                            activity_details_item.setCampaign_activity_entry_id(activity_entry_label_list.getJSONObject(i).getString("campaign_activity_entry_id"));
                            label_list.add(activity_details_item);
                        }*/

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
                    Intent intent = new Intent(SubListDashBoard.this, PhotoCategoryList.class);
                    intent.putExtra("activity_id",activity_id);
                    intent.putExtra("campaign_activity_id",campaign_activity_id);
                    intent.putExtra("hab_code",hab_code);
                    intent.putExtra("campaign_id",campaign_id);
                    intent.putExtra("campaign_activity_entry_id",campaign_activity_entry_id);
                    intent.putExtra("item_no", item_no);
                    intent.putExtra("no_of_images", no_of_images);

                    startActivity(intent);
                    //finish();
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }

            else {
                Intent intent = new Intent(SubListDashBoard.this, ViewFormActivity.class);
                intent.putExtra("activity_id",activity_id);
                intent.putExtra("campaign_activity_id",campaign_activity_id);
                intent.putExtra("hab_code",hab_code);
                intent.putExtra("campaign_id",campaign_id);
                intent.putExtra("campaign_activity_entry_id",campaign_activity_entry_id);
                intent.putExtra("item_no", item_no);
                intent.putExtra("no_of_images", no_of_images);

                startActivity(intent);
                //finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }
    }
}
