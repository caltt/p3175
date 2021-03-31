package com.example.p3175.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.p3175.db.entity.Overview;

import java.math.BigDecimal;
import java.time.LocalDate;


public class Calculator {

    /**
     * Reset today's allowed
     */
    public static void resetTodayAllowed(Overview overview, boolean addToSavings) {
        BigDecimal incomes = overview.getIncomes();
        BigDecimal savings = overview.getSavings();
        BigDecimal todayRemaining = overview.getTodayRemaining();
        LocalDate today = LocalDate.now();
        int remainingDaysThisMonth = today.lengthOfMonth() - today.getDayOfMonth() + 1;

        if (addToSavings) {
            incomes = incomes.subtract(todayRemaining);
            savings = savings.add(todayRemaining);
        }
        BigDecimal todayAllowed = incomes.divide(new BigDecimal(remainingDaysThisMonth), 2, BigDecimal.ROUND_HALF_UP);
        overview.setIncomes(incomes);
        overview.setSavings(savings);
        overview.setTodayAllowed(todayAllowed);
        overview.setTodayRemaining(todayAllowed);
        Log.d("tttt", "resetTodayAllowed: " + todayAllowed.toPlainString());
    }

    /**
     * Update overview after doing a transaction
     * <p>
     * 1. Income:
     * If savings < 0, go to savings until it reaches 0, the rest goes to incomes
     * When incomes change, today allowed changes accordingly
     * <p>
     * 2. Expense:
     * If incomes > 0, use incomes until it reaches 0, the rest is cost from savings
     * Change today remaining until it reaches 0
     */
    public static void updateIncomesSavings(Overview overview, BigDecimal amount) {
        BigDecimal incomes = overview.getIncomes();
        BigDecimal savings = overview.getSavings();

        if (amount.compareTo(BigDecimal.ZERO) >= 0) {
            // 1. FOR INCOME
            if (savings.compareTo(BigDecimal.ZERO) < 0) {
                // if savings < 0

                // go to savings first
                savings = savings.add(amount);

                // after savings = 0, the rest go to incomes
                if (savings.compareTo(BigDecimal.ZERO) > 0) {
                    incomes = incomes.add(savings);
                    savings = BigDecimal.ZERO;
                }
            } else {
                // if savings not < 0

                // all go to incomes
                incomes = incomes.add(amount);
            }

//            updateTodayRemainingAllowed(overview);

        } else {
            amount = amount.negate();   // make it positive

            if (incomes.compareTo(amount) >= 0) {
                // have enough incomes for this amount

                // cost from incomes
                incomes = incomes.subtract(amount);
            } else {
                // not have enough incomes for this amount

                // cost from incomes first
                amount = amount.subtract(incomes);
                incomes = BigDecimal.ZERO;

                // after incomes reach 0, cost the rest from savings
                savings = savings.subtract(amount);
            }

//            updateTodayRemaining(overview, amount);
        }

        // update overview
        overview.setIncomes(incomes);
        overview.setSavings(savings);
    }

    /**
     * Update today allowed & remaining after each transaction.
     * Different calculation for income & expense.
     */
    public static void updateTodayRemainingAllowed(Overview overview, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            // EXPENSE

            BigDecimal todayRemaining = overview.getTodayRemaining();

            // only cost from today remaining until it reaches 0
            todayRemaining = BigDecimal.ZERO.max(todayRemaining.add(amount));
            overview.setTodayRemaining(todayRemaining);

        } else {
            // INCOME: NOT MAKING ANY CHANGES TO AVOID INCORRECT INCOME NUMBER

            BigDecimal incomes = overview.getIncomes();
            BigDecimal todayRemaining = overview.getTodayRemaining();
            BigDecimal todayAllowed = overview.getTodayAllowed();
            LocalDate today = LocalDate.now();
            int remainingDaysThisMonth = today.lengthOfMonth() - today.getDayOfMonth() + 1;

            // re-calculate today's allowed and adjust today's remaining
//            BigDecimal newTodayAllowed = incomes.divide(new BigDecimal(remainingDaysThisMonth), 2, BigDecimal.ROUND_HALF_UP);
//            overview.setTodayAllowed(newTodayAllowed);
//            overview.setTodayRemaining(todayRemaining.add(newTodayAllowed.subtract(todayAllowed)));
        }
    }

    public static void updateIncomesSavings(Overview overview, BigDecimal incomes, BigDecimal savings){
        overview.setIncomes(overview.getIncomes().subtract(incomes));
        overview.setSavings(overview.getSavings().subtract(savings));
    }

}
