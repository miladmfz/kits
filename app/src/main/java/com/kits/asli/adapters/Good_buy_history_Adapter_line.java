package com.kits.asli.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.Good;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Good_buy_history_Adapter_line extends RecyclerView.Adapter<Good_buy_history_Adapter_line.GoodViewHolder> {

    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private ArrayList<Good> goods;
    private long sum = 0;


    public Good_buy_history_Adapter_line(ArrayList<Good> goods, Context mContext) {
        this.goods = goods;
    }

    @NonNull
    @Override
    public GoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy_history_line, parent, false);
        return new GoodViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodViewHolder holder, int position) {
        Good goodView = goods.get(position);

        Integer sellprice = goodView.getPrice();
        final Integer fac_amount = goodView.getAmount();
        final Integer unit_value = goodView.getDefaultUnitValue();

        long price = (long) sellprice * fac_amount * unit_value;
        sum = sum + price;


        holder.goodnameTextView.setText(Farsi_number.PerisanNumber(goodView.getGoodName()));
        holder.priceTextView.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + goodView.getPrice()))));
        holder.amount.setText(Farsi_number.PerisanNumber(goodView.getAmount().toString()));
        holder.code.setText(Farsi_number.PerisanNumber(goodView.getGoodCode().toString()));
        holder.total.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + price))));

//        intent = new Intent(mContext, BuyActivity.class);
//        intent.putExtra("sum", sum);


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    class GoodViewHolder extends RecyclerView.ViewHolder {
        private TextView goodnameTextView;
        private TextView priceTextView;
        private TextView total;
        private TextView amount;
        private TextView code;
        CardView rltv;

        GoodViewHolder(View itemView) {
            super(itemView);
            goodnameTextView = itemView.findViewById(R.id.good_buy_history_name_line);
            priceTextView = itemView.findViewById(R.id.good_buy_history_price_line);
            total = itemView.findViewById(R.id.good_buy_history_total_line);
            amount = itemView.findViewById(R.id.good_buy_history_amount_line);
            code = itemView.findViewById(R.id.good_buy_history_code_line);
            rltv = itemView.findViewById(R.id.good_buy_history_line);
        }
    }


}
