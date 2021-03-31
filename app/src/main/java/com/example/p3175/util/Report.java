package com.example.p3175.util;

import com.example.p3175.db.DatabaseHelper;
import com.example.p3175.db.entity.Transaction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class Report {
    String yearMonth;
    private BigDecimal totalIncomes = BigDecimal.ZERO;
    private BigDecimal totalExpenses = BigDecimal.ZERO;
    private HashMap<String, BigDecimal> amountForIncomeCategories = new HashMap<>();
    private HashMap<String, BigDecimal> amountForExpenseCategories = new HashMap<>();

    public Report(String yearMonth, BigDecimal totalIncomes, BigDecimal totalExpenses, HashMap<String, BigDecimal> amountForIncomeCategories, HashMap<String, BigDecimal> amountForExpenseCategories) {
        this.yearMonth = yearMonth;
        this.totalIncomes = totalIncomes;
        this.totalExpenses = totalExpenses;
        this.amountForIncomeCategories = amountForIncomeCategories;
        this.amountForExpenseCategories = amountForExpenseCategories;
    }

    public static Report getReport(DatabaseHelper db, int userId, String yearMonth) {
        BigDecimal totalIncomes = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        HashMap<String, BigDecimal> amountForIncomeCategories = new HashMap<>();
        HashMap<String, BigDecimal> amountForExpenseCategories = new HashMap<>();

        List<Transaction> transactions = db.listTransactionsByUserIdYearMonth(userId, yearMonth);

        for (Transaction t : transactions) {
            BigDecimal amount = t.getAmount();
            String categoryName = db.selectCategory(t.getCategoryId()).getName();

            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                // total income & expense
                totalIncomes = totalIncomes.add(amount);

                // for each category
                if (amountForIncomeCategories.get(categoryName) == null) {
                    // if no such category, create
                    amountForIncomeCategories.put(categoryName, amount);
                } else {
                    // or update the amount for this category
                    amountForIncomeCategories.put(categoryName, amountForIncomeCategories.get(categoryName).add(amount));
                }
            } else {
                totalExpenses = totalExpenses.add(amount);

                // for each category
                if (amountForExpenseCategories.get(categoryName) == null) {
                    // if no such category, create
                    amountForExpenseCategories.put(categoryName, amount);
                } else {
                    // or update the amount for this category
                    amountForExpenseCategories.put(categoryName, amountForExpenseCategories.get(categoryName).add(amount));
                }
            }

        }

        return new Report(yearMonth, totalIncomes, totalExpenses, amountForIncomeCategories, amountForExpenseCategories);
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public BigDecimal getTotalIncomes() {
        return totalIncomes;
    }

    public void setTotalIncomes(BigDecimal totalIncomes) {
        this.totalIncomes = totalIncomes;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public HashMap<String, BigDecimal> getAmountForIncomeCategories() {
        return amountForIncomeCategories;
    }

    public void setAmountForIncomeCategories(HashMap<String, BigDecimal> amountForIncomeCategories) {
        this.amountForIncomeCategories = amountForIncomeCategories;
    }

    public HashMap<String, BigDecimal> getAmountForExpenseCategories() {
        return amountForExpenseCategories;
    }

    public void setAmountForExpenseCategories(HashMap<String, BigDecimal> amountForExpenseCategories) {
        this.amountForExpenseCategories = amountForExpenseCategories;
    }
}
