package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Account_Table;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Account_number;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Balance;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Bank_name;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.Customer_name;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.util.List;
import java.util.Date;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final SQLiteHelper helper;
    private SQLiteDatabase database;
    public PersistentAccountDAO(Context context) {
        helper= new SQLiteHelper(context) ;

    }

    @Override
    public List<String> getAccountNumbersList() {
        database=helper.getReadableDatabase();
        String[] array= {
                Account_number
        };
        Cursor crsr= database.query(Account_Table, array, null,null,null,null,null);
        List<String> AccountNumbers_list= new ArrayList<String>();
        while (crsr.moveToNext()){
            String acc_number= crsr.getString(crsr.getColumnIndexOrThrow(Account_number));
            AccountNumbers_list.add(acc_number);
        }
        crsr.close();

        return AccountNumbers_list;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> Account_list = new ArrayList<Account>();
        database= helper.getReadableDatabase();
        String[] array= {Account_number,
                       Bank_name,
                       Customer_name,
                       Balance
                     };
        Cursor crsr= database.query(Account_Table,array,null, null,null,null,null);
        while (crsr.moveToNext()){
            String Acc_number= crsr.getString(crsr.getColumnIndexOrThrow(Account_number));
            String Bnk_name= crsr.getString(crsr.getColumnIndexOrThrow(Bank_name));
            String cst_name=crsr.getString(crsr.getColumnIndexOrThrow(Customer_name));
            double blnce=crsr.getDouble(crsr.getColumnIndexOrThrow(Balance));
            Account acc= new Account(Acc_number,Bnk_name,cst_name,blnce);
            Account_list.add(acc);
        }
        crsr.close();
        return Account_list;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        database= helper.getReadableDatabase();
        String[] array= {Account_number,
                Bank_name,
                Customer_name,
                Balance
        };
        String selection= Account_number + "=?";
        String[] selectionArgs ={accountNo};
        Cursor crsr= database.query(Account_Table,array,selection, selectionArgs,null,null,null);
        if(crsr!=null) {
            crsr.moveToFirst();
            Account acc = new Account(accountNo, crsr.getString(crsr.getColumnIndexOrThrow(Bank_name)),
                    crsr.getString(crsr.getColumnIndexOrThrow(Customer_name)), crsr.getDouble(crsr.getColumnIndexOrThrow(Balance)));
            return acc;
        }
        else{
            String message = "Account" + accountNo + "is invalid!";
            throw new InvalidAccountException(message);
        }

    }

    @Override
    public void addAccount(Account account) {
        database= helper.getWritableDatabase();
        ContentValues val= new ContentValues();
        val.put(Account_number,account.getAccountNo());
        val.put(Bank_name,account.getBankName());
        val.put(Customer_name,account.getAccountHolderName());
        val.put(Balance,account.getBalance());
        database.insert(Account_Table,null,val);
        database.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
       database =helper.getWritableDatabase();
       database.delete(Account_Table,Account_number+"=?",new String[]{accountNo});
       database.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        database= helper.getWritableDatabase();
        String[] array={Balance};
        String selection= Account_number+"=?";
        String[] selectionArgs={accountNo};
        Cursor crsr=database.query(Account_Table,array,selection,selectionArgs,null,null,null);
        double blnce;
        if(crsr.moveToFirst()){
            blnce=crsr.getDouble(0);

        }
        else{
            String message = "Account" + accountNo + "is invalid!";
            throw new InvalidAccountException(message);
        }
        ContentValues val= new ContentValues();
        switch (expenseType){
            case EXPENSE:
                val.put(Balance,blnce-amount);
                break;
            case INCOME:
                val.put(Balance,blnce+amount);
                break;
        }
        database.update(Account_Table,val,Account_number+"=?",new String[]{accountNo});
        crsr.close();
        database.close();
    }
}
