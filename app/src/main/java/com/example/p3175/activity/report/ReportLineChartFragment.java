package com.example.p3175.activity.report;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseFragment;
import com.example.p3175.db.entity.Transaction;
import com.example.p3175.util.Converter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ReportLineChartFragment extends BaseFragment implements OnSpinnerItemSelectedListener {

    ReportActivity reportActivity;
    LineChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_line_chart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chart = activity.findViewById(R.id.lineChart);

        reportActivity = (ReportActivity) activity;
        reportActivity.addListener(this);
        onItemSelected((String) reportActivity.getSpinner().getSelectedItem());
    }

    @Override
    public void onItemSelected(String yearMonth) {

        // get all transactions in this month
        List<Transaction> transactions = db.listTransactionsByUserIdYearMonth(currentUserId, yearMonth);

        // prepare each day's data from transactions
        LocalDate date = Converter.stringToLocalDate(yearMonth + "-01");
        int monthLength = date.lengthOfMonth();

        float[] incomesEachDay = new float[monthLength];
        float[] expensesEachDay = new float[monthLength];
        Arrays.fill(incomesEachDay, 0);
        Arrays.fill(expensesEachDay, 0);

        for (Transaction t : transactions) {                    // put each day's sum to the array
            float amount = t.getAmount().floatValue();
            int index = t.getDate().getDayOfMonth() - 1;

            if (amount > 0) {
                incomesEachDay[index] += amount;
            } else {
                expensesEachDay[index] += -amount;
            }
        }

        // convert each day's data to entries for chart
        List<Entry> entriesIncome = new ArrayList<>(monthLength);
        List<Entry> entriesExpense = new ArrayList<>(monthLength);

        for (int i = 0; i < monthLength; i++) {     // convert each day's data to entry
            entriesIncome.add(new Entry(i, incomesEachDay[i]));
            entriesExpense.add(new Entry(i, expensesEachDay[i]));
        }

        List<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet dataSet1 = new LineDataSet(entriesIncome, "Incomes");
        LineDataSet dataSet2 = new LineDataSet(entriesExpense, "Expenses");
        dataSet1.setColor(Color.GREEN);
        dataSet2.setColor(Color.RED);
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);

        chart.setData(new LineData(dataSets));
        chart.setVisibleXRangeMaximum(15);
        chart.invalidate();
    }
}