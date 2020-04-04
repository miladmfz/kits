package com.kits.asli.model;

import com.google.gson.annotations.SerializedName;

public class PreFactor {

    @SerializedName("PreFactorCode")
    private Integer PreFactorCode;
    @SerializedName("PreFactorKowsarCode")
    private Integer PreFactorKowsarCode;
    @SerializedName("PreFactorDate")
    private String PreFactorDate;
    @SerializedName("PreFactorTime")
    private String PreFactorTime;
    @SerializedName("PreFactorkowsarDate")
    private String PreFactorkowsarDate;
    @SerializedName("PreFactorExplain")
    private String PreFactorExplain;

    private String Customer;
    private long SumPrice;
    private Integer SumAmount;
    private Integer RowCount;
    private Integer Act;
    private String LastCode;
    private String LastFactor;


    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getLastCode() {
        return LastCode;
    }

    public void setLastCode(String lastCode) {
        LastCode = lastCode;
    }

    public String getLastFactor() {
        return LastFactor;
    }

    public void setLastFactor(String lastFactor) {
        LastFactor = lastFactor;
    }

    public Integer getAct() {
        return Act;
    }

    public void setAct(Integer act) {
        Act = act;
    }

    public String getPreFactorExplain() {
        return PreFactorExplain;
    }

    void setPreFactorExplain(String preFactorExplain) {
        PreFactorExplain = preFactorExplain;
    }

    public Integer getPreFactorKowsarCode() {
        return PreFactorKowsarCode;
    }

    void setPreFactorKowsarCode(Integer preFactorKowsarCode) {
        PreFactorKowsarCode = preFactorKowsarCode;

    }

    public Integer getSumAmount() {
        return SumAmount;
    }

    void setSumAmount(Integer sumAmount) {
        SumAmount = sumAmount;
    }

    public long getSumPrice() {
        return SumPrice;
    }

    void setSumPrice(long sumPrice) {
        SumPrice = sumPrice;
    }

    public Integer getRowCount() {
        return RowCount;
    }

    void setRowCount(Integer rowCount) {
        RowCount = rowCount;
    }

    public Integer getPreFactorCode() {
        return PreFactorCode;
    }

    void setPreFactorCode(Integer preFactorCode) {
        PreFactorCode = preFactorCode;
    }

    public String getPreFactorDate() {
        return PreFactorDate;
    }

    void setPreFactorDate(String preFactorDate) {
        PreFactorDate = preFactorDate;
    }

    public String getPreFactorkowsarDate() {
        return PreFactorkowsarDate;
    }

    void setPreFactorkowsarDate(String preFactorkowsarDate) {
        PreFactorkowsarDate = preFactorkowsarDate;
    }

    public String getPreFactorTime() {
        return PreFactorTime;
    }

    void setPreFactorTime(String preFactorTime) {
        PreFactorTime = preFactorTime;
    }

}
