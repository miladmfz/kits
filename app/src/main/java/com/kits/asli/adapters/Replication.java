package com.kits.asli.adapters;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kits.asli.R;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.UserInfo;
import com.kits.asli.webService.APIClient;
import com.kits.asli.webService.APIInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class Replication {


    private final Context mContext;
    APIInterface apiInterface = APIClient.getCleint().create(APIInterface.class);
    private SharedPreferences.Editor sEdit;

    private SharedPreferences shPref;
    private String SERVER_IP_ADDRESS;
    private SQLiteDatabase database;
    private Integer RepRowCount = 200;
    private String RepType = "1";
    private String LastRepCode = "0";
    private String RepTable = "";
    private String xCode = "0";
    private Dialog dialog;
    private DatabaseHelper dbh;
    String url;

    public Replication(Context mContext) {
        this.mContext = mContext;
        this.dbh = new DatabaseHelper(mContext);
        SERVER_IP_ADDRESS = mContext.getString(R.string.SERVERIP);
        dialog = new Dialog(mContext);
        shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
        database = mContext.openOrCreateDatabase("KowsarDb.sqlite", Context.MODE_PRIVATE, null);
        url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
    }


    public void dialog() {
        dialog.setContentView(R.layout.rep_prog);
        dialog.show();
    }


    public void BrokerStack() {
        UserInfo auser = dbh.LoadPersonalInfo();
        Call<String> call1 = apiInterface.BrokerStack("BrokerStack", auser.getBrokerCode());
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().equals(shPref.getString("brokerstack", null))) {
                        sEdit = shPref.edit();
                        sEdit.putString("brokerstack", response.body());
                        sEdit.apply();
                        Log.e("brokerstack=", "" + shPref.getString("brokerstack", ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });

    }

    public void replicate_all() {
        replicateCentralChange();
    }


    public void replicate_customer() {
        replicateCentralChange_customer();
    }

    public void replicateCentralChange() {

        dialog();
        RepTable = "Central";
        if (LastRepCode.equals("0")) {

            Cursor cd = database.rawQuery("Select DataValue From Config Where KeyValue = 'Central_LastRepCode'", null);
            cd.moveToFirst();
            LastRepCode = cd.getString(0);
            cd.close();
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");

                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:

                            for (int i = 0; i < il; i++) {

                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("CentralCode");
                                String qCol = "";

                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":

                                        String CentralPrivateCode = jo.getString("CentralPrivateCode");
                                        String CentralName = (jo.getString("Title") + jo.getString("FName") + jo.getString("Name")).trim();
                                        CentralName = CentralName.replaceAll("'", "''");

                                        String Manager = jo.getString("Manager");
                                        String Delegacy = jo.getString("Delegacy");
                                        String D_CodeMelli = jo.getString("D_CodeMelli");

                                        Cursor d = database.rawQuery("Select Count(*) AS cntRec From Central Where CentralCode =" + code, null);
                                        d.moveToFirst();
                                        int nc = d.getInt(d.getColumnIndex("cntRec"));
                                        if (nc == 0) {
                                            qCol = "INSERT INTO Central(CentralCode, CentralPrivateCode, CentralName, Manager, Delegacy,D_CodeMelli) Select " + code + "," + CentralPrivateCode + ",'" + CentralName + "','" + Manager + "','" + Delegacy + "','" + D_CodeMelli + "'";
                                        } else {
                                            qCol = "Update Central Set CentralPrivateCode=" + CentralPrivateCode + ", CentralName='" + CentralName + "', Manager='" + Manager + "', Delegacy='" + Delegacy + "', D_CodeMelli='" + D_CodeMelli + "' Where CentralCode=" + code;
                                        }

                                        database.execSQL(qCol);
                                        d.close();
                                        break;
                                }

                                Log.e("asli_repstrQuery", qCol);
                                xCode = code;
                                LastRepCode = repcode;

                            }

                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Central_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateCentralChange();
                } else {
                    LastRepCode = "0";
                    replicateCityChange();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);

    }

    public void replicateCityChange() {

        RepTable = "City";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='City_LastRepCode'", null);
            c.moveToFirst();
            LastRepCode = c.getString(0);
            c.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("CityCode");
                                String qCol = "";

                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        String CityName = jo.getString("Name");

                                        Cursor d = database.rawQuery("Select Count(*) AS cntRec From City Where CityCode =" + code, null);
                                        d.moveToFirst();
                                        int nc = d.getInt(d.getColumnIndex("cntRec"));
                                        if (nc == 0) {
                                            qCol = "INSERT INTO City(CityCode, CityName) Select " + code + ",'" + CityName + "'";
                                        } else {
                                            qCol = "Update City Set CityName='" + CityName + "' Where CityCode=" + code;
                                        }

                                        database.execSQL(qCol);
                                        d.close();
                                        break;
                                }

                                Log.e("asli_repstrQuery", qCol);
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'City_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateCityChange();
                } else {
                    LastRepCode = "0";
                    replicateAddressChange();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);

    }

    public void replicateAddressChange() {

        RepTable = "Address";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='Address_LastRepCode'", null);
            c.moveToFirst();
            LastRepCode = c.getString(0);
            c.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("AddressCode");
                                String qCol = "";

                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        String CentralRef = jo.getString("CentralRef");
                                        String CityCode = jo.getString("CityCode");
                                        String Address = jo.getString("Address");
                                        Address = Address.replaceAll("'", "''");

                                        String Phone = jo.getString("Phone");
                                        String Mobile = jo.getString("Mobile");
                                        String MobileName = jo.getString("MobileName");
                                        String Email = jo.getString("Email");
                                        String Fax = jo.getString("Fax");
                                        String ZipCode = jo.getString("ZipCode");
                                        String PostCode = jo.getString("PostCode");

                                        Cursor d = database.rawQuery("Select Count(*) AS cntRec From Address Where AddressCode =" + code, null);
                                        d.moveToFirst();
                                        int nc = d.getInt(d.getColumnIndex("cntRec"));
                                        if (nc == 0) {
                                            qCol = "INSERT INTO Address(AddressCode, CentralRef, CityCode, Address, Phone, Mobile, MobileName, Email, Fax, ZipCode, PostCode) Select " + code + "," + CentralRef + "," + CityCode + ",'" + Address + "','" + Phone + "','" + Mobile + "','" + MobileName + "','" + Email + "','" + Fax + "','" + ZipCode + "','" + PostCode + "'";
                                        } else {
                                            qCol = "Update Address Set CentralRef=" + CentralRef + ", CityCode=" + CityCode + ", Address='" + Address + "', Phone='" + Phone + "', Mobile='" + Mobile + "', MobileName='" + MobileName + "', Email='" + Email + "', Fax='" + Fax + "', ZipCode='" + ZipCode + "', PostCode='" + PostCode + "' Where AddressCode=" + code;
                                        }

                                        database.execSQL(qCol);
                                        d.close();
                                        break;
                                }

                                Log.e("asli_repstrQuery", qCol);
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Address_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateAddressChange();
                } else {
                    LastRepCode = "0";
                    replicateCustomerChange();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateCustomerChange() {

        RepTable = "Customer";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='Customer_LastRepCode'", null);
            c.moveToFirst();
            LastRepCode = c.getString(0);
            c.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("CustomerCode");
                                String qCol = "";

                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        String CentralRef = jo.getString("CentralRef");
                                        String AddressRef = jo.getString("AddressRef");
                                        String Bestankar = String.valueOf((jo.getDouble("CustomerBestankar") - jo.getDouble("CustomerBedehkar")));
                                        String Active = jo.getString("Active");
                                        String EtebarNaghd = jo.getString("EtebarNaghd");
                                        String EtebarCheck = jo.getString("EtebarCheck");
                                        String Takhfif = jo.getString("Takhfif");
                                        String PriceTip = jo.getString("PriceTip");
                                        Cursor d = database.rawQuery("Select Count(*) AS cntRec From Customer Where CustomerCode =" + code, null);
                                        d.moveToFirst();
                                        int nc = d.getInt(d.getColumnIndex("cntRec"));
                                        if (nc == 0) {
                                            qCol = "INSERT INTO Customer(CustomerCode, CentralRef, AddressRef, Bestankar, Active, EtebarNaghd, EtebarCheck, Takhfif, PriceTip) Select " + code + "," + CentralRef + "," + AddressRef + "," + Bestankar + "," + Active + "," + EtebarNaghd + "," + EtebarCheck + "," + Takhfif + "," + PriceTip;
                                        } else {
                                            qCol = "Update Customer Set CentralRef=" + CentralRef + ", AddressRef=" + AddressRef + ", Bestankar=" + Bestankar + ", Active=" + Active + ", EtebarNaghd=" + EtebarNaghd + ", EtebarCheck=" + EtebarCheck + ", Takhfif=" + Takhfif + ", PriceTip=" + PriceTip + " Where CustomerCode=" + code;
                                        }
                                        database.execSQL(qCol);
                                        d.close();
                                        break;
                                }
                                Log.e("asli_repstrQuery", qCol);
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Customer_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateCustomerChange();
                } else {
                    LastRepCode = "0";
                    replicateGoodChange();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodChange() {


        RepTable = "Good";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery(" Select DataValue From Config Where KeyValue = 'Good_LastRepCode' ", null);
            c.moveToFirst();
            LastRepCode = c.getString(c.getColumnIndex("DataValue"));
            c.close();
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("GoodCode");
                                StringBuilder qCol = new StringBuilder();
                                StringBuilder qVal = new StringBuilder();
                                StringBuilder qUpd = new StringBuilder();
                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        Iterator<String> iter = jo.keys();
                                        while (iter.hasNext()) {
                                            String key = iter.next();
                                            if ((!key.equals("RLOpType")) & (!key.equals("RepLogDataCode")) & (!key.equals("GoodCode"))) {
                                                try {
                                                    Object value = jo.get(key);

                                                    value = value.toString().replaceAll("'", "''");
                                                    if (!key.equals("RLClassName")) {
                                                        qCol.append(",").append(key);
                                                        qVal.append(",'").append(value).append("'");

                                                        if (qUpd.toString().equals("")) {
                                                            qUpd = new StringBuilder("Update Good Set " + key + "='" + value + "'");
                                                        } else {
                                                            qUpd.append(",").append(key).append("='").append(value).append("'");
                                                        }
                                                    }
                                                } catch (JSONException ignored) {
                                                }
                                            }
                                        }
                                        Cursor d = database.rawQuery("Select Count(*) As cntRec From Good Where GoodCode =" + code, null);
                                        d.moveToFirst();
                                        int nc = d.getInt(d.getColumnIndex("cntRec"));
                                        if (nc == 0) {
                                            qCol = new StringBuilder("INSERT INTO Good( GoodCode " + qCol + ") VALUES(" + code + qVal + ")");
                                        } else {
                                            qCol = new StringBuilder(qUpd + " Where GoodCode=" + code);
                                        }
                                        database.execSQL(qCol.toString());
                                        d.close();
                                        break;
                                    case "D":
                                    case "d":

                                        database.execSQL("delete from good where goodcode = " + code + " and not exists (select 1 From PreFactor Where GoodRef =" + code + ")");
                                        break;
                                }
                                Log.e("asli_repstrQuery", qCol.toString());
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Good_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateGoodChange();
                } else {
                    LastRepCode = "0";
                    replicateGoodStackChange();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("asli_volleyError", volleyError + "");
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodStackChange() {


        RepTable = "GoodStack";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='GoodStack_LastRepCode'", null);
            c.moveToFirst();
            LastRepCode = c.getString(0);
            c.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("GoodRef");
                                String qCol = "";

                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        String Amount = String.valueOf((jo.getDouble("Amount")));
                                        String ReservedAmount = String.valueOf((jo.getDouble("ReservedAmount")));
                                        String GoodStackCode = String.valueOf((jo.getDouble("GoodStackCode")));
                                        String StackRef = String.valueOf((jo.getDouble("StackRef")));
                                        String ActiveStack = jo.getString("ActiveStack");
                                        Cursor d = database.rawQuery("Select Count(*) AS cntRec From GoodStack Where GoodRef =" + code + " And StackRef=" + StackRef, null);
                                        d.moveToFirst();
                                        int nc = d.getInt(d.getColumnIndex("cntRec"));
                                        if (nc == 0) {
                                            qCol = "INSERT INTO GoodStack( GoodStackCode ,GoodRef,StackRef,Amount,ReservedAmount,ActiveStack)  VALUES ( " + GoodStackCode + "," + code + "," + StackRef + "," + Amount + "," + ReservedAmount + "," + ActiveStack + ")";
                                        } else {
                                            qCol = "Update GoodStack Set Amount = " + Amount + ", ActiveStack=" + ActiveStack + ", ReservedAmount=" + ReservedAmount + " Where GoodRef=" + code + " And StackRef=" + StackRef;
                                        }
                                        database.execSQL(qCol);
                                        d.close();

                                        break;
                                }


                                Log.e("asli_repstrQuery", qCol);
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'GoodStack_LastRepCode'");
                            break;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateGoodStackChange();
                } else {
                    LastRepCode = "0";
                    replicateGoodsGrpChange();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodsGrpChange() {

        RepTable = "GoodsGrp";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='GoodsGrp_LastRepCode'", null);
            c.moveToFirst();
            LastRepCode = c.getString(0);
            c.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("GroupCode");
                                StringBuilder qCol = new StringBuilder();
                                StringBuilder qVal = new StringBuilder();
                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        Iterator<String> iter = jo.keys();
                                        while (iter.hasNext()) {
                                            String key = iter.next();
                                            if ((!key.equals("RLOpType")) & (!key.equals("RepLogDataCode")) & (!key.equals("GroupCode"))) {
                                                try {
                                                    Object value = jo.get(key);
//                                                    qCol = qCol + "," + key;
//                                                    qVal = qVal + ",'" + value + "'";
                                                    value = value.toString().replaceAll("'", "''");
                                                    if (!key.equals("RLClassName")) {

                                                        qCol.append(",").append(key);
                                                        qVal.append(",'").append(value).append("'");
                                                    }
                                                } catch (JSONException ignored) {
                                                }
                                            }
                                        }
                                        qCol = new StringBuilder("INSERT OR REPLACE INTO GoodsGrp( GroupCode " + qCol + ") VALUES(" + code + qVal + ")");
                                        database.execSQL(qCol.toString());
                                        break;
                                }
                                Log.e("asli_repstrQuery", qCol.toString());
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'GoodsGrp_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateGoodsGrpChange();
                } else {
                    LastRepCode = "0";
                    replicateGoodGroupChange();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodGroupChange() {

        RepTable = "GoodGroup";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='GoodGroup_LastRepCode'", null);
            c.moveToFirst();
            LastRepCode = c.getString(0);
            c.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;

                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("GoodGroupCode");
                                StringBuilder qCol = new StringBuilder();
                                StringBuilder qVal = new StringBuilder();
                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        Iterator<String> iter = jo.keys();
                                        while (iter.hasNext()) {
                                            String key = iter.next();
                                            if ((!key.equals("RLOpType")) & (!key.equals("RepLogDataCode")) & (!key.equals("GoodGroupCode"))) {
                                                try {
                                                    Object value = jo.get(key);
                                                    if (!key.equals("RLClassName")) {

                                                        qCol.append(",").append(key);
                                                        qVal.append(",'").append(value).append("'");
                                                    }
                                                } catch (JSONException ignored) {
                                                }
                                            }
                                        }
                                        qCol = new StringBuilder("INSERT OR REPLACE INTO GoodGroup( GoodGroupCode " + qCol + ") VALUES(" + code + qVal + ")");
                                        database.execSQL(qCol.toString());
                                        break;
                                    case "D":
                                    case "d":
                                        database.execSQL("delete from GoodGroup where GoodGroupCode = " + code);
                                        break;
                                }
                                Log.e("asli_repstrQuery", qCol.toString());
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'GoodGroup_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateGoodGroupChange();
                } else {
                    LastRepCode = "0";
                    replicateGoodImageChange();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodImageChange() {


        RepTable = "KsrImage";

        if (LastRepCode.equals("0")) {

            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='KsrImage_LastRepCode'", null);
            c.moveToFirst();

            LastRepCode = c.getString(0);
            c.close();

        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");

                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {

                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                    case "d":
                                    case "D":
                                        String aGoodRef = jo.getString("ObjectRef");
                                        if (!aGoodRef.equals("null")) {
                                            Image_info image_info = new Image_info(mContext);
                                            image_info.DeleteImage(Integer.parseInt(aGoodRef));
                                        }
                                        break;
                                }
                                LastRepCode = repcode;
                                database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'KsrImage_LastRepCode'");
                            }
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateGoodImageChange();
                } else {
                    LastRepCode = "0";
                    replicateGoodPropertyValueChange();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "getImageInfo");
                params.put("code", LastRepCode);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodPropertyValueChange() {

        RepTable = "PropertyValue";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='PropertyValue_LastRepCode'", null);
            c.moveToFirst();
            LastRepCode = c.getString(0);
            c.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("ObjectRef");
                                StringBuilder qCol = new StringBuilder();
//                                String qVal = "";
                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        Iterator<String> iter = jo.keys();
                                        while (iter.hasNext()) {
                                            String key = iter.next();
                                            if ((!key.equals("RLOpType")) & (!key.equals("RepLogDataCode")) & (!key.equals("ObjectRef"))) {
                                                try {
                                                    Object value = jo.get(key);
                                                    value = value.toString().replaceAll("'", "''");
                                                    if (!key.equals("RLClassName")) {

                                                        if (qCol.toString().equals("")) {
                                                            qCol = new StringBuilder(key + "='" + value + "'");
                                                        } else {
                                                            qCol.append(",").append(key).append("='").append(value).append("'");
                                                        }
                                                    }
                                                } catch (JSONException ignored) {
                                                }
                                            }
                                        }
                                        qCol = new StringBuilder("Update Good Set " + qCol + " Where GoodCode=" + code);
//                                        qCol = "INSERT OR REPLACE INTO Good( GoodCode " + qCol + ") VALUES(" + code + qVal + ")";
                                        Log.e("asli_repstrQuery", qCol.toString());
                                        database.execSQL(qCol.toString());
                                        break;
                                }
                                Log.e("asli_repstrQuery", qCol.toString());
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'PropertyValue_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateGoodPropertyValueChange();
                } else {
                    dialog.dismiss();
                    Toast.makeText(mContext, "بروز رسانی انجام شد", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }


    public void replicateCentralChange_customer() {

        dialog();
        database = mContext.openOrCreateDatabase("KowsarDb.sqlite", Context.MODE_PRIVATE, null);

        RepTable = "Central";
        if (LastRepCode.equals("0")) {
            Cursor cd = database.rawQuery("Select DataValue From Config Where KeyValue = 'Central_LastRepCode'", null);
            cd.moveToFirst();
            LastRepCode = cd.getString(0);
            cd.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("CentralCode");
                                String qCol = "";

                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        String CentralPrivateCode = jo.getString("CentralPrivateCode");
                                        String CentralName = (jo.getString("Title") + jo.getString("FName") + jo.getString("Name")).trim();
                                        CentralName = CentralName.replaceAll("'", "''");

                                        String Manager = jo.getString("Manager");
                                        String Delegacy = jo.getString("Delegacy");

                                        Cursor d = database.rawQuery("Select Count(*) AS cntRec From Central Where CentralCode =" + code, null);
                                        d.moveToFirst();
                                        int nc = d.getInt(d.getColumnIndex("cntRec"));
                                        if (nc == 0) {
                                            qCol = "INSERT INTO Central(CentralCode, CentralPrivateCode, CentralName, Manager, Delegacy) Select " + code + "," + CentralPrivateCode + ",'" + CentralName + "','" + Manager + "','" + Delegacy + "'";
                                        } else {
                                            qCol = "Update Central Set CentralPrivateCode=" + CentralPrivateCode + ", CentralName='" + CentralName + "', Manager='" + Manager + "', Delegacy='" + Delegacy + "' Where CentralCode=" + code;
                                        }

                                        database.execSQL(qCol);
                                        d.close();
                                        break;
                                }

                                Log.e("asli_repstrQuery", qCol);
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Central_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateCentralChange_customer();
                } else {
                    LastRepCode = "0";
                    replicateCityChange_customer();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);

    }

    public void replicateCityChange_customer() {

        RepTable = "City";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='City_LastRepCode'", null);
            c.moveToFirst();
            LastRepCode = c.getString(0);
            c.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("CityCode");
                                String qCol = "";

                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        String CityName = jo.getString("Name");

                                        Cursor d = database.rawQuery("Select Count(*) AS cntRec From City Where CityCode =" + code, null);
                                        d.moveToFirst();
                                        int nc = d.getInt(d.getColumnIndex("cntRec"));
                                        if (nc == 0) {
                                            qCol = "INSERT INTO City(CityCode, CityName) Select " + code + ",'" + CityName + "'";
                                        } else {
                                            qCol = "Update City Set CityName='" + CityName + "' Where CityCode=" + code;
                                        }

                                        database.execSQL(qCol);
                                        d.close();
                                        break;
                                }

                                Log.e("asli_repstrQuery", qCol);
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'City_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateCityChange_customer();
                } else {
                    LastRepCode = "0";
                    replicateAddressChange_customer();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);

    }

    public void replicateAddressChange_customer() {

        RepTable = "Address";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='Address_LastRepCode'", null);
            c.moveToFirst();
            LastRepCode = c.getString(0);
            c.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("AddressCode");
                                String qCol = "";

                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        String CentralRef = jo.getString("CentralRef");
                                        String CityCode = jo.getString("CityCode");
                                        String Address = jo.getString("Address");
                                        Address = Address.replaceAll("'", "''");

                                        String Phone = jo.getString("Phone");
                                        String Mobile = jo.getString("Mobile");
                                        String MobileName = jo.getString("MobileName");
                                        String Email = jo.getString("Email");
                                        String Fax = jo.getString("Fax");
                                        String ZipCode = jo.getString("ZipCode");
                                        String PostCode = jo.getString("PostCode");

                                        Cursor d = database.rawQuery("Select Count(*) AS cntRec From Address Where AddressCode =" + code, null);
                                        d.moveToFirst();
                                        int nc = d.getInt(d.getColumnIndex("cntRec"));
                                        if (nc == 0) {
                                            qCol = "INSERT INTO Address(AddressCode, CentralRef, CityCode, Address, Phone, Mobile, MobileName, Email, Fax, ZipCode, PostCode) Select " + code + "," + CentralRef + "," + CityCode + ",'" + Address + "','" + Phone + "','" + Mobile + "','" + MobileName + "','" + Email + "','" + Fax + "','" + ZipCode + "','" + PostCode + "'";
                                        } else {
                                            qCol = "Update Address Set CentralRef=" + CentralRef + ", CityCode=" + CityCode + ", Address='" + Address + "', Phone='" + Phone + "', Mobile='" + Mobile + "', MobileName='" + MobileName + "', Email='" + Email + "', Fax='" + Fax + "', ZipCode='" + ZipCode + "', PostCode='" + PostCode + "' Where AddressCode=" + code;
                                        }

                                        database.execSQL(qCol);
                                        d.close();
                                        break;
                                }

                                Log.e("asli_repstrQuery", qCol);
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Address_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateAddressChange_customer();
                } else {
                    LastRepCode = "0";
                    replicateCustomerChange_customer();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateCustomerChange_customer() {

        RepTable = "Customer";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery("Select DataValue From Config Where KeyValue ='Customer_LastRepCode'", null);
            c.moveToFirst();
            LastRepCode = c.getString(0);
            c.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int il = 0;
                try {
                    JSONArray object = new JSONArray(response);
                    JSONObject jo = object.getJSONObject(0);
                    il = object.length();
                    String state = jo.getString("RLOpType");
                    switch (state) {
                        case "n":
                        case "N":
                            break;
                        default:
                            for (int i = 0; i < il; i++) {
                                jo = object.getJSONObject(i);
                                String optype = jo.getString("RLOpType");
                                String repcode = jo.getString("RepLogDataCode");
                                String code = jo.getString("CustomerCode");
                                String qCol = "";

                                switch (optype) {
                                    case "U":
                                    case "u":
                                    case "I":
                                    case "i":
                                        String CentralRef = jo.getString("CentralRef");
                                        String AddressRef = jo.getString("AddressRef");
                                        String Bestankar = String.valueOf((jo.getDouble("CustomerBestankar") - jo.getDouble("CustomerBedehkar")));
                                        String Active = jo.getString("Active");
                                        String EtebarNaghd = jo.getString("EtebarNaghd");
                                        String EtebarCheck = jo.getString("EtebarCheck");
                                        String Takhfif = jo.getString("Takhfif");
                                        String PriceTip = jo.getString("PriceTip");
                                        Cursor d = database.rawQuery("Select Count(*) AS cntRec From Customer Where CustomerCode =" + code, null);
                                        d.moveToFirst();
                                        int nc = d.getInt(d.getColumnIndex("cntRec"));
                                        if (nc == 0) {
                                            qCol = "INSERT INTO Customer(CustomerCode, CentralRef, AddressRef, Bestankar, Active, EtebarNaghd, EtebarCheck, Takhfif, PriceTip) Select " + code + "," + CentralRef + "," + AddressRef + "," + Bestankar + "," + Active + "," + EtebarNaghd + "," + EtebarCheck + "," + Takhfif + "," + PriceTip;
                                        } else {
                                            qCol = "Update Customer Set CentralRef=" + CentralRef + ", AddressRef=" + AddressRef + ", Bestankar=" + Bestankar + ", Active=" + Active + ", EtebarNaghd=" + EtebarNaghd + ", EtebarCheck=" + EtebarCheck + ", Takhfif=" + Takhfif + ", PriceTip=" + PriceTip + " Where CustomerCode=" + code;
                                        }
                                        database.execSQL(qCol);
                                        d.close();
                                        break;
                                }
                                Log.e("asli_repstrQuery", qCol);
                                xCode = code;
                                LastRepCode = repcode;
                            }
                            database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Customer_LastRepCode'");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (il >= RepRowCount) {
                    replicateCustomerChange_customer();
                } else {
                    dialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }


}
