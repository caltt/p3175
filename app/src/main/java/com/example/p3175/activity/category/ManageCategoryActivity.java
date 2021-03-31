package com.example.p3175.activity.category;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.transaction.CreateTransactionActivity;
import com.example.p3175.adapter.CategoryAdapter;
import com.example.p3175.adapter.OnClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ManageCategoryActivity extends BaseActivity {

    CategoryAdapter adapterIncome, adapterExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        //region 0. VIEW

        RecyclerView recyclerViewIncome = findViewById(R.id.recyclerViewIncomeCategories);
        RecyclerView recyclerViewExpense = findViewById(R.id.recyclerViewExpenseCategories);
        FloatingActionButton buttonAdd = findViewById(R.id.floatingActionButtonToAddCategory);
        //endregion

        //region 1. RECYCLER VIEW

        adapterIncome = new CategoryAdapter();
        adapterExpense = new CategoryAdapter();
        OnClickListener onClickListener = id -> {   // onclick handler
            Intent intent = new Intent(this, EditCategoryActivity.class);
            intent.putExtra(getString(R.string.category_id), id);
            startActivity(intent);
        };
        adapterIncome.setOnClickListener(onClickListener);
        adapterExpense.setOnClickListener(onClickListener);

        recyclerViewIncome.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExpense.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewIncome.setAdapter(adapterIncome);
        recyclerViewExpense.setAdapter(adapterExpense);
        //endregion

        //region 2. BUTTON CREATE
        buttonAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateCategoryActivity.class));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // refresh the list here
        adapterIncome.submitList(db.listCategories(true));
        adapterExpense.submitList(db.listCategories(false));
    }
}