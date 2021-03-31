package com.example.p3175.activity.recurringtransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.category.ManageCategoryActivity;
import com.example.p3175.activity.main.MainActivity;
import com.example.p3175.activity.overview.AddSavingsActivity;
import com.example.p3175.activity.report.ReportActivity;
import com.example.p3175.activity.user.EditUserActivity;
import com.example.p3175.activity.user.LoginActivity;
import com.example.p3175.db.entity.RecurringTransaction;
import com.example.p3175.util.Converter;

import java.math.BigDecimal;


public class EditRecurringTransactionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recurring_transaction);

        //region 0. VIEW

        EditText editTextAmount = findViewById(R.id.editTextEditRecurringTransactionAmount);
        EditText editTextDayOfMonth = findViewById(R.id.editTextEditRecurringTransactionDayOfMonth);
        EditText editTextDescription = findViewById(R.id.editTextEditRecurringTransactionDescription);
        Button buttonOK = findViewById(R.id.buttonEditRecurringTransactionOK);
        RadioButton radioButtonIsIncome = findViewById(R.id.radioButtonIsIncome);
        RadioButton radioButtonIsBill = findViewById(R.id.radioButtonIsBill);

        radioButtonIsIncome.setChecked(true);
        buttonOK.setEnabled(false);
        //endregion

        //region 1. VALIDATE INPUT

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonOK.setEnabled(!editTextAmount.getText().toString().isEmpty()
                        && !editTextDayOfMonth.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextAmount.addTextChangedListener(textWatcher);
        editTextDayOfMonth.addTextChangedListener(textWatcher);
        //endregion

        //region 2. FILL DATA OF ITEM BEING EDITED

        // db select
        RecurringTransaction recurringTransaction = db.selectRecurringTransaction(getIntent().getIntExtra(getString(R.string.recurring_transaction_id), -1));
        assert recurringTransaction != null;

        // fill data to edit text
        radioButtonIsBill.setChecked(recurringTransaction.getAmount().compareTo(BigDecimal.ZERO) < 0);
        editTextAmount.setText(Converter.bigDecimalToString(recurringTransaction.getAmount().abs()));
        editTextDayOfMonth.setText(String.valueOf(recurringTransaction.getDayOfMonth()));
        editTextDescription.setText(recurringTransaction.getDescription());
        //endregion


        //region 3. BUTTON

        buttonOK.setOnClickListener(v -> {
            // db update
            recurringTransaction.setAmount(radioButtonIsIncome.isChecked()
                    ? Converter.stringToBigDecimal(editTextAmount.getText().toString())
                    : Converter.stringToBigDecimal(editTextAmount.getText().toString()).negate());
            recurringTransaction.setDayOfMonth(Integer.parseInt(editTextDayOfMonth.getText().toString()));
            recurringTransaction.setDescription(editTextDescription.getText().toString());
            db.updateRecurringTransaction(recurringTransaction);

            // nav back
            onBackPressed();
        });
        //endregion
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