package com.example.p3175.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.db.entity.Overview;
import com.example.p3175.db.entity.User;
import com.example.p3175.util.Converter;

import java.math.BigDecimal;
import java.util.regex.Pattern;


public class CreateUserActivity extends BaseActivity {

    private boolean isEmailValid = false;
    private boolean isPasswordValid = false;
    private boolean isVerifyPasswordValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        //region 0. VIEW

        TextView textViewTitle = findViewById(R.id.textViewEditUserTitle);
        EditText editTextEmail = findViewById(R.id.editTextEditUserEmail);
        EditText editTextPassword = findViewById(R.id.editTextEditUserPassword);
        EditText editTextVerifyPassword = findViewById(R.id.editTextEditUserVerifyPassword);
        EditText editTextOldPassword = findViewById(R.id.editTextEditUserOldPassword);
        editTextOldPassword.setVisibility(View.GONE);
        Button buttonOK = findViewById(R.id.buttonEditUserOK);
//        buttonOK.setEnabled(false);
        //endregion

        //region 1. VALIDATE

        editTextEmail.setOnFocusChangeListener((v, hasFocus) -> {
            String txtEmail = editTextEmail.getText().toString();
            if (!hasFocus) {
                if (txtEmail.isEmpty()) {
                    editTextEmail.setError("Email cannot be blank.");
                } else if (!checkEmail(txtEmail)) {
                    editTextEmail.setError("Please enter valid email address.");
                } else {
                    isEmailValid = true;
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

        //region 2. BUTTON

        buttonOK.setOnClickListener(v -> {
            editTextEmail.clearFocus();
            editTextPassword.clearFocus();
            editTextVerifyPassword.clearFocus();

            if (!isEmailValid || !isPasswordValid || !isVerifyPasswordValid) {
                Toast.makeText(this, "Please make sure all inputs are valid.", Toast.LENGTH_SHORT).show();
            } else {

                // check email exists or not
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (db.selectUserByEmail(email) != null) {
                    // email exists

                    Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // email doesn't exist

                    // db insert: email
                    db.insertUser(new User(email, Converter.toMd5(password)));
                    int userId = db.selectUserByEmail(email).getId();

                    // db insert: overview
                    db.insertOverview(new Overview(userId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

                    // if previous activity is manage user activity (admin), mark "need change password" in shard pref
                    if (getIntent().getBooleanExtra("isCreatedByAdmin", false)) {
                        editor.putBoolean(getString(R.string.need_change_password) + userId, true).commit();
                    }

                    // nav to initialize money activity, hide keyboard
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Intent intent = new Intent(this, InitializeMoneyActivity.class);
                    intent.putExtra(getString(R.string.current_user_id), userId);
                    startActivity(intent);
                }
            }
        });
        //endregion
    }

    private boolean checkEmail(String email) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
}