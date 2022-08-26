package com.nic.nammaoorusuper.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.adapter.ImageUploadAdapter;
import com.nic.nammaoorusuper.adapter.PendingAdapter;
import com.nic.nammaoorusuper.api.Api;
import com.nic.nammaoorusuper.api.ApiService;
import com.nic.nammaoorusuper.api.ServerResponse;
import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.databinding.PendingScreenBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.UrlGenerator;
import com.nic.nammaoorusuper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PendingScreen extends AppCompatActivity implements Api.ServerResponseListener {
    public PendingScreenBinding pendingScreenBinding;
    private RecyclerView recyclerView;
    private PendingAdapter pendingAdapter;
    private ImageUploadAdapter imageUploadAdapter;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private Activity context;
    private int pos;
    ArrayList<NOS> imageList;
    private String location_save_details_primary_id;


    String dcode;
    String bcode;
    String pvcode;
    String hab_code;
    String activity_id;
    String campaign_id;
    String item_no;
    String campaign_activity_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pendingScreenBinding = DataBindingUtil.setContentView(this, R.layout.pending_screen);
        pendingScreenBinding.setActivity(this);
        context = this;
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
            Utils.statuscolor(PendingScreen.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView = pendingScreenBinding.pendingList;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pendingScreenBinding.imageUploadRecycler.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setVisibility(View.GONE);
        pendingScreenBinding.imageUploadRecycler.setVisibility(View.GONE);
        pendingScreenBinding.notFoundTv.setVisibility(View.GONE);

        pendingScreenBinding.locationRl.setBackgroundDrawable(getResources().getDrawable(R.drawable.left_side_curve_bg));
        pendingScreenBinding.imageRl.setBackgroundDrawable(null);
        new fetchPendingTask().execute();

        pendingScreenBinding.locationRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pendingScreenBinding.locationRl.setBackgroundDrawable(getResources().getDrawable(R.drawable.left_side_curve_bg));
                pendingScreenBinding.imageRl.setBackgroundDrawable(null);
                new fetchPendingTask().execute();
            }
        });
        pendingScreenBinding.imageRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pendingScreenBinding.imageRl.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_side_curve_bg));
                pendingScreenBinding.locationRl.setBackgroundDrawable(null);
                new fetchPendingImageTask().execute();
            }
        });

    }



    public class fetchPendingTask extends AsyncTask<Void, Void,ArrayList<NOS>> {
        @Override
        protected ArrayList<NOS> doInBackground(Void... params) {
            imageList = new ArrayList<>();
            dbData.open();
            imageList = dbData.get_Particular_Location_Save_List("","","","","","","","","All");
            Log.d("COUNT", String.valueOf(imageList.size()));
            return imageList;
        }

        @Override
        protected void onPostExecute(ArrayList<NOS> list) {
            super.onPostExecute(list);
            if(list.size()>0){
                recyclerView.setVisibility(View.VISIBLE);
                pendingScreenBinding.imageUploadRecycler.setVisibility(View.GONE);
                pendingScreenBinding.notFoundTv.setVisibility(View.GONE);
                pendingAdapter = new PendingAdapter(PendingScreen.this, list,dbData);
                recyclerView.setAdapter(pendingAdapter);
            }
            else {
                recyclerView.setVisibility(View.GONE);
                pendingScreenBinding.imageUploadRecycler.setVisibility(View.GONE);
                pendingScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(null);
            }
        }
    }

    public class fetchPendingImageTask extends AsyncTask<Void, Void,ArrayList<NOS>> {
        @Override
        protected ArrayList<NOS> doInBackground(Void... params) {
            dbData.open();
            ArrayList<NOS> NOS = new ArrayList<>();
            NOS = dbData.get_Particular_Save_Image_Details_List("","","","","","","","","","","All");
            Log.d("COUNT", String.valueOf(NOS.size()));
            return NOS;
        }

        @Override
        protected void onPostExecute(ArrayList<NOS> list) {
            super.onPostExecute(list);
            if(list.size()>0){
                recyclerView.setVisibility(View.GONE);
                pendingScreenBinding.imageUploadRecycler.setVisibility(View.VISIBLE);
                pendingScreenBinding.notFoundTv.setVisibility(View.GONE);
                imageUploadAdapter = new ImageUploadAdapter(PendingScreen.this, list,dbData);
                pendingScreenBinding.imageUploadRecycler.setAdapter(imageUploadAdapter);
            }
            else {
                recyclerView.setVisibility(View.GONE);
                pendingScreenBinding.imageUploadRecycler.setVisibility(View.GONE);
                pendingScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
                pendingScreenBinding.imageUploadRecycler.setAdapter(null);
            }
        }
    }



    public JSONObject saveLocationJsonParams(JSONObject savePMAYDataSet,String location_save_details_primary_id_) {
        location_save_details_primary_id = location_save_details_primary_id_;
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), savePMAYDataSet.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);

            new ApiService(this).makeJSONObjectRequest("saveLocation", Api.Method.POST, UrlGenerator.getPMAYListUrl(), dataSet, "not cache", this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("saveLocation", "" + authKey);
        return dataSet;
    }
    public JSONObject saveImageJsonParams(JSONObject savePMAYDataSet,int position) {
        pos=position;
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), savePMAYDataSet.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);

            new ApiService(this).makeJSONObjectRequest("saveImage", Api.Method.POST, UrlGenerator.getPMAYListUrl(), dataSet, "not cache", this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("saveImage", "" + authKey);
        return dataSet;
    }
    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("saveLocation".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Toasty.success(PendingScreen.this,jsonObject.getString("MESSAGE"),Toasty.LENGTH_SHORT,true).show();
                    dcode= prefManager.getDistrictCode();
                    bcode= prefManager.getBlockCode();
                    pvcode=prefManager.getPvCode();
                     hab_code= jsonObject.getString("hab_code");
                     activity_id= jsonObject.getString("activity_id");
                    campaign_id= jsonObject.getString("campaign_id");
                     item_no= jsonObject.getString("item_no");
                     campaign_activity_id= jsonObject.getString("campaign_activity_id");
                    String campaign_activity_entry_id= jsonObject.getString("campaign_activity_entry_id");

                    uploadPendingImage(campaign_id,activity_id,campaign_activity_id,location_save_details_primary_id,dcode,bcode,pvcode,hab_code,item_no,campaign_activity_entry_id);
/*
                    String whereClass = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? ";
                    String[] whereargs = new String[]{campaign_id,activity_id,campaign_activity_id,dcode,bcode,pvcode,hab_code,item_no};

                    try {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("campaign_id",campaign_id);
                        contentValues.put("activity_id",activity_id);
                        contentValues.put("campaign_activity_id",campaign_activity_id);
                        contentValues.put("campaign_activity_entry_id",campaign_activity_entry_id);
                        contentValues.put("dcode",dcode);
                        contentValues.put("bcode",bcode);
                        contentValues.put("pvcode",pvcode);
                        contentValues.put("hab_code",hab_code);
                        contentValues.put("item_no",item_no);
                        dbData.open();
                        ArrayList<NOS> get_category_count= new ArrayList<>(dbData.get_Particular_Category_Get_List(campaign_id,activity_id,campaign_activity_id,dcode,bcode,pvcode,hab_code,item_no,""));
                        if(get_category_count.size()>0){
                            long row_update_id = db.update(DBHelper.GET_CATEGORY_TABLE,contentValues,whereClass,whereargs);
                            Log.d("get_category_update>>",""+row_update_id);
                        }
                        else {
                            long insert_id = db.insert(DBHelper.GET_CATEGORY_TABLE,null,contentValues);
                            Log.d("get_category_insert>",""+insert_id);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    int sdsm = db.delete(DBHelper.LOCATION_SAVE_DETAILS_TABLE, whereClass, whereargs);*/


                }
                else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")){
                    Toasty.error(this, jsonObject.getString("MESSAGE"), Toast.LENGTH_LONG, true).show();
                }
                Log.d("saved_response", "" + responseDecryptedBlockKey);
            }
            else if ("saveImage".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Toasty.success(PendingScreen.this,jsonObject.getString("MESSAGE"),Toasty.LENGTH_SHORT,true).show();

                    String whereClass = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? ";
                    String[] whereargs = new String[]{campaign_id,activity_id,campaign_activity_id,dcode,bcode,pvcode,hab_code,item_no};

                    int sdsm = db.delete(DBHelper.LOCATION_SAVE_DETAILS_TABLE, whereClass, whereargs);
                    int sdsm1 = db.delete(DBHelper.SAVE_IMAGE_DETAILS_TABLE, whereClass, whereargs);
                    new fetchPendingTask().execute();
                    pendingAdapter.notifyDataSetChanged();

                }
                else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")){
                    Toasty.error(this, jsonObject.getString("MESSAGE"), Toast.LENGTH_LONG, true).show();
                }
                Log.d("saved_response", "" + responseDecryptedBlockKey);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void homePage() {
        Intent intent = new Intent(this, MainPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    private void uploadPendingImage(String campaign_id_,String activity_id_,String campaign_activity_id_,String location_save_details_primary_id_,String dcode_,String bcode_,String pvcode_,String hab_code_,String item_no_,String campaign_activity_entry_id) {
        JSONArray jsonArray = new JSONArray();
        dbData.open();
        JSONObject dataset = new JSONObject();
        ArrayList<NOS> image_count = new ArrayList<>(dbData.get_Particular_Save_Image_Details_List(campaign_id_,activity_id_,campaign_activity_id_,location_save_details_primary_id_,item_no_,dcode_,bcode_,pvcode_,hab_code_,"","save"));
        if(image_count.size()>0){
            String whereClass = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and location_save_details_primary_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? ";
            String[] whereargs = new String[]{campaign_id_,activity_id_,campaign_activity_id_,location_save_details_primary_id_,dcode_,bcode_,pvcode_,hab_code_,item_no_};

            for(int i=0;i<image_count.size();i++){
                JSONObject image_json = new JSONObject();
                try {
                    image_json.put("image_category_id",image_count.get(i).getImage_category_id());
                    image_json.put("image_serial_no",image_count.get(i).getImage_serial_no());
                    image_json.put("image_lat",image_count.get(i).getImage_lat());
                    image_json.put("image_long",image_count.get(i).getImage_long());
                    image_json.put("image_description",image_count.get(i).getImage_description());
                    image_json.put("image",imageString(image_count.get(i).getImage_path()));
                    jsonArray.put(image_json);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }

        try {

            dataset = new JSONObject();
            dataset.put(AppConstant.KEY_SERVICE_ID, "campaign_activity_photos_save");
            dataset.put("hab_code",hab_code_);
            dataset.put("campaign_id", campaign_id_);
            dataset.put("activity_id", activity_id_);
            dataset.put("campaign_activity_id", campaign_activity_id_);
            dataset.put("campaign_activity_entry_id", campaign_activity_entry_id);
            dataset.put("item_no", item_no_);
            dataset.put("image_details", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Utils.isOnline()) {
            saveImageJsonParams(dataset,0);
        } else {
            Utils.showAlert(context, "Turn On Mobile Data To Upload");
        }

    }
    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        String temp1 = BitMapCompress(bitmap);
        //String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp1;
    }
    private String BitMapCompress(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    private String imageString(String file_path){
        String image_string = "";
        File imgFile = new File(file_path);

        if(imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            image_string = BitMapToString(myBitmap);
            deleteFileDirectory(file_path);

        }
        return image_string;
    }
    private void deleteFileDirectory(String file_path){
        File file = new File(file_path);
        // call deleteDirectory method to delete directory
        // recursively
        file.delete();

    }
}
