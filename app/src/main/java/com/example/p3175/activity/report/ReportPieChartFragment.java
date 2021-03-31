package com.example.p3175.activity.report;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseFragment;
import com.example.p3175.db.entity.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReportPieChartFragment extends BaseFragment implements OnSpinnerItemSelectedListener {

    ReportActivity reportActivity;
    PieChart chartIncome, chartExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_pie_chart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chartIncome = activity.findViewById(R.id.pieChartIncome);
        chartExpense = activity.findViewById(R.id.pieChartExpense);

        reportActivity = (ReportActivity) activity;
        reportActivity.addListener(this);
        onItemSelected(reportActivity.getSpinner().getSelectedItem().toString());
    }

    @Override
    public void onItemSelected(String yearMonth) {

        // get all transactions in this month
        List<Transaction> transactions = db.listTransactionsByUserIdYearMonth(currentUserId, yearMonth);

        // calculate sum for income / expense / each category
        float incomeTotalAmount = 0;
        float expenseTotalAmount = 0;
        HashMap<Integer, Float> amountPerIncomeCategory = new HashMap<>();    // categoryId, totalAmount
        HashMap<Integer, Float> amountPerExpenseCategory = new HashMap<>();    // categoryId, totalAmount

        for (Transaction t : transactions) {
            float amount = t.getAmount().floatValue();
            int categoryId = t.getCategoryId();

            if (amount > 0) {
                incomeTotalAmount += amount;
                if (amountPerIncomeCategory.containsKey(categoryId)) {
                    amountPerIncomeCategory.put(categoryId, amountPerIncomeCategory.get(categoryId) + amount);
                } else {
                    amountPerIncomeCategory.put(categoryId, amount);
                }
            } else {
                expenseTotalAmount -= amount;
                if (amountPerExpenseCategory.containsKey(categoryId)) {
                    amountPerExpenseCategory.put(categoryId, amountPerExpenseCategory.get(categoryId) - amount);
                } else {
                    amountPerExpenseCategory.put(categoryId, -amount);
                }
            }
        }

        // convert to chart entry
        List<PieEntry> entriesIncome = new ArrayList<>();
        List<PieEntry> entriesExpense = new ArrayList<>();

        for (Map.Entry<Integer, Float> entry : amountPerIncomeCategory.entrySet()) {
            entriesIncome.add(new PieEntry(entry.getValue() / incomeTotalAmount, db.selectCategory(entry.getKey()).getName()));
        }
        for (Map.Entry<Integer, Float> entry : amountPerExpenseCategory.entrySet()) {
            entriesExpense.add(new PieEntry(entry.getValue() / expenseTotalAmount, db.selectCategory(entry.getKey()).getName()));
        }

        PieDataSet dataSet1 = new PieDataSet(entriesIncome, "Incomes");
        PieDataSet dataSet2 = new PieDataSet(entriesExpense, "Expenses");
        dataSet1.setColors(
                Color.rgb(235, 67, 52),
                Color.rgb(27, 204, 118),
                Color.rgb(235, 201, 52),
                Color.rgb(19, 101, 189),
                Color.rgb(235, 110, 52),
                Color.rgb(65, 38, 199)
                );
        dataSet2.setColors(
                Color.rgb(19, 101, 189),
                Color.rgb(235, 201, 52),
                Color.rgb(65, 38, 199),
                Color.rgb(235, 67, 52),
                Color.rgb(27, 204, 118),
                Color.rgb(235, 110, 52)
        );
        chartIncome.setData(new PieData(dataSet1));
        chartExpense.setData(new PieData(dataSet2));
        chartIncome.invalidate();
        chartExpense.invalidate();
    }
}