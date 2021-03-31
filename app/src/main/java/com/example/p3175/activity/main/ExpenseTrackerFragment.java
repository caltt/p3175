package com.example.p3175.activity.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.p3175.activity.base.BaseFragment;
import com.example.p3175.R;
import com.example.p3175.activity.transaction.EditTransactionActivity;
import com.example.p3175.adapter.OnClickListener;
import com.example.p3175.adapter.TransactionAdapter;
import com.example.p3175.db.DatabaseHelper;
import com.example.p3175.db.entity.Overview;
import com.example.p3175.db.entity.RecurringTransaction;
import com.example.p3175.db.entity.Transaction;
import com.example.p3175.util.Calculator;
import com.example.p3175.util.Converter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;


public class ExpenseTrackerFragment extends BaseFragment {
    TransactionAdapter adapter;

    TextView textViewTodayRemaining, textViewTodayAllowed, textViewSavings, textViewIncome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expense_tracker, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //region 0. VIEW

        textViewTodayRemaining = activity.findViewById(R.id.textViewTodayRemainingAmount);
        textViewTodayAllowed = activity.findViewById(R.id.textViewTodayAllowedAmount);
        textViewSavings = activity.findViewById(R.id.textViewSavingsAmount);
        textViewIncome = activity.findViewById(R.id.textViewIncomeAmount);
        RecyclerView recyclerView = activity.findViewById(R.id.recyclerViewTransaction);
        //endregion

        //region 1. BOTTOM HALF: RECYCLER VIEW FOR LIST

        adapter = new TransactionAdapter(db);
        adapter.setOnClickListener(id -> {
            Intent intent = new Intent(activity, EditTransactionActivity.class);
            intent.putExtra(activity.getString(R.string.transaction_id), id);
            activity.startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        //endregion

        //region 2 SWIPE TO DELETE

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(activity)
                        .setTitle("Delete?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Transaction itemToDelete = adapter.getCurrentList().get(viewHolder.getAdapterPosition());

                            // db update: overview
                            Calculator.updateIncomesSavings(currentOverview, itemToDelete.getAmount().negate());
                            // FIXME: 3/29/2021 
                            if (LocalDate.now().equals(itemToDelete.getDate())){
                                
                            }
                            db.updateOverview(currentOverview);

                            // db delete: transaction
                            db.delete(DatabaseHelper.TABLE_TRANSACTION, itemToDelete.getId());

                            // refresh overview & list
                            refreshOverview();
                            adapter.submitList(db.listTransactionsByUserId(currentUserId));
                        })
                        .setNegativeButton("No",
                                (dialog, which) -> adapter.notifyItemChanged(viewHolder.getAdapterPosition()))
                        .create()
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
        //endregion


    }

    @Override
    public void onResume() {
        super.onResume();

        //region 2. IF LOGIN ANOTHER DAY, DO PENDING RECURRING TRANSACTIONS AND UPDATE TODAY'S ALLOWED

        String lastLoginDateString = preferences.getString(getString(R.string.last_login_date) + currentUserId, "");
        LocalDate lastLoginDate = lastLoginDateString.isEmpty()
                ? LocalDate.now()
                : Converter.stringToLocalDate(lastLoginDateString);
        assert lastLoginDate != null;

        // if login in a new day (first login / login on another day)
        if (!LocalDate.now().equals(lastLoginDate)) {

            // 1. check recurring transactions between last login date (exclusive) & today (inclusive)

            // get that period
            Period period = Period.between(lastLoginDate, LocalDate.now()); // start inclusive & end exclusive
            int duration = period.getDays();                                // still a correct number of days

            // for each date during this period, get and do recurring transactions on that date
            LocalDate date;

            for (int i = 1; i <= duration; i++) {
                // get
                date = lastLoginDate.plusDays(i);
                List<RecurringTransaction> pendingTransactions
                        = db.listRecurringTransactionsByUserIdDayOfMonth(currentUserId, date.getDayOfMonth());

                // do transactions
                if (pendingTransactions != null) {
                    for (RecurringTransaction t : pendingTransactions) {
                        // db insert: transaction
                        db.insertTransaction(new Transaction(
                                currentUserId, 1, t.getAmount(), date, t.getDescription()
                        ));

                        // db update: overview
                        Calculator.updateIncomesSavings(currentOverview, t.getAmount());
                    }
                }
            }

            // 2. update today available

            // reset today allowed
            if (currentOverview.getTodayRemaining().compareTo(BigDecimal.ZERO) > 0) {
                new AlertDialog.Builder(activity)
                        .setTitle("Add remaining to savings?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Calculator.resetTodayAllowed(currentOverview, true);
                            db.updateOverview(currentOverview);
                            refreshOverview();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            Calculator.resetTodayAllowed(currentOverview, false);
                            db.updateOverview(currentOverview);
                            refreshOverview();
                        })
                        .create()
                        .show();
            } else {
                Calculator.resetTodayAllowed(currentOverview, false);
                db.updateOverview(currentOverview);
                refreshOverview();
            }
        }

        refreshOverview();
        editor.putString(getString(R.string.last_login_date) + currentUserId, LocalDate.now().toString()).apply();
        //endregion


        // refresh recycler view
        adapter.submitList(db.listTransactionsByUserId(currentUserId));
    }

    private void refreshOverview() {
        textViewIncome.setText(Converter.bigDecimalToString(currentOverview.getIncomes()));
        textViewSavings.setText(Converter.bigDecimalToString(currentOverview.getSavings()));
        textViewTodayAllowed.setText(Converter.bigDecimalToString(currentOverview.getTodayAllowed()));
        textViewTodayRemaining.setText(Converter.bigDecimalToString(currentOverview.getTodayRemaining()));
    }
}