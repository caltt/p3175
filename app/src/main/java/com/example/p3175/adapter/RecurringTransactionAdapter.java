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
import com.example.p3175.db.entity.RecurringTransaction;
import com.example.p3175.util.Converter;


public class RecurringTransactionAdapter extends ListAdapter<RecurringTransaction, RecurringTransactionAdapter.RecurringTransactionViewHolder> {
    OnClickListener onClickListener;

    public RecurringTransactionAdapter() {
        super(new DiffUtil.ItemCallback<RecurringTransaction>() {
            @Override
            public boolean areItemsTheSame(@NonNull RecurringTransaction oldItem, @NonNull RecurringTransaction newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull RecurringTransaction oldItem, @NonNull RecurringTransaction newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public RecurringTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // itemView
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.cell_recurring_transaction, parent, false);

        // viewHolder
        return new RecurringTransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecurringTransactionViewHolder holder, int position) {
        RecurringTransaction recurringTransaction = getItem(position);

        // set text
        holder.textViewDayOfMonth.setText(String.valueOf(recurringTransaction.getDayOfMonth()));
        holder.textViewDescription.setText(recurringTransaction.getDescription());
        holder.textViewAmount.setText(Converter.bigDecimalToString(recurringTransaction.getAmount()));

        if (onClickListener != null) {
            holder.itemView.setOnClickListener(v -> onClickListener.onClick(recurringTransaction.getId()));
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    static class RecurringTransactionViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDayOfMonth,
                textViewAmount,
                textViewDescription;


        public RecurringTransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDayOfMonth = itemView.findViewById(R.id.textViewRecurringTransactionDate);
            textViewAmount = itemView.findViewById(R.id.textViewRecurringTransactionAmount);
            textViewDescription = itemView.findViewById(R.id.textViewRecurringTransactionDescription);
        }
    }
}
