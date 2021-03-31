package com.example.p3175.activity.bigexpense;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.main.MainActivity;
import com.example.p3175.adapter.BigExpenseAdapter;
import com.example.p3175.db.entity.BigExpense;
import com.example.p3175.util.Calculator;
import com.example.p3175.util.Converter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class GetBigExpensePlanActivity extends BaseActivity {

    private BigExpenseAdapter adapter;

    int ratio;
    BigDecimal amount, incomes, savings, incomesNeeded, savingsNeeded, loanNeeded, remaining;
    String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_big_expense_plan);

        //region 0. VIEW

        RecyclerView recyclerView = findViewById(R.id.recyclerViewBigExpensePlan);
        SeekBar seekBar = findViewById(R.id.seekBar);
        TextView textViewRatio = findViewById(R.id.textViewGetBigExpensePlanRatio);
        //endregion

        //region 2. PREPARE DATA FOR GETTING PLANS

        amount = Converter.longToBigDecimal(getIntent().getLongExtra(getString(R.string.amount), 0));
        description = getIntent().getStringExtra(getString(R.string.description));
        incomes = currentOverview.getIncomes();
        savings = currentOverview.getSavings();
        ratio = 50;


        //region 3. RECYCLER VIEW

        adapter = new BigExpenseAdapter(true);
        adapter.setOnClickListener(new BigExpenseAdapter.OnClickListener() {
            @Override
            public void onClick(BigExpense bigExpense) {
                new AlertDialog.Builder(GetBigExpensePlanActivity.this)
                        .setTitle("Choose this plan?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // db insert: big expense
                            db.insertBigExpense(bigExpense);

                            // db update: overview
                            Calculator.updateIncomesSavings(currentOverview, bigExpense.getIncomeNeeded(), bigExpense.getSavingNeeded());
                            db.updateOverview(currentOverview);

                            // nav back
                            Intent intent = new Intent(GetBigExpensePlanActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            GetBigExpensePlanActivity.this.startActivity(intent);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                        })
                        .create()
                        .show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        //endregion

        //region 4. SEEK BAR

        seekBar.setProgress(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ratio = progress;
                textViewRatio.setText(getString(R.string.label_incomes_savings_ratio, ratio, 100 - ratio));
                onResume();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //endregion
    }

    @Override
    protected void onResume() {
        super.onResume();

        //region RECYCLER VIEW
        List<BigExpense> plans = new ArrayList<>();

        // plan 1: income -> savings

        remaining = amount;
        incomesNeeded = BigDecimal.ZERO;
        savingsNeeded = BigDecimal.ZERO;
        loanNeeded = BigDecimal.ZERO;

        if (incomes.compareTo(remaining) >= 0) {
            incomesNeeded = remaining;         // covered by incomes
        } else {
            incomesNeeded = incomes;        // if incomes not enough, use all incomes
            remaining = remaining.subtract(incomes);
            if (savings.compareTo(remaining) >= 0) {
                savingsNeeded = remaining;     // the rest covered by savings
            } else {
                savingsNeeded = savings;    // if savings not enough, use all savings
                remaining = remaining.subtract(savings);
                loanNeeded = remaining;        // need a loan
            }
        }
        plans.add(new BigExpense(-1, currentUserId, amount, LocalDate.now(), description, incomesNeeded, savingsNeeded, loanNeeded));

        // plan 2: savings -> incomes

        remaining = amount;
        incomesNeeded = BigDecimal.ZERO;
        savingsNeeded = BigDecimal.ZERO;
        loanNeeded = BigDecimal.ZERO;

        if (savings.compareTo(remaining) >= 0) {
            savingsNeeded = remaining;         // covered by savings
        } else {
            savingsNeeded = savings;        // if savings not enough, use all savings
            remaining = remaining.subtract(savings);
            if (incomes.compareTo(remaining) >= 0) {
                incomesNeeded = remaining;     // the rest covered by incomes
            } else {
                incomesNeeded = incomes;    // if incomes not enough, use all incomes
                remaining = remaining.subtract(incomes);
                loanNeeded = remaining;        // need a loan
            }
        }
        plans.add(new BigExpense(-2, currentUserId, amount, LocalDate.now(), description, incomesNeeded, savingsNeeded, loanNeeded));

        // plan 3:

        incomesNeeded = BigDecimal.ZERO;
        savingsNeeded = BigDecimal.ZERO;
        loanNeeded = BigDecimal.ZERO;

        BigDecimal incomesPortion = amount.multiply(
                new BigDecimal(ratio).divide(new BigDecimal(100), 2, BigDecimal.ROUND_UP));
        BigDecimal savingsPortion = amount.multiply(
                new BigDecimal(100 - ratio).divide(new BigDecimal(100), 2, BigDecimal.ROUND_UP));

        if (incomes.compareTo(incomesPortion) >= 0 && savings.compareTo(savingsPortion) >= 0) {
            // case 1: enough incomes & enough savings

            incomesNeeded = incomesPortion;
            savingsNeeded = savingsPortion;
        } else if (incomes.compareTo(incomesPortion) >= 0 && savings.compareTo(savingsPortion) <= 0) {
            // case 2: enough incomes & NOT enough savings, incomes have to cover more

            savingsNeeded = savings;    // all savings will be used
            incomesPortion = incomesPortion.add(savingsPortion.subtract(savings));  // incomes cover the part that savings cannot
            if (incomes.compareTo(incomesPortion) >= 0) {
                incomesNeeded = incomesPortion;     // enough incomes for savings part
            } else {
                incomesNeeded = incomes;            // not enough incomes, loan needed
                loanNeeded = incomesPortion.subtract(incomes);
            }
        } else if (incomes.compareTo(incomesPortion) <= 0 && savings.compareTo(savingsPortion) >= 0) {
            // case 3: NOT enough incomes & enough savings:

            incomesNeeded = incomes;
            savingsPortion = savingsPortion.add(incomesPortion.subtract(incomes));
            if (savings.compareTo(savingsPortion) >= 0) {
                savingsNeeded = savingsPortion;
            } else {
                savingsNeeded = savings;
                loanNeeded = savingsPortion.subtract(savings);
            }
        } else if (incomes.compareTo(incomesPortion) <= 0 && savings.compareTo(savingsPortion) <= 0) {
            // case 4: NOT enough incomes & NOT enough savings

            incomesNeeded = incomes;
            savingsNeeded = savings;
            loanNeeded = incomesPortion.subtract(incomes).add(savingsPortion.subtract(savings));
        }
        plans.add(new BigExpense(-3, currentUserId, amount, LocalDate.now(), description, incomesNeeded, savingsNeeded, loanNeeded));

        adapter.submitList(plans);

        //endregion
    }
}