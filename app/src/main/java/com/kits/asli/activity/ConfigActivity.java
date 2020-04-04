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
import androidx.appcompat.widget.SwitchCompat;

import com.kits.asli.R;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.UserInfo;

import java.text.DecimalFormat;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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


//        Button detailsett = findViewById(R.id.config_detailsetting);
//        TextView email = findViewById(R.id.config_email);
//        TextView gname = findViewById(R.id.config_name);
//        TextView mobile = findViewById(R.id.config_mobile);
//        TextView melicode = findViewById(R.id.config_melicode);
//        TextView address = findViewById(R.id.config_address);
//        TextView phone = findViewById(R.id.config_phone);
//        TextView postalcode = findViewById(R.id.config_postalcode);
//        TextView hdb = findViewById(R.id.config_bhd);


        TextView borker = findViewById(R.id.config_borker);
        TextView grid = findViewById(R.id.config_grid);
        TextView delay = findViewById(R.id.config_delay);
        TextView itemamount = findViewById(R.id.config_itemamount);
        TextView sum_factor = findViewById(R.id.config_sum_factor);

        SwitchCompat regselloff = findViewById(R.id.config_selloff);


        sum_factor.setText(Farsi_number.PerisanNumber(decimalFormat.format(dbh.getsum_sumfactor())));


        UserInfo auser = dbh.LoadPersonalInfo();


        borker.setText(Farsi_number.PerisanNumber(auser.getBrokerCode()));
//        email.setText(Farsi_number.PerisanNumber(auser.getEmail()));
//        gname.setText(Farsi_number.PerisanNumber(auser.getNameFamily()));
//        address.setText(Farsi_number.PerisanNumber(auser.getAddress()));
//        mobile.setText(Farsi_number.PerisanNumber(auser.getMobile()));
//        melicode.setText(Farsi_number.PerisanNumber(auser.getMelliCode()));
//        phone.setText(Farsi_number.PerisanNumber(auser.getPhone()));
//        postalcode.setText(Farsi_number.PerisanNumber(auser.getPostalCode()));
//        hdb.setText(Farsi_number.PerisanNumber(auser.getBirthDate()));

        grid.setText(Farsi_number.PerisanNumber(String.valueOf(Integer.parseInt(Objects.requireNonNull(shPref.getString("grid", null))))));
        delay.setText(Farsi_number.PerisanNumber(String.valueOf(Integer.parseInt(Objects.requireNonNull(shPref.getString("delay", null))))));
        itemamount.setText(Farsi_number.PerisanNumber(String.valueOf(Integer.parseInt(Objects.requireNonNull(shPref.getString("itemamount", null))))));

        if (Integer.parseInt(Objects.requireNonNull(shPref.getString("selloff", null))) == 0) {
            regselloff.setChecked(false);
        } else {
            regselloff.setChecked(true);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
