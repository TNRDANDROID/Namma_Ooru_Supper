package com.nic.nammaoorusuper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.adapter.DynamicRecyclerWidgetAdapter;
import com.nic.nammaoorusuper.api.Api;
import com.nic.nammaoorusuper.api.ApiService;
import com.nic.nammaoorusuper.api.ServerResponse;
import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.databinding.ActivityLocationSaveBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.UrlGenerator;
import com.nic.nammaoorusuper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import static android.os.Build.VERSION_CODES.N;

public class LocationSaveActivity extends AppCompatActivity implements Api.ServerResponseListener {

    ActivityLocationSaveBinding activityLocationSaveBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String hab_code;
    private String campaign_id;
    private String activity_id;
    private String campaign_activity_id;
    private String item_no;
    private String no_of_images;

    ArrayList<NOS> dynamicWidgetList= new ArrayList<>();
    DynamicRecyclerWidgetAdapter dynamicRecyclerWidgetAdapter;
    private static final int LOCATION_SPEECH = 103;
    private static final int DESCRIPTION_SPEECH = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_save);
        Utils.statuscolor(LocationSaveActivity.this);
        activityLocationSaveBinding = DataBindingUtil.setContentView(this, R.layout.activity_location_save);
        activityLocationSaveBinding.setActivity(this);
        /*ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.background));*/
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        activityLocationSaveBinding.dynamicWidgetRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        campaign_id = getIntent().getStringExtra("campaign_id");
        campaign_activity_id = getIntent().getStringExtra("campaign_activity_id");
        activity_id = getIntent().getStringExtra("activity_id");
        hab_code = getIntent().getStringExtra("hab_code");
        item_no = getIntent().getStringExtra("item_no");
        no_of_images = getIntent().getStringExtra("no_of_images");

        if(Utils.isOnline()){
            getDynamicWidgetList();
        }

        activityLocationSaveBinding.locationCloseIcon.setVisibility(View.GONE);
        activityLocationSaveBinding.descriptionCloseIcon.setVisibility(View.GONE);
        activityLocationSaveBinding.locationAudioIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechToText("Location");
            }
        });
        activityLocationSaveBinding.descriptionAudioIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechToText("Description");
            }
        });
        activityLocationSaveBinding.locationCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityLocationSaveBinding.locationEt.setText("");
                activityLocationSaveBinding.locationAudioIcon.setVisibility(View.VISIBLE);
                activityLocationSaveBinding.locationCloseIcon.setVisibility(View.GONE);
            }
        });
        activityLocationSaveBinding.descriptionCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityLocationSaveBinding.descriptionEt.setText("");
                activityLocationSaveBinding.descriptionAudioIcon.setVisibility(View.VISIBLE);
                activityLocationSaveBinding.descriptionCloseIcon.setVisibility(View.GONE);
            }
        });

        activityLocationSaveBinding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });
    }
    public void getDynamicWidgetList() {
        try {
            new ApiService(this).makeJSONObjectRequest("dynamic_widget_details", Api.Method.POST, UrlGenerator.getPMAYListUrl(), dynamic_widget_en_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject dynamic_widget_en_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), dynamic_widget_no_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("sub_activty", "" + authKey);
        return dataSet;
    }
    private JSONObject dynamic_widget_no_JsonParams() {
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_SERVICE_ID, "campaign_activity_label_list");
            //dataSet.put("hab_code",hab_code);
            dataSet.put("campaign_id", campaign_id);
            dataSet.put("activity_id", activity_id);
            dataSet.put("campaign_activity_id", campaign_activity_id);
            Log.d("sub_activty", "" + dataSet);
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

            if ("dynamic_widget_details".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertDynamicWidgetList().execute(jsonObject);
                }
                else {
                    activityLocationSaveBinding.dynamicWidgetRecycler.setVisibility(View.GONE);
                    activityLocationSaveBinding.noDataIcon.setVisibility(View.VISIBLE);
                    activityLocationSaveBinding.dynamicWidgetRecycler.setAdapter(null);
                }
                Log.d("dynamic_widget_details", "" + responseDecryptedBlockKey);
            }
            if ("save_details".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Toasty.success(LocationSaveActivity.this,jsonObject.getString("MESSAGE"),Toasty.LENGTH_SHORT,true).show();
                    String hab_code= jsonObject.getString("hab_code");
                    String activity_id= jsonObject.getString("activity_id");
                    String campaign_id= jsonObject.getString("campaign_id");
                    String item_no= jsonObject.getString("item_no");
                    String campaign_activity_id= jsonObject.getString("campaign_activity_id");
                    String campaign_activity_entry_id= jsonObject.getString("campaign_activity_entry_id");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(LocationSaveActivity.this, PhotoCategoryList.class);
                            intent.putExtra("activity_id",activity_id);
                            intent.putExtra("campaign_activity_id",campaign_activity_id);
                            intent.putExtra("hab_code",hab_code);
                            intent.putExtra("campaign_id",campaign_id);
                            intent.putExtra("campaign_activity_entry_id",campaign_activity_entry_id);
                            intent.putExtra("item_no", item_no);
                            intent.putExtra("no_of_images", no_of_images);

                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }
                    }, 400);
                }
                else {
                    Utils.showAlert(LocationSaveActivity.this,jsonObject.getString("MESSAGE"));
                }
                Log.d("save_details", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
    public class InsertDynamicWidgetList extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dynamicWidgetList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    NOS activity_details_item = new NOS();
                    try {
                        activity_details_item.setCampaign_activity_data(jsonArray.getJSONObject(i).getString("campaign_activity_data"));
                        activity_details_item.setCampaign_id(jsonArray.getJSONObject(i).getString("campaign_id"));
                        activity_details_item.setActivity_id(jsonArray.getJSONObject(i).getString("activity_id"));
                        activity_details_item.setCampaign_activity_id(jsonArray.getJSONObject(i).getString("campaign_activity_id"));
                        activity_details_item.setCampaign_data_label(jsonArray.getJSONObject(i).getString("campaign_data_label"));
                        activity_details_item.setCampaign_data_type(jsonArray.getJSONObject(i).getString("campaign_data_type"));

                        dynamicWidgetList.add(activity_details_item);
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
            if(dynamicWidgetList.size()>0){
                activityLocationSaveBinding.dynamicWidgetRecycler.setVisibility(View.VISIBLE);
                activityLocationSaveBinding.dynamicWidgetLayout.setVisibility(View.VISIBLE);
                activityLocationSaveBinding.noDataIcon.setVisibility(View.GONE);
                dynamicRecyclerWidgetAdapter = new DynamicRecyclerWidgetAdapter(LocationSaveActivity.this,dynamicWidgetList,dbData);
                activityLocationSaveBinding.dynamicWidgetRecycler.setAdapter(dynamicRecyclerWidgetAdapter);
            }
            else {
                activityLocationSaveBinding.dynamicWidgetLayout.setVisibility(View.GONE);
                activityLocationSaveBinding.dynamicWidgetRecycler.setVisibility(View.GONE);
                activityLocationSaveBinding.noDataIcon.setVisibility(View.VISIBLE);
                activityLocationSaveBinding.dynamicWidgetRecycler.setAdapter(null);
            }
        }
    }

    public void speechToText(String request_id) {
        Intent intent
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                "ta-IND");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {

            if (request_id.equals("Location")) {
                startActivityForResult(intent, LOCATION_SPEECH);
            } else if (request_id.equals("Description")) {
                startActivityForResult(intent, DESCRIPTION_SPEECH);
            }

        } catch (Exception e) {
            Toast
                    .makeText(LocationSaveActivity.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case LOCATION_SPEECH:

                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    activityLocationSaveBinding.locationEt.setText(
                            Objects.requireNonNull(result).get(0));
                    activityLocationSaveBinding.locationAudioIcon.setVisibility(View.GONE);
                    activityLocationSaveBinding.locationCloseIcon.setVisibility(View.VISIBLE);

                }

                break;
            case DESCRIPTION_SPEECH:

                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    activityLocationSaveBinding.descriptionEt.setText(
                            Objects.requireNonNull(result).get(0));
                    activityLocationSaveBinding.descriptionAudioIcon.setVisibility(View.GONE);
                    activityLocationSaveBinding.descriptionCloseIcon.setVisibility(View.VISIBLE);
                }

                break;

            default:
                break;
        }
    }

    public void checkValidation(){
       /* if(!activityLocationSaveBinding.locationEt.getText().toString().equals("")){
            if(!activityLocationSaveBinding.descriptionEt.getText().toString().equals("")){

            }
            else {
                Utils.showAlert(LocationSaveActivity.this,"Please Enter Description");
            }
        }
        else {
            Utils.showAlert(LocationSaveActivity.this,"Please Enter Location");
        }*/
        if(dynamicWidgetList.size()>0){
            if(checkListItemsText()){
                if(Utils.isOnline()){
                    saveLocationDetails();
                }
                else {
                    Utils.showAlert(LocationSaveActivity.this,"No Internet");
                }
            }
            else {
                Utils.showAlert(LocationSaveActivity.this,"Please Fill All Details");
            }
        }
        else {
            if(Utils.isOnline()){
                saveLocationDetails();
            }

            else {
                Utils.showAlert(LocationSaveActivity.this,"Please Fill All Details");
            }
        }
    }

    public boolean checkListItemsText(){
        boolean validation_check=true;
        View v;
        int itemCount = activityLocationSaveBinding.dynamicWidgetRecycler.getAdapter().getItemCount();
        for(int i = 0; i < itemCount; i++){
            v = activityLocationSaveBinding.dynamicWidgetRecycler.getChildAt(i); /* assuming you have your EditText in a view group like LinearLayout*/
            EditText et = v.findViewById(R.id.description_et);
            String text = et.getText().toString();
            if(!text.equals("")){
                validation_check= true;
            }
            else {
                validation_check= false;
                break;
            }
        }
        if(validation_check){
            return true;
        }
        else {
            return false;
        }

    }
    public void saveLocationDetails() {
        try {
            new ApiService(this).makeJSONObjectRequest("save_details", Api.Method.POST, UrlGenerator.getPMAYListUrl(), save_location_details_en_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject save_location_details_en_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), save_location_details_no_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("save_data", "" + authKey);
        return dataSet;
    }

    private JSONObject save_location_details_no_JsonParams(){
        JSONObject data_set = new JSONObject();
        JSONObject activity_entry_list_items = new JSONObject();
        JSONArray activity_entry_label_list = new JSONArray();
        try {
            data_set.put(AppConstant.KEY_SERVICE_ID,"campaign_activity_data_save");
            data_set.put("campaign_id",campaign_id);
            data_set.put("hab_code",hab_code);
            data_set.put("activity_id",activity_id);
            data_set.put("campaign_activity_id",campaign_activity_id);
            data_set.put("item_no",item_no);
            if(!activityLocationSaveBinding.locationEt.getText().toString().equals("")){
                activity_entry_list_items.put("location_details",activityLocationSaveBinding.locationEt.getText().toString());
            }
            else {
                activity_entry_list_items.put("location_details","");
            }
            if(!activityLocationSaveBinding.descriptionEt.getText().toString().equals("")){
                activity_entry_list_items.put("location_description",activityLocationSaveBinding.descriptionEt.getText().toString());

            }
            else {
                activity_entry_list_items.put("location_description","");
            }

            data_set.put("activity_entry_list",activity_entry_list_items);
            if(dynamicWidgetList.size()>0){
                int itemCount = activityLocationSaveBinding.dynamicWidgetRecycler.getAdapter().getItemCount();
                if(itemCount>0){
                    View v;
                    for(int i = 0; i < itemCount; i++){
                        JSONObject jsonObject = new JSONObject();
                        v = activityLocationSaveBinding.dynamicWidgetRecycler.getChildAt(i); /* assuming you have your EditText in a view group like LinearLayout*/
                        EditText et = v.findViewById(R.id.description_et);
                        String text = et.getText().toString();
                        if(!text.equals("")){
                            jsonObject.put("campaign_activity_data",dynamicWidgetList.get(i).getCampaign_activity_data());
                            jsonObject.put("label_value",text);
                            activity_entry_label_list.put(jsonObject);
                        }
                    }
                    data_set.put("activity_entry_label_list",activity_entry_label_list);
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return data_set;
    }
}
