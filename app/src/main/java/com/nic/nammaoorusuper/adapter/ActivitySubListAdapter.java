package com.nic.nammaoorusuper.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.nammaoorusuper.OfflineActivities.SubActivityListOffline;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.ActivityList;
import com.nic.nammaoorusuper.activity.SubActivityList;
import com.nic.nammaoorusuper.databinding.ActivityRecyclerItemBinding;
import com.nic.nammaoorusuper.databinding.ActivitySubItemViewBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;

import java.util.List;

public class ActivitySubListAdapter extends RecyclerView.Adapter<ActivitySubListAdapter.MyViewHolder> {

    private Activity context;
    private PrefManager prefManager;
    private List<NOS> activitySubList;
    com.nic.nammaoorusuper.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;
    private String On_Off_Type;

    public ActivitySubListAdapter(Activity context,List<NOS> activitySubList, com.nic.nammaoorusuper.dataBase.dbData dbData,String On_Off_Type) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
        this.On_Off_Type=On_Off_Type;
        this.activitySubList = activitySubList;
    }

    @Override
    public ActivitySubListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        ActivitySubItemViewBinding activitySubItemViewBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.activity_sub_item_view, viewGroup, false);
        return new MyViewHolder(activitySubItemViewBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ActivitySubItemViewBinding activitySubItemViewBinding;

        public MyViewHolder(ActivitySubItemViewBinding Binding) {
            super(Binding.getRoot());
            activitySubItemViewBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ActivitySubListAdapter.MyViewHolder holder, final int position) {

      holder.activitySubItemViewBinding.name.setText(activitySubList.get(position).getActivity_sub_list());
      holder.activitySubItemViewBinding.no.setText(activitySubList.get(position).getItem_no());

      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(On_Off_Type.equals("")){
                  ((SubActivityList)context).activityListItemClicked(position);
              }
              else {
                  ((SubActivityListOffline)context).activityListItemClicked(position);
              }

          }
      });
    }




    @Override
    public int getItemCount() {
        return activitySubList.size();
    }




}

