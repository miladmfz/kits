package com.kits.asli.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.kits.asli.R;
import com.kits.asli.model.DatabaseHelper;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailsettingActivity extends AppCompatActivity {

    private DatabaseHelper dbh = new DatabaseHelper(this);
    String aedt1, aedt2, aedt3, aedt4, aedt5, aedt6, aedt7, aedt8, aedt9, aedt10, aedt11;
    String aedt12, aedt13, aedt14, aedt15, aedt16, aedt17, aedt18, aedt19, aedt20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailsetting);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 100);


    }

//************************************************************************

    public void init() {

        final EditText edt1 = findViewById(R.id.dsetting_ed1);
        final EditText edt2 = findViewById(R.id.dsetting_ed2);
        final EditText edt3 = findViewById(R.id.dsetting_ed3);
        final EditText edt4 = findViewById(R.id.dsetting_ed4);
        final EditText edt5 = findViewById(R.id.dsetting_ed5);
        final EditText edt6 = findViewById(R.id.dsetting_ed6);
        final EditText edt7 = findViewById(R.id.dsetting_ed7);
        final EditText edt8 = findViewById(R.id.dsetting_ed8);
        final EditText edt9 = findViewById(R.id.dsetting_ed9);
        final EditText edt10 = findViewById(R.id.dsetting_ed10);
        final EditText edt11 = findViewById(R.id.dsetting_ed11);
        final EditText edt12 = findViewById(R.id.dsetting_ed12);
        final EditText edt13 = findViewById(R.id.dsetting_ed13);
        final EditText edt14 = findViewById(R.id.dsetting_ed14);
        final EditText edt15 = findViewById(R.id.dsetting_ed15);
        final EditText edt16 = findViewById(R.id.dsetting_ed16);
        final EditText edt17 = findViewById(R.id.dsetting_ed17);
        final EditText edt18 = findViewById(R.id.dsetting_ed18);
        final EditText edt19 = findViewById(R.id.dsetting_ed19);

        final Spinner spinner1 = findViewById(R.id.dsetting_spinner1);
        final Spinner spinner2 = findViewById(R.id.dsetting_spinner2);
        final Spinner spinner3 = findViewById(R.id.dsetting_spinner3);
        final Spinner spinner4 = findViewById(R.id.dsetting_spinner4);
        final Spinner spinner5 = findViewById(R.id.dsetting_spinner5);
        final Spinner spinner6 = findViewById(R.id.dsetting_spinner6);
        final Spinner spinner7 = findViewById(R.id.dsetting_spinner7);
        final Spinner spinner8 = findViewById(R.id.dsetting_spinner8);
        final Spinner spinner9 = findViewById(R.id.dsetting_spinner9);
        final Spinner spinner10 = findViewById(R.id.dsetting_spinner10);
        final Spinner spinner11 = findViewById(R.id.dsetting_spinner11);
        final Spinner spinner12 = findViewById(R.id.dsetting_spinner12);
        final Spinner spinner13 = findViewById(R.id.dsetting_spinner13);
        final Spinner spinner14 = findViewById(R.id.dsetting_spinner14);
        final Spinner spinner15 = findViewById(R.id.dsetting_spinner15);
        final Spinner spinner16 = findViewById(R.id.dsetting_spinner16);
        final Spinner spinner17 = findViewById(R.id.dsetting_spinner17);
        final Spinner spinner18 = findViewById(R.id.dsetting_spinner18);
        final Spinner spinner19 = findViewById(R.id.dsetting_spinner19);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                dbh.getAllSpinnerContent());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(dataAdapter);
        spinner2.setAdapter(dataAdapter);
        spinner3.setAdapter(dataAdapter);
        spinner4.setAdapter(dataAdapter);
        spinner5.setAdapter(dataAdapter);
        spinner6.setAdapter(dataAdapter);
        spinner7.setAdapter(dataAdapter);
        spinner8.setAdapter(dataAdapter);
        spinner9.setAdapter(dataAdapter);
        spinner10.setAdapter(dataAdapter);
        spinner11.setAdapter(dataAdapter);
        spinner12.setAdapter(dataAdapter);
        spinner13.setAdapter(dataAdapter);
        spinner14.setAdapter(dataAdapter);
        spinner15.setAdapter(dataAdapter);
        spinner16.setAdapter(dataAdapter);
        spinner17.setAdapter(dataAdapter);
        spinner18.setAdapter(dataAdapter);
        spinner19.setAdapter(dataAdapter);


        aedt1 = edt1.getText().toString();
        aedt2 = edt2.getText().toString();
        aedt3 = edt3.getText().toString();
        aedt4 = edt4.getText().toString();
        aedt5 = edt5.getText().toString();
        aedt6 = edt6.getText().toString();
        aedt7 = edt7.getText().toString();
        aedt8 = edt8.getText().toString();
        aedt9 = edt9.getText().toString();
        aedt10 = edt10.getText().toString();
        aedt11 = edt11.getText().toString();
        aedt12 = edt12.getText().toString();
        aedt13 = edt13.getText().toString();
        aedt14 = edt14.getText().toString();
        aedt15 = edt15.getText().toString();
        aedt16 = edt16.getText().toString();
        aedt17 = edt17.getText().toString();
        aedt18 = edt18.getText().toString();
        aedt19 = edt19.getText().toString();


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
