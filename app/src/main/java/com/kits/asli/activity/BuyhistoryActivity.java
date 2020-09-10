package com.kits.asli.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.adapters.Action;
import com.kits.asli.adapters.Good_buy_history_Adapter;
import com.kits.asli.adapters.Good_buy_history_Adapter_line;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.Good;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class BuyhistoryActivity extends AppCompatActivity {

    private Integer PreFac = 0, j = 1;
    private String srch = "";
    private SharedPreferences shPref;
    private ArrayList<Good> goods = new ArrayList<>();
    private DatabaseHelper dbh = new DatabaseHelper(BuyhistoryActivity.this);
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private Action action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyhistory);

        final Dialog dialog1;
        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        intent();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                init();

            }
        }, 100);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog1.dismiss();

            }
        }, 1000);

    }

    //*****************************************************************
    public void init() {
        action = new Action(BuyhistoryActivity.this);
        shPref = getSharedPreferences("act", Context.MODE_PRIVATE);


        TextView row = findViewById(R.id.Buy_history_Activity_total_row_buy);
        TextView price = findViewById(R.id.Buy_history_Activity_total_price_buy);
        TextView amount = findViewById(R.id.Buy_history_Activity_total_amount_buy);
        EditText edtse = findViewById(R.id.Buy_history_Activity_edtsearch);
        final Button history_row = findViewById(R.id.Buy_history_Activity_row);
        final RecyclerView re = findViewById(R.id.Buy_history_Activity_R1);
        final Handler handler = new Handler();


        edtse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srch = action.arabicToenglish(editable.toString());
                        if (j == 0) {
                            goods = dbh.getAllPreFactorRows(srch, PreFac);
                            history_row.setBackground(ContextCompat.getDrawable(BuyhistoryActivity.this, R.drawable.bg_round_green_history_line));
                            Good_buy_history_Adapter adapter = new Good_buy_history_Adapter(goods, BuyhistoryActivity.this);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(BuyhistoryActivity.this, 1);//grid
                            re.setLayoutManager(gridLayoutManager);
                            re.setAdapter(adapter);
                            re.setItemAnimator(new DefaultItemAnimator());

                        } else {
                            goods = dbh.getAllPreFactorRows(srch, PreFac);
                            history_row.setBackground(ContextCompat.getDrawable(BuyhistoryActivity.this, R.drawable.bg_round_green_history));
                            Good_buy_history_Adapter_line adapter = new Good_buy_history_Adapter_line(goods, BuyhistoryActivity.this);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(BuyhistoryActivity.this, 1);//grid
                            re.setLayoutManager(gridLayoutManager);
                            re.setAdapter(adapter);
                            re.setItemAnimator(new DefaultItemAnimator());
                        }
                    }
                }, Integer.parseInt(Objects.requireNonNull(shPref.getString("delay", null))));
            }
        });

        history_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (j == 0) {
                    j = j + 1;
                    goods = dbh.getAllPreFactorRows(srch, PreFac);
                    history_row.setBackground(ContextCompat.getDrawable(BuyhistoryActivity.this, R.drawable.bg_round_green_history_line));
                    Good_buy_history_Adapter adapter = new Good_buy_history_Adapter(goods, BuyhistoryActivity.this);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(BuyhistoryActivity.this, 1);//grid
                    re.setLayoutManager(gridLayoutManager);
                    re.setAdapter(adapter);
                    re.setItemAnimator(new DefaultItemAnimator());

                } else {
                    j = j - 1;
                    goods = dbh.getAllPreFactorRows(srch, PreFac);
                    history_row.setBackground(ContextCompat.getDrawable(BuyhistoryActivity.this, R.drawable.bg_round_green_history));
                    Good_buy_history_Adapter_line adapter = new Good_buy_history_Adapter_line(goods, BuyhistoryActivity.this);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(BuyhistoryActivity.this, 1);//grid
                    re.setLayoutManager(gridLayoutManager);
                    re.setAdapter(adapter);
                    re.setItemAnimator(new DefaultItemAnimator());
                }


            }
        });
        goods = dbh.getAllPreFactorRows(srch, PreFac);

        Good_buy_history_Adapter adapter = new Good_buy_history_Adapter(goods, BuyhistoryActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(BuyhistoryActivity.this, 1);//grid
        re.setLayoutManager(gridLayoutManager);
        re.setAdapter(adapter);
        re.setItemAnimator(new DefaultItemAnimator());

        price.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + dbh.getFactorSum(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_good", null))))))));
        amount.setText(Farsi_number.PerisanNumber("" + dbh.getFactorSumAmount(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_good", null))))));
        row.setText(Farsi_number.PerisanNumber("" + goods.size()));


    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        PreFac = data.getInt("PreFac");

    }


}
