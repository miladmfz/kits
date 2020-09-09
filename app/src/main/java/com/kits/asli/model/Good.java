package com.kits.asli.model;

import android.os.Environment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static android.text.TextUtils.isEmpty;

public class Good implements Serializable {

    @SerializedName("GoodCode")
    private Integer GoodCode;
    @SerializedName("state")
    private Integer state;
    @SerializedName("GoodMainCode")
    private Integer GoodMainCode;
    @SerializedName("GoodSubCode")
    private String GoodSubCode;
    @SerializedName("MaxSellPrice")
    private Integer MaxSellPrice;
    @SerializedName("DefaultUnitValue")
    private Integer DefaultUnitValue;
    @SerializedName("Amount")
    private Integer Amount;
    @SerializedName("Amount1")
    private Integer Amount1;
    @SerializedName("Amount2")
    private Integer Amount2;

    @SerializedName("ReservedAmount")
    private Integer ReservedAmount;
    @SerializedName("SellPrice1")
    private Integer SellPrice1;
    @SerializedName("FactorAmount")
    private Integer FactorAmount;
    @SerializedName("Shortage")
    private Integer Shortage;
    @SerializedName("Price")
    private Integer Price;
    @SerializedName("Float1")
    private Integer Float1;
    @SerializedName("Float5")
    private Integer Float5;
    @SerializedName("GoodName")
    private String GoodName;
    @SerializedName("GoodExplain1")
    private String GoodExplain1;
    @SerializedName("GoodExplain2")
    private String GoodExplain2;
    @SerializedName("GoodExplain3")
    private String GoodExplain3;
    @SerializedName("GoodExplain4")
    private String GoodExplain4;
    @SerializedName("FirstBarCode")
    private String FirstBarCode;
    @SerializedName("ImageName")
    private String ImageName;
    @SerializedName("UnitName")
    private String UnitName;
    @SerializedName("Nvarchar1")
    private String Nvarchar1;
    @SerializedName("Nvarchar2")
    private String Nvarchar2;
    @SerializedName("Nvarchar10")
    private String Nvarchar10;
    @SerializedName("Nvarchar13")
    private String Nvarchar13;
    @SerializedName("Nvarchar20")
    private String Nvarchar20;
    @SerializedName("Date2")
    private String Date2;
    @SerializedName("Date1")
    private String Date1;
    @SerializedName("RowCode")
    private Integer RowCode;
    @SerializedName("Isbn")
    private String Isbn;
    @SerializedName("GoodType")
    private String GoodType;

    @SerializedName("Itam_Show")
    private String Itam_Show;

    @SerializedName("Check")
    private boolean Check;

    public String getDate1() {
        return Date1;
    }

    public void setDate1(String date1) {
        Date1 = date1;
    }

    public Integer getAmount1() {
        return Amount1;
    }

    public String getGoodSubCode() {
        return GoodSubCode;
    }

    public String getNvarchar10() {
        return Nvarchar10;
    }

    public void setNvarchar10(String nvarchar10) {
        Nvarchar10 = nvarchar10;
    }

    public Integer getSellPrice1() {
        return SellPrice1;
    }

    public String getGoodExplain3() {
        return GoodExplain3;
    }

    public void setGoodExplain3(String goodExplain3) {
        GoodExplain3 = goodExplain3;
    }

    public String getGoodExplain4() {
        return GoodExplain4;
    }

    public void setGoodExplain4(String goodExplain4) {
        GoodExplain4 = goodExplain4;
    }

    public void setSellPrice1(Integer sellPrice1) {
        SellPrice1 = sellPrice1;
    }

    public void setGoodSubCode(String goodSubCode) {
        GoodSubCode = goodSubCode;
    }

    public void setAmount1(Integer amount1) {
        Amount1 = amount1;
    }

    public Integer getAmount2() {
        return Amount2;
    }

    public void setAmount2(Integer amount2) {
        Amount2 = amount2;
    }

    public boolean isCheck() {
        return Check;
    }

    public void setCheck(boolean check) {
        Check = check;
    }

    public String getItam_Show() {
        return Itam_Show;
    }

