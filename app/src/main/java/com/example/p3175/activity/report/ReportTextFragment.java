package com.example.p3175.activity.report;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseFragment;
import com.example.p3175.util.Converter;
import com.example.p3175.util.Report;

import java.math.BigDecimal;
import java.util.Map;


public class ReportTextFragment extends BaseFragment implements OnSpinnerItemSelectedListener{
    ReportActivity reportActivity;
    TableLayout table;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_text, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        reportActivity = (ReportActivity) activity;
        reportActivity.addListener(this);

        //region 0. VIEW

        table = activity.findViewById(R.id.tableLayout);
        table.setColumnStretchable(0, true);
        //endregion
    }

    @Override
    public void onItemSelected(String yearMonth) {

        // clear the table
        table.removeAllViews();

        // calculate
        Report report = Report.getReport(db, currentUserId, yearMonth);

        // table
        addRow(getString(R.string.label_total_incomes), Converter.bigDecimalToString(report.getTotalIncomes()));
        addRow(getString(R.string.label_total_expenses), Converter.bigDecimalToString(report.getTotalExpenses()));
        addDivider();

        for (Map.Entry<String, BigDecimal> entry : report.getAmountForIncomeCategories().entrySet()) {
            addRow(entry.getKey(), Converter.bigDecimalToString(entry.getValue()));
        }
        addDivider();
        for (Map.Entry<String, BigDecimal> entry : report.getAmountForExpenseCategories().entrySet()) {
            addRow(entry.getKey(), Converter.bigDecimalToString(entry.getValue()));
        }
    }

    private void addRow(String label, String value) {
        // add a row
        TableRow row = new TableRow(activity);
        row.setPadding(0, 8, 0, 8);
        table.addView(row);

        // add two cells to that row
        TextView cell = new TextView(activity);
        cell.setText(label);
        cell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        row.addView(cell);

        cell = new TextView(activity);
        cell.setText(value);
        cell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        cell.setGravity(Gravity.END);
        row.addView(cell);
    }

    private void addDivider() {
        View divider = new View(activity);
        divider.setBackgroundColor(Color.GRAY);
        divider.setLayoutParams(new TableLayout.LayoutParams(0, 1));
        table.addView(divider);
    }
}