package com.nic.nammaoorusuper.OfflineActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.ActivityList;
import com.nic.nammaoorusuper.activity.HomePage;
import com.nic.nammaoorusuper.activity.SubActivityList;
import com.nic.nammaoorusuper.adapter.ActivityListAdapter;
import com.nic.nammaoorusuper.adapter.CampaignListAdapter;
import com.nic.nammaoorusuper.adapter.CommonAdapter;
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
import java.util.List;

public class ActivityListOffLine extends AppCompatActivity {
    private com.nic.nammaoorusuper.databinding.ActivityListOffLineBinding activityListBinding;
    private PrefManager prefManager;
    public com.nic.nammaoorusuper.dataBase.dbData dbData = new dbData(this);
    public DBHelper dbHelper;
    public SQLiteDatabase db;

    ArrayList<NOS> activityList=new ArrayList<>();
    ActivityListAdapter activityListAdapter;
    private String campaign_id;
    private String hab_code;
    private String hab_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityListBinding = DataBindingUtil.setContentView(this, R.layout.activity_list_off_line);
        activityListBinding.setActivity(this);
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
        Utils.statuscolor(ActivityListOffLine.this);
        campaign_id = getIntent().getStringExtra("campaign_id");
        hab_code = getIntent().getStringExtra("hab_code");
        hab_name = getIntent().getStringExtra("hab_name");
        activityListBinding.activityRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        new fetch_Activity_list().execute();
    }

    @SuppressLint("StaticFieldLeak")
    public class fetch_Activity_list extends AsyncTask<Void, Void,ArrayList<NOS>> {
        @Override
        protected ArrayList<NOS> doInBackground(Void... params) {
            dbData.open();
            activityList = new ArrayList<>();
            activityList = dbData.get_Particular_Campaign_Activity_List(campaign_id,hab_code);
            Log.d("activity_list_count", String.valueOf(activityList.size()));
            return activityList;
        }

        @Override
        protected void onPostExecute(ArrayList<NOS> activity_list) {
            super.onPostExecute(activity_list);
            if(!Utils.isOnline()) {
                if (activity_list.size() == 0) {
                    Utils.showAlert(ActivityListOffLine.this, "No Data Available in Local Database. Please, Turn On mobile data");
                }
            }
            if(activityList.size()>0){
                activityListBinding.noDataIcon.setVisibility(View.GONE);
                activityListBinding.activityRecycler.setVisibility(View.VISIBLE);
                activityListAdapter =new ActivityListAdapter(ActivityListOffLine.this,activityList,dbData,"Offline");
                activityListBinding.activityRecycler.setAdapter(activityListAdapter);
            }
            else {
                activityListBinding.noDataIcon.setVisibility(View.VISIBLE);
                activityListBinding.activityRecycler.setVisibility(View.GONE);
                activityListBinding.activityRecycler.setAdapter(null);
            }
        }
    }

    public void activityListItemClicked(int pos){

            Intent intent = new Intent(ActivityListOffLine.this, SubActivityListOffline.class);
            intent.putExtra("activity_id",activityList.get(pos).getActivity_id());
            intent.putExtra("activity_name",activityList.get(pos).getActivity_name());
            intent.putExtra("campaign_activity_id",activityList.get(pos).getCampaign_activity_id());
            intent.putExtra("hab_code",activityList.get(pos).getHabCode());
            intent.putExtra("hab_name",hab_name);
            intent.putExtra("campaign_id",activityList.get(pos).getCampaign_id());
            intent.putExtra("no_of_images",activityList.get(pos).getNo_of_images());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_out,R.anim.slide_in);

    }



}
