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
import com.example.p3175.db.DatabaseHelper;
import com.example.p3175.db.entity.Transaction;
import com.example.p3175.util.Converter;

public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder> {
    DatabaseHelper db;
    OnClickListener onClickListener;

    public TransactionAdapter(DatabaseHelper db) {
        super(new DiffUtil.ItemCallback<Transaction>() {
            @Override
            public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
                return oldItem.equals(newItem);
            }
        });

        this.db = db;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // itemView
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.cell_transaction, parent, false);

        // viewHolder
        return new TransactionViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = getItem(position);

        // set text
        holder.textViewCategory.setText(db.selectCategory(transaction.getCategoryId()).getName());
        holder.textViewDate.setText(Converter.localDateToString(transaction.getDate()));
        holder.textViewDescription.setText(transaction.getDescription());
        holder.textViewAmount.setText(Converter.bigDecimalToString(transaction.getAmount()));

        if (onClickListener != null) {
            holder.itemView.setOnClickListener(v -> onClickListener.onClick(transaction.getId()));
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategory, textViewDate, textViewDescription, textViewAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCategory = itemView.findViewById(R.id.textViewTransactionCategory);
            textViewDate = itemView.findViewById(R.id.textViewTransactionDate);
            textViewDescription = itemView.findViewById(R.id.textViewTransactionDescription);
            textViewAmount = itemView.findViewById(R.id.textViewTransactionAmount);
        }
    }
}
