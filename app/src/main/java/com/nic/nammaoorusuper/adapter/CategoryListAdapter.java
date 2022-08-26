package com.nic.nammaoorusuper.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.nammaoorusuper.OfflineActivities.PhotoCategoryOffline;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.PhotoCategoryList;
import com.nic.nammaoorusuper.activity.SubActivityList;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.databinding.CategoryItemViewBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {

    private Activity context;
    private PrefManager prefManager;
    private List<NOS> categoryList;
    com.nic.nammaoorusuper.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;
    int pos=-1;
    private String On_Off_Type;
    private String campaign_id;
    private String activity_id;
    private String campaign_activity_id;
    private String location_save_details_primary_id;
    private String item_no;
    private String hab_code;


    public CategoryListAdapter(Activity context,List<NOS> categoryList, com.nic.nammaoorusuper.dataBase.dbData dbData,
                               String On_Off_Type,String campaign_id,String activity_id,String campaign_activity_id,
                               String location_save_details_primary_id,String item_no,String hab_code) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
        this.On_Off_Type=On_Off_Type;
        this.campaign_id=campaign_id;
        this.activity_id=activity_id;
        this.campaign_activity_id=campaign_activity_id;
        this.location_save_details_primary_id=location_save_details_primary_id;
        this.item_no=item_no;
        this.hab_code=hab_code;
        this.categoryList = categoryList;
    }

    @Override
    public CategoryListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        CategoryItemViewBinding categoryItemViewBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.category_item_view, viewGroup, false);
        return new MyViewHolder(categoryItemViewBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CategoryItemViewBinding categoryItemViewBinding;

        public MyViewHolder(CategoryItemViewBinding Binding) {
            super(Binding.getRoot());
            categoryItemViewBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryListAdapter.MyViewHolder holder, final int position) {

        holder.categoryItemViewBinding.categoryTitle.setText(categoryList.get(position).getImage_category_name());

        if(On_Off_Type.equals("")){
            if(categoryList.get(position).getIs_taken().equals("Y")){
                holder.categoryItemViewBinding.completedLayout.setVisibility(View.VISIBLE);
            }
            else {
                holder.categoryItemViewBinding.completedLayout.setVisibility(View.GONE);
            }
        }
        else {
            dbData.open();
            ArrayList<NOS> image_count = new ArrayList<>(dbData.get_Particular_Save_Image_Details_List(campaign_id,activity_id,campaign_activity_id,location_save_details_primary_id,item_no,prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),hab_code,categoryList.get(position).getImage_category_id(),""));
            if(image_count.size()>0){
                holder.categoryItemViewBinding.completedLayout.setVisibility(View.VISIBLE);
            }
            else {
                holder.categoryItemViewBinding.completedLayout.setVisibility(View.GONE);
            }

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if(categoryList.get(position).getIs_taken().equals("N")){
                    ((PhotoCategoryList)context).activityListItemClicked(position);
                }
                else {
                    Utils.showAlert(context,"Completed");
                }*/
              if(On_Off_Type.equals("")){
                  ((PhotoCategoryList)context).activityListItemClicked(position);
              }
              else {
                  if(holder.categoryItemViewBinding.completedLayout.getVisibility()==View.VISIBLE){
                      Utils.showAlert(context,"Completed");
                  }
                  else {
                      ((PhotoCategoryOffline)context).activityListItemClicked(position);
                  }

              }



            }
        });
    }




    @Override
    public int getItemCount() {
        return categoryList.size();
    }




}

