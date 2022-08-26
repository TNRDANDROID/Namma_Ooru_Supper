package com.nic.nammaoorusuper.adapter;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.PendingScreen;
import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.databinding.PendingAdapterBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageUploadAdapter extends RecyclerView.Adapter<ImageUploadAdapter.MyViewHolder> {

    private Activity context;
    private PrefManager prefManager;
    private List<NOS> pendingListValues;
    JSONObject dataset = new JSONObject();
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    dbData dbData;
    private LayoutInflater layoutInflater;

    public ImageUploadAdapter(Activity context, List<NOS> pendingListValues,dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.dbData =dbData;
        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.pendingListValues = pendingListValues;
    }

    @Override
    public ImageUploadAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        PendingAdapterBinding pendingAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.pending_adapter, viewGroup, false);
        return new MyViewHolder(pendingAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private PendingAdapterBinding pendingAdapterBinding;

        public MyViewHolder(PendingAdapterBinding Binding) {
            super(Binding.getRoot());
            pendingAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ImageUploadAdapter.MyViewHolder holder, final int position) {
        holder.pendingAdapterBinding.habName.setText(pendingListValues.get(position).getHabitationName());
        holder.pendingAdapterBinding.activityName.setText(pendingListValues.get(position).getActivity_name());
        holder.pendingAdapterBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.pendingAdapterBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePending(position);
            }
        });
        holder.pendingAdapterBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPending(position);
            }
        });



    }


    private void deletePending(int position) {
        String campaign_id = pendingListValues.get(position).getCampaign_id();
        String activity_id = pendingListValues.get(position).getActivity_id();
        String campaign_activity_id = pendingListValues.get(position).getCampaign_activity_id();
        String location_save_details_primary_idString = pendingListValues.get(position).getCampaign_activity_entry_id();
        String dcode = pendingListValues.get(position).getDistictCode();
        String bcode = pendingListValues.get(position).getBlockCode();
        String pvcode = pendingListValues.get(position).getPvCode();
        String hab_code = pendingListValues.get(position).getHabCode();
        String item_no = pendingListValues.get(position).getItem_no();
        String image_category_id = pendingListValues.get(position).getImage_category_id();
        dbData.open();
        ArrayList<NOS> image_count = new ArrayList<>(dbData.get_Particular_Save_Image_Details_List(campaign_id,activity_id,campaign_activity_id,location_save_details_primary_idString,item_no,dcode,bcode,pvcode,hab_code,image_category_id,""));
        if(image_count.size()>0){
            String whereClass = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and location_save_details_primary_idString = ? and  dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? and image_category_id = ?";
            String[] whereargs = new String[]{campaign_id,activity_id,campaign_activity_id,location_save_details_primary_idString,item_no,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code,image_category_id};
            db.delete(DBHelper.SAVE_IMAGE_DETAILS_TABLE,whereClass,whereargs);
            for(int i=0;i<image_count.size();i++){
                String file_path = image_count.get(i).getImage_path();
                deleteFileDirectory(file_path);

            }
        }
        pendingListValues.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, pendingListValues.size());

    }



    private void uploadPending(int position) {
        JSONArray jsonArray = new JSONArray();
        String campaign_id = pendingListValues.get(position).getCampaign_id();
        String activity_id = pendingListValues.get(position).getActivity_id();
        String campaign_activity_id = pendingListValues.get(position).getCampaign_activity_id();
        String location_save_details_primary_idString = pendingListValues.get(position).getLocation_save_details_primary_id();
        String dcode = pendingListValues.get(position).getDistictCode();
        String bcode = pendingListValues.get(position).getBlockCode();
        String pvcode = pendingListValues.get(position).getPvCode();
        String hab_code = pendingListValues.get(position).getHabCode();
        String item_no = pendingListValues.get(position).getItem_no();
        String image_category_id = pendingListValues.get(position).getImage_category_id();
        dbData.open();
        ArrayList<NOS> image_count = new ArrayList<>(dbData.get_Particular_Save_Image_Details_List(campaign_id,activity_id,campaign_activity_id,location_save_details_primary_idString,item_no,dcode,bcode,pvcode,hab_code,image_category_id,""));
        if(image_count.size()>0){
            String whereClass = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and location_save_details_primary_idString = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? and image_category_id = ?";
            String[] whereargs = new String[]{campaign_id,activity_id,campaign_activity_id,location_save_details_primary_idString,item_no,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code,image_category_id};

            for(int i=0;i<image_count.size();i++){
                JSONObject image_json = new JSONObject();
                try {
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
            dataset.put("hab_code",hab_code);
            dataset.put("campaign_id", campaign_id);
            dataset.put("activity_id", activity_id);
            dataset.put("campaign_activity_id", campaign_activity_id);
            dataset.put("campaign_activity_entry_id", location_save_details_primary_idString);
            dataset.put("item_no", item_no);
            dataset.put("image_category_id", image_category_id);
            dataset.put("image_details", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Utils.isOnline()) {
            ((PendingScreen)context).saveImageJsonParams(dataset,position);
        } else {
            Utils.showAlert(context, "Turn On Mobile Data To Upload");
        }

    }

    @Override
    public int getItemCount() {
        return pendingListValues.size();
    }
    private String BitMapToString(Bitmap bitmap){
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
