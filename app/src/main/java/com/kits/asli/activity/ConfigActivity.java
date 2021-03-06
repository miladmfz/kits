package com.kits.asli.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import com.kits.asli.R;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.UserInfo;

import java.text.DecimalFormat;
import java.util.Objects;


public class ConfigActivity extends AppCompatActivity {

    private DatabaseHelper dbh = new DatabaseHelper(this);
    DecimalFormat decimalFormat = new DecimalFormat("0,000");


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        init();

    }


//****************************************************

    public void init() {

        SharedPreferences shPref = getSharedPreferences("act", Context.MODE_PRIVATE);


        Button tohome = findViewById(R.id.config_to_home);
        Button toreg = findViewById(R.id.config_to_reg);



        TextView borker = findViewById(R.id.config_borker);
        TextView grid = findViewById(R.id.config_grid);
        TextView delay = findViewById(R.id.config_delay);
        TextView itemamount = findViewById(R.id.config_itemamount);
        TextView sum_factor = findViewById(R.id.config_sum_factor);

        SwitchMaterial regselloff = findViewById(R.id.config_selloff);
        SwitchMaterial real_amount = findViewById(R.id.config_real_amount);
        SwitchMaterial auto_rep = findViewById(R.id.config_autorep);



        sum_factor.setText(Farsi_number.PerisanNumber(decimalFormat.format(dbh.getsum_sumfactor())));


        UserInfo auser = dbh.LoadPersonalInfo();


        borker.setText(Farsi_number.PerisanNumber(auser.getBrokerCode()));


        grid.setText(Farsi_number.PerisanNumber(String.valueOf(Integer.parseInt(Objects.requireNonNull(shPref.getString("grid", null))))));
        delay.setText(Farsi_number.PerisanNumber(String.valueOf(Integer.parseInt(Objects.requireNonNull(shPref.getString("delay", null))))));
        itemamount.setText(Farsi_number.PerisanNumber(String.valueOf(Integer.parseInt(Objects.requireNonNull(shPref.getString("itemamount", null))))));

        if (Integer.parseInt(Objects.requireNonNull(shPref.getString("selloff", null))) == 0) {
            regselloff.setChecked(false);
        } else {
            regselloff.setChecked(true);
        }

        if (shPref.getBoolean("real_amount", true)) {
            real_amount.setChecked(true);
        } else {
            real_amount.setChecked(false);
        }

        if (shPref.getBoolean("auto_rep", true)) {
            auto_rep.setChecked(true);
        } else {
            auto_rep.setChecked(false);
        }

        tohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(ConfigActivity.this, NavActivity.class);
                startActivity(home);
            }
        });


        toreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg = new Intent(ConfigActivity.this, RegistrationActivity.class);
                startActivity(reg);
            }
        });


    }


    @Override
    public void onRestart() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
        super.onRestart();

    }


}
