package com.kits.asli.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kits.asli.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class DatabaseHelper extends SQLiteOpenHelper {

    private SharedPreferences shPref;
    private Context mContext;
    @SuppressLint("SdCardPath")
    private static final String DATABASE_NAME = "/data/data/com.kits.asli/databases/KowsarDb.sqlite";
    private static final String KEY_CODE = "GoodCode";
    private static final String KEY_NAME = "GoodName";
    private static final String KEY3 = "GoodExplain1";
    private static final String KEY4 = "GoodExplain2";
    private static final String KEY5 = "FirstBarCode";
    private static final String KEY6 = "MaxSellPrice";
    private static final String KEY7 = "GoodExplain6";
    private static final String KEY8 = "HasAmount";
    private static final String KEY9 = "Writer";
    private static final String KEY10 = "TahvilDate";
    private static final String KEY11 = "Dragoman";
    private static final String KEY12 = "PrintPeriod";
    private static final String KEY13 = "PrintYear";
    private static final String KEY14 = "Size";
    private static final String KEY15 = "CoverType";
    private static final String KEY16 = "Tiraj";
    private static final String KEY17 = "PageNo";
    private static final String KEY18 = "Weight";
    private static final String KEY19 = "GroupsWhitoutCode";
    private static final String KEY20 = "StackAmount";
    private static final String KEY21 = "SellPrice1";
    private static final String KEY22 = "SellPrice2";
    private static final String KEY23 = "SellPrice3";
    private static final String KEY24 = "SellPrice4";
    private static final String KEY25 = "SellPrice5";
    private static final String KEY26 = "SellPrice6";
    private static final String KEY27 = "GoodType";
    private static final String KEY28 = "MinSellPrice";
    private static final String KEY29 = "UnitName";
    private static final String KEY30 = "DefaultUnitValue";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.mContext = context;

    }


    public ArrayList<GoodGroup> getAllGroups(String GName, Integer GL) {
        String gs = GL.toString();

        // Exists(Select GoodRef From GoodGroup Where GoodGroupRef in(s.GroupCode, s.L1, s.L2, s.L3, s.L4, s.L5)) And
        String query = "SELECT * FROM GoodsGrp s WHERE Name like '%" + GName + "%'";
        if (GL > 0) {
            query = query + " And ((L1=" + gs + " And L2=0) or (L2=" + gs + " And L3=0) or (L3=" + gs + " And L4=0) or (L4=" + gs + " And L5=0) or (L5=" + gs + "))";
        } else {
            query = query + " And L1>0 and L2=0 order by 1 desc";
        }

        ArrayList<GoodGroup> groups = new ArrayList<GoodGroup>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        if (c != null) {
            while (c.moveToNext()) {
                GoodGroup grp = new GoodGroup();
                grp.setGoodGroupCode(c.getInt(c.getColumnIndex("GroupCode")));
                grp.setName(c.getString(c.getColumnIndex("Name")));
                grp.setL1(c.getInt(c.getColumnIndex("L1")));
                grp.setL2(c.getInt(c.getColumnIndex("L2")));
                grp.setL3(c.getInt(c.getColumnIndex("L3")));
                grp.setL4(c.getInt(c.getColumnIndex("L4")));
                grp.setL5(c.getInt(c.getColumnIndex("L5")));
                groups.add(grp);

            }
        }
        c.close();
        return groups;
    }

    public ArrayList<Good> getAllGood_Extended(String aFromDate, String aToDate, Integer aGroupCode, String name, String aWriter, String aDragoMan, String aNasher, Integer aPrintPeriod, String aPrintYear, Boolean aOnlyActive, Boolean aOnlyAvailable) {
        String cond = " Where 1=1";
        String query = "SELECT *, 0 Amount, 0 Shortage, 0 Price, 0 RowCode  FROM Good Join Units on UnitCode = GoodUnitRef ";
        if (aOnlyActive) {
            cond = cond + " And ActiveStack = 1";
        }
        if (aOnlyAvailable) {
            SharedPreferences shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
            if (shPref.getBoolean("real_amount", true)) {
                cond = cond + " And StackAmount-ReservedAmount > 0 ";
            } else {
                cond = cond + " And StackAmount > 0 ";
            }
        }
        if (!aFromDate.equals("")) {
            cond = cond + " And Date2 >= '" + aFromDate + "'";
        }
        if (!aToDate.equals("")) {
            cond = cond + " And Date2 <= '" + aToDate + "'";
        }
        if (aGroupCode > 0) {
            cond = cond + " And GoodCode in(Select GoodRef From GoodGroup p "
                    + "Join GoodsGrp s on p.GoodGroupRef = s.GroupCode "
                    + "Where s.GroupCode = " + aGroupCode.toString() + " or s.L1 = " + aGroupCode.toString()
                    + " or s.L2 = " + aGroupCode.toString() + " or s.L3 = " + aGroupCode.toString()
                    + " or s.L4 = " + aGroupCode.toString() + " or s.L5 = " + aGroupCode.toString() + ")";
        }
        if (!name.equals("")) {
            cond = cond + " And GoodName Like '%" + name.replaceAll(" ", "%") + "%'";
        }
        if (!aWriter.equals("")) {
            cond = cond + " And nvarchar1 Like '%" + aWriter.replaceAll(" ", "%") + "%'";
        }
        if (!aDragoMan.equals("")) {
            cond = cond + " And nvarchar2 Like '%" + aDragoMan.replaceAll(" ", "%") + "%'";
        }
        if (!aPrintYear.equals("")) {
            cond = cond + " And nvarchar9 Like '%" + aPrintYear.replaceAll(" ", "%") + "%'";
        }
        if (!aNasher.equals("")) {
            cond = cond + " And GoodExplain1 Like '%" + aNasher.replaceAll(" ", "%") + "%'";
        }
        if (aPrintPeriod > 0) {
            cond = cond + " And Float1 = " + aPrintPeriod;
        }

        query = query + cond + " order by Date2 DESC, GoodCode DESC LIMIT 250 ";

        ArrayList<Good> goods = new ArrayList<Good>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);// exect vase anjame ye amaliyat ______ rawquery vase gereftane ye etelaatii k az db mikhaym

        if (c != null) {
            while (c.moveToNext()) {
                Good gooddetail = new Good();
                gooddetail.setShortage(c.getInt(c.getColumnIndex("Shortage")));
                gooddetail.setGoodCode(c.getInt(c.getColumnIndex(KEY_CODE)));
                gooddetail.setRowCode(c.getInt(c.getColumnIndex("RowCode")));
                gooddetail.setGoodName(c.getString(c.getColumnIndex(KEY_NAME)));
                gooddetail.setGoodExplain1(c.getString(c.getColumnIndex(KEY3)));
                gooddetail.setGoodExplain2(c.getString(c.getColumnIndex(KEY4)));
                gooddetail.setFirstBarCode(c.getString(c.getColumnIndex(KEY5)));
                gooddetail.setMaxSellPrice(c.getInt(c.getColumnIndex(KEY6)));
                gooddetail.setSellPrice1(c.getInt(c.getColumnIndex("SellPrice1")));
                gooddetail.setSellPrice1(c.getInt(c.getColumnIndex("SellPrice1")));
                gooddetail.setPrice(c.getInt(c.getColumnIndex("Price")));
                gooddetail.setImageName(c.getString(c.getColumnIndex(KEY7)));
                gooddetail.setAmount(c.getInt(c.getColumnIndex(KEY20)));
                gooddetail.setGoodType(c.getString(c.getColumnIndex(KEY27)));
                gooddetail.setUnitName(c.getString(c.getColumnIndex(KEY29)));
                gooddetail.setDefaultUnitValue(c.getInt(c.getColumnIndex(KEY30)));
                gooddetail.setReservedAmount(c.getInt(c.getColumnIndex("ReservedAmount")));
                gooddetail.setCheck(false);

                goods.add(gooddetail);
            }
        }
        c.close();
        return goods;
    }

    public ArrayList<Good> getAllGood_ByDate(Integer xDayAgo, Boolean aOnlyActive, Boolean aOnlyAvailable) throws ParseException {
        String cond = "";
        String query = "SELECT date('now','-" + xDayAgo + " day') As xDay";
        SQLiteDatabase database = getReadableDatabase();
        Cursor dc = database.rawQuery(query, null);
        dc.moveToFirst();

        Utilities utilities = new Utilities();
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        Date mDate = frmt.parse(dc.getString(dc.getColumnIndex("xDay")));
        String xDate = utilities.getShamsidate(mDate);

        query = "SELECT *, 0 Amount, 0 Shortage, 0 Price, 0 RowCode FROM Good Join Units on UnitCode = GoodUnitRef ";
        cond = "Where Date2>='" + xDate + "'";
        if (aOnlyActive) {
            cond = cond + " And ActiveStack = 1";
        }
        if (aOnlyAvailable) {
            SharedPreferences shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
            if (shPref.getBoolean("real_amount", true)) {
                cond = cond + " And StackAmount-ReservedAmount > 0 ";
            } else {
                cond = cond + " And StackAmount > 0 ";
            }
        }
        query = query + cond + " order by Date2 DESC , GoodCode DESC ";
        ArrayList<Good> goods = new ArrayList<Good>();
        Cursor c = database.rawQuery(query, null);// exect vase anjame ye amaliyat ______ rawquery vase gereftane ye etelaatii k az db mikhaym

        if (c != null) {
            while (c.moveToNext()) {
                Good gooddetail = new Good();
                gooddetail.setShortage(c.getInt(c.getColumnIndex("Shortage")));
                gooddetail.setRowCode(c.getInt(c.getColumnIndex("RowCode")));
                gooddetail.setGoodCode(c.getInt(c.getColumnIndex(KEY_CODE)));
                gooddetail.setGoodName(c.getString(c.getColumnIndex(KEY_NAME)));
                gooddetail.setGoodExplain1(c.getString(c.getColumnIndex(KEY3)));
                gooddetail.setGoodExplain2(c.getString(c.getColumnIndex(KEY4)));
                gooddetail.setFirstBarCode(c.getString(c.getColumnIndex(KEY5)));
                gooddetail.setMaxSellPrice(c.getInt(c.getColumnIndex(KEY6)));
                gooddetail.setSellPrice1(c.getInt(c.getColumnIndex("SellPrice1")));
                gooddetail.setPrice(c.getInt(c.getColumnIndex("Price")));
                gooddetail.setImageName(c.getString(c.getColumnIndex(KEY7)));
                gooddetail.setAmount(c.getInt(c.getColumnIndex(KEY20)));
                gooddetail.setGoodType(c.getString(c.getColumnIndex(KEY27)));
                gooddetail.setUnitName(c.getString(c.getColumnIndex(KEY29)));
                gooddetail.setDefaultUnitValue(c.getInt(c.getColumnIndex(KEY30)));
                gooddetail.setReservedAmount(c.getInt(c.getColumnIndex("ReservedAmount")));
                gooddetail.setCheck(false);

                goods.add(gooddetail);
            }
        }
        c.close();
        return goods;
    }


    public ArrayList<Good> getAllGood_ByDate_asim() throws ParseException {

        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT *, 0 Amount, 0 Shortage, 0 Price, 0 RowCode FROM Good Join Units on UnitCode = GoodUnitRef  order by Date1 DESC , GoodCode DESC ";
        ArrayList<Good> goods = new ArrayList<Good>();
        Cursor c = database.rawQuery(query, null);// exect vase anjame ye amaliyat ______ rawquery vase gereftane ye etelaatii k az db mikhaym

        if (c != null) {
            while (c.moveToNext()) {
                Good gooddetail = new Good();
                gooddetail.setShortage(c.getInt(c.getColumnIndex("Shortage")));
                gooddetail.setRowCode(c.getInt(c.getColumnIndex("RowCode")));
                gooddetail.setGoodCode(c.getInt(c.getColumnIndex(KEY_CODE)));
                gooddetail.setGoodName(c.getString(c.getColumnIndex(KEY_NAME)));
                gooddetail.setGoodExplain1(c.getString(c.getColumnIndex(KEY3)));
                gooddetail.setGoodExplain2(c.getString(c.getColumnIndex(KEY4)));
                gooddetail.setFirstBarCode(c.getString(c.getColumnIndex(KEY5)));
                gooddetail.setMaxSellPrice(c.getInt(c.getColumnIndex(KEY6)));
                gooddetail.setSellPrice1(c.getInt(c.getColumnIndex("SellPrice1")));
                gooddetail.setPrice(c.getInt(c.getColumnIndex("Price")));
                gooddetail.setImageName(c.getString(c.getColumnIndex(KEY7)));
                gooddetail.setAmount(c.getInt(c.getColumnIndex(KEY20)));
                gooddetail.setGoodType(c.getString(c.getColumnIndex(KEY27)));
                gooddetail.setUnitName(c.getString(c.getColumnIndex(KEY29)));
                gooddetail.setDefaultUnitValue(c.getInt(c.getColumnIndex(KEY30)));
                gooddetail.setReservedAmount(c.getInt(c.getColumnIndex("ReservedAmount")));
                gooddetail.setCheck(false);

                goods.add(gooddetail);
            }
        }
        c.close();
        return goods;
    }


    public ArrayList<Good> getAllGood(String name, Integer aGroupCode, Integer aShowFlag, Integer LikeGoodRef, Boolean aOnlyActive, Boolean aOnlyAvailable, Integer itemamount) {
        String cond;
        String order = " order by ";
        String query = "SELECT *, 0 Amount, 0 Shortage, 0 Price, 0 RowCode FROM Good Join Units on UnitCode = GoodUnitRef ";

        name = name.replaceAll(" ", "%");

        if (name.equals("")) {
            cond = "Where 1=1";
        } else {
            cond = "Where (GoodName Like '%" + name + "%' or GoodMainCode Like '%" + name + "%' or GoodCode Like '%" + name + "%' or FirstBarCode Like '%" + name + "%' or Isbn Like '%" + name + "%' or GoodExplain1 Like '%" + name + "%' or GoodExplain2 Like '%" + name + "%')";
        }
        if (aOnlyActive) {
            cond = cond + " And ActiveStack = 1";
        }

        if (aOnlyAvailable) {
            SharedPreferences shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
            if (shPref.getBoolean("real_amount", true)) {
                cond = cond + " And StackAmount-ReservedAmount > 0 ";
            } else {
                cond = cond + " And StackAmount > 0 ";
            }
        }


        if (mContext.getString(R.string.app_name).equals("انتشارات ماهریس")) {
            if (aGroupCode > 0) {    //baraye farhangee
                cond = cond + " And GoodCode in(Select GoodRef From GoodGroup Where GoodGroupRef = " + aGroupCode.toString() + ")";
                if ((aGroupCode == 1255) || (aGroupCode == 1257)) {
                    order = order + " (Select Max(GoodGroupCode) From GoodGroup Where GoodGroupRef = " + aGroupCode.toString() + " And GoodRef = GoodCode) DESC,";
                }
            } else if (aShowFlag == 1) {
                cond = cond + " And GoodCode in(Select GoodRef From Favorites)";
            } else if (aShowFlag == 2) {
                query = "SELECT * FROM Good Join Units on UnitCode = GoodUnitRef Join PreFactor on GoodRef = GoodCode ";
                cond = cond + " and ifnull(PreFactorCode,0)=0";
            } else if (LikeGoodRef > 0) {
                cond = cond + " And GoodCode in(select p.goodref from GoodGroup p where GoodGroupRef in(select s.GoodGroupRef From GoodGroup s where s.GoodRef = " + LikeGoodRef.toString() + "))";
            }
        } else {
            if (aGroupCode > 0) {
                cond = cond + " And GoodCode in(Select GoodRef From GoodGroup p "
                        + "Join GoodsGrp s on p.GoodGroupRef = s.GroupCode "
                        + "Where s.GroupCode = " + aGroupCode.toString() + " or s.L1 = " + aGroupCode.toString()
                        + " or s.L2 = " + aGroupCode.toString() + " or s.L3 = " + aGroupCode.toString()
                        + " or s.L4 = " + aGroupCode.toString() + " or s.L5 = " + aGroupCode.toString() + ")";
            } else if (aShowFlag == 1) {
                cond = cond + " And GoodCode in(Select GoodRef From Favorites)";
            } else if (aShowFlag == 2) {
                query = "SELECT * FROM Good Join Units on UnitCode = GoodUnitRef Join PreFactor on GoodRef = GoodCode ";
                cond = cond + " and ifnull(PreFactorCode,0)=0";
            } else if (LikeGoodRef > 0) {
                cond = cond + " And GoodCode in(select p.goodref from GoodGroup p where GoodGroupRef in(select s.GoodGroupRef From GoodGroup s where s.GoodRef = " + LikeGoodRef.toString() + "))";
            }

        }


        order = order + "Date2 DESC , GoodCode DESC";

        query = query + cond + order + " LIMIT " + itemamount;

        ArrayList<Good> goods = new ArrayList<Good>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);// exect vase anjame ye amaliyat ______ rawquery vase gereftane ye etelaatii k az db mikhaym

        if (c != null) {
            while (c.moveToNext()) {
                Good gooddetail = new Good();
                gooddetail.setShortage(c.getInt(c.getColumnIndex("Shortage")));
                gooddetail.setRowCode(c.getInt(c.getColumnIndex("RowCode")));
                gooddetail.setGoodCode(c.getInt(c.getColumnIndex(KEY_CODE)));
                gooddetail.setGoodName(c.getString(c.getColumnIndex(KEY_NAME)));
                gooddetail.setGoodExplain1(c.getString(c.getColumnIndex("GoodExplain1")));
                gooddetail.setGoodExplain2(c.getString(c.getColumnIndex(KEY4)));
                gooddetail.setFirstBarCode(c.getString(c.getColumnIndex(KEY5)));
                gooddetail.setMaxSellPrice(c.getInt(c.getColumnIndex(KEY6)));
                gooddetail.setSellPrice1(c.getInt(c.getColumnIndex("SellPrice1")));
                gooddetail.setPrice(c.getInt(c.getColumnIndex("Price")));
                gooddetail.setImageName(c.getString(c.getColumnIndex(KEY7)));
                gooddetail.setAmount(c.getInt(c.getColumnIndex(KEY20)));
                gooddetail.setGoodType(c.getString(c.getColumnIndex(KEY27)));
                gooddetail.setUnitName(c.getString(c.getColumnIndex(KEY29)));
                gooddetail.setDefaultUnitValue(c.getInt(c.getColumnIndex(KEY30)));
                gooddetail.setReservedAmount(c.getInt(c.getColumnIndex("ReservedAmount")));
                gooddetail.setCheck(false);

                goods.add(gooddetail);
            }
        }
        c.close();
        return goods;
    }

    public ArrayList<Good> getAllGood_pfcode(Integer pfcode, String name, Integer aGroupCode, Integer aShowFlag, Integer LikeGoodRef, Boolean aOnlyActive) {
        String cond;
        String query = "SELECT *, 0 Amount, 0 Shortage, 0 Price, 0 RowCode FROM Good Join Units on UnitCode = GoodUnitRef ";
        if (name == "") {
            cond = "Where 1=1";
        } else {
            cond = "Where (GoodName Like '%" + name + "%' or GoodMainCode Like '%" + name + "%' or GoodCode Like '%" + name + "%' or FirstBarCode Like '%" + name + "%' or GoodExplain1 Like '%" + name + "%' or GoodExplain2 Like '%" + name + "%')";
        }
        if (aOnlyActive) {
            cond = cond + " And ActiveStack = 1";
        }
        if (aGroupCode > 0) {
            cond = cond + " And GoodCode in(Select GoodRef From GoodGroup Where GoodGroupRef = " + aGroupCode.toString() + ")";
        } else if (aShowFlag == 1) {
            cond = cond + " And GoodCode in(Select GoodRef From Favorites)";
        } else if (aShowFlag == 2) {
            query = "SELECT * FROM Good Join Units on UnitCode = GoodUnitRef Join PreFactor on GoodRef = GoodCode ";
            cond = cond + " and ifnull(PreFactorCode,0)=" + pfcode;
        } else if (LikeGoodRef > 0) {
            cond = cond + " And GoodCode in(select p.goodref from GoodGroup p where GoodGroupRef in(select s.GoodGroupRef From GoodGroup s where s.GoodRef = " + LikeGoodRef.toString() + "))";
        }
        cond = cond + " order by Date2 DESC , GoodCode DESC ";
        query = query + cond + " LIMIT 200 ";
        ArrayList<Good> goods = new ArrayList<Good>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);// exect vase anjame ye amaliyat ______ rawquery vase gereftane ye etelaatii k az db mikhaym
        Log.e("asli_getAllGood_pfcode", query);
        if (c != null) {
            while (c.moveToNext()) {
                Good gooddetail = new Good();
                gooddetail.setShortage(c.getInt(c.getColumnIndex("Shortage")));
                gooddetail.setRowCode(c.getInt(c.getColumnIndex("RowCode")));
                gooddetail.setGoodCode(c.getInt(c.getColumnIndex(KEY_CODE)));
                gooddetail.setGoodMainCode(c.getInt(c.getColumnIndex("GoodMainCode")));
                gooddetail.setGoodName(c.getString(c.getColumnIndex(KEY_NAME)));
                gooddetail.setGoodExplain1(c.getString(c.getColumnIndex(KEY3)));
                gooddetail.setGoodExplain2(c.getString(c.getColumnIndex(KEY4)));
                gooddetail.setFirstBarCode(c.getString(c.getColumnIndex(KEY5)));
                gooddetail.setMaxSellPrice(c.getInt(c.getColumnIndex(KEY6)));
                gooddetail.setSellPrice1(c.getInt(c.getColumnIndex("SellPrice1")));
                gooddetail.setPrice(c.getInt(c.getColumnIndex("Price")));
                gooddetail.setImageName(c.getString(c.getColumnIndex(KEY7)));
                gooddetail.setAmount(c.getInt(c.getColumnIndex(KEY20)));
                gooddetail.setFactorAmount(c.getInt(c.getColumnIndex("Amount")));
                gooddetail.setGoodType(c.getString(c.getColumnIndex(KEY27)));
                gooddetail.setUnitName(c.getString(c.getColumnIndex(KEY29)));
                gooddetail.setDefaultUnitValue(c.getInt(c.getColumnIndex(KEY30)));
                gooddetail.setReservedAmount(c.getInt(c.getColumnIndex("ReservedAmount")));
                gooddetail.setCheck(false);

                goods.add(gooddetail);
            }
        }
        c.close();
        return goods;
    }

    public Integer getCustomerGoodSellPrice(int aPreFactorHeaderCode, int aGoodCode) {
        Integer iResult = 0;
        String query = "Select MaxSellPrice, Case c.PriceTip When 1 Then SellPrice1 When 2 Then SellPrice2 "
                + " When 3 Then SellPrice3 When 4 Then SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 "
                + "  Else Case When g.SellPriceType = 0 Then MaxSellPrice Else 100 End End "
                + " * Case When g.SellPriceType = 0 Then 1 Else MaxSellPrice /100 End As Price "
                + "From Good g Join PreFactorHeader h on PreFactorCode = " + aPreFactorHeaderCode
                + " Join Customer c on c.CustomerCode = h.CustomerRef Where GoodCode = " + aGoodCode;
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            iResult = c.getInt(c.getColumnIndex("Price"));
        }
        c.close();
        return iResult;
    }


    public Good getGoodByCode(int code, int pfcode) {
        String query = "SELECT * FROM Good Join Units on UnitCode = GoodUnitRef "
                + " Left Join (Select GoodRef, Sum(Amount) FactorAmount From PreFactor Where PreFactorCode =" + pfcode + " Group BY GoodRef) ss "
                + "  on GoodRef = GoodCode "
                + " WHERE GoodCode = " + code + " or FirstBarCode = '" + code + "' or Isbn ='" + code + "'";
        Good gd = new Good();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            gd.setGoodCode(c.getInt(c.getColumnIndex(KEY_CODE)));
            gd.setGoodName(c.getString(c.getColumnIndex(KEY_NAME)));
            gd.setGoodExplain1(c.getString(c.getColumnIndex(KEY3)));
            gd.setMaxSellPrice(c.getInt(c.getColumnIndex(KEY6)));
            gd.setImageName(c.getString(c.getColumnIndex(KEY7)));
            gd.setUnitName(c.getString(c.getColumnIndex(KEY29)));
            gd.setDefaultUnitValue(c.getInt(c.getColumnIndex(KEY30)));
            gd.setAmount(c.getInt(c.getColumnIndex(KEY20)));
            gd.setIsbn(c.getString(c.getColumnIndex("Isbn")));
            gd.setFirstBarCode(c.getString(c.getColumnIndex("FirstBarCode")));
            gd.setDate2(c.getString(c.getColumnIndex("Date2")));
            gd.setNvarchar1(c.getString(c.getColumnIndex("Nvarchar1")));
            gd.setNvarchar2(c.getString(c.getColumnIndex("Nvarchar2")));
            gd.setFloat1(c.getInt(c.getColumnIndex("Float1")));
            gd.setNvarchar13(c.getString(c.getColumnIndex("Nvarchar13")));
            gd.setNvarchar20(c.getString(c.getColumnIndex("Nvarchar20")));
            gd.setFloat5(c.getInt(c.getColumnIndex("Float5")));
            gd.setFactorAmount(c.getInt(c.getColumnIndex("FactorAmount")));
            gd.setUnitName(c.getString(c.getColumnIndex("UnitName")));
            gd.setReservedAmount(c.getInt(c.getColumnIndex("ReservedAmount")));
            try {
                gd.setDate1(c.getString(c.getColumnIndex("Date1")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            gd.setCheck(false);

            if (mContext.getString(R.string.app_name).equals("چشمه")) {
                gd.setGoodSubCode(c.getString(c.getColumnIndex("GoodSubCode")));
                gd.setAmount1(c.getInt(c.getColumnIndex("StackAmount2")));
                gd.setAmount2(c.getInt(c.getColumnIndex("StackAmount3")));
            }
            if (mContext.getString(R.string.app_name).equals("چشمه غیر کتابی")) {
                gd.setSellPrice1(c.getInt(c.getColumnIndex("SellPrice1")));
                gd.setGoodExplain2(c.getString(c.getColumnIndex("GoodExplain2")));
                gd.setGoodExplain3(c.getString(c.getColumnIndex("GoodExplain3")));
                gd.setGoodExplain4(c.getString(c.getColumnIndex("GoodExplain4")));
                gd.setNvarchar10(c.getString(c.getColumnIndex("Nvarchar13")));

            }

        }
        c.close();
        return gd;
    }


    public void InsertPreFactor(int pfcode, int goodcode, int amount, int price, int rowcode) {
        SQLiteDatabase db = getWritableDatabase();
        String query;
        if (rowcode > 0) {
            if (price >= 0) {
                query = "Update PreFactor set Amount = " + amount + ", Price = " + price + " Where RowCode=" + rowcode;
            } else {
                query = "Update PreFactor set Amount = " + amount + " Where RowCode=" + rowcode;
            }
            db.execSQL(query);
        } else {
            query = " Select * From PreFactor Where IfNull(PreFactorCode,0)=" + pfcode + " And GoodRef =" + goodcode;
            if (price >= 0) {
                query = query + " And Price =" + price;
            }

            Cursor c = db.rawQuery(query, null);

            if (c.getCount() > 0) {
                c.moveToFirst();
                db.execSQL("Update PreFactor set Amount = Amount +" + amount + " Where RowCode=" + c.getString(c.getColumnIndex("RowCode")) + ";");
            } else {
                db.execSQL("INSERT INTO PreFactor(PreFactorCode, GoodRef, Amount, Price) Select " + pfcode + "," + goodcode + ", " + amount + "," +
                        "Case When " + price + ">=0 Then " + price + " Else Case PriceTip When 1 Then SellPrice1 When 2 Then SellPrice2 When 3 Then SellPrice3 When 4 Then SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 End " +
                        "* Case When SellPriceType = 1 Then MaxSellPrice/100 Else 1 End End " +
                        "From Good g Join PreFactorHeader h on 1=1 Join Customer c on h.CustomerRef=c.CustomerCode " +
                        "Where h.PreFactorCode =" + pfcode + " And GoodCode = " + goodcode);
            }
            c.close();
        }
    }

    public String getgoodgroups(int code) {
        String query;
        query = "Select Name From GoodGroup p join GoodsGrp g on p.GoodGroupRef = g.Groupcode where GoodRef = " + code;
        SQLiteDatabase database = getReadableDatabase();
        String Res = "";
        Cursor c = database.rawQuery(query, null);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {

                if (Res.equals("")) {
                    Res = c.getString(c.getColumnIndex("Name"));

                } else {
                    Res = Res + " - " + c.getString(c.getColumnIndex("Name"));
                }
            }
        }
        c.close();
        return Res;
    }

    public void InsertPreFactorHeader(String Customer, Integer CustomerRef) {

        Utilities utilities = new Utilities();
        String Date = utilities.getCurrentShamsidate();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        SQLiteDatabase db = getWritableDatabase();


        UserInfo user = new UserInfo();
        String query = "Select * From Config Where KeyValue = 'BrokerCode' ";
        String key = "";
        String val = "";
        Cursor dc = db.rawQuery(query, null);
        if (dc != null) {
            while (dc.moveToNext()) {
                key = dc.getString(dc.getColumnIndex("KeyValue"));
                val = dc.getString(dc.getColumnIndex("DataValue"));
                switch (key) {
                    case "ActiveCode":
                        user.setActiveCode(val);
                        break;
                    case "BrokerCode":
                        user.setBrokerCode(val);
                        break;

                }
            }
        }
        db.execSQL("INSERT INTO PrefactorHeader(PreFactorKowsarCode,PreFactorDate" +
                ",PreFactorKowsarDate ,PreFactorTime,PreFactorExplain,CustomerRef,BrokerRef) " +
                "VALUES(0,'" + Date + "','-----','" + strDate + "','" + Customer + "','" + CustomerRef + "','" + val + "'); ");
    }

    public ArrayList<PreFactor> getAllPrefactorHeaderopen() {
        String query = "SELECT h.*, s.SumAmount , s.SumPrice, s.RowCount ,n.CentralName CustomerName  FROM PreFactorHeader h Join Customer c  on c.CustomerCode = h.CustomerRef "
                + " join Central n on c.CentralRef=n.CentralCode "
                + "Left Join (SELECT P.PreFactorCode, sum(p.Amount) as SumAmount , sum(p.Amount * p.Price*g.DefaultUnitValue) as SumPrice, count(*) as RowCount "
                + "From Good g Join Units on UnitCode = GoodUnitRef  Join PreFactor p on GoodRef = GoodCode  Where IfNull(PreFactorCode, 0)>0 "
                + "Group BY PreFactorCode ) s on h.PreFactorCode = s.PreFactorCode Where NOT IfNull(PreFactorKowsarCode, 0)>0 "
                + "Order By h.PreFactorCode DESC";

        ArrayList<PreFactor> pfh = new ArrayList<PreFactor>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        if (c != null) {
            while (c.moveToNext()) {
                PreFactor pf = new PreFactor();
                pf.setPreFactorCode(c.getInt(c.getColumnIndex("PreFactorCode")));
                pf.setPreFactorDate(c.getString(c.getColumnIndex("PreFactorDate")));
                pf.setPreFactorTime(c.getString(c.getColumnIndex("PreFactorTime")));
                pf.setPreFactorkowsarDate(c.getString(c.getColumnIndex("PreFactorKowsarDate")));
                pf.setPreFactorKowsarCode(c.getInt(c.getColumnIndex("PreFactorKowsarCode")));
                pf.setPreFactorExplain(c.getString(c.getColumnIndex("PreFactorExplain")));
                pf.setCustomer(c.getString(c.getColumnIndex("CustomerName")));
                pf.setSumAmount(c.getInt(c.getColumnIndex("SumAmount")));
                pf.setSumPrice(c.getInt(c.getColumnIndex("SumPrice")));
                pf.setRowCount(c.getInt(c.getColumnIndex("RowCount")));

                pfh.add(pf);
            }
        }
        c.close();
        return pfh;
    }


    public ArrayList<PreFactor> getAllPrefactorHeader(String name) {


        String query = "SELECT h.*, s.SumAmount , s.SumPrice , s.RowCount ,n.CentralName CustomerName FROM PreFactorHeader h Join Customer c  on c.CustomerCode = h.CustomerRef " +
                "join Central n on c.CentralRef=n.CentralCode "
                + "Left Join (SELECT P.PreFactorCode, sum(p.Amount) as SumAmount , sum(p.Amount * p.Price*g.DefaultUnitValue) as SumPrice, count(*) as RowCount "
                + "From Good g Join Units on UnitCode = GoodUnitRef  Join PreFactor p on GoodRef = GoodCode  Where IfNull(PreFactorCode, 0)>0 "
                + "Group BY PreFactorCode ) s on h.PreFactorCode = s.PreFactorCode "
                + "Where n.CentralName Like '%" + name + "%'"
                + "Order By h.PreFactorCode DESC";

        ArrayList<PreFactor> pfh = new ArrayList<PreFactor>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        if (c != null) {
            while (c.moveToNext()) {
                PreFactor pf = new PreFactor();
                pf.setPreFactorCode(c.getInt(c.getColumnIndex("PreFactorCode")));
                pf.setPreFactorDate(c.getString(c.getColumnIndex("PreFactorDate")));
                pf.setPreFactorTime(c.getString(c.getColumnIndex("PreFactorTime")));
                pf.setPreFactorkowsarDate(c.getString(c.getColumnIndex("PreFactorKowsarDate")));
                pf.setPreFactorKowsarCode(c.getInt(c.getColumnIndex("PreFactorKowsarCode")));
                pf.setPreFactorExplain(c.getString(c.getColumnIndex("PreFactorExplain")));
                pf.setCustomer(c.getString(c.getColumnIndex("CustomerName")));
                pf.setSumAmount(c.getInt(c.getColumnIndex("SumAmount")));
                pf.setSumPrice(c.getInt(c.getColumnIndex("SumPrice")));
                pf.setRowCount(c.getInt(c.getColumnIndex("RowCount")));

                pfh.add(pf);
            }
        }
        c.close();
        return pfh;
    }

    public ArrayList<PreFactor> getOpenPrefactorHeader() {
        String query = "SELECT h.*, s.SumAmount , s.SumPrice, s.RowCount ,n.CentralName CustomerName  FROM PreFactorHeader h Join Customer c  on c.CustomerCode = h.CustomerRef "
                + " join Central n on c.CentralRef=n.CentralCode "
                + "Left Join (SELECT P.PreFactorCode, sum(p.Amount) as SumAmount , sum(p.Amount * g.MaxSellPrice*g.DefaultUnitValue) as SumPrice, count(*) as RowCount "
                + "From Good g Join Units on UnitCode = GoodUnitRef  Join PreFactor p on GoodRef = GoodCode  Where IfNull(PreFactorCode, 0)>0 "
                + "Group BY PreFactorCode ) s on h.PreFactorCode = s.PreFactorCode Where NOT IfNull(PreFactorKowsarCode, 0)>0 "
                + "Order By h.PreFactorCode DESC";

        ArrayList<PreFactor> pfh = new ArrayList<PreFactor>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        if (c != null) {
            while (c.moveToNext()) {
                PreFactor pf = new PreFactor();
                pf.setPreFactorCode(c.getInt(c.getColumnIndex("PreFactorCode")));
                pf.setPreFactorDate(c.getString(c.getColumnIndex("PreFactorDate")));
                pf.setPreFactorTime(c.getString(c.getColumnIndex("PreFactorTime")));
                pf.setPreFactorkowsarDate(c.getString(c.getColumnIndex("PreFactorKowsarDate")));
                pf.setPreFactorKowsarCode(c.getInt(c.getColumnIndex("PreFactorKowsarCode")));
                pf.setPreFactorExplain(c.getString(c.getColumnIndex("PreFactorExplain")));
                pf.setCustomer(c.getString(c.getColumnIndex("CustomerName")));
                pf.setSumAmount(c.getInt(c.getColumnIndex("SumAmount")));
                pf.setSumPrice(c.getInt(c.getColumnIndex("SumPrice")));
                pf.setRowCount(c.getInt(c.getColumnIndex("RowCount")));

                pfh.add(pf);
            }
        }
        c.close();
        return pfh;

    }

    public ArrayList<Good> getAllPreFactorRows(String name, Integer aPreFactorCode) {
        name = name.replaceAll(" ", "%");
        String query = "SELECT * FROM Good g Join PreFactor on GoodRef = GoodCode Join Units u on u.UnitCode = g.GoodUnitRef  Where (GoodName Like '%" + name + "%' and PreFactorCode = " + aPreFactorCode + ") order by GoodCode DESC ";
        ArrayList<Good> goods = new ArrayList<Good>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        if (c != null) {
            while (c.moveToNext()) {
                Good gooddetail = new Good();
                gooddetail.setShortage(c.getInt(c.getColumnIndex("Shortage")));
                gooddetail.setRowCode(c.getInt(c.getColumnIndex("RowCode")));
                gooddetail.setGoodCode(c.getInt(c.getColumnIndex(KEY_CODE)));
                gooddetail.setGoodName(c.getString(c.getColumnIndex(KEY_NAME)));
                gooddetail.setGoodExplain1(c.getString(c.getColumnIndex(KEY3)));
                gooddetail.setGoodExplain2(c.getString(c.getColumnIndex(KEY4)));
                gooddetail.setFirstBarCode(c.getString(c.getColumnIndex(KEY5)));
                gooddetail.setMaxSellPrice(c.getInt(c.getColumnIndex(KEY6)));
                gooddetail.setSellPrice1(c.getInt(c.getColumnIndex("SellPrice1")));

                gooddetail.setPrice(c.getInt(c.getColumnIndex("Price")));
                gooddetail.setImageName(c.getString(c.getColumnIndex(KEY7)));
                gooddetail.setUnitName(c.getString(c.getColumnIndex(KEY29)));
                gooddetail.setDefaultUnitValue(c.getInt(c.getColumnIndex(KEY30)));
                gooddetail.setAmount(c.getInt(c.getColumnIndex("Amount")));
                gooddetail.setGoodType(c.getString(c.getColumnIndex(KEY27)));
                goods.add(gooddetail);
            }
        }
        c.close();
        return goods;
    }

    public ArrayList<Good> getAllPreFactorRows(Integer aPreFactorCode) {
        String query = "SELECT * FROM Good g Join PreFactor on GoodRef = GoodCode Join Units u on u.UnitCode = g.GoodUnitRef Where PreFactorCode = " + aPreFactorCode + " order by GoodCode DESC ";
        ArrayList<Good> goods = new ArrayList<Good>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        if (c != null) {
            while (c.moveToNext()) {
                Good gooddetail = new Good();
                gooddetail.setShortage(c.getInt(c.getColumnIndex("Shortage")));
                gooddetail.setRowCode(c.getInt(c.getColumnIndex("RowCode")));
                gooddetail.setGoodCode(c.getInt(c.getColumnIndex(KEY_CODE)));
                gooddetail.setGoodName(c.getString(c.getColumnIndex(KEY_NAME)));
                gooddetail.setGoodExplain1(c.getString(c.getColumnIndex(KEY3)));
                gooddetail.setGoodExplain2(c.getString(c.getColumnIndex(KEY4)));
                gooddetail.setFirstBarCode(c.getString(c.getColumnIndex(KEY5)));
                gooddetail.setMaxSellPrice(c.getInt(c.getColumnIndex(KEY6)));
                gooddetail.setSellPrice1(c.getInt(c.getColumnIndex("SellPrice1")));

                gooddetail.setPrice(c.getInt(c.getColumnIndex("Price")));
                gooddetail.setImageName(c.getString(c.getColumnIndex(KEY7)));
                gooddetail.setUnitName(c.getString(c.getColumnIndex(KEY29)));
                gooddetail.setDefaultUnitValue(c.getInt(c.getColumnIndex(KEY30)));
                gooddetail.setAmount(c.getInt(c.getColumnIndex("Amount")));
                gooddetail.setGoodType(c.getString(c.getColumnIndex(KEY27)));
                goods.add(gooddetail);
            }
        }
        c.close();
        return goods;
    }

    public ArrayList<Customer> AllCustomer(String name) {

        name = name.replaceAll(" ", "%");
        String query = "SELECT u.CustomerCode,u.PriceTip,CentralName,Address,Manager,Mobile,Phone,Delegacy, CityName, Bestankar, Active, CentralPrivateCode, EtebarNaghd" +
                ",EtebarCheck, Takhfif, MobileName, Email, Fax, ZipCode, PostCode FROM Customer u " +
                "join Central c on u.CentralRef= c.CentralCode " +
                "Left join Address d on u.AddressRef=d.AddressCode " +
                "Left join City y on d.CityCode=y.CityCode" +
                " Where (CentralName Like '%" + name + "%' or CustomerCode Like '%" + name + "%' or  Manager Like '%" + name + "%')" +
                " order by CustomerCode DESC  LIMIT 30";
        ArrayList<Customer> Customers = new ArrayList<Customer>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        if (c != null) {
            while (c.moveToNext()) {
                Customer customerdetail = new Customer();
                customerdetail.setCustomerCode(c.getInt(c.getColumnIndex("CustomerCode")));
                customerdetail.setPriceTip(c.getInt(c.getColumnIndex("PriceTip")));
                customerdetail.setCustomerName(c.getString(c.getColumnIndex("CentralName")));
                customerdetail.setAddress(c.getString(c.getColumnIndex("Address")));
                customerdetail.setManager(c.getString(c.getColumnIndex("Manager")));
                customerdetail.setMobile(c.getString(c.getColumnIndex("Mobile")));
                customerdetail.setPhone(c.getString(c.getColumnIndex("Phone")));
                customerdetail.setDelegacy(c.getString(c.getColumnIndex("Delegacy")));
                customerdetail.setCityName(c.getString(c.getColumnIndex("CityName")));
                customerdetail.setBestankar(c.getInt(c.getColumnIndex("Bestankar")));
                customerdetail.setActive(c.getInt(c.getColumnIndex("Active")));
                customerdetail.setCentralPrivateCode(c.getInt(c.getColumnIndex("CentralPrivateCode")));
                customerdetail.setEtebarNaghd(c.getInt(c.getColumnIndex("EtebarNaghd")));
                customerdetail.setEtebarCheck(c.getInt(c.getColumnIndex("EtebarCheck")));
                customerdetail.setTakhfif(c.getInt(c.getColumnIndex("Takhfif")));
                customerdetail.setMobileName(c.getString(c.getColumnIndex("MobileName")));
                customerdetail.setEmail(c.getString(c.getColumnIndex("Email")));
                customerdetail.setFax(c.getString(c.getColumnIndex("Fax")));
                customerdetail.setZipCode(c.getString(c.getColumnIndex("ZipCode")));
                customerdetail.setPostCode(c.getString(c.getColumnIndex("PostCode")));

                Customers.add(customerdetail);
            }
        }
        c.close();
        return Customers;
    }

    public void UpdatePreFactorHeader_Customer(int pfcode, int Customer) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("Update PrefactorHeader set CustomerRef='" + Customer + "' where PreFactorCode = " + pfcode + "; ");
        Cursor c = db.rawQuery("Select * From(Select Case PriceTip When 1 Then SellPrice1 When 2 Then SellPrice2 When 3 Then SellPrice3 \n" +
                "       When 4 Then SellPrice4 When 5 Then SellPrice5 When 6 Then SellPrice6 End \n" +
                "   * Case When SellPriceType = 1 Then MaxSellPrice/100 Else 1 End as NewPrice, Price, GoodCode From PreFactor p \n" +
                "Join PreFactorHeader h on h.PreFactorCode = p.PreFactorCode\n" +
                "Join Customer on CustomerCode = CustomerRef Join Good g on GoodRef = GoodCode\n" +
                "Where h.PreFactorCode = " + pfcode + ") ss Where Price<> NewPrice", null);


        if (c != null) {
            while (c.moveToNext()) {

                db.execSQL("Update PreFactor set Price=" + c.getString(c.getColumnIndex("NewPrice"))
                        + " Where PreFactorCode =" + pfcode + " And GoodRef =" + c.getString(c.getColumnIndex("GoodCode")));
            }
        }


        c.close();
    }

    public Integer GetLastPreFactorHeader() {

        String query = "SELECT * FROM PrefactorHeader Where PreFactorKowsarCode = 0 order by PreFactorCode DESC";
        SQLiteDatabase database = getReadableDatabase();
        Integer Res = 0;
        Cursor c = database.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToNext();
            Res = c.getInt(c.getColumnIndex("PreFactorCode"));
        }
        c.close();
        return Res;
    }


    public void update_explain(int pfcode, String explain) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "Update PreFactorHeader set PreFactorExplain = '" + explain + "' Where IfNull(PreFactorCode,0)=" + pfcode;
        Log.e("asli_query", query);
        db.execSQL(query);
    }

    public void DeletePreFactorRow(Integer pfcode, Integer code) {
        SQLiteDatabase db = getWritableDatabase();
        String query = " Delete From PreFactor Where IfNull(PreFactorCode,0)=" + pfcode + " And (RowCode =" + code + " or 0=" + code + ")";
        db.execSQL(query);
    }

    public void DeletePreFactor(Integer pfcode) {
        SQLiteDatabase db = getWritableDatabase();
        String query = " Delete From PrefactorHeader Where IfNull(PreFactorCode,0)=" + pfcode;
        db.execSQL(query);
    }

    public void DeleteEmptyPreFactor() {
        SQLiteDatabase db = getWritableDatabase();
        String query = " DELETE FROM PrefactorHeader WHERE PreFactorCode NOT IN (SELECT PreFactorCode FROM Prefactor )";
        db.execSQL(query);
    }

    public void UpdatePreFactor(Integer PreFactorCode, Integer PreFactorKowsarCode, String PreFactorDate) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "Update PreFactorHeader Set PreFactorKowsarCode = " + PreFactorKowsarCode + ", PreFactorKowsarDate = '" + PreFactorDate + "' Where ifnull(PreFactorCode ,0)= " + PreFactorCode + ";";
        Log.e("asli_query", query);
        db.execSQL(query);
    }

    public long getFactorSum(Integer pfcode) {
        SQLiteDatabase db = getReadableDatabase();
        String query = " select sum(Amount*price*DefaultUnitValue) as sm From PreFactor join Good on GoodRef=GoodCode Where IfNull(PreFactorCode,0)=" + pfcode;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        long sm = c.getLong(c.getColumnIndex("sm"));
        c.close();
        return sm;
    }

    public Integer getFactorSumAmount(Integer pfcode) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "select sum(Amount) as sm From PreFactor join Good on GoodRef=GoodCode Where IfNull(PreFactorCode,0)=" + pfcode;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Integer sm = c.getInt(c.getColumnIndex("sm"));
        c.close();
        return sm;
    }

    public String getFactorCustomer(Integer pfcode) {
        SQLiteDatabase db = getReadableDatabase();
        String sm;

        String query = "SELECT n.CentralName CustomerName  FROM PreFactorHeader h Join Customer c  on c.CustomerCode = h.CustomerRef "
                + " join Central n on c.CentralRef=n.CentralCode "
                + " Where IfNull(PreFactorCode,0)= " + pfcode;
        //String query = "select n.CentralName CustomerName as sm From PreFactor join Good on GoodRef=GoodCode Where IfNull(PreFactorCode,0)="+pfcode;
        Cursor c = db.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            sm = c.getString(c.getColumnIndex("CustomerName"));
            c.close();
        } else {
            sm = "فاکتوری انتخاب نشده";
        }
        return sm;
    }


    public void InsertFavorites(int code) {
        SQLiteDatabase db = getWritableDatabase();
        String query;
        query = " Select * From Favorites Where GoodRef =" + code;
        Cursor c = db.rawQuery(query, null);

        if (c.getCount() == 0) {
            db.execSQL("INSERT INTO Favorites(GoodRef) VALUES(" + code + "); ");
        }
        c.close();
    }

    public String getActiveCode() {
        SQLiteDatabase db = getReadableDatabase();
        String query = " select DataValue From Config Where KeyValue = 'ActiveCode' ";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if (c.getCount() > 0) query = c.getString(c.getColumnIndex("DataValue"));
        else query = "";
        c.close();
        return query;
    }

    public UserInfo LoadPersonalInfo() {
        UserInfo user = new UserInfo();
        SQLiteDatabase db = getWritableDatabase();
        String query = "Select * From Config";
        String key = "";
        String val = "";
        Cursor c = db.rawQuery(query, null);

        if (c != null) {
            while (c.moveToNext()) {
                key = c.getString(c.getColumnIndex("KeyValue"));
                val = c.getString(c.getColumnIndex("DataValue"));

                switch (key) {
                    case "Email":
                        user.setEmail(val);
                        break;
                    case "NameFamily":
                        user.setNameFamily(val);
                        break;
                    case "Address":
                        user.setAddress(val);
                        break;
                    case "Mobile":
                        user.setMobile(val);
                        break;
                    case "Phone":
                        user.setPhone(val);
                        break;
                    case "BirthDate":
                        user.setBirthDate(val);
                        break;
                    case "PostalCode":
                        user.setPostalCode(val);
                        break;
                    case "MelliCode":
                        user.setMelliCode(val);
                        break;
                    case "ActiveCode":
                        user.setActiveCode(val);
                        break;
                    case "BrokerCode":
                        user.setBrokerCode(val);
                        break;
                }
            }
        }
        return user;
    }

    public void SavePersonalInfo(UserInfo user) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "";

        if (user.getEmail() != "") {
            query = " Update Config set DataValue = '" + user.getEmail() + "' Where KeyValue = 'Email';";
            db.execSQL(query);
            query = " Insert Into Config(KeyValue, DataValue)\n" +
                    "  Select 'Email', '" + user.getEmail() + "' Where Not Exists(Select * From Config Where KeyValue = 'Email');";
            db.execSQL(query);
        }
        if (user.getNameFamily() != "") {
            query = " Update Config set DataValue = '" + user.getNameFamily() + "' Where KeyValue = 'NameFamily';";
            db.execSQL(query);
            query = " Insert Into Config(KeyValue, DataValue)\n" +
                    "  Select 'NameFamily', '" + user.getNameFamily() + "' Where Not Exists(Select * From Config Where KeyValue = 'NameFamily');";
            db.execSQL(query);
        }
        if (user.getAddress() != "") {
            query = " Update Config set DataValue = '" + user.getAddress() + "' Where KeyValue = 'Address';";
            db.execSQL(query);
            query = " Insert Into Config(KeyValue, DataValue)\n" +
                    "  Select 'Address', '" + user.getAddress() + "' Where Not Exists(Select * From Config Where KeyValue = 'Address');";
            db.execSQL(query);
        }
        if (user.getMobile() != "") {
            query = " Update Config set DataValue = '" + user.getMobile() + "' Where KeyValue = 'Mobile';";
            db.execSQL(query);
            query = " Insert Into Config(KeyValue, DataValue)\n" +
                    "  Select 'Mobile', '" + user.getMobile() + "' Where Not Exists(Select * From Config Where KeyValue = 'Mobile');";
            db.execSQL(query);
        }
        if (user.getPhone() != "") {
            query = " Update Config set DataValue = '" + user.getPhone() + "' Where KeyValue = 'Phone';";
            db.execSQL(query);
            query = " Insert Into Config(KeyValue, DataValue)\n" +
                    "  Select 'Phone', '" + user.getPhone() + "' Where Not Exists(Select * From Config Where KeyValue = 'Phone');";
            db.execSQL(query);
        }
        if (user.getBirthDate() != "") {
            query = " Update Config set DataValue = '" + user.getBirthDate() + "' Where KeyValue = 'BirthDate';";
            db.execSQL(query);
            query = " Insert Into Config(KeyValue, DataValue)\n" +
                    "  Select 'BirthDate', '" + user.getBirthDate() + "' Where Not Exists(Select * From Config Where KeyValue = 'BirthDate');";
            db.execSQL(query);
        }
        if (user.getMelliCode() != "") {
            query = " Update Config set DataValue = '" + user.getMelliCode() + "' Where KeyValue = 'MelliCode';";
            db.execSQL(query);
            query = " Insert Into Config(KeyValue, DataValue)\n" +
                    "  Select 'MelliCode', '" + user.getMelliCode() + "' Where Not Exists(Select * From Config Where KeyValue = 'MelliCode');";
            db.execSQL(query);
        }
        if (user.getPostalCode() != "") {
            query = " Update Config set DataValue = '" + user.getPostalCode() + "' Where KeyValue = 'PostalCode';";
            db.execSQL(query);
            query = " Insert Into Config(KeyValue, DataValue)\n" +
                    "  Select 'PostalCode', '" + user.getPostalCode() + "' Where Not Exists(Select * From Config Where KeyValue = 'PostalCode');";
            db.execSQL(query);
        }
        if (user.getBrokerCode() != "") {
            query = " Update Config set DataValue = '" + user.getBrokerCode() + "' Where KeyValue = 'BrokerCode';";
            db.execSQL(query);
            query = " Insert Into Config(KeyValue, DataValue)\n" +
                    "  Select 'BrokerCode', '" + user.getBrokerCode() + "' Where Not Exists(Select * From Config Where KeyValue = 'BrokerCode');";
            db.execSQL(query);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }


    public String[] getAllSpinnerContent() {

        String query = "Select * from AppColumns ";
//where ObjectType = '"+obj+"'
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        ArrayList<String> spinnerContent = new ArrayList<String>();
        if (c.moveToFirst()) {
            do {
                String PropertySchemaCode = c.getString(c.getColumnIndexOrThrow("DisplayName"));
                //  ArrayList<String> property =
                spinnerContent.add(PropertySchemaCode);
            } while (c.moveToNext());
        }
        c.close();


        String[] allSpinner = new String[spinnerContent.size()];
        allSpinner = spinnerContent.toArray(allSpinner);

        return allSpinner;
    }


    public long getsum_sumfactor() {
        String query = "select sum(price) as sm From PreFactor";
        SQLiteDatabase database = getReadableDatabase();
        long Res = 0;
        Cursor c = database.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToNext();
            Res = c.getLong(c.getColumnIndex("sm"));
        }
        c.close();
        return Res;
    }

}