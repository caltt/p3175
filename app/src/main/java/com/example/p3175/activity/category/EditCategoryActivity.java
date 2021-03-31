package com.example.p3175.activity.category;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.RequiresApi;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.db.entity.Category;


public class EditCategoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        //region 0. VIEW

        RadioButton radioButtonIsIncome = findViewById(R.id.radioButtonEditCategoryIsIncome);
        RadioButton radioButtonIsExpense = findViewById(R.id.radioButtonEditCategoryIsExpense);
        EditText editTextCategoryName = findViewById(R.id.editTextEditCategoryName);
        Button buttonOK = findViewById(R.id.buttonEditCategoryOK);
        buttonOK.setEnabled(false);
        //endregion

        //region 1. VALIDATE INPUT

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

        //region 2. FILL DATA OF ITEM BEING EDITED

        // db select
        int categoryId = getIntent().getIntExtra("categoryId", -1);
        Category category = db.selectCategory(categoryId);
        assert category != null;

        // fill data
        editTextCategoryName.setText(category.getName());
        if (category.isIncome()) {
            radioButtonIsIncome.setChecked(true);
        } else {
            radioButtonIsExpense.setChecked(true);
        }
        //endregion


        //region 3. BUTTON

        buttonOK.setOnClickListener(v -> {
            // db update
            category.setName(editTextCategoryName.getText().toString());
            category.setIncome(radioButtonIsIncome.isChecked());
            db.updateCategory(category);

            // nav back
            onBackPressed();
        });

        //endregion
    }

}