package com.kits.asli.activity;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kits.asli.R;
import com.kits.asli.adapters.Action;
import com.kits.asli.adapters.Replication;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;

import java.text.DecimalFormat;

import java.util.Objects;

//import com.kits.asli.model.Good;
//import java.util.ArrayList;
//import com.kits.asli.webService.APIInterface;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.widget.ProgressBar;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import com.kits.asli.model.GoodResponse;
//import com.kits.asli.webService.APIClient;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Action action;
    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;
    private SharedPreferences shPref;
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private Replication replication;

//    ArrayList<Good> goods;
//    RecyclerView rc_test;
//    GridLayoutManager gridLayoutManager = new GridLayoutManager(NavActivity.this, 2, GridLayoutManager.VERTICAL, false);
//    APIInterface apiInterface;
//    ProgressBar progressBar;
//    private int page = 1;
//    private int recorde = 14;
//    private boolean isloading = true;
//    private int past_visible, visible_item_show, total_item, last_total = 0;
//    private int view_thesshold = 14;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 100);
    }
//************************************************************

    public void init() {
        action = new Action(NavActivity.this);
        replication = new Replication(NavActivity.this);


        shPref = getSharedPreferences("act", Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.NavActivity_toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.NavActivity_nav);
        navigationView.setNavigationItemSelectedListener(this);


        noti();

        TextView customer = findViewById(R.id.MainActivity_customer);
        TextView sumfac = findViewById(R.id.MainActivity_sum_factor);
        TextView customer_code = findViewById(R.id.MainActivity_customer_code);
        Button create_factor = findViewById(R.id.mainactivity_create_factor);
        Button good_search = findViewById(R.id.mainactivity_good_search);
        Button open_factor = findViewById(R.id.mainactivity_open_factor);
        Button all_factor = findViewById(R.id.mainactivity_all_factor);

        final DatabaseHelper dbh = new DatabaseHelper(NavActivity.this);

        if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) == 0) {
            customer.setText("فاکتوری انتخاب نشده");
            sumfac.setText("0");

        } else {
            customer.setText(dbh.getFactorCustomer(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)))));
            sumfac.setText(Farsi_number.PerisanNumber(decimalFormat.format(dbh.getFactorSum(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)))))));
            customer_code.setText(Farsi_number.PerisanNumber(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) + ""));
        }


        create_factor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(NavActivity.this, CustomerActivity.class);
                intent.putExtra("edit", "0");
                intent.putExtra("factor_code", 0);
                startActivity(intent);
            }
        });


        good_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(NavActivity.this, SearchActivity.class);
                intent.putExtra("scan", " ");
                startActivity(intent);
            }
        });

        open_factor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(NavActivity.this, PrefactoropenActivity.class);
                intent.putExtra("fac", 1);
                startActivity(intent);
            }
        });

        all_factor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(NavActivity.this, PrefactorActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج مجددا کلیک کنید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            intent = new Intent(NavActivity.this, SearchActivity.class);
            intent.putExtra("scan", " ");
            startActivity(intent);
        } else if (id == R.id.aboutus) {
            intent = new Intent(NavActivity.this, AboutusActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_buy_history) {
            intent = new Intent(NavActivity.this, PrefactorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_open_fac) {
            intent = new Intent(NavActivity.this, PrefactoropenActivity.class);
            intent.putExtra("fac", 1);
            startActivity(intent);
        } else if (id == R.id.nav_rep) {

            action.app_info();
            replication.replicateCentralChange();

        } else if (id == R.id.nav_buy) {
            if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) > 0) {
                intent = new Intent(NavActivity.this, BuyActivity.class);
                intent.putExtra("PreFac", Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))));
                intent.putExtra("showflag", 2);
                startActivity(intent);
            } else {
                Toast.makeText(this, "سبد خرید خالی است.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_search_date) {
            intent = new Intent(NavActivity.this, Search_dateActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_cfg) {
            intent = new Intent(NavActivity.this, ConfigActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                intent = new Intent(NavActivity.this, BuyActivity.class);
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


    private void noti() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Kowsarmobile", "Kowsarmobile", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }


        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successfull";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                        Log.e("msg=", "" + msg);
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic("broker")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successfull";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                        Log.e("msg=", "" + msg);
                    }
                });


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}

