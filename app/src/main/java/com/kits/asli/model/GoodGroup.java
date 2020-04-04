package com.kits.asli.model;

import com.google.gson.annotations.SerializedName;

public class GoodGroup {

    @SerializedName("GoodGroupCode")
    private Integer GoodGroupCode;
    @SerializedName("Name")
    private String Name;
    @SerializedName("L1")
    private Integer L1;
    @SerializedName("L2")
    private Integer L2;
    @SerializedName("L3")
    private Integer L3;
    @SerializedName("L4")
    private Integer L4;
    @SerializedName("L5")
    private Integer L5;


    public Integer getGoodGroupCode() {
        return GoodGroupCode;
    }

    void setGoodGroupCode(Integer goodGroupCode) {
        GoodGroupCode = goodGroupCode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    void setL1(Integer l1) {
        L1 = l1;
    }


    void setL2(Integer l2) {
        L2 = l2;
    }

    void setL3(Integer l3) {
        L3 = l3;
    }


    void setL4(Integer l4) {
        L4 = l4;
    }


    void setL5(Integer l5) {
        L5 = l5;
    }


    public Integer getL1() {
        return L1;
    }

    public Integer getL2() {
        return L2;
    }

    public Integer getL3() {
        return L3;
    }

    public Integer getL4() {
        return L4;
    }

    public Integer getL5() {
        return L5;
    }

}
