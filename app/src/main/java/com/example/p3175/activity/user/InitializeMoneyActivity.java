package com.example.p3175.activity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.recurringtransaction.ManageIncome;
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
        //Button buttonManageRecurringTransaction = findViewById(R.id.buttonInitialManageRecurring);
        Button buttonOK = findViewById(R.id.buttonInitialOK);
        //endregion



        buttonOK.setOnClickListener(v -> {
            String salary = editTextSalary.getText().toString();
            String savings = editTextSavings.getText().toString();

            // db insert: recurring transaction
            if (!salary.isEmpty()&&!savings.isEmpty()) {
                db.insertRecurringTransaction(new RecurringTransaction(
                        currentUserId,
                        Converter.stringToBigDecimal(salary),
                        1,
                        "Salary"
                ));

                // db update: overview
                currentOverview.setSavings(Converter.stringToBigDecimal(savings));
                db.updateOverview(currentOverview);

                currentOverview.setIncomes(Converter.stringToBigDecimal(salary));
                db.updateOverview(currentOverview);

                // nav to main activity, unable to nav back
                AlertDialog.Builder altdial = new AlertDialog.Builder(InitializeMoneyActivity.this);
                altdial.setMessage("Do you want to add an additional income?")
                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(InitializeMoneyActivity.this, ManageIncome.class);
                        intent.putExtra(getString(R.string.current_user_id), currentUserId);
                        startActivity(intent);
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(InitializeMoneyActivity.this, "Account created", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(InitializeMoneyActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        });

                AlertDialog alert = altdial.create();
                alert.setTitle("Additional Income");
                alert.show();

            }else{
                Toast.makeText(InitializeMoneyActivity.this,"Please Enter Salary and Savings", Toast.LENGTH_SHORT).show();

            }

//            // db update: overview
//            if (!savings.isEmpty()) {
//                currentOverview.setSavings(Converter.stringToBigDecimal(savings));
//                db.updateOverview(currentOverview);
//            }

        });
        //endregion
    }
}