package com.example.p3175.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.db.entity.BigExpense;
import com.example.p3175.util.Converter;


public class BigExpenseAdapter extends ListAdapter<BigExpense, BigExpenseAdapter.BigExpenseViewHolder> {
    OnClickListener onClickListener;
    boolean isForPlan;

    public BigExpenseAdapter(boolean isForPlan) {
        super(new DiffUtil.ItemCallback<BigExpense>() {
            @Override
            public boolean areItemsTheSame(@NonNull BigExpense oldItem, @NonNull BigExpense newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull BigExpense oldItem, @NonNull BigExpense newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.isForPlan = isForPlan;
    }

    @NonNull
    @Override
    public BigExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.cell_big_expense, parent, false);

        return new BigExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BigExpenseViewHolder holder, int position) {
        BigExpense bigExpense = getItem(position);

        if (isForPlan) {
            holder.textViewAmount.setVisibility(View.GONE);
            holder.textViewDescription.setVisibility(View.GONE);
        }

        holder.textViewAmount.setText(Converter.bigDecimalToString(bigExpense.getAmount()));
        holder.textViewIncomes.setText(Converter.bigDecimalToString(bigExpense.getIncomeNeeded()));
        holder.textViewSavings.setText(Converter.bigDecimalToString(bigExpense.getSavingNeeded()));
        holder.textViewLoan.setText(Converter.bigDecimalToString(bigExpense.getLoanNeeded()));
        holder.textViewDescription.setText(bigExpense.getDescription());

        if (onClickListener != null) {
            holder.itemView.setOnClickListener(v -> onClickListener.onClick(bigExpense));
        }

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    static class BigExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAmount, textViewDescription, textViewIncomes, textViewSavings, textViewLoan;

        public BigExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAmount = itemView.findViewById(R.id.textViewBigExpenseAmount);
            textViewIncomes = itemView.findViewById(R.id.textViewBigExpenseIncomesAmount);
            textViewSavings = itemView.findViewById(R.id.textViewBigExpenseSavingsAmount);
            textViewLoan = itemView.findViewById(R.id.textViewBigExpenseLoanAmount);
            textViewDescription = itemView.findViewById(R.id.textViewBigExpenseDescription);
        }
    }

    public interface OnClickListener {
        void onClick(BigExpense bigExpense);
    }
}
