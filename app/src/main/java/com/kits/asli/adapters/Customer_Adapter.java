package com.kits.asli.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.activity.ConfigActivity;
import com.kits.asli.activity.PrefactorActivity;
import com.kits.asli.model.Customer;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.UserInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Customer_Adapter extends RecyclerView.Adapter<Customer_Adapter.facViewHolder> {
    private Context mContext;
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private ArrayList<Customer> customers;
    private String edit;
    private Integer factor_target;
    private DatabaseHelper dbh;
    private Action action;
    private Intent intent;


    public Customer_Adapter(ArrayList<Customer> customers, Context mContext, String edit, int factor_target) {
        this.mContext = mContext;
        this.customers = customers;
        this.edit = edit;
        this.factor_target = factor_target;
        this.dbh = new DatabaseHelper(mContext);
        this.action = new Action(mContext);

    }

    @NonNull
    @Override
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, int position) {

        final Customer Customerview = customers.get(position);

        holder.cus_code.setText(Farsi_number.PerisanNumber(String.valueOf(Customerview.getCustomerCode())));
        holder.cus_name.setText(Farsi_number.PerisanNumber(String.valueOf(Customerview.getCustomerName())));
        holder.cus_manage.setText(Farsi_number.PerisanNumber(String.valueOf(Customerview.getManager())));


        if (String.valueOf(Customerview.getAddress()) == "null") {
            holder.cus_addres.setText("");
        } else {
            holder.cus_addres.setText(Farsi_number.PerisanNumber(String.valueOf(Customerview.getAddress())));
        }

        if (String.valueOf(Customerview.getPhone()) == "null") {
            holder.cus_phone.setText("");
        } else {
            holder.cus_phone.setText(Farsi_number.PerisanNumber(String.valueOf(Customerview.getPhone())));
        }


        if (Customerview.getBestankar() > -1) {
            holder.cus_bes.setText(Farsi_number.PerisanNumber(String.valueOf(decimalFormat.format(Customerview.getBestankar()))));
            holder.cus_bes.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        } else {
            int a = (Customerview.getBestankar()) * (-1);
            holder.cus_bes.setText(Farsi_number.PerisanNumber(decimalFormat.format(a)));
            holder.cus_bes.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }

        holder.fac_rltv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (edit.equals("0")) {

                    DatabaseHelper dbh = new DatabaseHelper(mContext);
                    UserInfo auser = dbh.LoadPersonalInfo();
                    if (Integer.parseInt(auser.getBrokerCode()) > 0) {
                        action.addfactordialog(Customerview.getCustomerCode());
                    } else {
                        intent = new Intent(mContext, ConfigActivity.class);
                        Toast toast = Toast.makeText(mContext, "کد بازاریاب را وارد کنید", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 10, 10);
                        toast.show();
                        mContext.startActivity(intent);
                    }

                } else {
                    dbh.UpdatePreFactorHeader_Customer(factor_target, Customerview.getCustomerCode());
                    intent = new Intent(mContext, PrefactorActivity.class);
                    ((Activity) mContext).finish();
                    ((Activity) mContext).overridePendingTransition(0, 0);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(0, 0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    class facViewHolder extends RecyclerView.ViewHolder {
        private TextView cus_code;
        private TextView cus_name;
        private TextView cus_manage;
        private TextView cus_phone;
        private TextView cus_addres;
        private TextView cus_bes;
        CardView fac_rltv;

        facViewHolder(View itemView) {
            super(itemView);
            cus_code = itemView.findViewById(R.id.customer_code);
            cus_name = itemView.findViewById(R.id.customer_name);
            cus_manage = itemView.findViewById(R.id.customer_manage);
            cus_phone = itemView.findViewById(R.id.customer_phone);
            cus_addres = itemView.findViewById(R.id.customer_addres);
            cus_bes = itemView.findViewById(R.id.customer_bes);
            fac_rltv = itemView.findViewById(R.id.customer);
        }
    }

}
