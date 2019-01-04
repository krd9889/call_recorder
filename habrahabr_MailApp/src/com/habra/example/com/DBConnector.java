package com.habra.example.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Date;

public class DBConnector {

    // Данные базы данных и таблиц
    private static final String DATABASE_NAME = "simple.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "MyData";

    // Название столбцов
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "Date";
    private static final String COLUMN_TIME = "Time";
    private static final String COLUMN_PATH = "Path";
    private static final String COLUMN_TITLE = "Title";
    private static final String COLUMN_ICON = "Icon";

    // Номера столбцов
    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_DATE = 1;

    private static final int NUM_COLUMN_TIME = 2;
    private static final int NUM_COLUMN_PATH = 3;
    private static final int NUM_COLUMN_TITLE = 4;
    private static final int NUM_COLUMN_ICON = 5;


    private SQLiteDatabase mDataBase;

    public DBConnector(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insert(MyData md) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_DATE, md.getDate());
        cv.put(COLUMN_TIME, md.getTime());
        cv.put(COLUMN_PATH, md.getPath());
        cv.put(COLUMN_TITLE, md.getTitle());
        cv.put(COLUMN_ICON, md.getIcon());


        return mDataBase.insert(TABLE_NAME, null, cv);
    }

    public int update(MyData md) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_DATE, md.getDate());
        cv.put(COLUMN_TIME, md.getTime());
        cv.put(COLUMN_PATH, md.getPath());
        cv.put(COLUMN_TITLE, md.getTitle());
        cv.put(COLUMN_ICON, md.getIcon());
        return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] { String.valueOf(md.getID()) });
    }

    public void deleteAll() {
        mDataBase.delete(TABLE_NAME, null, null);
    }

    public void delete(long id) {
        mDataBase.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public MyData select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[] { String.valueOf(id) }, null, null, COLUMN_DATE);

        mCursor.moveToFirst();
        long date = mCursor.getLong(NUM_COLUMN_DATE);
        String time = mCursor.getString(NUM_COLUMN_TIME);
        String path = mCursor.getString(NUM_COLUMN_PATH);
        String title = mCursor.getString(NUM_COLUMN_TITLE);
        int icon = mCursor.getInt(NUM_COLUMN_ICON);
        return new MyData(id, date,time,path, title, icon);
    }

    public ArrayList<MyData> selectAll() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, COLUMN_DATE);

        ArrayList<MyData> arr = new ArrayList<MyData>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                long date = mCursor.getLong(NUM_COLUMN_DATE);
                String time = mCursor.getString(NUM_COLUMN_TIME);
                String path = mCursor.getString(NUM_COLUMN_PATH);
                String title = mCursor.getString(NUM_COLUMN_TITLE);
                int icon = mCursor.getInt(NUM_COLUMN_ICON);
                arr.add(0,new MyData(id, date,time,path, title, icon));  ///////////// ДОБАВЛЯЕМ В НАЧАЛО СПИСКА arr.add(0,...)
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    public ArrayList<MyData> selectForSearch(String strSearch) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, COLUMN_DATE);

        ArrayList<MyData> arr = new ArrayList<MyData>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                long date = mCursor.getLong(NUM_COLUMN_DATE);
                String time = mCursor.getString(NUM_COLUMN_TIME);
                String path = mCursor.getString(NUM_COLUMN_PATH);
                String title = mCursor.getString(NUM_COLUMN_TITLE);
                int icon = mCursor.getInt(NUM_COLUMN_ICON);

                if(   title.toLowerCase().contains(  strSearch.toLowerCase()  )   )
                {
                    arr.add(0,new MyData(id, date,time,path, title, icon));   ///////////// ДОБАВЛЯЕМ В НАЧАЛО СПИСКА arr.add(0,...)
                }


            } while (mCursor.moveToNext());
        }
        return arr;
    }

    /*public String getTime()
    {
        Date date = new Date();

        int years = date.getYear();
        int month = date.getMonth();
        int day = date.getDate();

        int hours = date.getHours();
        int min = date.getMinutes();
        //int sec = date.getSeconds();

        String text = String.valueOf(hours) + ":" + String.valueOf(min)+"  "+ String.valueOf(day)+" "+ String.valueOf(month)+" "+ String.valueOf(years); //+
        //":" + String.valueOf(sec) + " секунд.";

        return text;
    }*/

    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE + " LONG, " +
                    COLUMN_TIME + " STRING, " +
                    COLUMN_PATH + " STR, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_ICON + " INTEGER); ";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}