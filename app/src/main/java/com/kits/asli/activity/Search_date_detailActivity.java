package com.kits.asli.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

    private Integer conter = 0, id = 1;
    private Integer date, grid;
    private SharedPreferences shPref;
    private SharedPreferences.Editor sEdit;
    ArrayList<String[]> Multi_buy = new ArrayList<>();
    private ArrayList<Good> goods = new ArrayList<>();
    private DatabaseHelper dbh = new DatabaseHelper(Search_date_detailActivity.this);
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    FloatingActionButton fab;
    Good_ProSearch_Adapter adapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView re;
    int pastVisiblesItems = 0, visibleItemCount, totalItemCount, PageNo = 0;
    Menu item_multi;
    SwitchMaterial mySwitch_goodamount;
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
        if (getString(R.string.app_name).equals("آسیم")) {
            id = data.getInt("id");
        }
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

        re = findViewById(R.id.search_date_recycler);

        if (id == 1) {
            try {
                goods = dbh.getAllGood_ByDate(date, false, shPref.getBoolean("goodamount", true));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            adapter = new Good_ProSearch_Adapter(goods, Search_date_detailActivity.this);
            gridLayoutManager = new GridLayoutManager(Search_date_detailActivity.this, grid);
            re.setLayoutManager(gridLayoutManager);
            re.setAdapter(adapter);
            re.setItemAnimator(new DefaultItemAnimator());
        } else {
            mySwitch_goodamount.setVisibility(View.GONE);
            try {
                goods = dbh.getAllGood_ByDate1();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            adapter = new Good_ProSearch_Adapter(goods, Search_date_detailActivity.this);
            gridLayoutManager = new GridLayoutManager(Search_date_detailActivity.this, grid);
            re.setLayoutManager(gridLayoutManager);
            re.setAdapter(adapter);
            re.setItemAnimator(new DefaultItemAnimator());
        }

        mySwitch_goodamount = findViewById(R.id.search_date_switch_amount);

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

                        adapter = new Good_ProSearch_Adapter(goods, Search_date_detailActivity.this);
                        gridLayoutManager = new GridLayoutManager(Search_date_detailActivity.this, grid);
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

                        adapter = new Good_ProSearch_Adapter(goods, Search_date_detailActivity.this);
                        gridLayoutManager = new GridLayoutManager(Search_date_detailActivity.this, grid);
                        re.setLayoutManager(gridLayoutManager);
                        re.setAdapter(adapter);
                        re.setItemAnimator(new DefaultItemAnimator());
                    }


                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Search_date_detailActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//title laye nadashte bashim
                dialog.setContentView(R.layout.box_multi_buy);
                Button boxbuy = dialog.findViewById(R.id.box_multi_buy_btn);
                final EditText amount_mlti = dialog.findViewById(R.id.box_multi_buy_amount);
                final TextView tv = dialog.findViewById(R.id.box_multi_buy_factor);
                tv.setText(dbh.getFactorCustomer(Integer.valueOf(shPref.getString("prefactor_code", null))));
                dialog.show();
                amount_mlti.requestFocus();
                amount_mlti.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(amount_mlti, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 500);

                boxbuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String amo = amount_mlti.getText().toString();
                        if (!amo.equals("")) {
                            if (Integer.parseInt(amo) != 0) {
                                for (String[] s : Multi_buy) {
                                    if (s[1].equals("")) s[1] = "-1";
                                    DatabaseHelper dbh = new DatabaseHelper(Search_date_detailActivity.this);
                                    String pf = shPref.getString("prefactor_code", null);
                                    dbh.InsertPreFactor(Integer.parseInt(pf),
                                            Integer.parseInt(s[0]),
                                            Integer.parseInt(amo),
                                            Integer.parseInt(s[1]),
                                            0);
                                }
                                Toast toast = Toast.makeText(Search_date_detailActivity.this, "به سبد خرید اضافه شد", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 10, 10);
                                toast.show();
                                dialog.dismiss();
                                item_multi.findItem(R.id.menu_multi).setVisible(false);
                                for (Good good : goods) {
                                    good.setCheck(false);
                                }
                                Multi_buy.clear();
                                adapter = new Good_ProSearch_Adapter(goods, Search_date_detailActivity.this);
                                adapter.multi_select = false;
                                gridLayoutManager = new GridLayoutManager(Search_date_detailActivity.this, grid);
                                gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
                                re.setLayoutManager(gridLayoutManager);
                                re.setAdapter(adapter);
                                re.setItemAnimator(new DefaultItemAnimator());
                                fab.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(Search_date_detailActivity.this, "تعداد مورد نظر صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Search_date_detailActivity.this, "تعداد مورد نظر صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item_multi = menu;

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
        if (item.getItemId() == R.id.menu_multi) {
            item_multi.findItem(R.id.menu_multi).setVisible(false);
            for (Good good : goods) {
                good.setCheck(false);
            }
            Multi_buy.clear();
            adapter = new Good_ProSearch_Adapter(goods, Search_date_detailActivity.this);
            adapter.multi_select = false;

            gridLayoutManager = new GridLayoutManager(Search_date_detailActivity.this, grid);
            gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
            re.setLayoutManager(gridLayoutManager);
            re.setAdapter(adapter);
            re.setItemAnimator(new DefaultItemAnimator());
            fab.setVisibility(View.GONE);
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
            Multi_buy.add(new String[]{String.valueOf(code_fun), String.valueOf(price_fun)});
            item_multi.findItem(R.id.menu_multi).setVisible(true);

        } else {
            int b = 0, c = 0;
            for (String[] s : Multi_buy) {
                if (s[0].equals(String.valueOf(code_fun))) b = c;
                c++;
            }
            Multi_buy.remove(b);
            if (Multi_buy.size() < 1) {
                fab.setVisibility(View.GONE);
                adapter.multi_select = false;

                item_multi.findItem(R.id.menu_multi).setVisible(false);
            }
        }
    }

}
