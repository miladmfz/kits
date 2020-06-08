package com.kits.asli.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kits.asli.R;
import com.kits.asli.adapters.Action;
import com.kits.asli.adapters.Good_buy_Adapter;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.Good;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BuyActivity extends AppCompatActivity {

    private Action action;
    private int Internet = 1, il = 0;
    private String SERVER_IP_ADDRESS;
    private Integer PreFac = 0;
    private Intent intent;
    private DatabaseHelper dbh = new DatabaseHelper(BuyActivity.this);
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private SharedPreferences shPref;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);


        intent();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                init();

            }
        }, 100);


    }

    //*****************************************************************
    public void init() {

        action = new Action(BuyActivity.this);

        shPref = getSharedPreferences("act", Context.MODE_PRIVATE);


        SERVER_IP_ADDRESS = getString(R.string.SERVERIP);
        Toolbar toolbar = findViewById(R.id.BuyActivity_toolbar);
        TextView row = findViewById(R.id.BuyActivity_total_row_buy);
        TextView price = findViewById(R.id.BuyActivity_total_price_buy);
        TextView customer = findViewById(R.id.BuyActivity_total_customer_buy);
        TextView amount = findViewById(R.id.BuyActivity_total_amount_buy);
        Button total_delete = findViewById(R.id.BuyActivity_total_delete);
        Button final_buy_test = findViewById(R.id.BuyActivity_test);
        RecyclerView re = findViewById(R.id.BuyActivity_R1);


        setSupportActionBar(toolbar);
        ArrayList<Good> goods = dbh.getAllPreFactorRows(PreFac);


        Good_buy_Adapter adapter = new Good_buy_Adapter(goods, BuyActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(BuyActivity.this, 1);//grid
        re.setLayoutManager(gridLayoutManager);
        re.setAdapter(adapter);


        price.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + dbh.getFactorSum(PreFac)))));
        amount.setText(Farsi_number.PerisanNumber("" + dbh.getFactorSumAmount(PreFac)));
        customer.setText(Farsi_number.PerisanNumber("" + dbh.getFactorCustomer(PreFac)));
        row.setText(Farsi_number.PerisanNumber("" + goods.size()));


        final_buy_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(BuyActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

                    new android.app.AlertDialog.Builder(BuyActivity.this)
                            .setTitle("توجه")
                            .setMessage("آیا فاکتور ارسال گردد؟")
                            .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    action.sendfactor(PreFac);
                                }
                            })

                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                } else {
                    requstforpermission();
                }


            }
        });


        total_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(BuyActivity.this)
                        .setTitle("توجه")
                        .setMessage("آیا مایل به خالی کردن سبد خرید می باشید؟")
                        .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dbh.DeletePreFactorRow(PreFac, 0);

                                finish();

                                Toast toast = Toast.makeText(BuyActivity.this, "سبد خرید با موفقیت حذف گردید!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 10, 10);
                                toast.show();
                            }
                        })
                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }
        });
        if (dbh.getAllGood_pfcode(PreFac, "", 0, 2, 0, false).size() < 1) {
            Toast.makeText(this, "کالای برای اصلاح موجود نمی باشد", Toast.LENGTH_SHORT).show();

            finish();
        }
    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        PreFac = data.getInt("PreFac");
    }


    public void requstforpermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
            new AlertDialog.Builder(this)
                    .setTitle("permission needed")
                    .setMessage("this permission is needed because of this abd that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(BuyActivity.this, new String[]{Manifest.permission.INTERNET}, Internet);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, Internet);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Internet) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
