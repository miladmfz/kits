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


    }

//************************************************************************


}
