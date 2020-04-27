package com.kits.asli.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

public class SearchActivity extends AppCompatActivity {


    private int Camera = 1;
    private Action action;
    private Integer conter = 0;
    private ArrayList<Good> goods = new ArrayList<>();
    private Integer id = 0, showflag = 0, grid, itemamount;
    public static String scan = "";
    private boolean activestack = true, goodamount = true;
    private RecyclerView re;
    private EditText edtsearch;

    Intent intent;
    SharedPreferences shPref;
    DatabaseHelper dbh = new DatabaseHelper(SearchActivity.this);

    DecimalFormat decimalFormat = new DecimalFormat("0,000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


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


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        scan = data.getString("scan");
    }


    public void init() {

        action = new Action(getApplicationContext());
        shPref = getSharedPreferences("act", Context.MODE_PRIVATE);
        grid = Integer.parseInt(Objects.requireNonNull(shPref.getString("grid", null)));
        itemamount = Integer.parseInt(Objects.requireNonNull(shPref.getString("itemamount", null)));


        final SwitchMaterial mySwitch = findViewById(R.id.SearchActivityswitch);
        final SwitchMaterial mySwitch_amount = findViewById(R.id.SearchActivityswitch_amount);
        final Button change_search = findViewById(R.id.SearchActivity_change_search);
        final Button grp = findViewById(R.id.SearchActivity_grp);
        final Button filter_active = findViewById(R.id.SearchActivity_filter_active);
        final Button ref_fac = findViewById(R.id.SearchActivity_refresh_fac);
        final MaterialCardView line_pro = findViewById(R.id.SearchActivity_search_line_p);
        final MaterialCardView line = findViewById(R.id.SearchActivity_search_line);
        final Handler handler = new Handler();
        final TextView customer = findViewById(R.id.SearchActivity_customer);
        final TextView sumfac = findViewById(R.id.SearchActivity_sum_factor);
        final TextView customer_code = findViewById(R.id.SearchActivity_customer_code);
        Toolbar toolbar = findViewById(R.id.SearchActivity_toolbar);
        re = findViewById(R.id.SearchActivity_R1);
        final RecyclerView gsre = findViewById(R.id.SearchActivity_grp_recy);

        edtsearch = findViewById(R.id.SearchActivity_edtsearch);
        final Button btn_scan = findViewById(R.id.SearchActivity_scan);


        if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) == 0) {
            customer.setText("فاکتوری انتخاب نشده");
            sumfac.setText("0");
        } else {
            customer.setText(dbh.getFactorCustomer(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)))));
            sumfac.setText(Farsi_number.PerisanNumber(decimalFormat.format(dbh.getFactorSum(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)))))));
            customer_code.setText(Farsi_number.PerisanNumber(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) + ""));
        }


        ArrayList<GoodGroup> goodGroups = dbh.getAllGroups("", id);
        Grp_Vlist_detail_Adapter adapter4 = new Grp_Vlist_detail_Adapter(goodGroups, SearchActivity.this);
        gsre.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2, GridLayoutManager.HORIZONTAL, false));
        gsre.setAdapter(adapter4);
        gsre.setItemAnimator(new DefaultItemAnimator());

        setSupportActionBar(toolbar);

        edtsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtsearch.selectAll();
            }
        });
        edtsearch.addTextChangedListener(
                new TextWatcher() {
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
                                ArrayList<Good> sgoods = dbh.getAllGood(srch, id, showflag, 0, activestack, goodamount, itemamount);
                                Good_ProSearch_Adapter adapter = new Good_ProSearch_Adapter(sgoods, SearchActivity.this);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, grid);//grid
                                re.setLayoutManager(gridLayoutManager);
                                re.setAdapter(adapter);
                                re.setItemAnimator(new DefaultItemAnimator());
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


        goods = dbh.getAllGood(scan, id, showflag, 0, activestack, goodamount, itemamount);
        Good_ProSearch_Adapter adapter = new Good_ProSearch_Adapter(goods, SearchActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, grid);//grid
        re.setLayoutManager(gridLayoutManager);
        re.setAdapter(adapter);
        re.setItemAnimator(new DefaultItemAnimator());

        change_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conter == 0) {
                    line_pro.setVisibility(View.VISIBLE);
                    filter_active.setVisibility(View.VISIBLE);
                    line.setVisibility(View.GONE);
                    change_search.setText("جستجوی عادی");
                    conter = conter + 1;
                    Log.e("conter", "" + conter);
                } else {
                    line_pro.setVisibility(View.GONE);
                    filter_active.setVisibility(View.GONE);
                    line.setVisibility(View.VISIBLE);
                    change_search.setText("جستجوی پیشرفته");
                    conter = conter - 1;
                    Log.e("conter", "" + conter);
                }
            }
        });

        filter_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText goodname = findViewById(R.id.SearchActivity_search_pro_good);
                EditText dragoman = findViewById(R.id.SearchActivity_search_pro_dragoman);
                EditText nasher = findViewById(R.id.SearchActivity_search_pro_nasher);
                EditText period = findViewById(R.id.SearchActivity_search_pro_period);
                EditText writer = findViewById(R.id.SearchActivity_search_pro_writer);
                EditText printyear = findViewById(R.id.SearchActivity_search_pro_printyear);
                DatabaseHelper dbh = new DatabaseHelper(SearchActivity.this);
                int aperiod;
                String agoodname = action.arabicToenglish(goodname.getText().toString());
                String adragoman = action.arabicToenglish(dragoman.getText().toString());
                String anasher = action.arabicToenglish(nasher.getText().toString());
                String periodd = action.arabicToenglish(period.getText().toString());
                String awriter = action.arabicToenglish(writer.getText().toString());
                String aprintyear = action.arabicToenglish(printyear.getText().toString());
                if (!periodd.equals("")) {
                    aperiod = Integer.valueOf(action.arabicToenglish(period.getText().toString()));
                } else {
                    aperiod = 0;
                }
                goods = dbh.getAllGood_Extended("", "", 0, agoodname, awriter, adragoman, anasher, aperiod, aprintyear, activestack, goodamount);
                Good_ProSearch_Adapter adapter = new Good_ProSearch_Adapter(goods, SearchActivity.this);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, grid);
                re.setLayoutManager(gridLayoutManager);
                re.setAdapter(adapter);
                re.setItemAnimator(new DefaultItemAnimator());
                Toast.makeText(SearchActivity.this, "انجام شد ", Toast.LENGTH_SHORT).show();
            }
        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    intent = new Intent(SearchActivity.this, ScanCodeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    requstforpermission();
                }
            }
        });

        grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gsre.getVisibility() == View.GONE) {
                    gsre.setVisibility(View.VISIBLE);
                } else {
                    gsre.setVisibility(View.GONE);
                }
            }
        });

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

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    mySwitch.setText("فعال");
                    activestack = true;
                    if (conter == 0) {
                        String srch = action.arabicToenglish(edtsearch.getText().toString());
                        ArrayList<Good> sgoods = dbh.getAllGood(srch, id, showflag, 0, activestack, goodamount, itemamount);
                        Good_ProSearch_Adapter adapter = new Good_ProSearch_Adapter(sgoods, SearchActivity.this);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, grid);//grid
                        re.setLayoutManager(gridLayoutManager);
                        re.setAdapter(adapter);
                        re.setItemAnimator(new DefaultItemAnimator());
                    }
                } else {

                    mySwitch.setText("فعال -غیرفعال");
                    activestack = false;
                    if (conter == 0) {
                        String srch = action.arabicToenglish(edtsearch.getText().toString());
                        ArrayList<Good> sgoods = dbh.getAllGood(srch, id, showflag, 0, activestack, goodamount, itemamount);
                        Good_ProSearch_Adapter adapter = new Good_ProSearch_Adapter(sgoods, SearchActivity.this);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, grid);//grid
                        re.setLayoutManager(gridLayoutManager);
                        re.setAdapter(adapter);
                        re.setItemAnimator(new DefaultItemAnimator());

                    }
                }
            }
        });


        mySwitch_amount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    mySwitch_amount.setText("موجود");
                    goodamount = true;
                    if (conter == 0) {
                        String srch = action.arabicToenglish(edtsearch.getText().toString());
                        ArrayList<Good> sgoods = dbh.getAllGood(srch, id, showflag, 0, activestack, goodamount, itemamount);
                        Good_ProSearch_Adapter adapter = new Good_ProSearch_Adapter(sgoods, SearchActivity.this);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, grid);//grid
                        re.setLayoutManager(gridLayoutManager);
                        re.setAdapter(adapter);
                        re.setItemAnimator(new DefaultItemAnimator());
                    }
                } else {

                    mySwitch_amount.setText("هردو");
                    goodamount = false;
                    if (conter == 0) {
                        String srch = action.arabicToenglish(edtsearch.getText().toString());
                        ArrayList<Good> sgoods = dbh.getAllGood(srch, id, showflag, 0, activestack, goodamount, itemamount);
                        Good_ProSearch_Adapter adapter = new Good_ProSearch_Adapter(sgoods, SearchActivity.this);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, grid);//grid
                        re.setLayoutManager(gridLayoutManager);
                        re.setAdapter(adapter);
                        re.setItemAnimator(new DefaultItemAnimator());
                    }


                }
            }
        });


    }


    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) != 0) {
                intent = new Intent(SearchActivity.this, BuyActivity.class);
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


    public void requstforpermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("permission needed")
                    .setMessage("this permission is needed because of this abd that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.CAMERA}, Camera);
                        }
                    })
                    .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Camera);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Camera) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}




