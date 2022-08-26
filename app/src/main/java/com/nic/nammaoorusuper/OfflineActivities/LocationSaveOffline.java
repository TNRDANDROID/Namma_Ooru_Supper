package com.nic.nammaoorusuper.OfflineActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.LocationSaveActivity;
import com.nic.nammaoorusuper.activity.PhotoCategoryList;
import com.nic.nammaoorusuper.adapter.ActivitySubListAdapter;
import com.nic.nammaoorusuper.adapter.DynamicRecyclerWidgetAdapter;
import com.nic.nammaoorusuper.api.Api;
import com.nic.nammaoorusuper.api.ApiService;
import com.nic.nammaoorusuper.api.ServerResponse;
import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.databinding.ActivityLocationSaveBinding;
import com.nic.nammaoorusuper.databinding.ActivityLocationSaveOfflineBinding;
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

public class LocationSaveOffline extends AppCompatActivity {

    ActivityLocationSaveOfflineBinding activityLocationSaveBinding;
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

        activityLocationSaveBinding = DataBindingUtil.setContentView(this, R.layout.activity_location_save_offline);
        activityLocationSaveBinding.setActivity(this);

        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeUI();

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

    private void initializeUI() {
        Utils.statuscolor(LocationSaveOffline.this);
        activityLocationSaveBinding.dynamicWidgetRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        campaign_id = getIntent().getStringExtra("campaign_id");
        campaign_activity_id = getIntent().getStringExtra("campaign_activity_id");
        activity_id = getIntent().getStringExtra("activity_id");
        activity_name = getIntent().getStringExtra("activity_name");
        hab_code = getIntent().getStringExtra("hab_code");
        hab_name = getIntent().getStringExtra("hab_name");
        item_no = getIntent().getStringExtra("item_no");
        no_of_images = getIntent().getStringExtra("no_of_images");

        activityLocationSaveBinding.locationCloseIcon.setVisibility(View.GONE);
        activityLocationSaveBinding.descriptionCloseIcon.setVisibility(View.GONE);

        new fetch_dynamic_widget_list().execute();
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
                    .makeText(LocationSaveOffline.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class fetch_dynamic_widget_list extends AsyncTask<Void, Void,ArrayList<NOS>> {
        @Override
        protected ArrayList<NOS> doInBackground(Void... params) {
            dbData.open();
            dynamicWidgetList = new ArrayList<>();
            dynamicWidgetList = dbData.get_Particular_Dynamic_Widget_List(campaign_id,hab_code,activity_id,campaign_activity_id);
            Log.d("widget_list_count", String.valueOf(dynamicWidgetList.size()));
            return dynamicWidgetList;
        }

        @Override
        protected void onPostExecute(ArrayList<NOS> sub_activity_list) {
            super.onPostExecute(sub_activity_list);
            if(!Utils.isOnline()) {
                if (sub_activity_list.size() == 0) {
                    Utils.showAlert(LocationSaveOffline.this, "No Data Available in Local Database. Please, Turn On mobile data");
                }
            }
            if(sub_activity_list.size()>0){
                activityLocationSaveBinding.dynamicWidgetRecycler.setVisibility(View.VISIBLE);
                activityLocationSaveBinding.dynamicWidgetLayout.setVisibility(View.VISIBLE);
                activityLocationSaveBinding.noDataIcon.setVisibility(View.GONE);
                dynamicRecyclerWidgetAdapter = new DynamicRecyclerWidgetAdapter(LocationSaveOffline.this,sub_activity_list,dbData);
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
        if(dynamicWidgetList.size()>0){
            if(checkListItemsText()){
                if(Utils.isOnline()){
                    save_location_details();
                }
                else {
                    Utils.showAlert(LocationSaveOffline.this,"No Internet");
                }
            }
            else {
                Utils.showAlert(LocationSaveOffline.this,"Please Fill All Additional Details");
            }
        }
        else {
            if(Utils.isOnline()){
                save_location_details();
            }

            else {
                Utils.showAlert(LocationSaveOffline.this,"Please Fill All Additional Details");
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

    private JSONObject save_location_details(){
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
            ContentValues contentValues = new ContentValues();
            try {
                contentValues.put("campaign_id",campaign_id);
                contentValues.put("activity_id",activity_id);
                contentValues.put("activity_name",activity_name);
                contentValues.put("campaign_activity_id",campaign_activity_id);
                contentValues.put("dcode",prefManager.getDistrictCode());
                contentValues.put("bcode",prefManager.getBlockCode());
                contentValues.put("pvcode",prefManager.getPvCode());
                contentValues.put("hab_code",hab_code);
                contentValues.put("hab_name",hab_name);
                contentValues.put("item_no",item_no);
                contentValues.put("json_value",data_set.toString());

                String whereClass = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? ";
                String[] whereargs = new String[]{campaign_id,activity_id,campaign_activity_id,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code,item_no};

                dbData.open();
                ArrayList<NOS> old_items = new ArrayList<>(dbData.get_Particular_Location_Save_List(campaign_id,activity_id,campaign_activity_id,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code,item_no,""));
                if(old_items.size()>0){
                    long row_update_id = db.update(DBHelper.LOCATION_SAVE_DETAILS_TABLE,contentValues,whereClass,whereargs);
                    if(row_update_id>0){
                        Toasty.success(LocationSaveOffline.this,"Data Updated Locally",Toasty.LENGTH_SHORT,true).show();
                        gotoPhotoCategoryOffline(row_update_id);
                    }
                }
                else {
                    long insert_id = db.insert(DBHelper.LOCATION_SAVE_DETAILS_TABLE,null,contentValues);
                    if(insert_id>0){
                        Toasty.success(LocationSaveOffline.this,"Data Inserted Locally",Toasty.LENGTH_SHORT,true).show();
                        gotoPhotoCategoryOffline(insert_id);
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return data_set;
    }

    private void gotoPhotoCategoryOffline(long row_id){
        Intent intent = new Intent(LocationSaveOffline.this, PhotoCategoryOffline.class);
        intent.putExtra("campaign_id",campaign_id);
        intent.putExtra("activity_id",activity_id);
        intent.putExtra("activity_name",activity_name);
        intent.putExtra("location_save_details_primary_id",String.valueOf(row_id));
        intent.putExtra("campaign_activity_id",campaign_activity_id);
        intent.putExtra("campaign_activity_id",campaign_activity_id);
        intent.putExtra("dcode",prefManager.getDistrictCode());
        intent.putExtra("bcode",prefManager.getBlockCode());
        intent.putExtra("pvcode",prefManager.getPvCode());
        intent.putExtra("hab_code",hab_code);
        intent.putExtra("hab_name",hab_name);
        intent.putExtra("item_no",item_no);
        intent.putExtra("no_of_images", no_of_images);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

}