    public void setItam_Show(String itam_Show) {
        Itam_Show = itam_Show;
    }


    public Integer getFloat5() {
        return Float5;
    }

    public void setFloat5(Integer float5) {
        Float5 = float5;
    }

    public String getNvarchar13() {
        return Nvarchar13;
    }

    public void setNvarchar13(String nvarchar13) {
        Nvarchar13 = nvarchar13;
    }

    public String getNvarchar20() {
        return Nvarchar20;
    }

    public void setNvarchar20(String nvarchar20) {
        Nvarchar20 = nvarchar20;
    }


    public Integer getFactorAmount() {
        return FactorAmount;
    }

    public void setFactorAmount(Integer factorAmount) {
        FactorAmount = factorAmount;
    }

    public Integer getRowCode() {
        return RowCode;
    }

    public void setRowCode(Integer rowCode) {
        RowCode = rowCode;
    }

    public String getIsbn() {
        return Isbn;
    }

    public void setIsbn(String isbn) {
        Isbn = isbn;
    }


    public Integer getFloat1() {
        return Float1;
    }

    public void setFloat1(Integer float1) {
        Float1 = float1;
    }

    public String getNvarchar1() {
        return Nvarchar1;
    }

    public void setNvarchar1(String nvarchar1) {
        Nvarchar1 = nvarchar1;
    }

    public String getNvarchar2() {
        return Nvarchar2;
    }

    public void setNvarchar2(String nvarchar2) {
        Nvarchar2 = nvarchar2;
    }

    public String getDate2() {
        return Date2;
    }

    public void setDate2(String date2) {
        Date2 = date2;
    }

    public Integer getPrice() {
        return Price;
    }

    public void setPrice(Integer price) {
        Price = price;
    }

    public Integer getDefaultUnitValue() {
        return DefaultUnitValue;
    }

    public void setDefaultUnitValue(Integer defaultUnitValue) {
        DefaultUnitValue = defaultUnitValue;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }


    public String getGoodType() {
        return GoodType;
    }

    public void setGoodType(String goodType) {
        GoodType = goodType;
    }


    public Integer getShortage() {
        return Shortage;
    }

    public void setShortage(Integer shortage) {
        Shortage = shortage;
    }


    public Integer getAmount() {
        return Amount;
    }

    public void setAmount(Integer amount) {
        Amount = amount;
    }

    private int imageResource;
    private boolean isFavorite = false;
    private String iPath = Environment.getExternalStorageDirectory() + "/ksrshop/img/";

    public String getGoodName() {
        return GoodName;
    }

    void setGoodName(String goodName) {
        GoodName = goodName;
    }

    public String getGoodExplain1() {
        return GoodExplain1;
    }

    void setGoodExplain1(String goodExplain1) {
        GoodExplain1 = goodExplain1;
    }

    public String getGoodExplain2() {
        return GoodExplain2;
    }

    void setGoodExplain2(String goodExplain2) {
        GoodExplain2 = goodExplain2;
    }

    public String getFirstBarCode() {
        return FirstBarCode;
    }

    void setFirstBarCode(String firstBarCode) {
        FirstBarCode = firstBarCode;
    }

    public Integer getMaxSellPrice() {
        return MaxSellPrice;
    }

    void setMaxSellPrice(Integer maxSellPrice) {
        MaxSellPrice = maxSellPrice;
    }

    public Integer getGoodCode() {
        return GoodCode;
    }

    public void setGoodCode(Integer goodCode) {
        GoodCode = goodCode;
    }


    public String getImageName() {
        if (isEmpty(ImageName)) {
            return "";
        } else {
            return /*iPath +*/ ImageName;
        }
    }

    void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getUnitName() {
        return UnitName;
    }

    public void setUnitName(String unitName) {
        UnitName = unitName;
    }

    public Integer getGoodMainCode() {
        return GoodMainCode;
    }

    public void setGoodMainCode(Integer goodMainCode) {
        GoodMainCode = goodMainCode;
    }

    public Integer getReservedAmount() {
        return ReservedAmount;
    }

    public void setReservedAmount(Integer reservedAmount) {
        ReservedAmount = reservedAmount;
    }

}
