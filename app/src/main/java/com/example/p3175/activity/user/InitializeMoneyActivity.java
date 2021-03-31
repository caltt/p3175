package com.example.p3175.activity.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.recurringtransaction.ManageRecurringTransactionActivity;
import com.example.p3175.db.entity.RecurringTransaction;
import com.example.p3175.util.Converter;


public class InitializeMoneyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize_user);

        //region 0. VIEW

        EditText editTextSavings = findViewById(R.id.editTextInitialSavings);
        EditText editTextSalary = findViewById(R.id.editTextInitialSalary);
        Button buttonManageRecurringTransaction = findViewById(R.id.buttonInitialManageRecurring);
        Button buttonOK = findViewById(R.id.buttonInitialOK);
        //endregion

        //region 2. BUTTON

        buttonManageRecurringTransaction.setOnClickListener(v -> {
            // nav to manage salary & bill
            Intent intent = new Intent(this, ManageRecurringTransactionActivity.class);
            intent.putExtra(getString(R.string.current_user_id), currentUserId);
            startActivity(intent);
        });

        buttonOK.setOnClickListener(v -> {
            String salary = editTextSalary.getText().toString();
            String savings = editTextSavings.getText().toString();

            // db insert: recurring transaction
            if (!salary.isEmpty()) {
                db.insertRecurringTransaction(new RecurringTransaction(
                        currentUserId,
                        Converter.stringToBigDecimal(salary),
                        1,
                        "Salary"
                ));
            }

            // db update: overview
            if (!savings.isEmpty()) {
                currentOverview.setSavings(Converter.stringToBigDecimal(savings));
                db.updateOverview(currentOverview);
            }

            // nav to main activity, unable to nav back
            Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        //endregion
    }
}