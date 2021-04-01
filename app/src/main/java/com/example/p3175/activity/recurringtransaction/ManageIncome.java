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
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.RequiresApi;
        import androidx.navigation.NavController;
        import androidx.navigation.Navigation;
        import androidx.navigation.ui.AppBarConfiguration;
        import androidx.navigation.ui.NavigationUI;
        import androidx.recyclerview.widget.ItemTouchHelper;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.p3175.R;
        import com.example.p3175.activity.base.BaseActivity;
        import com.example.p3175.activity.bigexpense.CreateBigExpenseActivity;
        import com.example.p3175.activity.category.ChooseTransactionCategoryActivity;
        import com.example.p3175.activity.category.ManageCategoryActivity;
        import com.example.p3175.activity.main.MainActivity;
        import com.example.p3175.activity.overview.AddSavingsActivity;
        import com.example.p3175.activity.report.ReportActivity;
        import com.example.p3175.activity.user.EditUserActivity;
        import com.example.p3175.activity.user.InitializeMoneyActivity;
        import com.example.p3175.activity.user.LoginActivity;
        import com.example.p3175.adapter.OnClickListener;
        import com.example.p3175.adapter.RecurringTransactionAdapter;
        import com.example.p3175.db.DatabaseHelper;
        import com.example.p3175.db.entity.RecurringTransaction;
        import com.google.android.material.bottomnavigation.BottomNavigationView;
        import com.google.android.material.floatingactionbutton.FloatingActionButton;

        import java.util.Objects;


public class ManageIncome extends BaseActivity {

    RecurringTransactionAdapter adapterIncome, adapterBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_income);
        //region 0. VIEW

        RecyclerView recyclerViewIncome = findViewById(R.id.recyclerViewIncome);
        Button buttonCreate = findViewById(R.id.btnAdd);
        Button buttonFinish = findViewById(R.id.btnFinish);

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
        recyclerViewIncome.setAdapter(adapterIncome);
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

        //endregion

        //region 3. BUTTON CREATE

        buttonCreate.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddIncome.class);
            intent.putExtra(getString(R.string.current_user_id), currentUserId);
            startActivity(intent);

        });
        //endregion

        //region 3. BUTTON finish

        buttonFinish.setOnClickListener(v -> {
            Toast.makeText(ManageIncome.this, "Account created", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ManageIncome.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });
        //endregion
    }

    @Override
    protected void onResume() {
        super.onResume();

        // refresh recycler view
        adapterIncome.submitList(db.listRecurringTransactionsByUserId(currentUserId, true));

    }
}