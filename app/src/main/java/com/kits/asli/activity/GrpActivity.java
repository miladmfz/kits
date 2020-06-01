package com.kits.asli.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.adapters.Action;
import com.kits.asli.adapters.Good_ProSearch_Adapter;
import com.kits.asli.adapters.Grp_Vlist_detail_Adapter;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.Good;
import com.kits.asli.model.GoodGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class GrpActivity extends AppCompatActivity {

    private Integer grid, id = 0, conter = 0, itemamount;
    private Action action;
    private Intent intent;
    private SharedPreferences shPref;
    private SharedPreferences.Editor sEdit;
    ArrayList<String[]> intList = new ArrayList<>();
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private DatabaseHelper dbh = new DatabaseHelper(GrpActivity.this);
    private ArrayList<Good> goods = new ArrayList<>();
    private ArrayList<GoodGroup> GoodGroups = new ArrayList<>();
    private String title = "گروه ها";
    private RecyclerView rc_grp, rc_good;
    FloatingActionButton fab;
    int pastVisiblesItems = 0, visibleItemCount, totalItemCount, PageNo = 0;
    Menu item_multi;
    Good_ProSearch_Adapter adapter;
    GridLayoutManager gridLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grp);

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


    //*************************************************

    public void init() {


        action = new Action(getApplicationContext());
        shPref = getSharedPreferences("act", Context.MODE_PRIVATE);
        grid = Integer.parseInt(Objects.requireNonNull(shPref.getString("grid", null)));
        itemamount = Integer.parseInt(Objects.requireNonNull(shPref.getString("itemamount", null)));


        Toolbar toolbar = findViewById(R.id.GrpActivity_toolbar);
        final SwitchMaterial mySwitch_activestack = findViewById(R.id.qep_Activityswitch);
        final SwitchMaterial mySwitch_goodamount = findViewById(R.id.qep_Activityswitch_amount);
        final TextView customer = findViewById(R.id.GrpActivity_customer);
        final TextView sumfac = findViewById(R.id.GrpActivity_sum_factor);
        final TextView customer_code = findViewById(R.id.GrpActivity_customer_code);
        final Button change_search = findViewById(R.id.GrpActivity_change_search);
        final Button filter_active = findViewById(R.id.GrpActivity_filter_active);
        final Button ref_fac = findViewById(R.id.GrpActivity_refresh_fac);
        final MaterialCardView line_pro = findViewById(R.id.GrpActivity_search_line_p);
        final MaterialCardView line = findViewById(R.id.GrpActivity_search_line);
        final EditText edtsearch = findViewById(R.id.GrpActivity_edtsearch);
        final Handler handler = new Handler();
        rc_good = findViewById(R.id.GrpActivity_good);
        fab = findViewById(R.id.GrpActivity_fab);


        toolbar.setTitle(title);
        setSupportActionBar(toolbar);


        if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) == 0) {
            customer.setText("فاکتوری انتخاب نشده");
            sumfac.setText("0");
        } else {
            customer.setText(dbh.getFactorCustomer(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)))));
            sumfac.setText(Farsi_number.PerisanNumber(decimalFormat.format(dbh.getFactorSum(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)))))));
            customer_code.setText(Farsi_number.PerisanNumber(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) + ""));
        }

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

        rc_grp = findViewById(R.id.GrpActivity_grp);
        GoodGroups = dbh.getAllGroups("", id);


        Grp_Vlist_detail_Adapter adapter4 = new Grp_Vlist_detail_Adapter(GoodGroups, GrpActivity.this);
        rc_grp.setLayoutManager(new GridLayoutManager(GrpActivity.this, 2, GridLayoutManager.HORIZONTAL, false));
        if (GoodGroups.size() == 0) {
            rc_grp.getLayoutParams().height = 0;
        }
        rc_grp.setAdapter(adapter4);
        rc_grp.setItemAnimator(new DefaultItemAnimator());


        edtsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtsearch.selectAll();
            }
        });

        edtsearch.addTextChangedListener(new TextWatcher() {
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

                        String srch = action.arabicToenglish(editable.toString());
                        goods = dbh.getAllGood(srch, id, 0, 0, shPref.getBoolean("activestack", true), shPref.getBoolean("goodamount", true), itemamount);
                        adapter = new Good_ProSearch_Adapter(goods, GrpActivity.this);
                        gridLayoutManager = new GridLayoutManager(GrpActivity.this, grid);//grid
                        rc_good.setLayoutManager(gridLayoutManager);
                        rc_good.setAdapter(adapter);
                        rc_good.setItemAnimator(new DefaultItemAnimator());
                    }
                }, Integer.parseInt(Objects.requireNonNull(shPref.getString("delay", null))));

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        edtsearch.selectAll();
                    }
                }, 5000);
            }
        });

        goods = dbh.getAllGood("", id, 0, 0, shPref.getBoolean("activestack", true), shPref.getBoolean("goodamount", true), itemamount);
        adapter = new Good_ProSearch_Adapter(goods, GrpActivity.this);
        gridLayoutManager = new GridLayoutManager(GrpActivity.this, grid);//grid
        rc_good.setLayoutManager(gridLayoutManager);
        rc_good.setAdapter(adapter);
        rc_good.setItemAnimator(new DefaultItemAnimator());

        change_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conter == 0) {
                    line_pro.setVisibility(View.VISIBLE);
                    filter_active.setVisibility(View.VISIBLE);
                    line.setVisibility(View.GONE);
                    change_search.setText("جستجوی عادی");
                    conter = conter + 1;
                    Log.e("asli_conter", "" + conter);
                } else {
                    line_pro.setVisibility(View.GONE);
                    filter_active.setVisibility(View.GONE);
                    line.setVisibility(View.VISIBLE);
                    change_search.setText("جستجوی پیشرفته");
                    conter = conter - 1;
                    Log.e("asli_conter", "" + conter);
                }
            }
        });

        filter_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText goodname = findViewById(R.id.GrpActivity_search_pro_good);
                EditText dragoman = findViewById(R.id.GrpActivity_search_pro_dragoman);
                EditText nasher = findViewById(R.id.GrpActivity_search_pro_nasher);
                EditText period = findViewById(R.id.GrpActivity_search_pro_period);
                EditText writer = findViewById(R.id.GrpActivity_search_pro_writer);
                EditText PrintYear = findViewById(R.id.GrpActivity_search_pro_PrintYear);
                DatabaseHelper dbh = new DatabaseHelper(GrpActivity.this);
                int aperiod;
                String agoodname = action.arabicToenglish(goodname.getText().toString());
                String adragoman = action.arabicToenglish(dragoman.getText().toString());
                String anasher = action.arabicToenglish(nasher.getText().toString());
                String periodd = action.arabicToenglish(period.getText().toString());
                String awriter = action.arabicToenglish(writer.getText().toString());
                String aPrintYear = action.arabicToenglish(PrintYear.getText().toString());
                if (!periodd.equals("")) {
                    aperiod = Integer.valueOf(action.arabicToenglish(period.getText().toString()));
                } else {
                    aperiod = 0;
                }
                goods = dbh.getAllGood_Extended("", "", id, agoodname, awriter, adragoman, anasher, aperiod, aPrintYear, shPref.getBoolean("activestack", true), shPref.getBoolean("goodamount", true));
                adapter = new Good_ProSearch_Adapter(goods, GrpActivity.this);
                gridLayoutManager = new GridLayoutManager(GrpActivity.this, grid);//grid
                rc_good.setLayoutManager(gridLayoutManager);
                rc_good.setAdapter(adapter);
                rc_good.setItemAnimator(new DefaultItemAnimator());
                Toast.makeText(GrpActivity.this, "انجام شد ", Toast.LENGTH_SHORT).show();
            }
        });

        if (shPref.getBoolean("activestack", true)) {
            mySwitch_activestack.setChecked(true);
            mySwitch_activestack.setText("فعال");

        } else {
            mySwitch_activestack.setChecked(false);
            mySwitch_activestack.setText("فعال -غیرفعال");

        }

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
                        String srch = action.arabicToenglish(edtsearch.getText().toString());
                        goods = dbh.getAllGood(srch, id, 0, 0, shPref.getBoolean("activestack", true), shPref.getBoolean("goodamount", true), itemamount);
                        adapter = new Good_ProSearch_Adapter(goods, GrpActivity.this);
                        gridLayoutManager = new GridLayoutManager(GrpActivity.this, grid);//grid
                        rc_good.setLayoutManager(gridLayoutManager);
                        rc_good.setAdapter(adapter);
                        rc_good.setItemAnimator(new DefaultItemAnimator());
                    }

                } else {
                    mySwitch_goodamount.setText("هردو");
                    sEdit = shPref.edit();
                    sEdit.putBoolean("goodamount", false);
                    sEdit.apply();
                    if (conter == 0) {
                        String srch = action.arabicToenglish(edtsearch.getText().toString());
                        goods = dbh.getAllGood(srch, id, 0, 0, shPref.getBoolean("activestack", true), shPref.getBoolean("goodamount", true), itemamount);
                        adapter = new Good_ProSearch_Adapter(goods, GrpActivity.this);
                        gridLayoutManager = new GridLayoutManager(GrpActivity.this, grid);//grid
                        rc_good.setLayoutManager(gridLayoutManager);
                        rc_good.setAdapter(adapter);
                        rc_good.setItemAnimator(new DefaultItemAnimator());
                    }
                }
            }
        });


        mySwitch_activestack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {


                    mySwitch_activestack.setText("فعال");
                    sEdit = shPref.edit();
                    sEdit.putBoolean("activestack", true);
                    sEdit.apply();
                    if (conter == 0) {
                        String srch = action.arabicToenglish(edtsearch.getText().toString());
                        goods = dbh.getAllGood(srch, id, 0, 0, shPref.getBoolean("activestack", true), shPref.getBoolean("goodamount", true), itemamount);
                        adapter = new Good_ProSearch_Adapter(goods, GrpActivity.this);
                        gridLayoutManager = new GridLayoutManager(GrpActivity.this, grid);//grid
                        rc_good.setLayoutManager(gridLayoutManager);
                        rc_good.setAdapter(adapter);
                        rc_good.setItemAnimator(new DefaultItemAnimator());


                    }
                } else {
                    mySwitch_activestack.setText("فعال -غیرفعال");
                    sEdit = shPref.edit();
                    sEdit.putBoolean("activestack", false);
                    sEdit.apply();
                    if (conter == 0) {
                        String srch = action.arabicToenglish(edtsearch.getText().toString());
                        goods = dbh.getAllGood(srch, id, 0, 0, shPref.getBoolean("activestack", true), shPref.getBoolean("goodamount", true), itemamount);
                        adapter = new Good_ProSearch_Adapter(goods, GrpActivity.this);
                        gridLayoutManager = new GridLayoutManager(GrpActivity.this, grid);//grid
                        rc_good.setLayoutManager(gridLayoutManager);
                        rc_good.setAdapter(adapter);
                        rc_good.setItemAnimator(new DefaultItemAnimator());
                    }
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(GrpActivity.this);
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
                                for (String[] s : intList) {
                                    if (s[1].equals("")) s[1] = "-1";
                                    DatabaseHelper dbh = new DatabaseHelper(GrpActivity.this);
                                    String pf = shPref.getString("prefactor_code", null);
                                    dbh.InsertPreFactor(Integer.parseInt(pf),
                                            Integer.parseInt(s[0]),
                                            Integer.parseInt(amo),
                                            Integer.parseInt(s[1]),
                                            0);
                                }
                                Toast toast = Toast.makeText(GrpActivity.this, "به سبد خرید اضافه شد", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 10, 10);
                                toast.show();
                                dialog.dismiss();
                                item_multi.findItem(R.id.menu_multi).setVisible(false);
                                for (Good good : goods) {
                                    good.setCheck(false);
                                }
                                intList.clear();
                                adapter = new Good_ProSearch_Adapter(goods, GrpActivity.this);
                                gridLayoutManager = new GridLayoutManager(GrpActivity.this, grid);
                                gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
                                rc_good.setLayoutManager(gridLayoutManager);
                                rc_good.setAdapter(adapter);
                                rc_good.setItemAnimator(new DefaultItemAnimator());
                                fab.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(GrpActivity.this, "تعداد مورد نظر صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(GrpActivity.this, "تعداد مورد نظر صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        id = data.getInt("id");
        title = data.getString("title");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item_multi = menu;

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override//option menu
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) != 0) {
                intent = new Intent(GrpActivity.this, BuyActivity.class);
                intent.putExtra("PreFac", Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))));
                intent.putExtra("showflag", 2);
                startActivity(intent);
            } else {
                Intent home = new Intent(GrpActivity.this, NavActivity.class);
                startActivity(home);
            }
            return true;
        }
        if (item.getItemId() == R.id.menu_multi) {
            item_multi.findItem(R.id.menu_multi).setVisible(false);
            for (Good good : goods) {
                good.setCheck(false);
            }
            intList.clear();
            adapter = new Good_ProSearch_Adapter(goods, GrpActivity.this);
            gridLayoutManager = new GridLayoutManager(GrpActivity.this, grid);
            gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
            rc_good.setLayoutManager(gridLayoutManager);
            rc_good.setAdapter(adapter);
            rc_good.setItemAnimator(new DefaultItemAnimator());
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
            intList.add(new String[]{String.valueOf(code_fun), String.valueOf(price_fun)});
            item_multi.findItem(R.id.menu_multi).setVisible(true);

        } else {
            int b = 0, c = 0;
            for (String[] s : intList) {
                if (s[0].equals(String.valueOf(code_fun))) b = c;
                c++;
            }
            intList.remove(b);
            if (intList.size() < 1) {
                fab.setVisibility(View.GONE);
                item_multi.findItem(R.id.menu_multi).setVisible(false);
            }
        }
    }

}


