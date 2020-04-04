package com.kits.asli.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.PreFactor;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Prefactor_Header_Box_adapter extends RecyclerView.Adapter<Prefactor_Header_Box_adapter.facViewHolder> {
    private final Context mContext;
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<PreFactor> PreFactors;


    public Prefactor_Header_Box_adapter(ArrayList<PreFactor> PreFactors, Context mContext) {
        this.mContext = mContext;
        this.PreFactors = PreFactors;
    }

    @NonNull
    @Override
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prefactor_header_box, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, final int position) {

        PreFactor facView = PreFactors.get(position);


        holder.fac_date.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getPreFactorDate())));
        holder.fac_time.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getPreFactorTime())));
        holder.fac_code.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getPreFactorCode())));
        holder.fac_detail.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getPreFactorExplain())));
        holder.fac_customer.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getCustomer())));
        holder.fac_row.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getRowCount())));
        holder.fac_count.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getSumAmount())));
        holder.fac_price.setText(Farsi_number.PerisanNumber(String.valueOf(decimalFormat.format(Integer.valueOf(String.valueOf(facView.getSumPrice()))))));


        holder.fac_rltv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SharedPreferences shPref;
                PreFactor facView = PreFactors.get(position);
                final String prefactor_code = "prefactor_code";
                shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                SharedPreferences.Editor sEdit = shPref.edit();
                sEdit.putString(prefactor_code, facView.getPreFactorCode().toString());
                sEdit.apply();
                Toast.makeText(mContext, "فاکتور مورد نظر انتخاب شد", Toast.LENGTH_SHORT).show();
                ((Activity) mContext).overridePendingTransition(0, 0);
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);


            }
        });


    }

    @Override
    public int getItemCount() {
        return PreFactors.size();
    }

    static class facViewHolder extends RecyclerView.ViewHolder {
        private TextView fac_code;
        private TextView fac_date;
        private TextView fac_time;
        private TextView fac_detail;
        private TextView fac_row;
        private TextView fac_count;
        private TextView fac_price;
        private TextView fac_customer;

        CardView fac_rltv;

        facViewHolder(View itemView) {
            super(itemView);
            fac_code = itemView.findViewById(R.id.pf_header_box_code);
            fac_date = itemView.findViewById(R.id.pf_header_box_date);
            fac_time = itemView.findViewById(R.id.pf_header_box_time);
            fac_row = itemView.findViewById(R.id.pf_header_box_row);
            fac_count = itemView.findViewById(R.id.pf_header_box_count);
            fac_price = itemView.findViewById(R.id.pf_header_box_price);
            fac_detail = itemView.findViewById(R.id.pf_header_box_detail);
            fac_customer = itemView.findViewById(R.id.pf_header_box_customer);


            fac_rltv = itemView.findViewById(R.id.pf_header_box);
        }
    }


}
