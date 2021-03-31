package com.example.p3175.activity.main;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseFragment;
import com.example.p3175.adapter.BigExpenseAdapter;


public class BigExpensePlannerFragment extends BaseFragment {
    BigExpenseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_big_expense_planner, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // RECYCLER VIEW
        adapter = new BigExpenseAdapter(false);
        RecyclerView recyclerView = activity.findViewById(R.id.recyclerViewBigExpense);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        // REFRESH LIST
        adapter.submitList(db.listBigExpensesByUserId(currentUserId));
    }


}