package com.kits.asli.adapters;


import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kits.asli.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Replication {

    private final Context mContext;

    private String SERVER_IP_ADDRESS;
    private SQLiteDatabase database;
    private Integer RepRowCount = 200;
    private String RepType = "1";
    private String LastRepCode = "0";
    private String RepTable = "";
    private String xCode = "0";
    private Dialog dialog;

    public Replication(Context mContext) {
        this.mContext = mContext;
        SERVER_IP_ADDRESS = mContext.getString(R.string.SERVERIP);
        dialog = new Dialog(mContext);

    }


    public void dialog() {
        dialog.setContentView(R.layout.rep_prog);
        dialog.show();
    }

    public void replicateCentralChange() {

        dialog();
        database = mContext.openOrCreateDatabase("KowsarDb.sqlite", Context.MODE_PRIVATE, null);
        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
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

                                Log.e("repstrQuery", qCol);
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
        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
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

                                Log.e("repstrQuery", qCol);
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
        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
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

                                Log.e("repstrQuery", qCol);
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
        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
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
                                Log.e("repstrQuery", qCol);
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

        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
        RepTable = "Good";
        if (LastRepCode.equals("0")) {
            Cursor c = database.rawQuery(" Select DataValue From Config Where KeyValue = 'Good_LastRepCode' ", null);
            c.moveToFirst();
            LastRepCode = c.getString(c.getColumnIndex("DataValue"));
            c.close();
        }

        Log.e("10.01.LastRepCode=", LastRepCode);
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("10.1.onResponse=", response);
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
//                                                    qCol = qCol + "," + key;
//                                                    qVal = qVal + ",'" + value + "'";

                                                    qCol.append(",").append(key);
                                                    qVal.append(",'").append(value).append("'");
                                                    if (qUpd.toString().equals("")) {
                                                        qUpd = new StringBuilder("Update Good Set " + key + "='" + value + "'");
                                                    }
//                                                   else{qUpd = qUpd+","+key+"='" + value + "'";}
                                                    else {
                                                        qUpd.append(",").append(key).append("='").append(value).append("'");
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
                                Log.e("repstrQuery", qCol.toString());
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
                Log.e("volleyError", volleyError + "");
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
                Log.e("55", 55 + "");
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodStackChange() {

        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
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
                                        //String Amount = String.valueOf((jo.getDouble("Amount") - jo.getDouble("ReservedAmount")));
                                        String Amount = String.valueOf((jo.getDouble("Amount")));
                                        String ReservedAmount = String.valueOf((jo.getDouble("ReservedAmount")));
                                        String ActiveStack = jo.getString("ActiveStack");

                                        Cursor d = database.rawQuery("Select Count(*) AS cntRec From Good Where GoodCode =" + code, null);
                                        d.moveToFirst();

                                    {
                                        qCol = "Update Good Set StackAmount = " + Amount + ", ActiveStack=" + ActiveStack + ", ReservedAmount=" + ReservedAmount + " Where GoodCode=" + code;
                                    }

                                    database.execSQL(qCol);
                                    d.close();
                                    break;
                                }


                                Log.e("repstrQuery", qCol);
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
        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
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
                                                    qCol.append(",").append(key);
                                                    qVal.append(",'").append(value).append("'");
                                                } catch (JSONException ignored) {
                                                }
                                            }
                                        }
                                        qCol = new StringBuilder("INSERT OR REPLACE INTO GoodsGrp( GroupCode " + qCol + ") VALUES(" + code + qVal + ")");
                                        database.execSQL(qCol.toString());
                                        break;
                                }
                                Log.e("repstrQuery", qCol.toString());
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
        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
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
                                                    qCol.append(",").append(key);
                                                    qVal.append(",'").append(value).append("'");
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
                                Log.e("repstrQuery", qCol.toString());
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
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodPropertyValueChange() {
        String url = "http://" + SERVER_IP_ADDRESS + "/login/index.php";
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
                                                    if (qCol.toString().equals("")) {
                                                        qCol = new StringBuilder(key + "='" + value + "'");
                                                    } else {
                                                        qCol.append(",").append(key).append("='").append(value).append("'");
                                                    }
                                                } catch (JSONException ignored) {
                                                }
                                            }
                                        }
                                        qCol = new StringBuilder("Update Good Set " + qCol + " Where GoodCode=" + code);
//                                        qCol = "INSERT OR REPLACE INTO Good( GoodCode " + qCol + ") VALUES(" + code + qVal + ")";
                                        Log.e("repstrQuery", qCol.toString());
                                        database.execSQL(qCol.toString());
                                        break;
                                }
                                Log.e("repstrQuery", qCol.toString());
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


}
