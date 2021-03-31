package com.example.p3175.activity.overview;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.main.MainActivity;
import com.example.p3175.util.Converter;

import java.math.BigDecimal;


public class AddSavingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_savings);

        //region 0. VIEW

        EditText editTextAmount = findViewById(R.id.editTextAddSavingsAmount);
        TextView textViewIncomes = findViewById(R.id.textViewAddSavingsCurrentIncomesAmount);
        Switch switchFromIncomes = findViewById(R.id.switchAddSavingsFromIncomes);
        Button buttonOK = findViewById(R.id.buttonAddSavingsOK);
        buttonOK.setEnabled(false);
        //endregion

        //region 2. FILL DATA

        textViewIncomes.setText(Converter.bigDecimalToString(currentOverview.getIncomes()));
        //endregion

        //region 3. VALIDATE INPUT

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


        //region 4. BUTTON
        buttonOK.setOnClickListener(v -> {
            BigDecimal amount = Converter.stringToBigDecimal(editTextAmount.getText().toString());
            BigDecimal incomes = currentOverview.getIncomes();
            boolean isFromIncomes = switchFromIncomes.isChecked();

            // move from income to savings
            if (isFromIncomes && amount.compareTo(incomes) > 0) {
                Toast.makeText(this, "Insufficient incomes", Toast.LENGTH_SHORT).show();
            } else {
                if (isFromIncomes) {
                    // deduct from incomes
                    currentOverview.setIncomes(incomes.subtract(amount));
                }
                // db update
                currentOverview.setSavings(currentOverview.getSavings().add(amount));
                db.updateOverview(currentOverview);

                // nav back
                Toast.makeText(this, "Done.", Toast.LENGTH_SHORT).show();
//                onBackPressed();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //endregion
    }
}