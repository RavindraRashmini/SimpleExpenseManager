package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String database_name="200260A.sqlite";
    private static final int  version=1;

    //table names
    public static final String Account_Table="account";
    public static final String Transaction_Table= "transac";

    //column names

    public static final String Account_number="accountno";
    public static final String Bank_name ="bankname";
    public static final String Customer_name="accountholdername";
    public static final String Balance="balance";


    public static final String ID="id";
    public static final String Date="date";
    public static final String Expense_Type="expenseType";
    public static final String Amount="amount";

    public SQLiteHelper(Context context) {
        super(context,database_name,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       sqLiteDatabase.execSQL("create table "+Account_Table+"("+
               Account_number+"Text primary key, "+
               Bank_name+"Text not null ,"+
               Customer_name+"TEXT not null, "+
               Balance+"Real not null)");
       sqLiteDatabase.execSQL(String.format("create table %s(%sINTEGERT PRIMARY KEY AUTOINCREMENT, %s Text NOT NULL,%s TEXT NOT NULL,%s REAL NOT NULL,%s TEXT,FOREIGN KEY (%s) REFERENCES %s(%s))", Transaction_Table, ID, Date, Expense_Type, Amount, Account_number, Account_number, Account_Table, Account_number)
               );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE  IF EXISTS "+ Account_Table);
        sqLiteDatabase.execSQL("DROP TABLE  IF EXISTS "+ Transaction_Table);
        onCreate(sqLiteDatabase);
    }
}
