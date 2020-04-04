package com.kits.asli.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAccess {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    private Cursor c = null;

    private DatabaseAccess(Context context) {
        this.openHelper = new MyDatabase(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null) {
            this.db.close();
        }


    }

    public String getAdress(String name) {

        c = db.rawQuery("select * from android_metadata ", new String[]{});
        StringBuilder buffer = new StringBuilder();
        while (c.moveToNext()) {
            String add = c.getString(0);
            buffer.append("").append(add);
        }
        return buffer.toString();
    }


}