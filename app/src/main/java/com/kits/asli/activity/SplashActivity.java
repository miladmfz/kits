package com.kits.asli.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kits.asli.R;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.UserInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    Intent intent;
    private int STORAGE_PERMISSION_CODE = 1;
    SQLiteDatabase database;
    SharedPreferences shPref;
    SharedPreferences.Editor sEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        copyFileDb();
        createtable();

        init();

    }


    //***************************************************************************************

    public void init() {
        shPref = getSharedPreferences("act", Context.MODE_PRIVATE);
        boolean firstStart = shPref.getBoolean("firstStart", true);
        sEdit = shPref.edit();
        sEdit.putString("prefactor_code", "0");
        sEdit.putString("prefactor_good", "0");

        sEdit.apply();

        if (firstStart) {
            Registration();

            //database.execSQL("Alter Table Good Add Column ReservedAmount INTEGER");

            if (getString(R.string.app_name).equals("ققنوس")) {
                database.execSQL("Alter Table Good Add Column ReservedAmount INTEGER");

            }
            sEdit.putBoolean("firstStart", false);
            sEdit.putString("selloff", "1");
            sEdit.putString("grid", "3");
            sEdit.putString("delay", "1000");
            sEdit.putString("itemamount", "200");
            sEdit.putBoolean("real_amount", true);

            sEdit.apply();
        }
        Handler handler;

        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        intent = new Intent(SplashActivity.this, NavActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1000);
            } else {
                requstforpermission();
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        intent = new Intent(SplashActivity.this, SplashActivity.class);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }, 5000);
            }

        } else {
            requstforpermission();
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    intent = new Intent(SplashActivity.this, SplashActivity.class);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }, 5000);
        }
    }


    public void copyFileDb() {
        InputStream in;
        OutputStream out;
        try {

            String filename = getApplicationInfo().dataDir + "/databases/KowsarDb.sqlite";

            File dbfile = new File(filename);
            if (!dbfile.exists()) {
                if (!Objects.requireNonNull(dbfile.getParentFile()).exists()) {
                    dbfile.getParentFile().mkdirs();
                }
                if (!dbfile.exists()) {
                    dbfile.createNewFile();
                }

                in = getApplicationContext().getAssets().open("KowsarDb.db");
                out = new FileOutputStream(dbfile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createtable() {
        database = openOrCreateDatabase("KowsarDb.sqlite", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS PreFactor (RowCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , GoodRef INTEGER, Amount INTEGER, Shortage INTEGER, PreFactorDate TEXT, PreFactorCode INTEGER, Price INTEGER)");
        database.execSQL("CREATE TABLE IF NOT EXISTS PrefactorHeader ( PreFactorCode INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, PreFactorDate TEXT," +
                " PreFactorTime TEXT, PreFactorKowsarCode INTEGER, PreFactorKowsarDate TEXT, PreFactorExplain TEXT, CustomerRef INTEGER, BrokerRef INTEGER)");
        database.execSQL("CREATE TABLE IF NOT EXISTS Favorites ( GoodRef INTEGER )");
        database.execSQL("CREATE TABLE IF NOT EXISTS Customer ( CustomerCode INTEGER, CustomerName TEXT, CustomerAddress TEXT, CustomerSum INTEGER )");
        database.execSQL("CREATE TABLE IF NOT EXISTS Config ( KeyValue TEXT Primary Key, DataValue TEXT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS Units ( UnitCode INTEGER PRIMARY KEY, UnitName TEXT)");

        database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'Good_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'Good_LastRepCode')");
        database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'GoodStack_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'GoodStack_LastRepCode')");
        database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'GoodsGrp_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'GoodsGrp_LastRepCode')");
        database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'GoodGroup_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'GoodGroup_LastRepCode')");
        database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'PropertyValue_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'PropertyValue_LastRepCode')");
        database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'City_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'City_LastRepCode')");
        database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'Address_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'Address_LastRepCode')");
        database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'Central_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'Central_LastRepCode')");
        database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'Customer_LastRepCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'Customer_LastRepCode')");
        database.execSQL("INSERT INTO config(keyvalue, datavalue) Select 'BrokerCode', '0' Where Not Exists(Select * From Config Where KeyValue = 'BrokerCode')");


        database.execSQL("Create Index IF Not Exists IX_GoodGroup_GoodRef on GoodGroup (GoodRef)");
        database.execSQL("Create Index IF Not Exists IX_GoodGroup_GroupRef on GoodGroup (GoodGroupRef)");
        database.execSQL("Create Index IF Not Exists IX_PreFactor_GoodRef on PreFactor (GoodRef)");
        database.execSQL("Create Index IF Not Exists IX_PreFactor_GoodRef on PreFactor (PreFactorCode)");
        database.execSQL("Create Index IF Not Exists IX_Good_GoodUnitRef on Good (GoodUnitRef)");

    }


    public void requstforpermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("permission needed")
                    .setMessage("this permission is needed because of this abd that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("permission needed")
                    .setMessage("this permission is needed because of this abd that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }

        }

    }


    public void Registration() {
        DatabaseHelper dbh = new DatabaseHelper(this);

        UserInfo auser = new UserInfo();
        auser.setEmail(" ");
        auser.setNameFamily(" ");
        auser.setAddress(" ");
        auser.setPhone(" ");
        auser.setMobile(" ");
        auser.setBirthDate(" ");
        auser.setMelliCode(" ");
        auser.setPostalCode(" ");
        auser.setBrokerCode("0");
        dbh.SavePersonalInfo(auser);
    }


}
