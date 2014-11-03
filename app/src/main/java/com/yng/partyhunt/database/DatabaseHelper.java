package com.yng.partyhunt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by yng1905 on 3/1/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DB_PARTYHUNT";

    private static final String CREATE_TBL_PARTIES = "create table if not exists TBL_PARTIES " +
                                                   "(idNr integer primary key AUTOINCREMENT,serverId text, name text, " +
                                                    "beginDate text, endDate text,startTime text, endTime text, attendersCount text, " +
                                                    "longitude text, latitude text, description text, mayor text)";

    private static final String CREATE_TBL_PICTURES = "create table if not exists TBL_PICTURES " +
                                                   "(idNr integer primary key AUTOINCREMENT, URI text)";

    private static final String CREATE_TBL_PARTYPICS = "create table if not exists TBL_PARTYPICS " +
                                                       "(idNr integer primary key AUTOINCREMENT, partyId text, picId text)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    //Create tables if not exist.

        sqLiteDatabase.execSQL(CREATE_TBL_PARTIES);
        sqLiteDatabase.execSQL(CREATE_TBL_PICTURES);
        sqLiteDatabase.execSQL(CREATE_TBL_PARTYPICS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TBL_PARTIES");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TBL_PICTURES");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TBL_PARTYPICS");


        // Create tables again
        onCreate(sqLiteDatabase);
    }
    //All basic SQL Queries that methods of table classes use.

    public Long Insert(String tableName, ContentValues values)
    {
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try{
        id = db.insertOrThrow(tableName, null, values);
        }
        catch(Exception ex)
        {
            Log.d("Insert error: ",ex.toString());
        }
        return id;
    }

    public Cursor Select(String selectQuery)
    {
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try{
        cursor = db.rawQuery(selectQuery, null);
        }catch(Exception ex)
        {
            Log.d("Error: ",ex.toString());
        }

        return cursor;
    }

    public int Update(String tableName, ContentValues values, String whereClause, String[] whereArgs)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // updating row
        return db.update(tableName, values, whereClause,
                whereArgs);
    }

    public void Delete(String tableName, String whereClause, String[] whereArgs) {
        try{
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, whereClause, whereArgs);
        db.close();}
        catch(Exception ex)
        {
            Log.d("Error",ex.toString());
        }

    }
}
