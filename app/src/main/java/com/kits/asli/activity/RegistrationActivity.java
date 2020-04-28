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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegistrationActivity extends AppCompatActivity {

    private DatabaseHelper dbh = new DatabaseHelper(this);
    private SharedPreferences shPref;
    private SharedPreferences.Editor sEdit;
    private Action action;
    //    private EditText regemail, regname, regmobile, regmelicode, regaddress, regphone, regpostalcode, reghdb;
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
//        regemail = findViewById(R.id.Registr_email);
//        regname = findViewById(R.id.Registr_name);
//        regmobile = findViewById(R.id.Registr_mobile);
//        regmelicode = findViewById(R.id.Registr_melicode);
//        regaddress = findViewById(R.id.Registr_address);
//        regphone = findViewById(R.id.Registr_phone);
//        regpostalcode = findViewById(R.id.Registr_postalcode);
//        reghdb = findViewById(R.id.Registr_bhd);
        reggrid = findViewById(R.id.Registr_grid);
        regdelay = findViewById(R.id.Registr_delay);
        regitemamount = findViewById(R.id.Registr_itemamount);


        SwitchMaterial regselloff = findViewById(R.id.Registr_selloff);
        SwitchMaterial real_amount = findViewById(R.id.Registr_real_amount);


        UserInfo auser = dbh.LoadPersonalInfo();

        regborker.setText(Farsi_number.PerisanNumber(auser.getBrokerCode()));
//        regemail.setText(Farsi_number.PerisanNumber(auser.getEmail()));
//        regname.setText(Farsi_number.PerisanNumber(auser.getNameFamily()));
//        regaddress.setText(Farsi_number.PerisanNumber(auser.getAddress()));
//        regmobile.setText(Farsi_number.PerisanNumber(auser.getMobile()));
//        regmelicode.setText(Farsi_number.PerisanNumber(auser.getMelliCode()));
//        regphone.setText(Farsi_number.PerisanNumber(auser.getPhone()));
//        regpostalcode.setText(Farsi_number.PerisanNumber(auser.getPostalCode()));
//        reghdb.setText(Farsi_number.PerisanNumber(auser.getBirthDate()));
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
//        auser.setEmail(action.arabicToenglish(regemail.getText().toString()));
//        auser.setNameFamily(action.arabicToenglish(regname.getText().toString()));
//        auser.setAddress(action.arabicToenglish(regaddress.getText().toString()));
//        auser.setPhone(action.arabicToenglish(regphone.getText().toString()));
//        auser.setMobile(action.arabicToenglish(regmobile.getText().toString()));
//        auser.setBirthDate(action.arabicToenglish(reghdb.getText().toString()));
//        auser.setMelliCode(action.arabicToenglish(regmelicode.getText().toString()));
//        auser.setPostalCode(action.arabicToenglish(regpostalcode.getText().toString()));
        auser.setBrokerCode(action.arabicToenglish(regborker.getText().toString()));
        dbh.SavePersonalInfo(auser);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
