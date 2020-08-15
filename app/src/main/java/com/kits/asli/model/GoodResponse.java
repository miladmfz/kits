package com.kits.asli.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class GoodResponse {

    @SerializedName("Goods")

    private ArrayList<Good> Goods;

    @SerializedName("Customers")

    private ArrayList<Customer> Customers;


    public ArrayList<Good> getGoods() {
        return Goods;
    }

    public void setGoods(ArrayList<Good> goods) {
        this.Goods = goods;
    }

    public ArrayList<Customer> getCustomers() {
        return Customers;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        Customers = customers;
    }
}
