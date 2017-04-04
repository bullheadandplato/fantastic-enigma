package com.osama.smsbomber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by bullhead on 4/4/17.
 *
 */

public class RecentDataSource {
    private SQLiteDatabase mDb;
    private DbHandler dbHandler;
    private ArrayList<RecentModel> mModels;
    private String[] allColumns={DbHandler.COL1_NAME,DbHandler.COL2_NAME};
    public RecentDataSource(Context ctx){
        dbHandler=new DbHandler(ctx);
    }
    public void open(){
        mDb=dbHandler.getWritableDatabase();
    }
    public void close(){
        mDb.close();
    }
    public void insertRecent(String phone,int count){
        ContentValues values=new ContentValues();
        values.put(DbHandler.COL1_NAME,phone);
        values.put(DbHandler.COL2_NAME,count);
        mDb.insert(DbHandler.TABLE_NAME,null,values);
    }
    public void fillData(){
        mModels=new ArrayList<>();
        Cursor cursor=mDb.query(DbHandler.TABLE_NAME,allColumns,null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            mModels.add(cursorToModel(cursor));
            cursor.moveToNext();
        }
        cursor.close();
    }
    private RecentModel cursorToModel(Cursor cursor){
        RecentModel model=new RecentModel();
        model.setPhone(cursor.getString(0));
        model.setCount(cursor.getInt(1));
        return model;
    }

    public ArrayList<RecentModel> getModels() {
        return mModels;
    }
}
