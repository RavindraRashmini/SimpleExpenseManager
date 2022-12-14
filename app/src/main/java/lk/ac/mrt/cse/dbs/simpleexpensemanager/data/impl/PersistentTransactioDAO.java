package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Account_number;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Amount;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Balance;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Transaction_Table;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Date;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Expense_Type;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactioDAO implements TransactionDAO {
    private final SQLiteHelper helper;
    private SQLiteDatabase database;

    public PersistentTransactioDAO(Context context) {
        helper= new SQLiteHelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
            database=helper.getWritableDatabase();
        DateFormat dateFormat= new SimpleDateFormat("dd-MM-yyy");
        ContentValues val= new ContentValues();
        val.put(Date,dateFormat.format(date));
        val.put(Account_number,accountNo);
        val.put(Expense_Type, String.valueOf(expenseType));
        val.put(Amount,amount);
         database.insert(Transaction_Table,null,val);
         database.close();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactions= new ArrayList<Transaction>();
        database=helper.getReadableDatabase();
        String[] array={ Date,Account_number,Expense_Type,Amount};
        Cursor crsr=database.query(Transaction_Table,array,null,null,null,null,null);
        while(crsr.moveToNext()){
            String date=crsr.getString(crsr.getColumnIndexOrThrow(Date));
            Date date_= new SimpleDateFormat("dd-MM-yyy").parse(date);
            String account_num=crsr.getString(crsr.getColumnIndexOrThrow(Account_number));
            String type= crsr.getString(crsr.getColumnIndexOrThrow(Expense_Type));
            ExpenseType expenseType=ExpenseType.valueOf(type);
            double amount= crsr.getDouble(crsr.getColumnIndexOrThrow(Balance));
            Transaction transaction=new Transaction(date_,account_num,expenseType,amount);
            transactions.add(transaction);
        }
        crsr.close();

        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactions= new ArrayList<Transaction>();
        database=helper.getReadableDatabase();
        String[] array={ Date,Account_number,Expense_Type,Amount};
        Cursor crsr=database.query(Transaction_Table,array,null,null,null,null,null);
        int size=crsr.getCount();
        while(crsr.moveToNext()){
            String date=crsr.getString(crsr.getColumnIndexOrThrow(Date));
            Date date_= new SimpleDateFormat("dd-MM-yyy").parse(date);
            String account_num=crsr.getString(crsr.getColumnIndexOrThrow(Account_number));
            String type= crsr.getString(crsr.getColumnIndexOrThrow(Expense_Type));
            ExpenseType expenseType=ExpenseType.valueOf(type);
            double amount= crsr.getDouble(crsr.getColumnIndexOrThrow(Balance));
            Transaction transaction=new Transaction(date_,account_num,expenseType,amount);
            transactions.add(transaction);
        }
        if(size<=limit){
            return transactions;
        }
        return transactions.subList(size-limit,size);
    }
}
