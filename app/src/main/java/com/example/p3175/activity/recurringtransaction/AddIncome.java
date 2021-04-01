package com.example.p3175.activity.recurringtransaction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.db.entity.RecurringTransaction;
import com.example.p3175.util.Converter;

public class AddIncome extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        EditText editTextAmount = findViewById(R.id.editTextEditRecurringTransactionAmount);
        EditText editTextDayOfMonth = findViewById(R.id.editTextEditRecurringTransactionDayOfMonth);
        EditText editTextDescription = findViewById(R.id.editTextEditRecurringTransactionDescription);
        Button buttonOK = findViewById(R.id.buttonEditRecurringTransactionOK);
        RadioButton radioButtonIsIncome = findViewById(R.id.radioButtonIsIncome);


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


        //region 2. BUTTON
        buttonOK.setOnClickListener(v -> {
            // db insert
            db.insertRecurringTransaction(new RecurringTransaction(
                    currentUserId,
                    radioButtonIsIncome.isChecked()
                            ? Converter.stringToBigDecimal(editTextAmount.getText().toString())
                            : Converter.stringToBigDecimal(editTextAmount.getText().toString()).negate(),
                    Integer.parseInt(editTextDayOfMonth.getText().toString()),
                    editTextDescription.getText().toString()));

            // nav back
            onBackPressed();
        });
        //endregion
    }
}