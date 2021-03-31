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
import com.example.p3175.db.entity.User;

public class UserAdapter extends ListAdapter<User, UserAdapter.UserViewHolder> {

    public UserAdapter() {
        super(new DiffUtil.ItemCallback<User>() {
            @Override
            public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return oldItem.getId()==newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return oldItem.equals(newItem);
            }
        });

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.cell_user, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = getItem(position);

        // set text
        holder.textViewPosition.setText(String.valueOf(position + 1));    // position in the list
        holder.textViewEmail.setText(user.getEmail());
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPosition, textViewEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPosition = itemView.findViewById(R.id.textViewUserId);
            textViewEmail = itemView.findViewById(R.id.textViewUserEmail);
        }

    }
}
