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
                            intent.putExtra("showflag", 2);
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
                    Log.e("asli_response = ", response + "");
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
                            Log.e("asli_factorcode  ", kowsarcode.toString());
                            Log.e("asli_factordate  ", factorDate);
                            dbh.UpdatePreFactor(factor_code, kowsarcode, factorDate);
                            shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                            SharedPreferences.Editor sEdit = shPref.edit();
                            sEdit.putString("prefactor_code", "0");
                            sEdit.apply();
                            intent = new Intent(mContext, NavActivity.class);
                            intent.putExtra("showflag", 2);
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
                        intent.putExtra("showflag", 2);
                        ((Activity) mContext).finish();
                        ((Activity) mContext).overridePendingTransition(0, 0);
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(0, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "بروز خطا در اطلاعات", Toast.LENGTH_SHORT).show();
                    Log.e("asli_printStackTrace", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(mContext, "ارتباط با سرور میسر نمی باشد.", Toast.LENGTH_SHORT).show();
                Log.e("asli_printStackTrace", volleyError.toString());
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


                Log.e("asli_reqqqq", pr1);
                params.put("PFHDQASW", pr1);
                Cursor c = dtb.rawQuery("Select GoodRef, Amount, Price From PreFactor Where  GoodRef>0 and  Prefactorcode = " + factor_code, null);

                String pr2 = CursorToJson(c);
                //pr2 = CursorToJson(c);
                c.close();

                Log.e("asli_reqqqq", pr2);
                params.put("PFDTQASW", pr2);
                return params;
            }

        };
        queue.add(stringrequste);
        Log.e("asli_stringrequste =", stringrequste.toString() + "");
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

        Log.e("asli_device_Id", android_id);
        Log.e("asli_address_ip", mContext.getString(R.string.SERVERIP));
        Log.e("asli_server_name", mContext.getString(R.string.app_name));
        Log.e("asli_factor_code", Objects.requireNonNull(shPref.getString("prefactor_code", null)));
        Log.e("asli_Date", Date);
        Log.e("asli_strDate", strDate);
        UserInfo auser = dbh.LoadPersonalInfo();

        APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(APIInterface.class);
        Call<String> cl = apiInterface.Kowsar_log("Log_report", android_id, mContext.getString(R.string.SERVERIP), mContext.getString(R.string.app_name), shPref.getString("prefactor_code", null), Date + "--" + strDate, auser.getBrokerCode(), "");
        cl.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("asli_onResponse", "" + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("asli_onFailure", "" + t.toString());
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


}
