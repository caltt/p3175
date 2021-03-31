package com.example.p3175.activity.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.main.MainActivity;
import com.example.p3175.db.entity.User;
import com.example.p3175.util.Converter;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //region 0. VIEW

        EditText editTextEmail = findViewById(R.id.editTextLoginEmail);
        EditText editTextPassword = findViewById(R.id.editTextLoginPassword);
        TextView textViewCreateAccount = findViewById(R.id.textViewCreateAccount);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        //endregion

        //region 1. IF ALREADY LOGGED IN, NAV TO MAIN, UNABLE TO NAV BACK

        if (currentUserId != -1) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        //endregion

        //region 2. LOGIN BUTTON

        buttonLogin.setOnClickListener(v -> {

            // try to retrieve a user from db by input email & password
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            User user = db.selectUserByEmailPassword(email, Converter.toMd5(password));

            if (user == null) {
                // login failed
                Toast.makeText(this, "Invalid email / password.", Toast.LENGTH_SHORT).show();
            } else {
                // login successful

                if (getString(R.string.admin_username).equals(email)) {
                    // as admin

                    // nav to admin page
                    startActivity(new Intent(this, ManageUserActivity.class));
                } else {
                    // as user

                    // record logged in user id in shared pref
                    currentUserId = user.getId();
                    editor.putInt(getString(R.string.logged_in_user_id), currentUserId).apply();

                    // nav to different pages depending on flag in shared pref
                    if (preferences.getBoolean(getString(R.string.need_change_password) + currentUserId, false)) {
                        // need change password

                        // nav to first time login activity
                        startActivity(new Intent(this, FirstTimeLoginActivity.class));
                    } else {
                        // nav to main activity directly
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        // hide the keyboard
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        });
        //endregion

        //region 3. CREATE ACCOUNT BUTTON

        textViewCreateAccount.setOnClickListener(v -> {
            // nav to edit user activity
            Intent intent = new Intent(this, CreateUserActivity.class);
            startActivity(intent);

            // hide the keyboard
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });
        //endregion

    }
}