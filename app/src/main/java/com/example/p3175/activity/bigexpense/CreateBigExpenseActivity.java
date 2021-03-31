package com.example.p3175.activity.bigexpense;

import androidx.annotation.RequiresApi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.util.Converter;

import java.math.BigDecimal;


public class CreateBigExpenseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_big_expense);

        //region 0. VIEW

        EditText editTextAmount = findViewById(R.id.editTextEditBigExpenseAmount);
        EditText editTextDescription = findViewById(R.id.editTextEditBigExpenseDescription);
        Button buttonOK = findViewById(R.id.buttonEditBigExpenseOK);
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

        //region 2. BUTTON
        buttonOK.setOnClickListener(v -> {
            // nav to get plan activity
            Intent intent = new Intent(this, GetBigExpensePlanActivity.class);
            intent.putExtra(getString(R.string.amount), Converter.bigDecimalToLong(new BigDecimal(editTextAmount.getText().toString())));
            intent.putExtra(getString(R.string.description), editTextDescription.getText().toString());
            startActivity(intent);
        });
        //endregion
    }
}