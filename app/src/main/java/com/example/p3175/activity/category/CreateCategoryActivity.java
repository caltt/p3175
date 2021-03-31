package com.example.p3175.activity.category;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.main.MainActivity;
import com.example.p3175.activity.overview.AddSavingsActivity;
import com.example.p3175.activity.recurringtransaction.ManageRecurringTransactionActivity;
import com.example.p3175.activity.report.ReportActivity;
import com.example.p3175.activity.user.EditUserActivity;
import com.example.p3175.activity.user.LoginActivity;
import com.example.p3175.db.entity.Category;


public class CreateCategoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        //region 0. VIEW

        RadioButton radioButtonIsIncome = findViewById(R.id.radioButtonEditCategoryIsIncome);
        RadioButton radioButtonIsExpense = findViewById(R.id.radioButtonEditCategoryIsExpense);
        EditText editTextCategoryName = findViewById(R.id.editTextEditCategoryName);
        Button buttonOK = findViewById(R.id.buttonEditCategoryOK);
        radioButtonIsExpense.setChecked(true);
        buttonOK.setEnabled(false);
        //endregion

        //region 1. VALIDATE INPUT

        buttonOK.setActivated(false);

        editTextCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonOK.setEnabled(!editTextCategoryName.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //endregion

        //region 2. BUTTON

        buttonOK.setOnClickListener(v -> {
            // db insert
            db.insertCategory(new Category(editTextCategoryName.getText().toString(), radioButtonIsIncome.isChecked()));

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