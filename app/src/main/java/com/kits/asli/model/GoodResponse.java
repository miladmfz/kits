package com.kits.asli.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class GoodResponse {

    @SerializedName("Goods")

    private ArrayList<Good> Goods;

    public ArrayList<Good> getGoods() {
        return Goods;
    }

    public void setGoods(ArrayList<Good> goods) {
        this.Goods = goods;
    }


}
