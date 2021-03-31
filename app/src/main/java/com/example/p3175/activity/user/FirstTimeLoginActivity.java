package com.example.p3175.activity.user;

import androidx.annotation.RequiresApi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.main.MainActivity;
import com.example.p3175.util.Converter;


public class FirstTimeLoginActivity extends BaseActivity {

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
        Button buttonOK = findViewById(R.id.buttonEditUserOK);

        textViewTitle.setText(getString(R.string.title_first_login_set_password));
        editTextEmail.setVisibility(View.GONE);
        editTextOldPassword.setVisibility(View.GONE);

        //endregion

        //region 1. VALIDATE INPUT
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonOK.setEnabled(editTextPassword.getText().toString().length() >= 4
                        && editTextPassword.getText().toString().equals(editTextVerifyPassword.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextPassword.addTextChangedListener(textWatcher);
        editTextVerifyPassword.addTextChangedListener(textWatcher);

        //endregion

        //region 2. BUTTON

        buttonOK.setOnClickListener(v -> {
            // db update
            currentUser.setPassword(Converter.toMd5(editTextPassword.getText().toString()));
            db.updateUser(currentUser);

            // remove the mark in the shared pref
            editor.remove(getString(R.string.need_change_password) + currentUserId).apply();

            // nav to main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        //endregion
    }
}