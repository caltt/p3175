package com.example.p3175.activity.user;

import androidx.annotation.RequiresApi;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.util.Converter;


public class EditUserActivity extends BaseActivity {

    private boolean isOldPasswordValid = false;
    private boolean isPasswordValid = false;
    private boolean isVerifyPasswordValid = false;


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
//        buttonOK.setEnabled(false);
        //endregion

        //region 1. FILL DATA OF ITEM BEING EDITED

        editTextEmail.setText(currentUser.getEmail());
        //endregion

        //region 2. VALIDATE INPUT

        editTextOldPassword.setOnFocusChangeListener((v, hasFocus) -> {
            String txtOldPassword = editTextOldPassword.getText().toString();
            if (!hasFocus) {
                if (txtOldPassword.isEmpty()) {
                    editTextOldPassword.setError("Old password cannot be blank.");
                } else {
                    isOldPasswordValid = true;
                }
            }
        });
        editTextPassword.setOnFocusChangeListener((v, hasFocus) -> {
            String txtPassword = editTextPassword.getText().toString();
            if (!hasFocus) {
                if (txtPassword.isEmpty()) {
                    editTextPassword.setError("Password cannot be blank.");
                } else {
                    isPasswordValid = true;
                }
            }
        });
        editTextVerifyPassword.setOnFocusChangeListener((v, hasFocus) -> {
            String txtVerifyPassword = editTextVerifyPassword.getText().toString();
            if (!hasFocus) {
                if (txtVerifyPassword.isEmpty()) {
                    editTextVerifyPassword.setError("Verify password cannot be blank.");
                } else {
                    isVerifyPasswordValid = true;
                }
            }
        });
        //endregion

        //region 3. BUTTON

        buttonOK.setOnClickListener(v -> {
            editTextOldPassword.clearFocus();
            editTextPassword.clearFocus();
            editTextVerifyPassword.clearFocus();

            if (!isOldPasswordValid || !isPasswordValid || !isVerifyPasswordValid) {
                Toast.makeText(this, "Please make sure all inputs are valid.", Toast.LENGTH_SHORT).show();
            } else {
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
            }
        });
        //endregion
    }
}