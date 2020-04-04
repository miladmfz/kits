package com.kits.asli.model;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "databases/abd_db.db";

    private static final int DATABASE_VERSION = 1;

    MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }
}