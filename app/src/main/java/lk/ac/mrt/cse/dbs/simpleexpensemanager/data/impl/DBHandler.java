package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 * Created by Dasun K on 11/20/2016.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "140295T";

    public static final String TABLE_ACCOUNT = "account";
    public static final String KEY_ACC_NO = "accountNo";
    public static final String KEY_BANK_NAME = "bank";
    public static final String KEY_ACC_HOLDER = "accountHolder";
    public static final String KEY_BALANCE = "balance";

    public static final String TABLE_TRANSACTION = "transaction";
    public static final String KEY_TRA_ID = "transactionId";
    public static final String KEY_DATE = "date";
    public static final String KEY_TRA_ACC_NO = "accountNo";
    public static final String KEY_EXPENSE_TYPE = "expenseType";
    public static final String KEY_AMOUNT = "amount";



    public DBHandler(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + "("
        + KEY_ACC_NO + " TEXT PRIMARY KEY," + KEY_BANK_NAME + " TEXT,"
        + KEY_ACC_HOLDER + " TEXT," + KEY_BALANCE + "FLOAT" + ")";
        db.execSQL(CREATE_TABLE);

        String CREATE_TABLE2 = "CREATE TABLE " + TABLE_TRANSACTION + "("
                + KEY_TRA_ID + " TEXT PRIMARY KEY," + KEY_DATE + " TEXT," +
                KEY_TRA_ACC_NO + " TEXT," + KEY_EXPENSE_TYPE + " TEXT," +
                KEY_AMOUNT + " TEXT" + ")";
        db.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        onCreate(db);
    }

}
