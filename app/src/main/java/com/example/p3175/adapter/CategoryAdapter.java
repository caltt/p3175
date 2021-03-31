package com.example.p3175.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p3175.R;
import com.example.p3175.db.entity.Category;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {
    OnClickListener onClickListener;

    public CategoryAdapter() {
        super(new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.equals(newItem);
            }
        });

    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // itemView
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.cell_category, parent, false);

        // viewHolder
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = getItem(position);

        holder.textViewName.setText(category.getName());

        if (onClickListener != null) {
            holder.itemView.setOnClickListener(v -> onClickListener.onClick(category.getId()));
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewCategoryName);
        }
    }

}
