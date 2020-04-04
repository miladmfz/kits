package com.kits.asli.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kits.asli.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Search_dateActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_date);
        init();
    }


    //****************************************

    public void init() {
        Button btn_1 = findViewById(R.id.search_date_1);
        Button btn_3 = findViewById(R.id.search_date_3);
        Button btn_7 = findViewById(R.id.search_date_7);
        Button btn_30 = findViewById(R.id.search_date_30);
        Button btn_60 = findViewById(R.id.search_date_60);
        Button btn_90 = findViewById(R.id.search_date_90);


        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Search_dateActivity.this, Search_date_detailActivity.class);
                intent.putExtra("date", 1);
                startActivity(intent);
            }
        });

        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(Search_dateActivity.this, Search_date_detailActivity.class);
                intent.putExtra("date", 3);
                startActivity(intent);

            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Search_dateActivity.this, Search_date_detailActivity.class);
                intent.putExtra("date", 7);
                startActivity(intent);
            }
        });
        btn_30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Search_dateActivity.this, Search_date_detailActivity.class);
                intent.putExtra("date", 30);
                startActivity(intent);
            }
        });
        btn_60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Search_dateActivity.this, Search_date_detailActivity.class);
                intent.putExtra("date", 60);
                startActivity(intent);
            }
        });
        btn_90.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Search_dateActivity.this, Search_date_detailActivity.class);
                intent.putExtra("date", 90);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
