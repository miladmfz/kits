package com.kits.asli.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.juanlabrador.badgecounter.BadgeCounter;
import com.kits.asli.R;
import com.kits.asli.adapters.Action;
import com.kits.asli.adapters.Prefactor_Header_adapter;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.PreFactor;

import java.util.ArrayList;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PrefactorActivity extends AppCompatActivity {

    private Integer pfcode;
    private Intent intent;
    private Handler handler = new Handler();
    private SharedPreferences shPref;
    private EditText edtsearch;
    private ArrayList<PreFactor> preFactors = new ArrayList<>();
    private DatabaseHelper dbh = new DatabaseHelper(this);
    private Action action;
    private RecyclerView re;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefactor);


        final Dialog dialog1;
        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();

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


    //**************************************************


    public void init() {

        action = new Action(getApplicationContext());
        shPref = getSharedPreferences("act", Context.MODE_PRIVATE);
        pfcode = Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)));

        TextView lastfactor = findViewById(R.id.PrefactorActivity_lastfactor);
        edtsearch = findViewById(R.id.PrefactorActivity_edtsearch);
        Button addfactor = findViewById(R.id.PrefactorActivity_addfactor);
        Button refresh = findViewById(R.id.PrefactorActivity_refresh);
        Toolbar toolbar = findViewById(R.id.PrefactorActivity_toolbar);
        setSupportActionBar(toolbar);
        lastfactor.setText(Farsi_number.PerisanNumber(String.valueOf(pfcode)));
        re = findViewById(R.id.PrefactorActivity_recyclerView);


        preFactors = dbh.getAllPrefactorHeader("");
        Prefactor_Header_adapter adapter = new Prefactor_Header_adapter(preFactors, PrefactorActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PrefactorActivity.this, 1);//grid
        re.setLayoutManager(gridLayoutManager);
        re.setAdapter(adapter);
        re.setItemAnimator(new DefaultItemAnimator());


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());

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
                                preFactors = dbh.getAllPrefactorHeader(srch);
                                Prefactor_Header_adapter adapter = new Prefactor_Header_adapter(preFactors, PrefactorActivity.this);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(PrefactorActivity.this, 1);//grid
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

        addfactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(PrefactorActivity.this, CustomerActivity.class);
                intent.putExtra("edit", "0");
                intent.putExtra("factor_code", 0);
                startActivity(intent);

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        BadgeCounter.hide(menu.findItem(R.id.bag_shop));
        return true;
    }

    @Override
    protected void onRestart() {
        finish();
        startActivity(getIntent());
        super.onRestart();
    }

    @Override//option menu

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) != 0) {
                intent = new Intent(PrefactorActivity.this, BuyActivity.class);
                intent.putExtra("PreFac", Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))));
                intent.putExtra("showflag", 2);
                startActivity(intent);
            } else {
                if (pfcode != 0) {
                    intent = new Intent(PrefactorActivity.this, SearchActivity.class);
                    intent.putExtra("scan", " ");
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "سبد خرید خالی می باشد", Toast.LENGTH_SHORT).show();
                    intent = new Intent(PrefactorActivity.this, PrefactoropenActivity.class);
                    startActivity(intent);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
