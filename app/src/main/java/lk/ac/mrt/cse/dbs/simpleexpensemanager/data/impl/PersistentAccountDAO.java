package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by Dasun K on 11/20/2016.
 */
public class PersistentAccountDAO implements AccountDAO {
    //private final Map<String, Account> accounts;                // Saved in a hashmap ----> Save to the DB.

    private Context context;

    public PersistentAccountDAO(Context context) {
        this.context = context;
        //this.accounts = new HashMap<>();
    }

    @Override
    public List<String> getAccountNumbersList() {

        DBHandler handle = new DBHandler(context);
        SQLiteDatabase sdb = handle.getReadableDatabase();
        String query = "SELECT "+ handle.KEY_ACC_NO +" FROM " + handle.TABLE_ACCOUNT +" ORDER BY " + handle.KEY_ACC_NO;

        Cursor cursor = sdb.rawQuery(query, null);

        ArrayList<String> accNumbers = new ArrayList<>();

        while (cursor.moveToNext())
        {
            accNumbers.add(cursor.getString(cursor.getColumnIndex(handle.KEY_ACC_NO)));
        }

        cursor.close();

        return accNumbers;

    }

    @Override
    public List<Account> getAccountsList() {
        DBHandler handle = new DBHandler(context);
        SQLiteDatabase sdb = handle.getReadableDatabase();
        List<Account> accList = new ArrayList<Account>();

        String selectQuery = "SELECT * FROM " + handle.TABLE_ACCOUNT;


        Cursor cursor = sdb.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Account acc = new Account(cursor.getString(cursor.getColumnIndex(handle.KEY_ACC_NO)),
                        cursor.getString(cursor.getColumnIndex(handle.KEY_BANK_NAME)),
                        cursor.getString(cursor.getColumnIndex(handle.KEY_ACC_HOLDER)),
                        cursor.getDouble(cursor.getColumnIndex(handle.KEY_BALANCE)));

                accList.add(acc);
            } while (cursor.moveToNext());
        }

        return accList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        DBHandler handle = new DBHandler(context);
        SQLiteDatabase sdb = handle.getReadableDatabase();

        String query = "SELECT * FROM " + handle.TABLE_ACCOUNT + " WHERE " + handle.KEY_ACC_NO + " =  '" + accountNo + "'";

        Cursor cursor = sdb.rawQuery(query, null);

        Account account = null;

        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(cursor.getColumnIndex(handle.KEY_ACC_NO)),
                    cursor.getString(cursor.getColumnIndex(handle.KEY_BANK_NAME)),
                    cursor.getString(cursor.getColumnIndex(handle.KEY_ACC_HOLDER)),
                    cursor.getDouble(cursor.getColumnIndex(handle.KEY_BALANCE)));
        }

        else {
            throw new InvalidAccountException("Account is invalid.");
        }

        cursor.close();

        return account;
    }

    @Override
    public void addAccount(Account account) {

        DBHandler handle = new DBHandler(context);
        SQLiteDatabase sdb = handle.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(handle.KEY_ACC_NO, account.getAccountNo());
        values.put(handle.KEY_BANK_NAME, account.getBankName());
        values.put(handle.KEY_ACC_HOLDER, account.getAccountHolderName());
        values.put(handle.KEY_BALANCE, account.getBalance());

        sdb.insert(handle.TABLE_ACCOUNT, null, values);


    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        DBHandler handle = new DBHandler(context);
        SQLiteDatabase sdb = handle.getWritableDatabase();

        String query = "SELECT * FROM " + handle.TABLE_ACCOUNT + " WHERE " + handle.KEY_ACC_NO + " =  '" + accountNo + "'";

        Cursor cursor = sdb.rawQuery(query, null);

        Account account = null;

        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(cursor.getColumnIndex(handle.KEY_ACC_NO)),
                    cursor.getString(cursor.getColumnIndex(handle.KEY_BANK_NAME)),
                    cursor.getString(cursor.getColumnIndex(handle.KEY_ACC_HOLDER)),
                    cursor.getFloat(cursor.getColumnIndex(handle.KEY_BALANCE)));
            sdb.delete(handle.TABLE_ACCOUNT, handle.KEY_ACC_NO + " = ?", new String[] { accountNo });
            cursor.close();

        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("Invalid account.");
        }

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        DBHandler handle = new DBHandler(context);
        SQLiteDatabase sdb = handle.getWritableDatabase();

        ContentValues values = new ContentValues();

        Account account = getAccount(accountNo);

        if (account!=null) {

            double new_amount=0;

            if (expenseType.equals(ExpenseType.EXPENSE)) {
                new_amount = account.getBalance() - amount;
            }

            else if (expenseType.equals(ExpenseType.INCOME)) {
                new_amount = account.getBalance() + amount;
            }

            //Query to update BALANCE in the account table
            String strSQL = "UPDATE "+handle.TABLE_ACCOUNT +" SET "+handle.KEY_BALANCE +" = "+new_amount+" WHERE "+handle.KEY_ACC_NO +" = '"+ accountNo+"'";

            sdb.execSQL(strSQL);

        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("Invalid account.");
        }

    }
}
