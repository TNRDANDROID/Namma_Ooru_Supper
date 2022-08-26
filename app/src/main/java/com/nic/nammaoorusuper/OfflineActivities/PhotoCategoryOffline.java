package com.nic.nammaoorusuper.OfflineActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.CameraScreen;
import com.nic.nammaoorusuper.activity.PhotoCategoryList;
import com.nic.nammaoorusuper.adapter.ActivitySubListAdapter;
import com.nic.nammaoorusuper.adapter.CategoryListAdapter;
import com.nic.nammaoorusuper.adapter.ImageListAdapter;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.Utils;

import java.util.ArrayList;

public class PhotoCategoryOffline extends AppCompatActivity{
    private com.nic.nammaoorusuper.databinding.ActivityPhotoCategoryOfflineBinding photoCategoryOfflineBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    private String hab_code;
    private String hab_name;
    private String activity_name;
    private String campaign_id;
    private String activity_id;
    private String campaign_activity_id;
    private String campaign_activity_entry_id;
    private String location_save_details_primary_id;
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
        photoCategoryOfflineBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_category_offline);
        photoCategoryOfflineBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeUI();
    }

    private void initializeUI() {
        Utils.statuscolor(PhotoCategoryOffline.this);

        campaign_id = getIntent().getStringExtra("campaign_id");
        campaign_activity_id = getIntent().getStringExtra("campaign_activity_id");
        activity_id = getIntent().getStringExtra("activity_id");
        hab_code = getIntent().getStringExtra("hab_code");
        hab_name = getIntent().getStringExtra("hab_name");
        activity_name = getIntent().getStringExtra("activity_name");
        location_save_details_primary_id = getIntent().getStringExtra("location_save_details_primary_id");
        //campaign_activity_entry_id = getIntent().getStringExtra("campaign_activity_entry_id");
        item_no = getIntent().getStringExtra("item_no");
        no_of_images = getIntent().getStringExtra("no_of_images");

        photoCategoryOfflineBinding.categoryRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        new fetch_category_list().execute();


    }

    @SuppressLint("StaticFieldLeak")
    public class fetch_category_list extends AsyncTask<Void, Void,ArrayList<NOS>> {
        @Override
        protected ArrayList<NOS> doInBackground(Void... params) {
            dbData.open();
            photoCategoryList = new ArrayList<>();
            //photoCategoryList = dbData.get_Particular_Category_Get_DetailsList(campaign_id,activity_id,campaign_activity_id,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code,item_no);
            photoCategoryList = dbData.getAll_Photo_Category();
            Log.d("sub_list_count", String.valueOf(photoCategoryList.size()));
            return photoCategoryList;
        }

        @Override
        protected void onPostExecute(ArrayList<NOS> photoCategoryList) {
            super.onPostExecute(photoCategoryList);
            if(!Utils.isOnline()) {
                if (photoCategoryList.size() == 0) {
                    Utils.showAlert(PhotoCategoryOffline.this, "No Data Available in Local Database. Please, Turn On mobile data");
                }
            }
            if(photoCategoryList.size()>0){
                photoCategoryOfflineBinding.noDataIcon.setVisibility(View.GONE);
                photoCategoryOfflineBinding.categoryRecycler.setVisibility(View.VISIBLE);
                categoryListAdapter =new CategoryListAdapter(PhotoCategoryOffline.this, photoCategoryList,dbData,"Offline",
                        campaign_id,activity_id,campaign_activity_id,location_save_details_primary_id,item_no,hab_code);
                photoCategoryOfflineBinding.categoryRecycler.setAdapter(categoryListAdapter);
            }
            else {
                photoCategoryOfflineBinding.noDataIcon.setVisibility(View.VISIBLE);
                photoCategoryOfflineBinding.categoryRecycler.setVisibility(View.GONE);
                photoCategoryOfflineBinding.categoryRecycler.setAdapter(null);
            }

        }
    }

    public void activityListItemClicked(int pos){
        //activity_id = photoCategoryList.get(pos).getActivity_id();
        //campaign_activity_id = photoCategoryList.get(pos).getCampaign_activity_id();
        //campaign_activity_entry_id = photoCategoryList.get(pos).getCampaign_activity_entry_id();
        image_category_id = photoCategoryList.get(pos).getImage_category_id();
        //if(photoCategoryList.get(pos).getIs_taken().equals("N")){
                Intent intent = new Intent(PhotoCategoryOffline.this, CameraScreen.class);
                intent.putExtra("activity_id",activity_id);
                intent.putExtra("campaign_activity_id",campaign_activity_id);
                intent.putExtra("campaign_activity_entry_id","");
                intent.putExtra("location_save_details_primary_id",location_save_details_primary_id);
                intent.putExtra("hab_code",hab_code);
                intent.putExtra("hab_name",hab_name);
                intent.putExtra("activity_name",activity_name);
                intent.putExtra("campaign_id",campaign_id);
                intent.putExtra("item_no",item_no);
                intent.putExtra("no_of_images", no_of_images);
                intent.putExtra("image_category_id", photoCategoryList.get(pos).getImage_category_id());
                intent.putExtra("On_Off_Type", "Offline");
                startActivity(intent);
        /*}
        else {
            Utils.showAlert(PhotoCategoryOffline.this,"Completed");
        }*/

    }




    @Override
    protected void onResume() {
        super.onResume();
        new fetch_category_list().execute();
    }


}
