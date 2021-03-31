package com.example.p3175.activity.recurringtransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.category.ManageCategoryActivity;
import com.example.p3175.activity.main.MainActivity;
import com.example.p3175.activity.overview.AddSavingsActivity;
import com.example.p3175.activity.report.ReportActivity;
import com.example.p3175.activity.user.EditUserActivity;
import com.example.p3175.activity.user.LoginActivity;
import com.example.p3175.adapter.OnClickListener;
import com.example.p3175.adapter.RecurringTransactionAdapter;
import com.example.p3175.db.DatabaseHelper;
import com.example.p3175.db.entity.RecurringTransaction;


public class ManageRecurringTransactionActivity extends BaseActivity {

    RecurringTransactionAdapter adapterIncome, adapterBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recurring_transaction);
        //region 0. VIEW

        RecyclerView recyclerViewIncome = findViewById(R.id.recyclerViewIncome);
        RecyclerView recyclerViewBill = findViewById(R.id.recycleViewBill);
        Button buttonCreate = findViewById(R.id.buttonManageRecurringTransactionAdd);
        //endregion

        //region 1. RECYCLER VIEW

        adapterIncome = new RecurringTransactionAdapter();
        adapterBill = new RecurringTransactionAdapter();
        OnClickListener onClickListener = recurringTransactionId -> {
            Intent intent = new Intent(this, EditRecurringTransactionActivity.class);
            intent.putExtra(getString(R.string.recurring_transaction_id), recurringTransactionId);
            startActivity(intent);
        };
        adapterIncome.setOnClickListener(onClickListener);
        adapterBill.setOnClickListener(onClickListener);

        recyclerViewIncome.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBill.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewIncome.setAdapter(adapterIncome);
        recyclerViewBill.setAdapter(adapterBill);
        //endregion

        //region 2. SWIPE DELETE
        Context context = this;
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
                new AlertDialog.Builder(context)
                        .setTitle("Delete?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            RecurringTransaction itemToDelete = adapterIncome.getCurrentList().get(viewHolder.getAdapterPosition());
                            db.delete(DatabaseHelper.TABLE_RECURRING_TRANSACTION, itemToDelete.getId());
                        })
                        .setNegativeButton("No",
                                (dialog, which) -> adapterIncome.notifyItemChanged(viewHolder.getAdapterPosition()))
                        .create()
                        .show();
            }
        }).attachToRecyclerView(recyclerViewIncome);

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
                new AlertDialog.Builder(context)
                        .setTitle("Delete?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            RecurringTransaction itemToDelete = adapterBill.getCurrentList().get(viewHolder.getAdapterPosition());
                            db.delete(DatabaseHelper.TABLE_RECURRING_TRANSACTION, itemToDelete.getId());
                        })
                        .setNegativeButton("No",
                                (dialog, which) -> adapterBill.notifyItemChanged(viewHolder.getAdapterPosition()))
                        .create()
                        .show();
            }
        }).attachToRecyclerView(recyclerViewBill);
        //endregion

        //region 3. BUTTON CREATE

        buttonCreate.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateRecurringTransactionActivity.class);
            intent.putExtra(getString(R.string.current_user_id), currentUserId);
            startActivity(intent);
        });
        //endregion
    }

    @Override
    protected void onResume() {
        super.onResume();

        // refresh recycler view
        adapterIncome.submitList(db.listRecurringTransactionsByUserId(currentUserId, true));
        adapterBill.submitList(db.listRecurringTransactionsByUserId(currentUserId, false));
    }

    // region. TOP RIGHT MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuItemHome) {
            startActivity(new Intent(this, MainActivity.class));
        }else if (itemId == R.id.menuItemCategory) {
            startActivity(new Intent(this, ManageCategoryActivity.class));
        } else if (itemId == R.id.menuItemSalaryBill) {
            startActivity(new Intent(this, ManageRecurringTransactionActivity.class));
        } else if (itemId == R.id.menuItemAddSavings) {
            startActivity(new Intent(this, AddSavingsActivity.class));
        } else if (itemId == R.id.menuItemReport) {
            startActivity(new Intent(this, ReportActivity.class));
        } else if (itemId == R.id.menuItemAccount) {
            startActivity(new Intent(this, EditUserActivity.class));
        } else if (itemId == R.id.menuItemLogout) {
            // remove logged in tag in the shared pref
            editor.remove(getString(R.string.logged_in_user_id)).commit();

            // nav to login activity, unable to nav back
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}