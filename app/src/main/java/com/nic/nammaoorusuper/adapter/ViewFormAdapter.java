package com.nic.nammaoorusuper.adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.databinding.DynamicRecyclerItemViewBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.Utils;

import java.util.List;

public class ViewFormAdapter extends RecyclerView.Adapter<ViewFormAdapter.MyViewHolder> {

    private Activity context;
    private PrefManager prefManager;
    private List<NOS> widgetList;
    com.nic.nammaoorusuper.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;


    public ViewFormAdapter(Activity context, List<NOS> widgetList, com.nic.nammaoorusuper.dataBase.dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
        this.widgetList = widgetList;
    }

    @Override
    public ViewFormAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        DynamicRecyclerItemViewBinding dynamicRecyclerItemViewBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.dynamic_recycler_item_view, viewGroup, false);
        return new MyViewHolder(dynamicRecyclerItemViewBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private DynamicRecyclerItemViewBinding dynamicRecyclerItemViewBinding;

        public MyViewHolder(DynamicRecyclerItemViewBinding Binding) {
            super(Binding.getRoot());
            dynamicRecyclerItemViewBinding = Binding;

        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewFormAdapter.MyViewHolder holder, final int position) {
        holder.dynamicRecyclerItemViewBinding.descriptionEt.setTextColor(context.getResources().getColor(R.color.tool_bar_blue));
        holder.dynamicRecyclerItemViewBinding.enterDescription.setText(widgetList.get(position).getCampaign_data_label());
        holder.dynamicRecyclerItemViewBinding.descriptionEt.setFocusable(false);
        holder.dynamicRecyclerItemViewBinding.descriptionEt.setText(Utils.NotNullString(widgetList.get(position).getLabel_value()));



    }




    @Override
    public int getItemCount() {
        return widgetList.size();
    }


}

