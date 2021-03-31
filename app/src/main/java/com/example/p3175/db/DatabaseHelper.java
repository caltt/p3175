package com.example.p3175.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.p3175.db.entity.BigExpense;
import com.example.p3175.db.entity.Category;
import com.example.p3175.db.entity.Overview;
import com.example.p3175.db.entity.RecurringTransaction;
import com.example.p3175.db.entity.Transaction;
import com.example.p3175.db.entity.User;
import com.example.p3175.util.Converter;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("DefaultLocale")
public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;

    private static SQLiteDatabase writableDB;
    private static SQLiteDatabase readableDB;

    private static final String DB_NAME = "p3175_database"; // can change this name
    private static final int DB_VERSION = 1;

    public static final String TABLE_USER = "user";
    public static final String TABLE_OVERVIEW = "overview";
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_TRANSACTION = "`transaction`";
    public static final String TABLE_RECURRING_TRANSACTION = "recurring_transaction";
    public static final String TABLE_BIG_EXPENSE = "big_expense";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_CATEGORY_ID = "category_id";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DAY_OF_MONTH = "day_of_month";

    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";

    public static final String COLUMN_OVERVIEW_TODAY_REMAINING = "today_remaining";
    public static final String COLUMN_OVERVIEW_TODAY_ALLOWED = "today_allowed";
    public static final String COLUMN_OVERVIEW_INCOMES = "incomes";
    public static final String COLUMN_OVERVIEW_SAVINGS = "savings";

    public static final String COLUMN_CATEGORY_IS_INCOME = "is_income";

    public static final String COLUMN_BIG_EXPENSE_INCOMES_NEEDED = "incomes_needed";
    public static final String COLUMN_BIG_EXPENSE_SAVINGS_NEEDED = "savings_needed";
    public static final String COLUMN_BIG_EXPENSE_LOAN_NEEDED = "loan_needed";


    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        readableDB = getReadableDatabase();
        writableDB = getWritableDatabase();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        fillInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        doDestructiveMigration(db);
        onCreate(db);
    }

    //region COMMON METHODS


    public int delete(String table, int id) {
        return writableDB.delete(table, "id = ?", toArgs(id));
    }
    //endregion

    //region USER

    public User selectUser(int id) {
        String query = String.format("SELECT * FROM %s WHERE id = %d", TABLE_USER, id);
        Cursor cursor = readableDB.rawQuery(query, null);
        if (cursor.moveToNext()) {
            User result = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2));
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    public User selectUserByEmail(String email) {
        String query = String.format("SELECT * FROM %s WHERE email = '%s'", TABLE_USER, email);
        Cursor cursor = readableDB.rawQuery(query, null);
        if (cursor.moveToNext()) {
            User result = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2));
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    public User selectUserByEmailPassword(String email, String password) {
        String query = String.format("SELECT * FROM %s WHERE email = ? AND password = ?", TABLE_USER);
        Cursor cursor = readableDB.rawQuery(query, toArgs(email, password));
        if (cursor.moveToNext()) {
            User result = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2));
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    public List<User> listUsers() {
        String query = String.format("SELECT * FROM %s", TABLE_USER);
        Cursor cursor = readableDB.rawQuery(query, null);

//        if (cursor.getCount() == 0) {
//            cursor.close();
//            return null;
//        }

        List<User> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)));
        }
        cursor.close();
        return results;
    }

    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        return writableDB.insert(TABLE_USER, null, values);
    }

    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        return writableDB.update(TABLE_USER, values, "id = ?", toArgs(user.getId()));
    }
    //endregion

    //region OVERVIEW
    public Overview selectOverview(int id) {
        String query = String.format("SELECT * FROM %s WHERE id = %d", TABLE_OVERVIEW, id);
        Cursor cursor = readableDB.rawQuery(query, null);
        if (cursor.moveToNext()) {
            Overview result = new Overview(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    Converter.longToBigDecimal(cursor.getLong(2)),
                    Converter.longToBigDecimal(cursor.getLong(3)),
                    Converter.longToBigDecimal(cursor.getLong(4)),
                    Converter.longToBigDecimal(cursor.getLong(5)));
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    public Overview selectOverviewByUserId(int userId) {
        String query = String.format("SELECT * FROM %s WHERE user_id = %d", TABLE_OVERVIEW, userId);
        Cursor cursor = readableDB.rawQuery(query, null);
        if (cursor.moveToNext()) {
            Overview result = new Overview(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    Converter.longToBigDecimal(cursor.getLong(2)),
                    Converter.longToBigDecimal(cursor.getLong(3)),
                    Converter.longToBigDecimal(cursor.getLong(4)),
                    Converter.longToBigDecimal(cursor.getLong(5)));
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }


    public long insertOverview(Overview overview) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, overview.getUserId());
        values.put(COLUMN_OVERVIEW_INCOMES, Converter.bigDecimalToLong(overview.getIncomes()));
        values.put(COLUMN_OVERVIEW_SAVINGS, Converter.bigDecimalToLong(overview.getSavings()));
        values.put(COLUMN_OVERVIEW_TODAY_ALLOWED, Converter.bigDecimalToLong(overview.getTodayAllowed()));
        values.put(COLUMN_OVERVIEW_TODAY_REMAINING, Converter.bigDecimalToLong(overview.getTodayRemaining()));
        return writableDB.insert(TABLE_OVERVIEW, null, values);
    }

    public int updateOverview(Overview overview) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, overview.getUserId());
        values.put(COLUMN_OVERVIEW_INCOMES, Converter.bigDecimalToLong(overview.getIncomes()));
        values.put(COLUMN_OVERVIEW_SAVINGS, Converter.bigDecimalToLong(overview.getSavings()));
        values.put(COLUMN_OVERVIEW_TODAY_ALLOWED, Converter.bigDecimalToLong(overview.getTodayAllowed()));
        values.put(COLUMN_OVERVIEW_TODAY_REMAINING, Converter.bigDecimalToLong(overview.getTodayRemaining()));
        return writableDB.update(TABLE_OVERVIEW, values, "id = ?", toArgs(overview.getId()));
    }
    //endregion

    //region CATEGORY
    public Category selectCategory(int id) {
        String query = String.format("SELECT * FROM %s WHERE id = %d", TABLE_CATEGORY, id);
        Cursor cursor = readableDB.rawQuery(query, null);
        if (cursor.moveToNext()) {
            Category result = new Category(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2) == 1);
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    public List<Category> listCategories(boolean isIncome) {
        String query = String.format("SELECT * FROM %s WHERE id != 1 AND is_income = %d", TABLE_CATEGORY, isIncome ? 1 : 0);
        Cursor cursor = readableDB.rawQuery(query, null);

//        if (cursor.getCount() == 0) {
//            cursor.close();
//            return null;
//        }

        List<Category> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(new Category(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2) == 1));
        }
        cursor.close();
        return results;
    }

    public long insertCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, category.getName());
        values.put(COLUMN_CATEGORY_IS_INCOME, category.isIncome() ? 1 : 0);
        return writableDB.insert(TABLE_CATEGORY, null, values);
    }

    public int updateCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, category.getName());
        values.put(COLUMN_CATEGORY_IS_INCOME, category.isIncome() ? 1 : 0);
        return writableDB.update(TABLE_CATEGORY, values, "id = ?", toArgs(category.getId()));
    }
    //endregion

    //region TRANSACTION
    public Transaction selectTransaction(int id) {
        String query = String.format("SELECT * FROM %s WHERE id = %d", TABLE_TRANSACTION, id);
        Cursor cursor = readableDB.rawQuery(query, null);
        if (cursor.moveToNext()) {
            Transaction result = new Transaction(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    Converter.longToBigDecimal(cursor.getLong(3)),
                    Converter.stringToLocalDate(cursor.getString(4)),
                    cursor.getString(5));
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    public List<Transaction> listTransactionsByUserId(int userId) {
        String query = String.format("SELECT * FROM %s WHERE user_id = %d ORDER BY date DESC, id DESC", TABLE_TRANSACTION, userId);
        Cursor cursor = readableDB.rawQuery(query, null);

//        if (cursor.getCount() == 0) {
//            cursor.close();
//            return null;
//        }

        List<Transaction> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(new Transaction(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    Converter.longToBigDecimal(cursor.getLong(3)),
                    Converter.stringToLocalDate(cursor.getString(4)),
                    cursor.getString(5)));
        }
        cursor.close();
        return results;
    }

    /**
     * Select a user's all transaction during a given month
     */
    public List<Transaction> listTransactionsByUserIdYearMonth(int userId, String yearMonth) {
        String query = String.format(
                "SELECT * FROM %s WHERE user_id = %d AND date LIKE '%s'",
                TABLE_TRANSACTION, userId, yearMonth + "%");
        Cursor cursor = readableDB.rawQuery(query, null);

//        if (cursor.getCount() == 0) {
//            cursor.close();
//            return null;
//        }

        List<Transaction> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(new Transaction(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    Converter.longToBigDecimal(cursor.getLong(3)),
                    Converter.stringToLocalDate(cursor.getString(4)),
                    cursor.getString(2)));
        }
        cursor.close();
        return results;
    }

    public long insertTransaction(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, transaction.getUserId());
        values.put(COLUMN_CATEGORY_ID, transaction.getCategoryId());
        values.put(COLUMN_AMOUNT, Converter.bigDecimalToLong(transaction.getAmount()));
        values.put(COLUMN_DATE, Converter.localDateToString(transaction.getDate()));
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        return writableDB.insert(TABLE_TRANSACTION, null, values);
    }

    public int updateTransaction(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, transaction.getUserId());
        values.put(COLUMN_CATEGORY_ID, transaction.getCategoryId());
        values.put(COLUMN_AMOUNT, Converter.bigDecimalToLong(transaction.getAmount()));
        values.put(COLUMN_DATE, Converter.localDateToString(transaction.getDate()));
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        return writableDB.update(TABLE_TRANSACTION, values, "id = ?", toArgs(transaction.getId()));
    }
    //endregion

    //region RECURRING TRANSACTION
    public RecurringTransaction selectRecurringTransaction(int id) {
        String query = String.format("SELECT * FROM %s WHERE id = %d", TABLE_RECURRING_TRANSACTION, id);
        Cursor cursor = readableDB.rawQuery(query, null);
        if (cursor.moveToNext()) {
            RecurringTransaction result = new RecurringTransaction(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    Converter.longToBigDecimal(cursor.getLong(2)),
                    cursor.getInt(3),
                    cursor.getString(4));
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    public List<RecurringTransaction> listRecurringTransactionsByUserId(int userId, boolean isIncome) {
        String query = String.format("SELECT * FROM %s WHERE user_id = %d AND amount %s 0 ORDER BY day_of_month ASC",
                TABLE_RECURRING_TRANSACTION, userId, isIncome ? ">" : "<");
        Cursor cursor = readableDB.rawQuery(query, null);

//        if (cursor.getCount() == 0) {
//            cursor.close();
//            return null;
//        }

        List<RecurringTransaction> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(new RecurringTransaction(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    Converter.longToBigDecimal(cursor.getLong(2)),
                    cursor.getInt(3),
                    cursor.getString(4)));
        }
        cursor.close();
        return results;
    }

    /**
     * Select all recurring transactions on a given day of month
     */
    public List<RecurringTransaction> listRecurringTransactionsByUserIdDayOfMonth(int userId, int dayOfMonth) {
        String query = String.format(
                "SELECT * FROM %s WHERE user_id = %d AND day_of_month = %d",
                TABLE_RECURRING_TRANSACTION, userId, dayOfMonth);
        Cursor cursor = readableDB.rawQuery(query, null);

//        if (cursor.getCount() == 0) {
//            cursor.close();
//            return null;
//        }

        List<RecurringTransaction> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(new RecurringTransaction(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    Converter.longToBigDecimal(cursor.getLong(2)),
                    cursor.getInt(3),
                    cursor.getString(4)));
        }
        cursor.close();
        return results;
    }

    public long insertRecurringTransaction(RecurringTransaction transaction) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, transaction.getUserId());
        values.put(COLUMN_AMOUNT, Converter.bigDecimalToLong(transaction.getAmount()));
        values.put(COLUMN_DAY_OF_MONTH, transaction.getDayOfMonth());
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        return writableDB.insert(TABLE_RECURRING_TRANSACTION, null, values);
    }

    public int updateRecurringTransaction(RecurringTransaction transaction) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, transaction.getUserId());
        values.put(COLUMN_AMOUNT, Converter.bigDecimalToLong(transaction.getAmount()));
        values.put(COLUMN_DAY_OF_MONTH, transaction.getDayOfMonth());
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        return writableDB.update(TABLE_RECURRING_TRANSACTION, values, "id = ?", toArgs(transaction.getId()));
    }
    //endregion

    //region BIG EXPENSE
    public BigExpense selectBigExpense(int id) {
        String query = String.format("SELECT * FROM %s WHERE id = %d", TABLE_BIG_EXPENSE, id);
        Cursor cursor = readableDB.rawQuery(query, null);
        if (cursor.moveToNext()) {
            BigExpense result = new BigExpense(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    Converter.longToBigDecimal(cursor.getLong(2)),
                    Converter.stringToLocalDate(cursor.getString(3)),
                    cursor.getString(4),
                    Converter.longToBigDecimal(cursor.getLong(5)),
                    Converter.longToBigDecimal(cursor.getLong(6)),
                    Converter.longToBigDecimal(cursor.getLong(7)));
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    public List<BigExpense> listBigExpensesByUserId(int userId) {
        String query = String.format("SELECT * FROM %s WHERE user_id = %d ORDER BY date DESC, id DESC",
                TABLE_BIG_EXPENSE, userId);
        Cursor cursor = readableDB.rawQuery(query, null);

//        if (cursor.getCount() == 0) {
//            cursor.close();
//            return null;
//        }

        List<BigExpense> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(new BigExpense(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    Converter.longToBigDecimal(cursor.getLong(2)),
                    Converter.stringToLocalDate(cursor.getString(3)),
                    cursor.getString(4),
                    Converter.longToBigDecimal(cursor.getLong(5)),
                    Converter.longToBigDecimal(cursor.getLong(6)),
                    Converter.longToBigDecimal(cursor.getLong(7))));
        }
        cursor.close();
        return results;
    }

    public long insertBigExpense(BigExpense bigExpense) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, bigExpense.getUserId());
        values.put(COLUMN_AMOUNT, Converter.bigDecimalToLong(bigExpense.getAmount()));
        values.put(COLUMN_DATE, Converter.localDateToString(bigExpense.getDate()));
        values.put(COLUMN_DESCRIPTION, bigExpense.getDescription());
        values.put(COLUMN_BIG_EXPENSE_INCOMES_NEEDED, Converter.bigDecimalToLong(bigExpense.getIncomeNeeded()));
        values.put(COLUMN_BIG_EXPENSE_SAVINGS_NEEDED, Converter.bigDecimalToLong(bigExpense.getSavingNeeded()));
        values.put(COLUMN_BIG_EXPENSE_LOAN_NEEDED, Converter.bigDecimalToLong(bigExpense.getLoanNeeded()));
        return writableDB.insert(TABLE_BIG_EXPENSE, null, values);
    }

    public int updateBigExpense(BigExpense bigExpense) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, bigExpense.getUserId());
        values.put(COLUMN_AMOUNT, Converter.bigDecimalToLong(bigExpense.getAmount()));
        values.put(COLUMN_DATE, Converter.localDateToString(bigExpense.getDate()));
        values.put(COLUMN_DESCRIPTION, bigExpense.getDescription());
        values.put(COLUMN_BIG_EXPENSE_INCOMES_NEEDED, Converter.bigDecimalToLong(bigExpense.getIncomeNeeded()));
        values.put(COLUMN_BIG_EXPENSE_SAVINGS_NEEDED, Converter.bigDecimalToLong(bigExpense.getSavingNeeded()));
        values.put(COLUMN_BIG_EXPENSE_LOAN_NEEDED, Converter.bigDecimalToLong(bigExpense.getLoanNeeded()));
        return writableDB.update(TABLE_BIG_EXPENSE, values, "id = ?", toArgs(bigExpense.getId()));
    }
    //endregion

    //region OTHERS

    private void createTables(SQLiteDatabase db) {
        List<String> queries = new ArrayList<>();
        queries.add(String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY," +
                        "%s TEXT," +
                        "%s TEXT)",
                TABLE_USER,
                COLUMN_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_PASSWORD));

        queries.add(String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s INTEGER)",
                TABLE_OVERVIEW,
                COLUMN_ID,
                COLUMN_USER_ID,
                COLUMN_OVERVIEW_INCOMES,
                COLUMN_OVERVIEW_SAVINGS,
                COLUMN_OVERVIEW_TODAY_ALLOWED,
                COLUMN_OVERVIEW_TODAY_REMAINING));

        queries.add(String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY," +
                        "%s TEXT," +
                        "%s INTEGER)",
                TABLE_CATEGORY,
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_CATEGORY_IS_INCOME));

        queries.add(String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s TEXT," +    // LocalDate converted to TEXT
                        "%s TEXT)",
                TABLE_TRANSACTION,
                COLUMN_ID,
                COLUMN_USER_ID,
                COLUMN_CATEGORY_ID,
                COLUMN_AMOUNT,
                COLUMN_DATE,
                COLUMN_DESCRIPTION));

        queries.add(String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s TEXT)",
                TABLE_RECURRING_TRANSACTION,
                COLUMN_ID,
                COLUMN_USER_ID,
                COLUMN_AMOUNT,
                COLUMN_DAY_OF_MONTH,            // int
                COLUMN_DESCRIPTION));

        queries.add(String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s TEXT," +
                        "%s TEXT," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s INTEGER)",
                TABLE_BIG_EXPENSE,
                COLUMN_ID,
                COLUMN_USER_ID,
                COLUMN_AMOUNT,
                COLUMN_DATE,            // LocalDate to TEXT
                COLUMN_DESCRIPTION,
                COLUMN_BIG_EXPENSE_INCOMES_NEEDED,
                COLUMN_BIG_EXPENSE_SAVINGS_NEEDED,
                COLUMN_BIG_EXPENSE_LOAN_NEEDED));

        for (String query : queries) {
            db.execSQL(query);
        }
    }

    private void fillInitialData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_EMAIL, "admin");
        values.put(COLUMN_USER_PASSWORD, Converter.toMd5("admin"));
        db.insert(TABLE_USER, null, values);
        values.clear();

        values.put(COLUMN_NAME, "Recurring");
        values.put(COLUMN_CATEGORY_IS_INCOME, 0);
        db.insert(TABLE_CATEGORY, null, values);
        values.clear();

        values.put(COLUMN_NAME, "Extra income");
        values.put(COLUMN_CATEGORY_IS_INCOME, 1);
        db.insert(TABLE_CATEGORY, null, values);
        values.clear();

        values.put(COLUMN_NAME, "Entertainment");
        values.put(COLUMN_CATEGORY_IS_INCOME, 0);
        db.insert(TABLE_CATEGORY, null, values);
        values.clear();

        values.put(COLUMN_NAME, "Health");
        values.put(COLUMN_CATEGORY_IS_INCOME, 0);
        db.insert(TABLE_CATEGORY, null, values);
        values.clear();

        values.put(COLUMN_NAME, "Home");
        values.put(COLUMN_CATEGORY_IS_INCOME, 0);
        db.insert(TABLE_CATEGORY, null, values);
        values.clear();

        values.put(COLUMN_NAME, "Transportation");
        values.put(COLUMN_CATEGORY_IS_INCOME, 0);
        db.insert(TABLE_CATEGORY, null, values);
        values.clear();

    }

    /**
     * Drop all tables then recreate
     */
    private void doDestructiveMigration(SQLiteDatabase db) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_USER));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_OVERVIEW));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_CATEGORY));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_TRANSACTION));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_RECURRING_TRANSACTION));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_BIG_EXPENSE));
        createTables(db);
    }
    //endregion

    //region CONVERT ARGUMENTS

    /**
     * Convert a couple of objects to String[] for argument
     */
    public static String[] toArgs(Object... objects) {
        String[] result = new String[objects.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = objects[i].toString();
        }
        return result;
    }

    /**
     * Convert int to String[] for argument
     */
    public static String[] toArgs(int id) {
        return new String[]{String.valueOf(id)};
    }
    //endregion
}
