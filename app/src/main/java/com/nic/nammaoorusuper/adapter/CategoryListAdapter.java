package com.nic.nammaoorusuper.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.PhotoCategoryList;
import com.nic.nammaoorusuper.activity.SubActivityList;
import com.nic.nammaoorusuper.databinding.CategoryItemViewBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.Utils;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {

    private Activity context;
    private PrefManager prefManager;
    private List<NOS> categoryList;
    com.nic.nammaoorusuper.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;
    int pos=-1;

    public CategoryListAdapter(Activity context,List<NOS> categoryList, com.nic.nammaoorusuper.dataBase.dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
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

        if(categoryList.get(position).getIs_taken().equals("Y")){
            holder.categoryItemViewBinding.completedLayout.setVisibility(View.VISIBLE);
        }
        else {
            holder.categoryItemViewBinding.completedLayout.setVisibility(View.GONE);
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
                ((PhotoCategoryList)context).activityListItemClicked(position);


            }
        });
    }




    @Override
    public int getItemCount() {
        return categoryList.size();
    }




}

