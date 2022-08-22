package com.nic.nammaoorusuper.adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
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

public class DynamicRecyclerWidgetAdapter extends RecyclerView.Adapter<DynamicRecyclerWidgetAdapter.MyViewHolder> {

    private Activity context;
    private PrefManager prefManager;
    private List<NOS> widgetList;
    com.nic.nammaoorusuper.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;
    int pos=-1;
    private String[] mDataset;

    public DynamicRecyclerWidgetAdapter(Activity context, List<NOS> widgetList, com.nic.nammaoorusuper.dataBase.dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
        this.widgetList = widgetList;
    }

    @Override
    public DynamicRecyclerWidgetAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        DynamicRecyclerItemViewBinding dynamicRecyclerItemViewBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.dynamic_recycler_item_view, viewGroup, false);
        return new MyViewHolder(dynamicRecyclerItemViewBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private DynamicRecyclerItemViewBinding dynamicRecyclerItemViewBinding;
        //public MyCustomEditTextListener myCustomEditTextListener;

        public MyViewHolder(DynamicRecyclerItemViewBinding Binding) {
            super(Binding.getRoot());
            dynamicRecyclerItemViewBinding = Binding;
            /*this.myCustomEditTextListener=myCustomEditTextListener;
            this.dynamicRecyclerItemViewBinding.descriptionEt.addTextChangedListener(myCustomEditTextListener);*/
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final DynamicRecyclerWidgetAdapter.MyViewHolder holder, final int position) {
        holder.dynamicRecyclerItemViewBinding.enterDescription.setText(widgetList.get(position).getCampaign_data_label());
        holder.dynamicRecyclerItemViewBinding.descriptionEt.setHint(widgetList.get(position).getCampaign_data_label());

        if(widgetList.get(position).getCampaign_data_type().equals("integer")){
            holder.dynamicRecyclerItemViewBinding.descriptionEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        else if(widgetList.get(position).getCampaign_data_type().equals("text")){
            holder.dynamicRecyclerItemViewBinding.descriptionEt.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        else {
            holder.dynamicRecyclerItemViewBinding.descriptionEt.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        /*holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.dynamicRecyclerItemViewBinding.descriptionEt.setText(mDataset[holder.getAdapterPosition()]);
*/

    }




    @Override
    public int getItemCount() {
        return widgetList.size();
    }


    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            mDataset[position] = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

}

