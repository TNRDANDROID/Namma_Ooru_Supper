package com.nic.nammaoorusuper.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.ViewFormActivity;
import com.nic.nammaoorusuper.databinding.CategoryItemViewBinding;
import com.nic.nammaoorusuper.databinding.MenuItemTextBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;

import java.util.List;

public class CategoryListeNameAdapter extends RecyclerView.Adapter<CategoryListeNameAdapter.MyViewHolder> {

    private Activity context;
    private PrefManager prefManager;
    private List<NOS> categoryList;
    com.nic.nammaoorusuper.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;
    int pos=-1;

    public CategoryListeNameAdapter(Activity context,List<NOS> categoryList, com.nic.nammaoorusuper.dataBase.dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
        this.categoryList = categoryList;
    }

    @Override
    public CategoryListeNameAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        MenuItemTextBinding menuItemTextBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.menu_item_text, viewGroup, false);
        return new MyViewHolder(menuItemTextBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private MenuItemTextBinding menuItemTextBinding;

        public MyViewHolder(MenuItemTextBinding Binding) {
            super(Binding.getRoot());
            menuItemTextBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryListeNameAdapter.MyViewHolder holder, final int position) {

        holder.menuItemTextBinding.categoryTitle.setText(categoryList.get(position).getImage_category_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewFormActivity)context).categoryRecyclerItemClicked(position);
            }
        });
    }




    @Override
    public int getItemCount() {
        return categoryList.size();
    }




}

