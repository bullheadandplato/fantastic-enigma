package com.osama.smsbomber;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by bullhead on 4/4/17.
 *
 */

public class DbHandler extends SQLiteOpenHelper{
    private static final String DB_NAME="com.osama.smsbomber.smsdb";
    private static final int DB_VERSION=1;
    static final String TABLE_NAME="recent";
    static final String COL1_NAME="phone";
    static final String COL2_NAME="count";

    private static final String CREATE_TABLE_QUERY="create table "+TABLE_NAME
            + " ( "+ COL1_NAME +" TEXT, "+COL2_NAME + " int );";

    public DbHandler(Context ctx){
        super(ctx,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
