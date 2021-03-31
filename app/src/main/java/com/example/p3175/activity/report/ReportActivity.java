package com.example.p3175.activity.report;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.category.ManageCategoryActivity;
import com.example.p3175.activity.main.MainActivity;
import com.example.p3175.activity.overview.AddSavingsActivity;
import com.example.p3175.activity.recurringtransaction.ManageRecurringTransactionActivity;
import com.example.p3175.activity.user.EditUserActivity;
import com.example.p3175.activity.user.LoginActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ReportActivity extends BaseActivity {
    Spinner spinner;
    TextView textViewTitle;
    List<OnSpinnerItemSelectedListener> listeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        listeners = new ArrayList<>();

        //region 0. VIEW

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        textViewTitle = findViewById(R.id.textViewReportTitle);
        spinner = findViewById(R.id.spinnerMonth);
        //endregion

        //region 1. VIEW PAGER

        // disable swipe to avoid conflict with chart's swipe
        viewPager.setUserInputEnabled(false);

        // adapter
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                    default:
                        return new ReportTextFragment();
                    case 1:
                        return new ReportLineChartFragment();
                    case 2:
                        return new ReportPieChartFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        // mediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.label_tab_report_text));
                    break;
                case 1:
                    tab.setText(getString(R.string.label_tab_report_line_chart));
                    break;
                case 2:
                    tab.setText(getString(R.string.label_tab_report_pie_chart));
                    break;
            }
        }).attach();
        //endregion

        //region 2. SPINNER

        // array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // fill
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            adapter.add(now.plusMonths(-i).toString().substring(0, 7));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // change title
                textViewTitle.setText(getString(R.string.title_report, spinner.getSelectedItem()));

                // update all report fragments
                for (OnSpinnerItemSelectedListener listener : listeners) {
                    listener.onItemSelected((String) spinner.getSelectedItem());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setSelection(adapter.getCount() - 1);
        //endregion
    }

    public void addListener(OnSpinnerItemSelectedListener listener){
        listeners.add(listener);
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public TextView getTextViewTitle() {
        return textViewTitle;
    }

    // region. TOP RIGHT MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuItemHome) {
            startActivity(new Intent(this, MainActivity.class));
        }else if (itemId == R.id.menuItemCategory) {
            startActivity(new Intent(this, ManageCategoryActivity.class));
        } else if (itemId == R.id.menuItemSalaryBill) {
            startActivity(new Intent(this, ManageRecurringTransactionActivity.class));
        } else if (itemId == R.id.menuItemAddSavings) {
            startActivity(new Intent(this, AddSavingsActivity.class));
        } else if (itemId == R.id.menuItemReport) {
            startActivity(new Intent(this, ReportActivity.class));
        } else if (itemId == R.id.menuItemAccount) {
            startActivity(new Intent(this, EditUserActivity.class));
        } else if (itemId == R.id.menuItemLogout) {
            // remove logged in tag in the shared pref
            editor.remove(getString(R.string.logged_in_user_id)).commit();

            // nav to login activity, unable to nav back
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}