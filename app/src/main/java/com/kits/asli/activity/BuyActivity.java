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


//        final Dialog dialog1 ;
//        dialog1 = new Dialog(this);
//        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
//        dialog1.setContentView(R.layout.rep_prog);
//        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
//        repw.setText("در حال خواندن اطلاعات");
//        dialog1.show();
        intent();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                init();

            }
        }, 100);
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dialog1.dismiss();
//
//            }
//        },1000);


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


    public void sendfactor(final int factor_code) {
        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
        RequestQueue queue = Volley.newRequestQueue(BuyActivity.this);
        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response = ", response + "");
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    int code = jo.getInt("GoodCode");
                    if (code == 0) {
                        Integer kowsarcode = jo.getInt("PreFactorCode");
                        if (kowsarcode > 0) {
                            Toast toast = Toast.makeText(BuyActivity.this, "پیش فاکتور با موفقیت ارسال شد", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 10, 10);
                            toast.show();
                            String factorDate = jo.getString("PreFactorDate");
                            Log.e("factorcode  ", kowsarcode.toString());
                            Log.e("factordate  ", factorDate);
                            dbh.UpdatePreFactor(factor_code, kowsarcode, factorDate);
                            SharedPreferences.Editor sEdit = shPref.edit();
                            sEdit.putString("prefactor_code", "0");
                            sEdit.apply();
                            intent = new Intent(BuyActivity.this, NavActivity.class);
                            intent.putExtra("showflag", 2);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        } else {
                            Toast toast = Toast.makeText(BuyActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 10, 10);
                            toast.show();
                        }

                    } else {
                        SQLiteDatabase dtb = openOrCreateDatabase("KowsarDb.sqlite", MODE_PRIVATE, null);
                        for (int i = 0; i < il; i++) {
                            jo = object.getJSONObject(i);
                            code = jo.getInt("GoodCode");
                            int flag = jo.getInt("Flag");
                            dtb.execSQL("Update PreFactor set Shortage = " + flag + " Where IfNull(PreFactorCode,0)=" + factor_code + " And GoodRef = " + code);
                        }
                        Toast.makeText(BuyActivity.this, "کالاهای مورد نظر کسر موجودی دارند!", Toast.LENGTH_SHORT).show();
                        intent = new Intent(BuyActivity.this, BuyActivity.class);
                        intent.putExtra("PreFac", Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))));
                        intent.putExtra("showflag", 2);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BuyActivity.this, "بروز خطا در اطلاعات", Toast.LENGTH_SHORT).show();
                    Log.e("printStackTrace", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(BuyActivity.this, "ارتباط با سرور میسر نمی باشد.", Toast.LENGTH_SHORT).show();
                Log.e("printStackTrace", volleyError.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "PFQASWED");

                SQLiteDatabase dtb = BuyActivity.this.openOrCreateDatabase("KowsarDb.sqlite", Context.MODE_PRIVATE, null);

                Action action = new Action(getApplicationContext());


                Cursor pc = dtb.rawQuery("Select PreFactorCode, PreFactorDate, PreFactorExplain, CustomerRef, BrokerRef From PreFactorHeader Where PreFactorCode = " + factor_code, null);
                // pr1 = CursorToJson(pc);
                String pr1 = action.CursorToJson(pc);
                pc.close();


                Log.e("reqqqq", pr1);
                params.put("PFHDQASW", pr1);
                Cursor c = dtb.rawQuery("Select GoodRef, Amount, Price From PreFactor Where  GoodRef>0 and  Prefactorcode = " + factor_code, null);

                String pr2 = action.CursorToJson(c);
                //pr2 = CursorToJson(c);
                c.close();

                Log.e("reqqqq", pr2);
                params.put("PFDTQASW", pr2);
                return params;
            }

        };
        queue.add(stringrequste);
        Log.e("stringrequste =", stringrequste.toString() + "");
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
