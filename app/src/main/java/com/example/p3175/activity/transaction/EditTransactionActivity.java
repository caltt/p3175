package com.example.p3175.activity.transaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.category.ManageCategoryActivity;
import com.example.p3175.activity.main.MainActivity;
import com.example.p3175.activity.overview.AddSavingsActivity;
import com.example.p3175.activity.recurringtransaction.ManageRecurringTransactionActivity;
import com.example.p3175.activity.report.ReportActivity;
import com.example.p3175.activity.user.EditUserActivity;
import com.example.p3175.activity.user.LoginActivity;
import com.example.p3175.db.entity.Category;
import com.example.p3175.db.entity.Transaction;
import com.example.p3175.util.Calculator;
import com.example.p3175.util.Converter;

import java.math.BigDecimal;
import java.time.LocalDate;


public class EditTransactionActivity extends BaseActivity {

    private LocalDate datePickerDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        //region 0. VIEW

        EditText editTextAmount = findViewById(R.id.editTextEditTransactionAmount);
        EditText editTextDate = findViewById(R.id.editTextEditTransactionDate);
        EditText editTextDescription = findViewById(R.id.editTextEditTransactionDescription);
        EditText editTextCategory = findViewById(R.id.editTextEditTransactionCategoryName);
        ImageButton buttonDatePicker = findViewById(R.id.imageButtonDatePicker);
        Button buttonOK = findViewById(R.id.buttonEditTransactionOK);

        editTextCategory.setEnabled(false);
        editTextDate.setEnabled(false);
        buttonOK.setEnabled(false);
        //endregion

        //region 1. VALIDATE INPUT

        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonOK.setEnabled(!editTextAmount.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //endregion

        //region 2. FILL DATA OF ITEM BEING EDITED

        // db select
        Transaction transaction = db.selectTransaction(getIntent().getIntExtra(getString(R.string.transaction_id), -1));
        assert transaction != null;

        // fill data to edit text
        editTextCategory.setText(db.selectCategory(transaction.getCategoryId()).getName());
        editTextAmount.setText(Converter.bigDecimalToString(transaction.getAmount().abs()));
        editTextDescription.setText(transaction.getDescription());

        datePickerDate = transaction.getDate();
        editTextDate.setText(datePickerDate.toString());
        //endregion

        //region 3. DATE PICKER

        // date picker button
        buttonDatePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                datePickerDate = LocalDate.of(year, month + 1, dayOfMonth);
                editTextDate.setText(datePickerDate.toString());
            }, datePickerDate.getYear(), datePickerDate.getMonthValue() - 1, datePickerDate.getDayOfMonth());
            datePickerDialog.show();
        });
        //endregion

        //region 4. BUTTON

        buttonOK.setOnClickListener(v -> {
            BigDecimal oldAmount = transaction.getAmount();
            BigDecimal newAmount = Converter.stringToBigDecimal(editTextAmount.getText().toString());
            Category category = db.selectCategory(transaction.getCategoryId());
            newAmount = category.isIncome() ? newAmount : newAmount.negate();

            // db update: transaction
            transaction.setAmount(newAmount);
            transaction.setDate(Converter.stringToLocalDate(editTextDate.getText().toString()));
            transaction.setDescription(editTextDescription.getText().toString());
            db.updateTransaction(transaction);

            // db update: overview
            Calculator.updateIncomesSavings(currentOverview, newAmount.subtract(oldAmount));
            db.updateOverview(currentOverview);

            // nav back
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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