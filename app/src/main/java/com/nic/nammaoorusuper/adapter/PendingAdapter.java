package com.nic.nammaoorusuper.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

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

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder> {

    private  Activity context;
    private PrefManager prefManager;
    private List<NOS> pendingListValues;
    JSONObject dataset = new JSONObject();
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    dbData dbData;
    private LayoutInflater layoutInflater;

    public PendingAdapter(Activity context, List<NOS> pendingListValues, dbData dbData) {

        this.context = context;
        this.dbData = dbData;
        prefManager = new PrefManager(context);
        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.pendingListValues = pendingListValues;
    }

    @Override
    public PendingAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        PendingAdapterBinding pendingAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.pending_adapter, viewGroup, false);
        return new PendingAdapter.MyViewHolder(pendingAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private PendingAdapterBinding pendingAdapterBinding;

        public MyViewHolder(PendingAdapterBinding Binding) {
            super(Binding.getRoot());
            pendingAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.pendingAdapterBinding.habName.setText(pendingListValues.get(position).getHabitationName());
        holder.pendingAdapterBinding.activityName.setText(pendingListValues.get(position).getActivity_name()+" "+pendingListValues.get(position).getItem_no());

        String campaign_id = pendingListValues.get(position).getCampaign_id();
        String activity_id = pendingListValues.get(position).getActivity_id();
        String campaign_activity_id = pendingListValues.get(position).getCampaign_activity_id();
        String location_save_details_primary_id = pendingListValues.get(position).getLocation_save_details_primary_id();
        String item_no = pendingListValues.get(position).getItem_no();
        String hab_code = pendingListValues.get(position).getHabCode();
        dbData.open();
        ArrayList<NOS> image_count = new ArrayList<>(dbData.get_Particular_Save_Image_Details_List(campaign_id,activity_id,campaign_activity_id,location_save_details_primary_id,item_no,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code,"","save"));
        if(image_count.size()>0){
            holder.pendingAdapterBinding.upload.setVisibility(View.VISIBLE);
        }
        else {
            holder.pendingAdapterBinding.upload.setVisibility(View.GONE);
        }
        holder.pendingAdapterBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.pendingAdapterBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_and_delete_alert(position,"delete");
            }
        });
        holder.pendingAdapterBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_and_delete_alert(position,"save");
            }
        });



    }


    private void deletePending(int position) {
        String campaign_id = pendingListValues.get(position).getCampaign_id();
        String activity_id = pendingListValues.get(position).getActivity_id();
        String campaign_activity_id = pendingListValues.get(position).getCampaign_activity_id();
        String location_save_details_primary_id = pendingListValues.get(position).getLocation_save_details_primary_id();
        String dcode = pendingListValues.get(position).getDistictCode();
        String bcode = pendingListValues.get(position).getBlockCode();
        String pvcode = pendingListValues.get(position).getPvCode();
        String hab_code = pendingListValues.get(position).getHabCode();
        String item_no = pendingListValues.get(position).getItem_no();
        String whereClass = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? ";
        String[] whereargs = new String[]{campaign_id,activity_id,campaign_activity_id,dcode,bcode,pvcode,hab_code,item_no};

        int sdsm = db.delete(DBHelper.LOCATION_SAVE_DETAILS_TABLE, whereClass, whereargs);

        dbData.open();
        ArrayList<NOS> image_count = new ArrayList<>(dbData.get_Particular_Save_Image_Details_List(campaign_id,activity_id,campaign_activity_id,location_save_details_primary_id,item_no,dcode,bcode,pvcode,hab_code,"","delete"));
        if(image_count.size()>0){
             whereClass = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and location_save_details_primary_id = ? and  dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ?";
            whereargs = new String[]{campaign_id,activity_id,campaign_activity_id,location_save_details_primary_id,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code,item_no};
            db.delete(DBHelper.SAVE_IMAGE_DETAILS_TABLE,whereClass,whereargs);
            for(int i=0;i<image_count.size();i++){
                String file_path = image_count.get(i).getImage_path();
                deleteFileDirectory(file_path);

            }
        }
        pendingListValues.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, pendingListValues.size());
        Log.d("sdsm", String.valueOf(sdsm));
    }



    private void uploadPending(int position) {
        String json_values = pendingListValues.get(position).getJson_value();

        try {
            dataset = new JSONObject(json_values);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Utils.isOnline()) {
            ((PendingScreen)context).saveLocationJsonParams(dataset,pendingListValues.get(position).getLocation_save_details_primary_id());
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
    public void save_and_delete_alert(int position,String save_delete){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            if(save_delete.equals("save")) {
                text.setText(context.getResources().getString(R.string.do_u_want_to_upload));
            }
            else if(save_delete.equals("delete")){
                text.setText(context.getResources().getString(R.string.do_u_want_to_delete));
            }

            Button yesButton = (Button) dialog.findViewById(R.id.btn_ok);
            Button noButton = (Button) dialog.findViewById(R.id.btn_cancel);
            noButton.setVisibility(View.VISIBLE);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(save_delete.equals("save")) {
                        uploadPending(position);
                        dialog.dismiss();
                    }
                    else if(save_delete.equals("delete")) {
                        deletePending(position);
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
