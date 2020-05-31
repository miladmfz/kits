package com.kits.asli.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.adapters.Good_ProSearch_Adapter;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.Good;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Search_date_detailActivity extends AppCompatActivity {

    private Integer conter = 0;
    private Integer date, grid;
    private SharedPreferences shPref;
    private SharedPreferences.Editor sEdit;
    ArrayList<String> intList = new ArrayList<>();
    private ArrayList<Good> goods = new ArrayList<>();
    private DatabaseHelper dbh = new DatabaseHelper(Search_date_detailActivity.this);
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_date_detail);


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

    //***************************************************
    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        date = data.getInt("date");
    }

    public void init() {

        final TextView customer = findViewById(R.id.Search_date_detailActivity_customer);
        final TextView sumfac = findViewById(R.id.Search_date_detailActivity_sum_factor);
        final TextView customer_code = findViewById(R.id.Search_date_detailActivity_customer_code);
        Button ref_fac = findViewById(R.id.Search_date_detailActivity_refresh_fac);
        fab = findViewById(R.id.search_date_fab);

        shPref = getSharedPreferences("act", Context.MODE_PRIVATE);
        grid = Integer.parseInt(Objects.requireNonNull(shPref.getString("grid", null)));


        ref_fac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) == 0) {
                    customer.setText("فاکتوری انتخاب نشده");
                    sumfac.setText("0");
                } else {
                    customer.setText(dbh.getFactorCustomer(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)))));
                    sumfac.setText(Farsi_number.PerisanNumber(decimalFormat.format(dbh.getFactorSum(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)))))));
                    customer_code.setText(Farsi_number.PerisanNumber(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) + ""));
                }
            }
        });

        if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) == 0) {
            customer.setText("فاکتوری انتخاب نشده");
            sumfac.setText("0");

        } else {
            customer.setText(dbh.getFactorCustomer(Integer.valueOf(Objects.requireNonNull(shPref.getString("prefactor_code", null)))));
            sumfac.setText(Farsi_number.PerisanNumber(decimalFormat.format(dbh.getFactorSum(Integer.valueOf(Objects.requireNonNull(shPref.getString("prefactor_code", null)))))));
            customer_code.setText(Farsi_number.PerisanNumber(Objects.requireNonNull(shPref.getString("prefactor_code", null))));

        }


        final RecyclerView re = findViewById(R.id.search_date_recycler);
        try {
            goods = dbh.getAllGood_ByDate(date, false, shPref.getBoolean("goodamount", true));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Good_ProSearch_Adapter adapter = new Good_ProSearch_Adapter(goods, Search_date_detailActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Search_date_detailActivity.this, grid);
        re.setLayoutManager(gridLayoutManager);
        re.setAdapter(adapter);
        re.setItemAnimator(new DefaultItemAnimator());


        final SwitchMaterial mySwitch_goodamount = findViewById(R.id.search_date_switch_amount);

        if (shPref.getBoolean("goodamount", true)) {
            mySwitch_goodamount.setChecked(true);
            mySwitch_goodamount.setText("موجود");

        } else {
            mySwitch_goodamount.setChecked(false);
            mySwitch_goodamount.setText("هردو");

        }

        mySwitch_goodamount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    mySwitch_goodamount.setText("موجود");
                    sEdit = shPref.edit();
                    sEdit.putBoolean("goodamount", true);
                    sEdit.apply();
                    if (conter == 0) {

                        try {
                            goods = dbh.getAllGood_ByDate(date, false, shPref.getBoolean("goodamount", true));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Good_ProSearch_Adapter adapter = new Good_ProSearch_Adapter(goods, Search_date_detailActivity.this);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(Search_date_detailActivity.this, grid);
                        re.setLayoutManager(gridLayoutManager);
                        re.setAdapter(adapter);
                        re.setItemAnimator(new DefaultItemAnimator());
                    }
                } else {

                    mySwitch_goodamount.setText("هردو");
                    sEdit = shPref.edit();
                    sEdit.putBoolean("goodamount", false);
                    sEdit.apply();
                    if (conter == 0) {


                        try {
                            goods = dbh.getAllGood_ByDate(date, false, shPref.getBoolean("goodamount", true));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Good_ProSearch_Adapter adapter = new Good_ProSearch_Adapter(goods, Search_date_detailActivity.this);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(Search_date_detailActivity.this, grid);
                        re.setLayoutManager(gridLayoutManager);
                        re.setAdapter(adapter);
                        re.setItemAnimator(new DefaultItemAnimator());
                    }


                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) != 0) {
                Intent intent = new Intent(Search_date_detailActivity.this, BuyActivity.class);
                intent.putExtra("PreFac", Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))));
                intent.putExtra("showflag", 2);
                startActivity(intent);

            } else {
                Toast.makeText(this, "فاکتوری انتخاب نشده است", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void good_select_function(int price_fun, int code_fun, int flag) {
        if (flag == 1) {
            fab.setVisibility(View.VISIBLE);
            intList.add(String.valueOf(code_fun));

        } else {
            intList.remove(String.valueOf(code_fun));
            if (intList.size() < 1) {
                fab.setVisibility(View.GONE);

            }
        }
    }

}
