package com.nic.nammaoorusuper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.adapter.CategoryListAdapter;
import com.nic.nammaoorusuper.adapter.ImageListAdapter;
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

public class PhotoCategoryList extends AppCompatActivity implements Api.ServerResponseListener{
    private com.nic.nammaoorusuper.databinding.ActivityPhotoCategoryListBinding photoCategoryListBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String hab_code;
    private String campaign_id;
    private String activity_id;
    private String campaign_activity_id;
    private String campaign_activity_entry_id;
    private String item_no;
    private String no_of_images;
    private String image_category_id;

    ArrayList<NOS> photoCategoryList =new ArrayList<>();
    ArrayList<NOS> imageList =new ArrayList<>();
    CategoryListAdapter categoryListAdapter;
    ImageListAdapter imageListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoCategoryListBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_category_list);
        photoCategoryListBinding.setActivity(this);
        /*ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.background));*/
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.statuscolor(PhotoCategoryList.this);

        campaign_id = getIntent().getStringExtra("campaign_id");
        campaign_activity_id = getIntent().getStringExtra("campaign_activity_id");
        activity_id = getIntent().getStringExtra("activity_id");
        hab_code = getIntent().getStringExtra("hab_code");
        campaign_activity_entry_id = getIntent().getStringExtra("campaign_activity_entry_id");
        item_no = getIntent().getStringExtra("item_no");
        no_of_images = getIntent().getStringExtra("no_of_images");

        photoCategoryListBinding.categoryRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        photoCategoryListBinding.imageRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if(Utils.isOnline()){
            getPhotoCategoryList();
        }



    }

    public void activityListItemClicked(int pos){
        activity_id = photoCategoryList.get(pos).getActivity_id();
        campaign_activity_id = photoCategoryList.get(pos).getCampaign_activity_id();
        image_category_id = photoCategoryList.get(pos).getImage_category_id();
        if(photoCategoryList.get(pos).getIs_taken().equals("N")){
            if(Utils.isOnline()){
                Intent intent = new Intent(PhotoCategoryList.this,CameraScreen.class);
                intent.putExtra("activity_id",activity_id);
                intent.putExtra("campaign_activity_id",campaign_activity_id);
                intent.putExtra("campaign_activity_entry_id",campaign_activity_entry_id);
                intent.putExtra("hab_code",hab_code);
                intent.putExtra("campaign_id",campaign_id);
                intent.putExtra("item_no", photoCategoryList.get(pos).getItem_no());
                intent.putExtra("no_of_images", no_of_images);
                intent.putExtra("image_category_id", photoCategoryList.get(pos).getImage_category_id());
                startActivity(intent);
            }
            else {
                Utils.showAlert(PhotoCategoryList.this,"No Internet");
            }
        }
        else {
            Utils.showAlert(PhotoCategoryList.this,"Completed");
        }

    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;

            if ("category".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertPhotoCategoryList().execute(jsonObject);
                }
                else {
                    photoCategoryListBinding.noDataIcon.setVisibility(View.VISIBLE);
                    photoCategoryListBinding.categoryRecycler.setVisibility(View.GONE);
                    photoCategoryListBinding.categoryRecycler.setAdapter(null);
                }
                Log.d("category", "" + responseDecryptedBlockKey);
            }
            if ("get_activity_photos".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertImageList().execute(jsonObject);
                }
                else {
                    photoCategoryListBinding.noDataIcon.setVisibility(View.GONE);
                    photoCategoryListBinding.categoryRecycler.setVisibility(View.VISIBLE);
                    photoCategoryListBinding.imageRecycler.setVisibility(View.GONE);
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
    public void getPhotoCategoryList() {
        try {
            new ApiService(this).makeJSONObjectRequest("category", Api.Method.POST, UrlGenerator.getPMAYListUrl(), photo_category_en_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject photo_category_en_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), photo_category_no_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("category", "" + authKey);
        return dataSet;
    }
    private JSONObject photo_category_no_JsonParams() {
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_SERVICE_ID, "campaign_activity_image_category_list");
            dataSet.put("hab_code",hab_code);
            dataSet.put("campaign_id", campaign_id);
            dataSet.put("activity_id", activity_id);
            dataSet.put("campaign_activity_id", campaign_activity_id);
            dataSet.put("campaign_activity_entry_id", campaign_activity_entry_id);
            dataSet.put("item_no", item_no);
            Log.d("category", "" + dataSet);
        }
        catch (Exception e){

        }

        return dataSet;
    }


    public class InsertPhotoCategoryList extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                photoCategoryList = new ArrayList<>();
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
                        activity_details_item.setImage_category_id(jsonArray.getJSONObject(i).getString("image_category_id"));
                        activity_details_item.setImage_category_name(jsonArray.getJSONObject(i).getString("image_category_name"));
                        activity_details_item.setIs_taken(jsonArray.getJSONObject(i).getString("is_taken"));

                        photoCategoryList.add(activity_details_item);
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
            if(photoCategoryList.size()>0){
                photoCategoryListBinding.noDataIcon.setVisibility(View.GONE);
                photoCategoryListBinding.categoryRecycler.setVisibility(View.VISIBLE);
                photoCategoryListBinding.imageRecycler.setVisibility(View.GONE);
                categoryListAdapter =new CategoryListAdapter(PhotoCategoryList.this, photoCategoryList,dbData);
                photoCategoryListBinding.categoryRecycler.setAdapter(categoryListAdapter);
            }
            else {
                photoCategoryListBinding.noDataIcon.setVisibility(View.VISIBLE);
                photoCategoryListBinding.categoryRecycler.setVisibility(View.GONE);
                photoCategoryListBinding.categoryRecycler.setAdapter(null);
            }
        }
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
                photoCategoryListBinding.noDataIcon.setVisibility(View.GONE);
                photoCategoryListBinding.categoryRecycler.setVisibility(View.GONE);
                photoCategoryListBinding.imageRecycler.setVisibility(View.VISIBLE);
                imageListAdapter =new ImageListAdapter(PhotoCategoryList.this, imageList,dbData);
                photoCategoryListBinding.imageRecycler.setAdapter(imageListAdapter);
            }
            else {
                photoCategoryListBinding.noDataIcon.setVisibility(View.VISIBLE);
                photoCategoryListBinding.imageRecycler.setVisibility(View.GONE);
                photoCategoryListBinding.imageRecycler.setAdapter(null);
            }
        }
    }

    private  Bitmap convertStringToBitmap(String string) {
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
    @Override
    protected void onResume() {
        super.onResume();
        getPhotoCategoryList();
    }

    @Override
    public void onBackPressed() {
        if(photoCategoryListBinding.imageRecycler.getVisibility()==View.VISIBLE){
            photoCategoryListBinding.imageRecycler.setVisibility(View.GONE);
            photoCategoryListBinding.categoryRecycler.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
        }

    }
}
