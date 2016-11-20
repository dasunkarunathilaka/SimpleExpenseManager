package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Dasun K on 11/20/2016.
 */
public class PersistentTransactionDAO implements TransactionDAO{
    private Context context;

    //Constructor
    public PersistentTransactionDAO(Context context) {
        this.context = context;
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        DBHandler handle = new DBHandler(context);
        SQLiteDatabase sdb = handle.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(handle.KEY_TRA_ACC_NO, accountNo);
        values.put(handle.KEY_DATE, convertDateToString(date));
        values.put(handle.KEY_AMOUNT, amount);
        values.put(handle.KEY_EXPENSE_TYPE, expenseType.toString());

        sdb.insert(handle.TABLE_TRANSACTION,null,values);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        return getPaginatedTransactionLogs(0);

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        DBHandler handle = new DBHandler(context);
        SQLiteDatabase sdb = handle.getWritableDatabase();

        String query = "SELECT "+ handle.KEY_TRA_ACC_NO + ", " +
                handle.KEY_DATE + ", " +
                handle.KEY_EXPENSE_TYPE +", " +
                handle.KEY_AMOUNT +
                " FROM " + handle.TABLE_TRANSACTION + " ORDER BY " + handle.KEY_TRA_ID ;

        Cursor cursor = sdb.rawQuery(query, null);

        ArrayList<Transaction> transactionLogs = new ArrayList<>();

        while (cursor.moveToNext())
        {
            try {

                ExpenseType expenseType = null;
                if (cursor.getString(cursor.getColumnIndex(handle.KEY_EXPENSE_TYPE)).equals(ExpenseType.INCOME.toString())) {
                    expenseType = ExpenseType.INCOME;
                }
                else{
                    expenseType = ExpenseType.EXPENSE;
                }

                String dateString = cursor.getString(cursor.getColumnIndex(handle.KEY_DATE));
                Date date = convertStringToDate(dateString);

                Transaction t = new Transaction(
                        date,
                        cursor.getString(cursor.getColumnIndex(handle.KEY_TRA_ACC_NO)),
                        expenseType,
                        cursor.getDouble(cursor.getColumnIndex(handle.KEY_AMOUNT)));

                transactionLogs.add(t);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return transactionLogs;


    }

    public static String convertDateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(date);
        return dateString;

    }

    //Method to convert a string to a DATE object
    public static Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date strDate = dateFormat.parse(date);
        return strDate;
    }

}
