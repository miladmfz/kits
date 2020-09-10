package com.kits.asli.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import com.kits.asli.R;
import com.kits.asli.adapters.Action;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.UserInfo;

import java.util.Objects;


public class RegistrationActivity extends AppCompatActivity {

    private DatabaseHelper dbh = new DatabaseHelper(this);
    private SharedPreferences shPref;
    private SharedPreferences.Editor sEdit;
    private Action action;
    private EditText regborker, reggrid, regdelay, regitemamount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        init();

    }


    public void init() {
        action = new Action(getApplicationContext());
        shPref = getSharedPreferences("act", Context.MODE_PRIVATE);


        Button regbtn = findViewById(R.id.Registr_btn);

        regborker = findViewById(R.id.Registr_borker);
        reggrid = findViewById(R.id.Registr_grid);
        regdelay = findViewById(R.id.Registr_delay);
        regitemamount = findViewById(R.id.Registr_itemamount);


        SwitchMaterial regselloff = findViewById(R.id.Registr_selloff);
        SwitchMaterial real_amount = findViewById(R.id.Registr_real_amount);


        UserInfo auser = dbh.LoadPersonalInfo();

        regborker.setText(Farsi_number.PerisanNumber(auser.getBrokerCode()));
        reggrid.setText(Farsi_number.PerisanNumber(String.valueOf(Integer.parseInt(Objects.requireNonNull(shPref.getString("grid", null))))));
        regdelay.setText(Farsi_number.PerisanNumber(String.valueOf(Integer.parseInt(Objects.requireNonNull(shPref.getString("delay", null))))));
        regitemamount.setText(Farsi_number.PerisanNumber(String.valueOf(Integer.parseInt(Objects.requireNonNull(shPref.getString("itemamount", null))))));

        if (Integer.parseInt(Objects.requireNonNull(shPref.getString("selloff", null))) == 0) {
            regselloff.setChecked(false);
        } else {
            regselloff.setChecked(true);
        }

        regselloff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (Integer.parseInt(Objects.requireNonNull(shPref.getString("selloff", null))) == 0) {
                    Toast.makeText(RegistrationActivity.this, "بله", Toast.LENGTH_SHORT).show();
                    sEdit = shPref.edit();
                    sEdit.putString("selloff", "1");
                    sEdit.apply();

                } else {
                    sEdit = shPref.edit();
                    sEdit.putString("selloff", "0");
                    sEdit.apply();
                    Toast.makeText(RegistrationActivity.this, "خیر", Toast.LENGTH_SHORT).show();


                }
            }
        });


        if (shPref.getBoolean("real_amount", true)) {
            real_amount.setChecked(true);
        } else {
            real_amount.setChecked(false);
        }

        real_amount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sEdit = shPref.edit();
                    sEdit.putBoolean("real_amount", true);
                    sEdit.apply();
                    Toast.makeText(RegistrationActivity.this, "بله", Toast.LENGTH_SHORT).show();

                } else {

                    sEdit = shPref.edit();
                    sEdit.putBoolean("real_amount", false);
                    sEdit.apply();
                    Toast.makeText(RegistrationActivity.this, "خیر", Toast.LENGTH_SHORT).show();


                }
            }
        });


        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registration();

                SharedPreferences.Editor sEdit = shPref.edit();
                sEdit.putString("grid", action.arabicToenglish(reggrid.getText().toString()));
                sEdit.putString("delay", action.arabicToenglish(regdelay.getText().toString()));
                sEdit.putString("itemamount", action.arabicToenglish(regitemamount.getText().toString()));
                sEdit.apply();
                finish();
            }
        });


    }


    public void Registration() {
        UserInfo auser = new UserInfo();
        auser.setBrokerCode(action.arabicToenglish(regborker.getText().toString()));
        dbh.SavePersonalInfo(auser);
    }


}
