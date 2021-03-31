package com.example.p3175.activity.user;

import androidx.annotation.RequiresApi;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.util.Converter;


public class EditUserActivity extends BaseActivity {

    boolean isPasswordValid, isEmailValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        //region 0. VIEW

        TextView textViewTitle = findViewById(R.id.textViewEditUserTitle);
        textViewTitle.setText(getString(R.string.title_edit_account));
        EditText editTextEmail = findViewById(R.id.editTextEditUserEmail);
        editTextEmail.setEnabled(false);
        EditText editTextPassword = findViewById(R.id.editTextEditUserPassword);
        EditText editTextVerifyPassword = findViewById(R.id.editTextEditUserVerifyPassword);
        EditText editTextOldPassword = findViewById(R.id.editTextEditUserOldPassword);
        Button buttonOK = findViewById(R.id.buttonEditUserOK);
        buttonOK.setEnabled(false);
        //endregion

        //region 2. VALIDATE INPUT

        TextWatcher textWatcher = new TextWatcher() {
            String password, verifyPassword, oldPassword;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                oldPassword = editTextOldPassword.getText().toString();
                password = editTextPassword.getText().toString();
                verifyPassword = editTextVerifyPassword.getText().toString();

                buttonOK.setEnabled(password.length() >= 4 && password.equals(verifyPassword) && !oldPassword.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextOldPassword.addTextChangedListener(textWatcher);
        editTextPassword.addTextChangedListener(textWatcher);
        editTextVerifyPassword.addTextChangedListener(textWatcher);
        //endregion

        //region 1. FILL DATA OF ITEM BEING EDITED

        editTextEmail.setText(currentUser.getEmail());
        //endregion


        //region 3. BUTTON

        buttonOK.setOnClickListener(v -> {
            String oldPassword = editTextOldPassword.getText().toString();
            String password = editTextPassword.getText().toString();

            // check old password
            if (!currentUser.getPassword().equals(Converter.toMd5(oldPassword))) {
                Toast.makeText(this, "Incorrect old password", Toast.LENGTH_SHORT).show();
            } else {
                // db update
                currentUser.setPassword(Converter.toMd5(password));
                db.updateUser(currentUser);

                // nav back
                Toast.makeText(this, "Done.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
        //endregion
    }
}