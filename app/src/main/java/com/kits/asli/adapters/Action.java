package com.kits.asli.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kits.asli.R;
import com.kits.asli.activity.BuyActivity;
import com.kits.asli.activity.CustomerActivity;
import com.kits.asli.activity.GrpActivity;
import com.kits.asli.activity.NavActivity;
import com.kits.asli.activity.PrefactorActivity;
import com.kits.asli.activity.PrinterActivity;
import com.kits.asli.activity.SearchActivity;
import com.kits.asli.activity.Search_date_detailActivity;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.UserInfo;
import com.kits.asli.model.Utilities;
import com.kits.asli.webService.APIClient_kowsar;
import com.kits.asli.webService.APIInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;


public class Action {
    private final Context mContext;
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private DatabaseHelper dbh;
    private Integer code;
    private Intent intent;
    private SharedPreferences shPref;
    private String SERVER_IP_ADDRESS;
    private Integer il;

    public Action(Context mContext) {
        this.mContext = mContext;
        this.dbh = new DatabaseHelper(mContext);
        this.il = 0;
        SERVER_IP_ADDRESS = mContext.getString(R.string.SERVERIP);
        shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);

    }



    public void buydialog(int goodcode, final int maxprice, final int customer_price, int facamount, String UnitName) {
        SharedPreferences shPref;
        shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
        final String pfcode = shPref.getString("prefactor_code", null);
        final String selloff = shPref.getString("selloff", null);

        code = goodcode;
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//title laye nadashte bashim
        dialog.setContentView(R.layout.box_buy);
        Button boxbuy = dialog.findViewById(R.id.box_buy_btn);

        final EditText amount = dialog.findViewById(R.id.box_buy_amount);
        amount.setHint(UnitName);
        final TextView tv = dialog.findViewById(R.id.box_buy_factor);
        final TextView xpr = dialog.findViewById(R.id.box_buy_maxprice);


        tv.setText(dbh.getFactorCustomer(Integer.valueOf(pfcode)));
        xpr.setText("" + maxprice);


        final EditText percent = dialog.findViewById(R.id.box_buy_percent);
        final EditText price = dialog.findViewById(R.id.box_buy_price);
        final TextView sumprice = dialog.findViewById(R.id.box_buy_sumprice);
        final TextView factoramount = dialog.findViewById(R.id.box_buy_facamount);

        if (Integer.parseInt(selloff) == 0) {
            percent.setEnabled(false);
            price.setEnabled(false);
        } else {
            percent.setEnabled(true);
            price.setEnabled(true);
        }

        factoramount.setText("" + facamount);
        price.setText("" + customer_price);
        if (maxprice > 0) {
            percent.setText("" + (100 - (100 * customer_price / maxprice)));
        } else percent.setText("");

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    Integer iPrice = Integer.valueOf(price.getText().toString());
                    Integer iAmount = Integer.valueOf(amount.getText().toString());
                    sumprice.setText("" + iPrice * iAmount);
                } catch (Exception e) {
                    sumprice.setText("");
                }
            }
        });

        percent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (percent.hasFocus()) {
                    Integer iPercent;
                    Integer iAmount;
                    try {
                        iPercent = Integer.valueOf(percent.getText().toString());
                        if (maxprice > 0) {
                            if (iPercent > 100) {
                                iPercent = 100;
                                percent.setText(iPercent + "");
                                percent.setError("حداکثر تخفیف");
                            }
                            price.setText("" + (maxprice - (maxprice * iPercent / 100)));
                        }

                    } catch (Exception e) {
                        price.setText("" + maxprice);
                    }

                    try {
                        iAmount = Integer.valueOf(amount.getText().toString());
                        sumprice.setText("" + (iAmount * Integer.valueOf(price.getText().toString())));
                    } catch (Exception e) {
                        sumprice.setText("");
                    }
                }

            }
        });

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (price.hasFocus()) {
                    try {
                        Integer iPrice = Integer.valueOf(price.getText().toString());
                        if (maxprice > 0) {
                            percent.setText("" + (100 - (100 * iPrice / maxprice)));
                        } else
                            percent.setText("");

                        Integer iAmount = Integer.valueOf(amount.getText().toString());
                        sumprice.setText("" + iPrice * iAmount);
                    } catch (Exception e) {
                        sumprice.setText("");
                    }
                }
            }
        });

        dialog.show();
        amount.requestFocus();
        amount.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(amount, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);

        boxbuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amo = amount.getText().toString();
                String pr = price.getText().toString();
                if (pr == "") pr = "-1";

                if (!amo.equals("")) {
                    if (Integer.parseInt(amo) != 0) {
                        if (Integer.parseInt(pfcode) != 0) {

                            SharedPreferences shPref;
                            shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                            int pfcode = Integer.parseInt(shPref.getString("prefactor_code", null));
                            dbh.InsertPreFactor(pfcode, code, Integer.parseInt(amo), Integer.parseInt(pr), 0);
                            Toast toast = Toast.makeText(mContext, "به سبد خرید اضافه شد", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 10, 10);
                            toast.show();
                            dialog.dismiss();

                        } else {

                            intent = new Intent(mContext, CustomerActivity.class);
                            intent.putExtra("edit", "0");
                            intent.putExtra("factor_code", 0);
                            intent.putExtra("id", 0);
                            mContext.startActivity(intent);
                            dialog.dismiss();

                        }
                    } else {
                        Toast.makeText(mContext, "تعداد مورد نظر صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "تعداد مورد نظر صحیح نمی باشد.", Toast.LENGTH_SHORT).show();

                }

            }
        });


        if (percent.hasFocusable()) {
            percent.selectAll();
        }
        if (price.hasFocusable()) {
            price.selectAll();
        }

    }

    public void buydialog_goodbuy(final int goodcode, final int maxprice,
                                  final int customer_price, int facamount, int amoountnow, final int aRowCode) {

        SharedPreferences shPref;
        shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
        final String pfcode = shPref.getString("prefactor_code", null);
        final String selloff = shPref.getString("selloff", null);


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//title laye nadashte bashim
        dialog.setContentView(R.layout.box_buy);
        Button boxbuy = dialog.findViewById(R.id.box_buy_btn);
        final EditText amount = dialog.findViewById(R.id.box_buy_amount);
        amount.setHint("" + amoountnow);
        final TextView tv = dialog.findViewById(R.id.box_buy_factor);
        final TextView xpr = dialog.findViewById(R.id.box_buy_maxprice);


        tv.setText(dbh.getFactorCustomer(Integer.valueOf(pfcode)));
        xpr.setText("" + maxprice);


        final EditText percent = dialog.findViewById(R.id.box_buy_percent);
        final EditText price = dialog.findViewById(R.id.box_buy_price);
        final TextView sumprice = dialog.findViewById(R.id.box_buy_sumprice);
        final TextView factoramount = dialog.findViewById(R.id.box_buy_facamount);


        if (Integer.parseInt(selloff) == 0) {
            percent.setEnabled(false);
            price.setEnabled(false);
        } else {
            percent.setEnabled(true);
            price.setEnabled(true);
        }

        factoramount.setHint("" + facamount);
        price.setText("" + customer_price);
        if (maxprice > 0) {
            percent.setText("" + (100 - (100 * customer_price / maxprice)));
        } else percent.setText("");

        boxbuy.setText("اصلاح کالای مورد نظر");
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    Integer iPrice = Integer.valueOf(price.getText().toString());
                    Integer iAmount = Integer.valueOf(amount.getText().toString());
                    sumprice.setText("" + iPrice * iAmount);
                } catch (Exception e) {
                    sumprice.setText("");
                }
            }
        });

        percent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (percent.hasFocus()) {
                    Integer iPercent;
                    Integer iAmount;
                    try {
                        iPercent = Integer.valueOf(percent.getText().toString());

                        if (maxprice > 0) {
                            if (iPercent > 100) {
                                iPercent = 100;
                                percent.setText(iPercent + "");
                            }
                            price.setText("" + (maxprice - (maxprice * iPercent / 100)));
                        }
                    } catch (Exception e) {
                        price.setText("" + maxprice);
                    }

                    try {
                        iAmount = Integer.valueOf(amount.getText().toString());
                        sumprice.setText("" + (iAmount * Integer.valueOf(price.getText().toString())));
                    } catch (Exception e) {
                        sumprice.setText("");
                    }
                }

            }
        });

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (price.hasFocus()) {
                    try {
                        Integer iPrice = Integer.valueOf(price.getText().toString());
                        if (maxprice > 0) {
                            percent.setText("" + (100 - (100 * iPrice / maxprice)));
                        } else
                            percent.setText("");

                        Integer iAmount = Integer.valueOf(amount.getText().toString());
                        sumprice.setText("" + iPrice * iAmount);
                    } catch (Exception e) {
                        sumprice.setText("");
                    }
                }
            }
        });

        dialog.show();
        amount.requestFocus();
        amount.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(amount, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);

        boxbuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amo = amount.getText().toString();
                String pr = price.getText().toString();
                if (pr == "") pr = "-1";

                if (!amo.equals("")) {
                    if (Integer.parseInt(amo) != 0) {
                        if (Integer.parseInt(pfcode) != 0) {

                            SharedPreferences shPref;
                            shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                            final String prefactor_code = "prefactor_code";
                            int pfcode = Integer.parseInt(shPref.getString(prefactor_code, null));
                            dbh.InsertPreFactor(pfcode, goodcode, Integer.parseInt(amo), Integer.parseInt(pr), aRowCode);
                            Toast toast = Toast.makeText(mContext, "به سبد خرید اضافه شد", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 10, 10);
                            toast.show();
                            Intent intent = new Intent(mContext, BuyActivity.class);
                            intent.putExtra("PreFac", Integer.parseInt(shPref.getString("prefactor_code", null)));

                            ((Activity) mContext).finish();
                            ((Activity) mContext).overridePendingTransition(0, 0);
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(0, 0);
                        } else {

                            intent = new Intent(mContext, CustomerActivity.class);
                            intent.putExtra("edit", "0");
                            intent.putExtra("factor_code", 0);
                            intent.putExtra("id", 0);
                            mContext.startActivity(intent);
                            dialog.dismiss();

                        }
                    } else {
                        Toast.makeText(mContext, "تعداد مورد نظر صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "تعداد مورد نظر صحیح نمی باشد.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        if (percent.hasFocusable()) {
            percent.selectAll();
        }
        if (price.hasFocusable()) {
            price.selectAll();
        }
    }


    public void sendfactor(final int factor_code) {
        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("testanbar_response = ", response + "");
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    int code = jo.getInt("GoodCode");
                    if (code == 0) {
                        Integer kowsarcode = jo.getInt("PreFactorCode");
                        if (kowsarcode > 0) {
                            Toast toast = Toast.makeText(mContext, "پیش فاکتور با موفقیت ارسال شد", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 10, 10);
                            toast.show();
                            String factorDate = jo.getString("PreFactorDate");
                            Log.e("testanbar_factorcode  ", kowsarcode.toString());
                            Log.e("testanbar_factordate  ", factorDate);
                            dbh.UpdatePreFactor(factor_code, kowsarcode, factorDate);
                            shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                            SharedPreferences.Editor sEdit = shPref.edit();
                            sEdit.putString("prefactor_code", "0");
                            sEdit.apply();
                            intent = new Intent(mContext, NavActivity.class);

                            ((Activity) mContext).finish();
                            ((Activity) mContext).overridePendingTransition(0, 0);
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(0, 0);

                        } else {
                            Toast toast = Toast.makeText(mContext, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 10, 10);
                            toast.show();
                        }

                    } else {
                        SQLiteDatabase dtb = mContext.openOrCreateDatabase("KowsarDb.sqlite", Context.MODE_PRIVATE, null);
                        for (int i = 0; i < il; i++) {
                            jo = object.getJSONObject(i);
                            code = jo.getInt("GoodCode");
                            int flag = jo.getInt("Flag");
                            dtb.execSQL("Update PreFactor set Shortage = " + flag + " Where IfNull(PreFactorCode,0)=" + factor_code + " And GoodRef = " + code);
                        }
                        Toast.makeText(mContext, "کالاهای مورد نظر کسر موجودی دارند!", Toast.LENGTH_SHORT).show();
                        intent = new Intent(mContext, BuyActivity.class);
                        intent.putExtra("PreFac", Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))));

                        ((Activity) mContext).finish();
                        ((Activity) mContext).overridePendingTransition(0, 0);
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(0, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "بروز خطا در اطلاعات", Toast.LENGTH_SHORT).show();
                    Log.e("testanbar_prin", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(mContext, "ارتباط با سرور میسر نمی باشد.", Toast.LENGTH_SHORT).show();
                Log.e("testanbar_prin", volleyError.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "PFQASWED");

                SQLiteDatabase dtb = mContext.openOrCreateDatabase("KowsarDb.sqlite", Context.MODE_PRIVATE, null);

                Cursor pc = dtb.rawQuery("Select PreFactorCode, PreFactorDate, PreFactorExplain, CustomerRef, BrokerRef From PreFactorHeader Where PreFactorCode = " + factor_code, null);
                // pr1 = CursorToJson(pc);
                String pr1 = CursorToJson(pc);
                pc.close();


                Log.e("testanbar_reqqqq", pr1);
                params.put("PFHDQASW", pr1);
                Cursor c = dtb.rawQuery("Select GoodRef, FactorAmount, Price From PreFactor Where  GoodRef>0 and  Prefactorcode = " + factor_code, null);

                String pr2 = CursorToJson(c);
                //pr2 = CursorToJson(c);
                c.close();

                Log.e("testanbar_reqqqq", pr2);
                params.put("PFDTQASW", pr2);
                return params;
            }

        };
        queue.add(stringrequste);
    }


    public void edit_explain(final int factor_code) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//title laye nadashte bashim
        dialog.setContentView(R.layout.pf_detail);
        Button pf_detail_btn = dialog.findViewById(R.id.pf_detail_btn);
        pf_detail_btn.setText("ثبت توضیحات");
        final EditText pf_detail_detail = dialog.findViewById(R.id.pf_detail_detail);
        dialog.show();
        pf_detail_detail.requestFocus();
        pf_detail_detail.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(pf_detail_detail, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);

        pf_detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String detail = arabicToenglish(pf_detail_detail.getText().toString());
                dbh.update_explain(factor_code, detail);
                intent = new Intent(mContext, PrefactorActivity.class);
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0, 0);

            }
        });

    }


    void addfactordialog(final int customer_code) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//title laye nadashte bashim
        dialog.setContentView(R.layout.pf_detail);
        Button pf_detail_btn = dialog.findViewById(R.id.pf_detail_btn);
        final EditText pf_detail_detail = dialog.findViewById(R.id.pf_detail_detail);
        dialog.show();
        pf_detail_detail.requestFocus();
        pf_detail_detail.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(pf_detail_detail, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);

        pf_detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbh = new DatabaseHelper(mContext);

                String detail = pf_detail_detail.getText().toString();
                //  ,""+detail+customer_code);
                dbh.InsertPreFactorHeader(detail, customer_code);
                SharedPreferences shPref;
                shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                String prefactor_code = "prefactor_code";
                SharedPreferences.Editor sEdit = shPref.edit();
                sEdit.putString(prefactor_code, dbh.GetLastPreFactorHeader().toString());
                sEdit.apply();
                Toast.makeText(mContext, "ثبت گردید", Toast.LENGTH_SHORT).show();
                intent = new Intent(mContext, SearchActivity.class);
                intent.putExtra("scan", " ");

                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                ((Activity) mContext).startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0, 0);


            }
        });

    }


    public void app_info() {

        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(mContext
                .getContentResolver(), Settings.Secure.ANDROID_ID);
        String Date = Utilities.getCurrentShamsidate();
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        Log.e("testanbar_device_Id", android_id);
        Log.e("testanbar_address_ip", mContext.getString(R.string.SERVERIP));
        Log.e("testanbar_server_name", mContext.getString(R.string.app_name));
        Log.e("testanbar_factor_code", Objects.requireNonNull(shPref.getString("prefactor_code", null)));
        Log.e("testanbar_Date", Date);
        Log.e("testanbar_strDate", strDate);
        UserInfo auser = dbh.LoadPersonalInfo();

        APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(APIInterface.class);
        Call<String> cl = apiInterface.Kowsar_log("Log_report", android_id, mContext.getString(R.string.SERVERIP), mContext.getString(R.string.app_name), shPref.getString("prefactor_code", null), Date + "--" + strDate, auser.getBrokerCode(), "");
        cl.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("testanbar_onResponse", "" + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("testanbar_onFailure", "" + t.toString());
            }
        });

    }


    public String arabicToenglish(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }

    public String CursorToJson(Cursor cur) {
        JSONArray resultSet = new JSONArray();
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            int totalColumn = cur.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cur.getColumnName(i) != null) {
                    try {
                        rowObject.put(cur.getColumnName(i), cur.getString(i));
                    } catch (Exception e) {
                        Log.d("CursorToJson_Error: ", Objects.requireNonNull(e.getMessage()));
                    }
                }
            }
            resultSet.put(rowObject);
            cur.moveToNext();
        }
        cur.close();
        return resultSet.toString();
    }

    public byte[] Text_To_Byte(String Str_Text, boolean Reverse) {
        char[] Text = Str_Text.toCharArray();
        byte[] Text_Byte = new byte[Text.length];
        for (int i = 0; i < Text.length; i++) {
            boolean Monfasel_Pre = true;
            boolean Monfasel_Post = true;
            switch (Text[i]) {

                case 1548:
                    Text_Byte[i] = -118;
                    break;
                case 1567:
                    Text_Byte[i] = -116;
                    break;
                case 1569:
                    Text_Byte[i] = -113;
                    break;
                case 1570:
                    Text_Byte[i] = -115;
                    break;
                case 1574:
                    Text_Byte[i] = -114;
                    break;
                case 1575:
                    try {
                        if (!Monfasel_Char(Text[i + 1]) && !Is_English(Text[i + 1])) {
                            Text_Byte[i] = -111;
                            break;
                        } else {
                            Text_Byte[i] = -112;
                            break;
                        }
                    } catch (Exception e) {
                        Text_Byte[i] = -112;
                        break;
                    }
                case 1576:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -109;
                            break;
                        } else {
                            Text_Byte[i] = -110;
                            break;
                        }
                    } catch (Exception e2) {
                        Text_Byte[i] = -110;
                        break;
                    }
                case 1578:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -105;
                            break;
                        } else {
                            Text_Byte[i] = -106;
                            break;
                        }
                    } catch (Exception e3) {
                        Text_Byte[i] = -106;
                        break;
                    }
                case 1579:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -103;
                            break;
                        } else {
                            Text_Byte[i] = -104;
                            break;
                        }
                    } catch (Exception e4) {
                        Text_Byte[i] = -104;
                        break;
                    }
                case 1580:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -101;
                            break;
                        } else {
                            Text_Byte[i] = -102;
                            break;
                        }
                    } catch (Exception e5) {
                        Text_Byte[i] = -102;
                        break;
                    }
                case 1581:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -97;
                            break;
                        } else {
                            Text_Byte[i] = -98;
                            break;
                        }
                    } catch (Exception e6) {
                        Text_Byte[i] = -98;
                        break;
                    }
                case 1582:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -95;
                            break;
                        } else {
                            Text_Byte[i] = -96;
                            break;
                        }
                    } catch (Exception e7) {
                        Text_Byte[i] = -96;
                        break;
                    }
                case 1583:
                    Text_Byte[i] = -94;
                    break;
                case 1584:
                    Text_Byte[i] = -93;
                    break;
                case 1585:
                    Text_Byte[i] = -92;
                    break;
                case 1586:
                    Text_Byte[i] = -91;
                    break;
                case 1587:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -88;
                            break;
                        } else {
                            Text_Byte[i] = -89;
                            break;
                        }
                    } catch (Exception e8) {
                        Text_Byte[i] = -89;
                        break;
                    }
                case 1588:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -86;
                            break;
                        } else {
                            Text_Byte[i] = -87;
                            break;
                        }
                    } catch (Exception e9) {
                        Text_Byte[i] = -87;
                        break;
                    }
                case 1589:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -84;
                            break;
                        } else {
                            Text_Byte[i] = -85;
                            break;
                        }
                    } catch (Exception e10) {
                        Text_Byte[i] = -85;
                        break;
                    }
                case 1590:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -82;
                            break;
                        } else {
                            Text_Byte[i] = -83;
                            break;
                        }
                    } catch (Exception e11) {
                        Text_Byte[i] = -83;
                        break;
                    }
                case 1591:
                    Text_Byte[i] = -81;
                    break;
                case 1592:
                    Text_Byte[i] = -32;
                    break;
                case 1593:
                    try {
                        if (!Monfasel_Char(Text[i + 1]) && !Is_English(Text[i + 1])) {
                            Monfasel_Pre = false;
                        }
                    } catch (Exception e12) {
                    }
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Monfasel_Post = false;
                        }
                    } catch (Exception e13) {
                    }
                    if (!Monfasel_Pre || !Monfasel_Post) {
                        if (Monfasel_Pre || !Monfasel_Post) {
                            if (!Monfasel_Pre || Monfasel_Post) {
                                if (!Monfasel_Pre && !Monfasel_Post) {
                                    Text_Byte[i] = -29;
                                    break;
                                }
                            } else {
                                Text_Byte[i] = -28;
                                break;
                            }
                        } else {
                            Text_Byte[i] = -30;
                            break;
                        }
                    } else {
                        Text_Byte[i] = -31;
                        break;
                    }
                case 1594:
                    try {
                        if (!Monfasel_Char(Text[i + 1]) && !Is_English(Text[i + 1])) {
                            Monfasel_Pre = false;
                        }
                    } catch (Exception e14) {
                    }
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Monfasel_Post = false;
                        }
                    } catch (Exception e15) {
                    }
                    if (!Monfasel_Pre || !Monfasel_Post) {
                        if (Monfasel_Pre || !Monfasel_Post) {
                            if (!Monfasel_Pre || Monfasel_Post) {
                                if (!Monfasel_Pre && !Monfasel_Post) {
                                    Text_Byte[i] = -25;
                                    break;
                                }
                            } else {
                                Text_Byte[i] = -24;
                                break;
                            }
                        } else {
                            Text_Byte[i] = -26;
                            break;
                        }
                    } else {
                        Text_Byte[i] = -27;
                        break;
                    }
                case 1600:
                    Text_Byte[i] = -117;
                    break;
                case 1601:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -22;
                            break;
                        } else {
                            Text_Byte[i] = -23;
                            break;
                        }
                    } catch (Exception e16) {
                        Text_Byte[i] = -23;
                        break;
                    }
                case 1602:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -20;
                            break;
                        } else {
                            Text_Byte[i] = -21;
                            break;
                        }
                    } catch (Exception e17) {
                        Text_Byte[i] = -21;
                        break;
                    }
                case 1604:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -13;
                            break;
                        } else {
                            Text_Byte[i] = -15;
                            break;
                        }
                    } catch (Exception e18) {
                        Text_Byte[i] = -15;
                        break;
                    }
                case 1605:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -11;
                            break;
                        } else {
                            Text_Byte[i] = -12;
                            break;
                        }
                    } catch (Exception e19) {
                        Text_Byte[i] = -12;
                        break;
                    }
                case 1606:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -9;
                            break;
                        } else {
                            Text_Byte[i] = -10;
                            break;
                        }
                    } catch (Exception e20) {
                        Text_Byte[i] = -10;
                        break;
                    }
                case 1607:
                    try {
                        if (!Monfasel_Char(Text[i + 1]) && !Is_English(Text[i + 1])) {
                            Monfasel_Pre = false;
                        }
                    } catch (Exception e21) {
                    }
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Monfasel_Post = false;
                        }
                    } catch (Exception e22) {
                    }
                    if (!Monfasel_Pre || !Monfasel_Post) {
                        if (Monfasel_Pre || !Monfasel_Post) {
                            if (!Monfasel_Pre || Monfasel_Post) {
                                if (!Monfasel_Pre && !Monfasel_Post) {
                                    Text_Byte[i] = -6;
                                    break;
                                }
                            } else {
                                Text_Byte[i] = -5;
                                break;
                            }
                        } else {
                            Text_Byte[i] = -7;
                            break;
                        }
                    } else {
                        Text_Byte[i] = -7;
                        break;
                    }
                case 1608:
                    Text_Byte[i] = -8;
                    break;
                case 1662:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -107;
                            break;
                        } else {
                            Text_Byte[i] = -108;
                            break;
                        }
                    } catch (Exception e23) {
                        Text_Byte[i] = -108;
                        break;
                    }
                case 1670:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -99;
                            break;
                        } else {
                            Text_Byte[i] = -100;
                            break;
                        }
                    } catch (Exception e24) {
                        Text_Byte[i] = -100;
                        break;
                    }
                case 1688:
                    Text_Byte[i] = -90;
                    break;
                case 1705:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -18;
                            break;
                        } else {
                            Text_Byte[i] = -19;
                            break;
                        }
                    } catch (Exception e25) {
                        Text_Byte[i] = -19;
                        break;
                    }
                case 1711:
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Text_Byte[i] = -16;
                            break;
                        } else {
                            Text_Byte[i] = -17;
                            break;
                        }
                    } catch (Exception e26) {
                        Text_Byte[i] = -17;
                        break;
                    }
                case 1740:
                    try {
                        if (!Monfasel_Char(Text[i + 1]) && !Is_English(Text[i + 1])) {
                            Monfasel_Pre = false;
                        }
                    } catch (Exception e27) {
                    }
                    try {
                        if (Text[i - 1] != ' ' && !Is_English(Text[i - 1])) {
                            Monfasel_Post = false;
                        }
                    } catch (Exception e28) {
                    }
                    if (!Monfasel_Pre || !Monfasel_Post) {
                        if (Monfasel_Pre || !Monfasel_Post) {
                            if (!Monfasel_Pre || Monfasel_Post) {
                                if (!Monfasel_Pre && !Monfasel_Post) {
                                    Text_Byte[i] = -2;
                                    break;
                                }
                            } else {
                                Text_Byte[i] = -2;
                                break;
                            }
                        } else {
                            Text_Byte[i] = -3;
                            break;
                        }
                    } else {
                        Text_Byte[i] = -4;
                        break;
                    }
                case 1776:
                    Text_Byte[i] = Byte.MIN_VALUE;
                    break;
                case 1777:
                    Text_Byte[i] = -127;
                    break;
                case 1778:
                    Text_Byte[i] = -126;
                    break;
                case 1779:
                    Text_Byte[i] = -125;
                    break;
                case 1780:
                    Text_Byte[i] = -124;
                    break;
                case 1781:
                    Text_Byte[i] = -123;
                    break;
                case 1782:
                    Text_Byte[i] = -122;
                    break;
                case 1783:
                    Text_Byte[i] = -121;
                    break;
                case 1784:
                    Text_Byte[i] = -120;
                    break;
                case 1785:
                    Text_Byte[i] = -119;
                    break;
                default:
                    Text_Byte[i] = (byte) Text[i];
                    break;
            }
        }
        if (Reverse) {
            return Reverse_Byte(Text_Byte);
        }
        return Text_Byte;
    }


    public boolean Monfasel_Char(char Chr) {
        if (Chr == 1575 || Chr == 1583 || Chr == 25136 || Chr == 1585 || Chr == 1586 || Chr == 1688) {
            return true;
        }
        return false;
    }

    public boolean Is_English(char Chr) {
        switch (Chr) {
            case 0:
                return true;
            case 1:
                return true;
            case 2:
                return true;
            case 3:
                return true;
            case 4:
                return true;
            case 5:
                return true;
            case 6:
                return true;
            case 7:
                return true;
            case 8:
                return true;
            case 9:
                return true;
            case 10:
                return true;
            case 11:
                return true;
            case 12:
                return true;
            case 13:
                return true;
            case 14:
                return true;
            case 15:
                return true;
            case 16:
                return true;
            case 17:
                return true;
            case 18:
                return true;
            case 19:
                return true;
            case 20:
                return true;
            case 21:
                return true;
            case 22:
                return true;
            case 23:
                return true;
            case 24:
                return true;
            case 25:
                return true;
            case 26:
                return true;
            case 27:
                return true;
            case 28:
                return true;
            case 29:
                return true;
            case 30:
                return true;
            case 31:
                return true;
            case ' ':
                return true;
            case '!':
                return true;
            case '\"':
                return true;
            case '#':
                return true;
            case '$':
                return true;
            case '%':
                return true;
            case '&':
                return true;
            case '\'':
                return true;
            case '(':
                return true;
            case ')':
                return true;
            case '*':
                return true;
            case '+':
                return true;
            case ',':
                return true;
            case '-':
                return true;
            case '.':
                return true;
            case '/':
                return true;
            case '0':
                return true;
            case '1':
                return true;
            case '2':
                return true;
            case '3':
                return true;
            case '4':
                return true;
            case '5':
                return true;
            case '6':
                return true;
            case '7':
                return true;
            case '8':
                return true;
            case '9':
                return true;
            case ':':
                return true;
            case ';':
                return true;
            case '<':
                return true;
            case '=':
                return true;
            case '>':
                return true;
            case '?':
                return true;
            case '@':
                return true;
            case 'A':
                return true;
            case 'B':
                return true;
            case 'C':
                return true;
            case 'D':
                return true;
            case 'E':
                return true;
            case 'F':
                return true;
            case 'G':
                return true;
            case 'H':
                return true;
            case 'I':
                return true;
            case 'J':
                return true;
            case 'K':
                return true;
            case 'L':
                return true;
            case 'M':
                return true;
            case 'N':
                return true;
            case 'O':
                return true;
            case 'P':
                return true;
            case 'Q':
                return true;
            case 'R':
                return true;
            case 'S':
                return true;
            case 'T':
                return true;
            case 'U':
                return true;
            case 'V':
                return true;
            case 'W':
                return true;
            case 'X':
                return true;
            case 'Y':
                return true;
            case 'Z':
                return true;
            case '[':
                return true;
            case '\\':
                return true;
            case ']':
                return true;
            case '^':
                return true;
            case '_':
                return true;
            case '`':
                return true;
            case 'a':
                return true;
            case 'b':
                return true;
            case 'c':
                return true;
            case 'd':
                return true;
            case 'e':
                return true;
            case 'f':
                return true;
            case 'g':
                return true;
            case 'h':
                return true;
            case 'i':
                return true;
            case 'j':
                return true;
            case 'k':
                return true;
            case 'l':
                return true;
            case 'm':
                return true;
            case 'n':
                return true;
            case 'o':
                return true;
            case 'p':
                return true;
            case 'q':
                return true;
            case 'r':
                return true;
            case 's':
                return true;
            case 't':
                return true;
            case 'u':
                return true;
            case 'v':
                return true;
            case 'w':
                return true;
            case 'x':
                return true;
            case 'y':
                return true;
            case 'z':
                return true;
            case '{':
                return true;
            case '|':
                return true;
            case '}':
                return true;
            case '~':
                return true;
            case 1776:
                return true;
            case 1777:
                return true;
            case 1778:
                return true;
            case 1779:
                return true;
            case 1780:
                return true;
            case 1781:
                return true;
            case 1782:
                return true;
            case 1783:
                return true;
            case 1784:
                return true;
            case 1785:
                return true;
            default:
                return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean Is_Symbol(char Chr) {
        switch (Chr) {
            case ':':
                return true;
            case 1542:
                return true;
            case 1548:
                return true;
            case 1567:
                return true;
            case 1569:
                return true;
            case 1570:
                return true;
            case 1600:
                return true;
            default:
                return false;
        }
    }

    /* access modifiers changed from: package-private */
    public byte[] Reverse_Byte(byte[] B) {
        byte[] Reverse_B = new byte[B.length];
        for (int i = B.length - 1; i >= 0; i--) {
            Reverse_B[(B.length - 1) - i] = B[i];
        }
        return Reverse_B;
    }

}
