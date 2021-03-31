package com.example.p3175.activity.bigexpense;

import androidx.annotation.RequiresApi;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;


public class EditBigExpenseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_big_expense);

        //region 0. VIEW

        EditText editTextAmount = findViewById(R.id.editTextEditBigExpenseAmount);
        EditText editTextDescription = findViewById(R.id.editTextEditBigExpenseDescription);
        Button buttonOK = findViewById(R.id.buttonEditBigExpenseOK);
        //endregion

        //region 2. VALIDATE INPUT


        //endregion

        //region 1. FILL DATA OF ITEM BEING EDITED


        //endregion



        //region 3. BUTTON
        buttonOK.setOnClickListener(v -> {
            // db update

            // nav back
            finish();
        });
        //endregion
    }


}