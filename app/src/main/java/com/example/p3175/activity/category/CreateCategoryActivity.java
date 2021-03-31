package com.example.p3175.activity.category;

import androidx.annotation.RequiresApi;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
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
}