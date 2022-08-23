package com.nic.nammaoorusuper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.adapter.CategoryListAdapter;
import com.nic.nammaoorusuper.adapter.CategoryListeNameAdapter;
import com.nic.nammaoorusuper.adapter.DynamicRecyclerWidgetAdapter;
import com.nic.nammaoorusuper.adapter.ImageListAdapter;
import com.nic.nammaoorusuper.adapter.ViewFormAdapter;
import com.nic.nammaoorusuper.api.Api;
import com.nic.nammaoorusuper.api.ApiService;
import com.nic.nammaoorusuper.api.ServerResponse;
import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.databinding.ActivityViewFormBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.UrlGenerator;
import com.nic.nammaoorusuper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewFormActivity extends AppCompatActivity implements Api.ServerResponseListener {

    ActivityViewFormBinding activityViewFormBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String hab_code;
    private String campaign_id;
    private String activity_id;
    private String campaign_activity_id;
    String campaign_activity_entry_id;
    private String item_no;
    private String no_of_images;
    private String image_category_id;

    ArrayList<NOS> label_list= new ArrayList<>();
    ArrayList<NOS> categoryList= new ArrayList<>();
    ArrayList<NOS> imageList= new ArrayList<>();
    ViewFormAdapter viewFormAdapter;
    private static final int LOCATION_SPEECH = 103;
    private static final int DESCRIPTION_SPEECH = 101;
    CategoryListeNameAdapter categoryListeNameAdapter;
    ImageListAdapter imageListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_save);
        Utils.statuscolor(ViewFormActivity.this);
        activityViewFormBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_form);
        activityViewFormBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        activityViewFormBinding.menuRecyclerLayout.setVisibility(View.GONE);
        activityViewFormBinding.imageRecyclerLayout.setVisibility(View.GONE);
        activityViewFormBinding.dynamicWidgetRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityViewFormBinding.menuRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityViewFormBinding.imageRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        campaign_id = getIntent().getStringExtra("campaign_id");
        campaign_activity_id = getIntent().getStringExtra("campaign_activity_id");
        activity_id = getIntent().getStringExtra("activity_id");
        hab_code = getIntent().getStringExtra("hab_code");
        item_no = getIntent().getStringExtra("item_no");
        no_of_images = getIntent().getStringExtra("no_of_images");


        if(Utils.isOnline()){
            get_category_list();
            getActivityEntryId();
        }
        else {
            Utils.showAlert(ViewFormActivity.this,"No Internet");
        }


        activityViewFormBinding.maniLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityViewFormBinding.menuRecyclerLayout.setVisibility(View.GONE);
            }
        });
        activityViewFormBinding.scrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityViewFormBinding.menuRecyclerLayout.setVisibility(View.GONE);
            }
        });
        activityViewFormBinding.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityViewFormBinding.menuRecyclerLayout.setVisibility(View.VISIBLE);
            }
        });
       /* /////////////////////Popup Menu
        activityViewFormBinding.menuIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ViewFormActivity.this, activityViewFormBinding.menuIcon);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.view_photo_types_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.before:
                                break;
                            case R.id.during:
                                break;
                            case R.id.after:
                                break;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method
*/
}

    public void categoryRecyclerItemClicked(int pos){
        image_category_id = categoryList.get(pos).getImage_category_id();
        activityViewFormBinding.menuRecyclerLayout.setVisibility(View.GONE);
        get_activity_photos();
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
                        Utils.showAlert(ViewFormActivity.this,jsonObject.getString("MESSAGE"));
                    }
                    Log.d("activity_entry_details", "" + responseDecryptedBlockKey);
                }
                if ("get_category".equals(urlType) && loginResponse != null) {
                    String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                    String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                    JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                    if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                        new InsertCategoryList().execute(jsonObject);
                    }
                    else {
                        Utils.showAlert(ViewFormActivity.this,jsonObject.getString("MESSAGE"));
                    }
                    Log.d("activity_entry_details", "" + responseDecryptedBlockKey);
                }

                if ("get_activity_photos".equals(urlType) && loginResponse != null) {
                    String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                    String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                    JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                    if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                        new InsertImageList().execute(jsonObject);
                    }
                    else {
                        //activityViewFormBinding.noDataIcon.setVisibility(View.VISIBLE);
                        activityViewFormBinding.imageRecyclerLayout.setVisibility(View.GONE);
                        activityViewFormBinding.additionalRecycler.setVisibility(View.VISIBLE);
                        activityViewFormBinding.imageRecycler.setVisibility(View.GONE);
                        activityViewFormBinding.imageRecycler.setAdapter(null);
                    }

                    Log.d("category", "" + responseDecryptedBlockKey);
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

        String location_details;
        String location_description;
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
                    label_list = new ArrayList<>();
                    try {
                        activity_entry_list = jsonObject.getJSONObject("activity_entry_list");
                        hab_code= activity_entry_list.getString("hab_code");
                        activity_id= activity_entry_list.getString("activity_id");
                        campaign_id= activity_entry_list.getString("campaign_id");
                        item_no= activity_entry_list.getString("item_no");
                        campaign_activity_id= activity_entry_list.getString("campaign_activity_id");
                        campaign_activity_entry_id= activity_entry_list.getString("campaign_activity_entry_id");
                        location_details= activity_entry_list.getString("location_details");
                        location_description= activity_entry_list.getString("location_description");
                        activity_entry_label_list = jsonObject.getJSONArray("activity_entry_label_list");


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
                        }

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
            activityViewFormBinding.locationEt.setText(Utils.NotNullString(location_details));
            activityViewFormBinding.locationEt.setFocusable(false);
            activityViewFormBinding.descriptionEt.setFocusable(false);
            activityViewFormBinding.descriptionEt.setText(Utils.NotNullString(location_description));
            if(label_list.size()>0){
                activityViewFormBinding.dynamicWidgetRecycler.setVisibility(View.VISIBLE);
                activityViewFormBinding.noDataIcon.setVisibility(View.GONE);
                activityViewFormBinding.imageRecyclerLayout.setVisibility(View.GONE);
                activityViewFormBinding.additionalRecycler.setVisibility(View.VISIBLE);
                viewFormAdapter = new ViewFormAdapter(ViewFormActivity.this,label_list,dbData);
                activityViewFormBinding.dynamicWidgetRecycler.setAdapter(viewFormAdapter);
            }
            else {
                activityViewFormBinding.dynamicWidgetRecycler.setVisibility(View.GONE);
                activityViewFormBinding.additionalRecycler.setVisibility(View.GONE);
                activityViewFormBinding.noDataIcon.setVisibility(View.VISIBLE);
                activityViewFormBinding.dynamicWidgetRecycler.setAdapter(null);
            }
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
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                categoryList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    NOS activity_details_item = new NOS();
                    try {
                        activity_details_item.setImage_category_id(jsonArray.getJSONObject(i).getString("image_category_id"));
                        activity_details_item.setImage_category_name(jsonArray.getJSONObject(i).getString("image_category_name"));

                        categoryList.add(activity_details_item);
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
            if(categoryList.size()>0){
                activityViewFormBinding.noDataIcon.setVisibility(View.GONE);
                activityViewFormBinding.menuRecycler.setVisibility(View.VISIBLE);
                categoryListeNameAdapter =new CategoryListeNameAdapter(ViewFormActivity.this, categoryList,dbData);
                activityViewFormBinding.menuRecycler.setAdapter(categoryListeNameAdapter);
            }

        }
    }

    private Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);/* w  w  w.ja va 2 s  .  c om*/
        return bmp;
    }
    public void get_activity_photos() {
        try {
            new ApiService(this).makeJSONObjectRequest("get_activity_photos", Api.Method.POST, UrlGenerator.getPMAYListUrl(), get_activity_photos_en_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject get_activity_photos_en_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), get_activity_photos_no_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("get_activity_photos", "" + authKey);
        return dataSet;
    }
    private JSONObject get_activity_photos_no_JsonParams() {
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_SERVICE_ID, "get_activity_photos");
            dataSet.put("hab_code",hab_code);
            dataSet.put("campaign_id", campaign_id);
            dataSet.put("activity_id", activity_id);
            dataSet.put("campaign_activity_id", campaign_activity_id);
            dataSet.put("campaign_activity_entry_id", campaign_activity_entry_id);
            dataSet.put("item_no", item_no);
            dataSet.put("image_category_id", image_category_id);
            Log.d("get_activity_photos", "" + dataSet);
        }
        catch (Exception e){

        }

        return dataSet;
    }

    public class InsertImageList extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                imageList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    NOS activity_details_item = new NOS();
                    try {
                        activity_details_item.setCampaign_id(jsonArray.getJSONObject(i).getString("campaign_id"));
                        activity_details_item.setActivity_id(jsonArray.getJSONObject(i).getString("activity_id"));
                        activity_details_item.setCampaign_activity_id(jsonArray.getJSONObject(i).getString("campaign_activity_id"));
                        activity_details_item.setDistictCode(jsonArray.getJSONObject(i).getString("dcode"));
                        activity_details_item.setBlockCode(jsonArray.getJSONObject(i).getString("bcode"));
                        activity_details_item.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                        activity_details_item.setHabCode(jsonArray.getJSONObject(i).getString("hab_code"));
                        activity_details_item.setItem_no(jsonArray.getJSONObject(i).getString("item_no"));
                        activity_details_item.setCampaign_activity_entry_id(jsonArray.getJSONObject(i).getString("campaign_activity_entry_id"));
                        activity_details_item.setDescription(jsonArray.getJSONObject(i).getString("image_description"));
                        activity_details_item.setImage(convertStringToBitmap(jsonArray.getJSONObject(i).getString("image")));


                        imageList.add(activity_details_item);
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
            if(imageList.size()>0){
                activityViewFormBinding.noDataIcon.setVisibility(View.GONE);
                activityViewFormBinding.imageRecyclerLayout.setVisibility(View.VISIBLE);
                activityViewFormBinding.additionalRecycler.setVisibility(View.GONE);
                activityViewFormBinding.imageRecycler.setVisibility(View.VISIBLE);
                imageListAdapter =new ImageListAdapter(ViewFormActivity.this, imageList,dbData);
                activityViewFormBinding.imageRecycler.setAdapter(imageListAdapter);
            }
            else {
                activityViewFormBinding.noDataIcon.setVisibility(View.VISIBLE);
                activityViewFormBinding.imageRecyclerLayout.setVisibility(View.GONE);
                activityViewFormBinding.imageRecycler.setVisibility(View.GONE);
                activityViewFormBinding.imageRecycler.setAdapter(null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(activityViewFormBinding.menuRecyclerLayout.getVisibility()==View.VISIBLE){
            activityViewFormBinding.menuRecyclerLayout.setVisibility(View.GONE);
            activityViewFormBinding.additionalRecycler.setVisibility(View.VISIBLE);
        }
        else if(activityViewFormBinding.imageRecyclerLayout.getVisibility()==View.VISIBLE){
            activityViewFormBinding.imageRecyclerLayout.setVisibility(View.GONE);
            activityViewFormBinding.additionalRecycler.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
        }
    }
}
