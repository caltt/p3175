package com.example.p3175.activity.main;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.p3175.R;
import com.example.p3175.activity.base.BaseActivity;
import com.example.p3175.activity.bigexpense.CreateBigExpenseActivity;
import com.example.p3175.activity.bigexpense.EditBigExpenseActivity;
import com.example.p3175.activity.category.ChooseTransactionCategoryActivity;
import com.example.p3175.activity.category.ManageCategoryActivity;
import com.example.p3175.activity.overview.AddSavingsActivity;
import com.example.p3175.activity.recurringtransaction.ManageRecurringTransactionActivity;
import com.example.p3175.activity.report.ReportActivity;
import com.example.p3175.activity.user.EditUserActivity;
import com.example.p3175.activity.user.LoginActivity;
import com.example.p3175.db.entity.RecurringTransaction;
import com.example.p3175.util.Converter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region 1. BOTTOM NAVIGATION BAR

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.navHostFragmentBottomNavBar);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(bottomNavigationView.getMenu()).build();

        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setOnNavigationItemReselectedListener(item -> {
            // disable reselect current tab
        });
        //endregion

        //region 2. FLOATING BUTTON

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            int currentFragmentId = Objects.requireNonNull(navController.getCurrentDestination()).getId();

            if (currentFragmentId == R.id.expenseTrackerFragment) {

                // nav to category activity for adding transaction
                startActivity(new Intent(this, ChooseTransactionCategoryActivity.class));

            } else if (currentFragmentId == R.id.bigExpensePlannerFragment) {

                // nav to edit big expense activity for adding big expense
                startActivity(new Intent(this, CreateBigExpenseActivity.class));
            }
        });
        // endregion

        //region 3. ALARM

//        // alarm
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 9);
//
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        intent.putExtra("currentUserId", currentUserId);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//
//        Button buttonTest = findViewById(R.id.buttonTest);
//        buttonTest.setOnClickListener(v -> {
//            long timeClick = System.currentTimeMillis();
//            long inOneSec = 1000;
//            alarmManager.set(AlarmManager.RTC_WAKEUP, timeClick+inOneSec, pendingIntent);
//        });
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    // region 4. TOP RIGHT MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuItemCategory) {
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
    //endregion


    @Override
    public void onBackPressed() {
        finish();
    }

    //region 5. NOTIFICATION

//    public static class AlarmReceiver extends BroadcastReceiver{
//        private String CHANNEL_ID = "channel";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int currentUserId = intent.getIntExtra("currentUserId", -1);
//
//            // notification
//            List<RecurringTransaction> recurringTransactions
//                    = db.listRecurringTransactionsByUserIdDayOfMonth(currentUserId, LocalDate.now().getDayOfMonth());
//            StringBuilder content = new StringBuilder();
//            for (RecurringTransaction rt : recurringTransactions) {
//                content.append(rt.getDescription()).append(" ").append(Converter.bigDecimalToString(rt.getAmount())).append("\n");
//            }
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                    .setSmallIcon(R.drawable.ic_baseline_attach_money_24)
//                    .setContentTitle(context.getString(R.string.notification_title))
//                    .setContentText(content)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//            // notification channel
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                CharSequence name = context.getString(R.string.channel_name);
//                String description = context.getString(R.string.channel_description);
//                int importance = NotificationManager.IMPORTANCE_DEFAULT;
//                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//                channel.setDescription(description);
//                // Register the channel with the system; you can't change the importance
//                // or other notification behaviors after this
//                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//                notificationManager.createNotificationChannel(channel);
//            }
//
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//            notificationManager.notify(1, builder.build());
//        }
//    }
    //endregion
}