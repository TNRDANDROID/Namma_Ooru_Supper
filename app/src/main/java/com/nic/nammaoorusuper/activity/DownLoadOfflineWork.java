package com.nic.nammaoorusuper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.nic.nammaoorusuper.OfflineActivities.ActivityListOffLine;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.adapter.ActivityListAdapter;
import com.nic.nammaoorusuper.adapter.CommonAdapter;
import com.nic.nammaoorusuper.api.Api;
import com.nic.nammaoorusuper.api.ApiService;
import com.nic.nammaoorusuper.api.ServerResponse;
import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.databinding.ActivityDownLoadOfflineWorkBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.UrlGenerator;
import com.nic.nammaoorusuper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DownLoadOfflineWork extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    ActivityDownLoadOfflineWorkBinding downLoadOfflineWorkBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private String hab_code;
    private String hab_name;
    private String campaign_id;
    private List<NOS> habitationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downLoadOfflineWorkBinding = DataBindingUtil.setContentView(this, R.layout.activity_down_load_offline_work);
        downLoadOfflineWorkBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
            Utils.statuscolor(DownLoadOfflineWork.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        downLoadOfflineWorkBinding.downLoadYourWork.setVisibility(View.GONE);
        downLoadOfflineWorkBinding.downLoadYourCategory.setVisibility(View.GONE);
        downLoadOfflineWorkBinding.skipLayout.setVisibility(View.GONE);

        downLoadOfflineWorkBinding.habitationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    hab_code = habitationList.get(position).getHabCode();
                    hab_name = habitationList.get(position).getHabitationName();
                    downLoadOfflineWorkBinding.downLoadYourWork.setVisibility(View.VISIBLE);
                    dbData.open();
                    ArrayList<NOS> get_category_count = new ArrayList<>(dbData.get_Particular_Category_Get_List("","","","","","","","","All"));
                    if(get_category_count.size()>0){
                        downLoadOfflineWorkBinding.downLoadYourCategory.setVisibility(View.VISIBLE);
                    }
                    else {
                        downLoadOfflineWorkBinding.downLoadYourCategory.setVisibility(View.GONE);
                    }
                    ArrayList<NOS> activity_count= new ArrayList<>(dbData.get_Particular_Campaign_Activity_List(campaign_id,hab_code));
                    if(activity_count.size()>0){
                        downLoadOfflineWorkBinding.skipLayout.setVisibility(View.VISIBLE);
                    }
                    else {
                        downLoadOfflineWorkBinding.skipLayout.setVisibility(View.GONE);
                    }

                }
                else {
                    hab_code="";
                    hab_name="";
                    downLoadOfflineWorkBinding.downLoadYourWork.setVisibility(View.GONE);
                    downLoadOfflineWorkBinding.downLoadYourCategory.setVisibility(View.GONE);
                    downLoadOfflineWorkBinding.skipLayout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initializeUI();
    }

    private void initializeUI() {
        campaign_id = getIntent().getStringExtra("campaign_id");
        downLoadOfflineWorkBinding.downLoadYourWork.setOnClickListener(this::onClick);
        downLoadOfflineWorkBinding.downLoadYourCategory.setOnClickListener(this::onClick);
        downLoadOfflineWorkBinding.skipLayout.setOnClickListener(this::onClick);
        habitationFilterSpinner(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode());

    }
    private void habitationFilterSpinner(String dcode, String bcode, String pvcode) {
        Cursor HABList = null;
        HABList = db.rawQuery("SELECT * FROM " + DBHelper.HABITATION_TABLE_NAME + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' order by habitation_name asc", null);

        habitationList.clear();
        NOS habitationListValue = new NOS();
        habitationListValue.setHabitationName("Select Habitation");
        habitationList.add(habitationListValue);
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

                    habitationList.add(habList);
                    Log.d("spinnersize", "" + habitationList.size());
                } while (HABList.moveToNext());
            }
        }
        if(habitationList.size()>0){
            downLoadOfflineWorkBinding.habitationSpinner.setAdapter(new CommonAdapter(DownLoadOfflineWork.this,habitationList,"HabitationList"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.down_load_your_work:
                if(!hab_code.equals("")){
                    getDownLoadList();
                }
                else {
                    Utils.showAlert(DownLoadOfflineWork.this,"Please Select Habitation!");
                }
                break;

            case R.id.down_load_your_category:
                if(!hab_code.equals("")){
                    getDownLoadCategoryList();
                }
                else {
                    Utils.showAlert(DownLoadOfflineWork.this,"Please Select Habitation!");
                }
                break;

            case R.id.skip_layout:
                Intent intent = new Intent(DownLoadOfflineWork.this, ActivityListOffLine.class);
                intent.putExtra("campaign_id",campaign_id);
                intent.putExtra("hab_code",hab_code);
                intent.putExtra("hab_name",hab_name);

                startActivity(intent);
                break;
        }
    }

    private void getDownLoadList() {
        try {
            new ApiService(this).makeJSONObjectRequest("activity_details", Api.Method.POST, UrlGenerator.getPMAYListUrl(), activity_details_en_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private JSONObject activity_details_en_JsonParams() throws JSONException {
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
            dataSet.put(AppConstant.KEY_SERVICE_ID, "campaign_activity_details");
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


            if ("activity_details".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertActivityList().execute(jsonObject);
                }

                Log.d("activity_details", "" + responseDecryptedBlockKey);
            }
            if ("category_details".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertCategoryList().execute(jsonObject);
                }

                Log.d("category_details", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    @SuppressLint("StaticFieldLeak")
    private class InsertActivityList extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                String whereClass = "campaign_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? ";
                String[] whereArgs=new String[]{campaign_id,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code};
                dbData.open();
                db.delete(DBHelper.ACTIVITY_LIST_TABLE, whereClass,whereArgs);
                db.delete(DBHelper.SUB_ACTIVITY_LIST_TABLE, whereClass,whereArgs);
                db.delete(DBHelper.DYNAMIC_WIDGET_DETAILS_TABLE, whereClass,whereArgs);

                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

                        dbData.insertActivityList(activity_details_item);

                        JSONArray activity_sub_list_array = jsonArray.getJSONObject(i).getJSONArray("activity_sub_list");
                        for(int j=0;j<activity_sub_list_array.length();j++){
                            NOS activity_sub_list_items = new NOS();
                            activity_sub_list_items.setCampaign_activity_details_id(activity_sub_list_array.getJSONObject(j).getString("campaign_activity_details_id"));
                            activity_sub_list_items.setCampaign_id(activity_sub_list_array.getJSONObject(j).getString("campaign_id"));
                            activity_sub_list_items.setActivity_id(activity_sub_list_array.getJSONObject(j).getString("activity_id"));
                            activity_sub_list_items.setCampaign_activity_id(activity_sub_list_array.getJSONObject(j).getString("campaign_activity_id"));
                            activity_sub_list_items.setDistictCode(activity_sub_list_array.getJSONObject(j).getString("dcode"));
                            activity_sub_list_items.setBlockCode(activity_sub_list_array.getJSONObject(j).getString("bcode"));
                            activity_sub_list_items.setPvCode(activity_sub_list_array.getJSONObject(j).getString("pvcode"));
                            activity_sub_list_items.setHabCode(activity_sub_list_array.getJSONObject(j).getString("hab_code"));
                            activity_sub_list_items.setActivity_sub_list(activity_sub_list_array.getJSONObject(j).getString("activity_sub_list"));
                            activity_sub_list_items.setItem_no(activity_sub_list_array.getJSONObject(j).getString("item_no"));
                            activity_sub_list_items.setIs_taken(activity_sub_list_array.getJSONObject(j).getString("is_taken"));

                            dbData.insertSubActivityList(activity_sub_list_items);

                        }
                        JSONArray activity_label_list_array = jsonArray.getJSONObject(i).getJSONArray("activity_label_list");

                        for (int k=0;k<activity_label_list_array.length();k++){
                            NOS activity_label_list_items = new NOS();
                            activity_label_list_items.setCampaign_activity_data(activity_label_list_array.getJSONObject(k).getString("campaign_activity_data"));
                            activity_label_list_items.setCampaign_activity_id(activity_label_list_array.getJSONObject(k).getString("campaign_activity_id"));
                            activity_label_list_items.setCampaign_id(activity_label_list_array.getJSONObject(k).getString("campaign_id"));
                            activity_label_list_items.setActivity_id(activity_label_list_array.getJSONObject(k).getString("activity_id"));
                            activity_label_list_items.setDistictCode(jsonArray.getJSONObject(i).getString("dcode"));
                            activity_label_list_items.setBlockCode(jsonArray.getJSONObject(i).getString("bcode"));
                            activity_label_list_items.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                            activity_label_list_items.setHabCode(jsonArray.getJSONObject(i).getString("hab_code"));
                            activity_label_list_items.setCampaign_data_label(activity_label_list_array.getJSONObject(k).getString("campaign_data_label"));
                            activity_label_list_items.setCampaign_data_type(activity_label_list_array.getJSONObject(k).getString("campaign_data_type"));
                            dbData.insertDynamicWidgetList(activity_label_list_items);
                        }


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
            Intent intent = new Intent(DownLoadOfflineWork.this, ActivityListOffLine.class);
            intent.putExtra("campaign_id",campaign_id);
            intent.putExtra("hab_code",hab_code);
            intent.putExtra("hab_name",hab_name);

            startActivity(intent);
        }
    }

    private void getDownLoadCategoryList() {
        try {
            new ApiService(this).makeJSONObjectRequest("category_details", Api.Method.POST, UrlGenerator.getPMAYListUrl(), get_category_details_en_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private JSONObject get_category_details_en_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), get_category_details_no_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("category_details", "" + authKey);
        return dataSet;
    }
    private JSONObject get_category_details_no_JsonParams() {
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_SERVICE_ID, "campaign_activity_image_category_list");
            dataSet.put("hab_code",hab_code);
            dataSet.put("campaign_id", campaign_id);
            dataSet.put("activity_details", getCategoryList());
            Log.d("category_details", "" + dataSet);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return dataSet;
    }

    private JSONArray getCategoryList(){
        dbData.open();
        JSONArray jsonArray = new JSONArray();
        ArrayList<NOS> get_category_count = new ArrayList<>(dbData.get_Particular_Category_Get_List("","","","","","","","","All"));
        if(get_category_count.size()>0){
            for(int i=0;i<get_category_count.size();i++){
                JSONObject dataSet = new JSONObject();
                try {
                    dataSet.put("activity_id", get_category_count.get(i).getActivity_id());
                    dataSet.put("campaign_activity_id", get_category_count.get(i).getCampaign_activity_id());
                    dataSet.put("campaign_activity_entry_id", get_category_count.get(i).getCampaign_activity_entry_id());
                    dataSet.put("item_no", get_category_count.get(i).getItem_no());
                    jsonArray.put(dataSet);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
        return  jsonArray;
    }

    @SuppressLint("StaticFieldLeak")
    private class InsertCategoryList extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                String whereClass = "campaign_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? ";
                String[] whereArgs=new String[]{campaign_id,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code};
                dbData.open();
                db.delete(DBHelper.GET_CATEGORY_DETAILS_TABLE, whereClass,whereArgs);

                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    NOS activity_details_item = new NOS();
                    try {
                        activity_details_item.setCampaign_id(jsonArray.getJSONObject(i).getString("campaign_id"));
                        activity_details_item.setActivity_id(jsonArray.getJSONObject(i).getString("activity_id"));
                        activity_details_item.setCampaign_activity_id(jsonArray.getJSONObject(i).getString("campaign_activity_id"));
                        activity_details_item.setCampaign_activity_entry_id(jsonArray.getJSONObject(i).getString("campaign_activity_entry_id"));
                        activity_details_item.setDistictCode(jsonArray.getJSONObject(i).getString("dcode"));
                        activity_details_item.setBlockCode(jsonArray.getJSONObject(i).getString("bcode"));
                        activity_details_item.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                        activity_details_item.setHabCode(jsonArray.getJSONObject(i).getString("hab_code"));
                        activity_details_item.setItem_no(jsonArray.getJSONObject(i).getString("item_no"));
                        activity_details_item.setImage_category_id(jsonArray.getJSONObject(i).getString("image_category_id"));
                        activity_details_item.setImage_category_name(jsonArray.getJSONObject(i).getString("image_category_name"));
                        activity_details_item.setIs_taken(jsonArray.getJSONObject(i).getString("is_taken"));

                        dbData.insertCategoryDetailsList(activity_details_item);


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
            Intent intent = new Intent(DownLoadOfflineWork.this, ActivityListOffLine.class);
            intent.putExtra("campaign_id",campaign_id);
            intent.putExtra("hab_code",hab_code);
            intent.putExtra("hab_name",hab_name);

            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbData.open();
        ArrayList<NOS> activity_count= new ArrayList<>(dbData.get_Particular_Campaign_Activity_List(campaign_id,hab_code));
        if(activity_count.size()>0){
            downLoadOfflineWorkBinding.skipLayout.setVisibility(View.VISIBLE);
        }
        else {
            downLoadOfflineWorkBinding.skipLayout.setVisibility(View.GONE);
        }
    }
}
