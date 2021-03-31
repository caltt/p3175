package com.example.p3175.activity.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.p3175.R;
import com.example.p3175.db.DatabaseHelper;
import com.example.p3175.db.entity.Overview;
import com.example.p3175.db.entity.User;

@SuppressLint("CommitPrefEdits")

public class BaseFragment extends Fragment {

    protected static String TAG = "tttt";

    protected static FragmentActivity activity;
    protected static SharedPreferences preferences;
    protected static SharedPreferences.Editor editor;
    protected static DatabaseHelper db;

    protected int currentUserId;
    protected User currentUser;
    protected Overview currentOverview;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = requireActivity();
        preferences = activity.getSharedPreferences(getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        editor = preferences.edit();

        db = DatabaseHelper.getInstance(activity);

        currentUserId = preferences.getInt(getString(R.string.logged_in_user_id), -1);
        currentUser = db.selectUser(currentUserId);
        currentOverview = db.selectOverviewByUserId(currentUserId);

    }
}