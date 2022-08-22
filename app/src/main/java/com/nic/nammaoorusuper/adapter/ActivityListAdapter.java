package com.nic.nammaoorusuper.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.ActivityList;
import com.nic.nammaoorusuper.databinding.ActivityRecyclerItemBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.MyViewHolder> {

    private Activity context;
    private PrefManager prefManager;
    private List<NOS> activityList;

    com.nic.nammaoorusuper.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;


    public ActivityListAdapter(Activity context, List<NOS> activityList,com.nic.nammaoorusuper.dataBase.dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
        this.activityList = activityList;

    }

    @Override
    public ActivityListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        ActivityRecyclerItemBinding activityRecyclerItemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.activity_recycler_item, viewGroup, false);
        return new MyViewHolder(activityRecyclerItemBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ActivityRecyclerItemBinding activityRecyclerItemBinding;

        public MyViewHolder(ActivityRecyclerItemBinding Binding) {
            super(Binding.getRoot());
            activityRecyclerItemBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ActivityListAdapter.MyViewHolder holder, final int position) {
        holder.activityRecyclerItemBinding.activityCount.setText(activityList.get(position).getNo_of_items());
        holder.activityRecyclerItemBinding.activityName.setText(activityList.get(position).getActivity_name());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityList)context).activityListItemClicked(position);
            }
        });


    }




    @Override
    public int getItemCount() {
        return activityList.size();
    }




}

