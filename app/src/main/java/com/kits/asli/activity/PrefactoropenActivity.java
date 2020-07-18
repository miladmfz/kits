package com.kits.asli.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.adapters.Prefactor_Header_Box_adapter;
import com.kits.asli.adapters.Prefactor_Header_adapter;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.PreFactor;

import java.util.ArrayList;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PrefactoropenActivity extends AppCompatActivity {


    private DatabaseHelper dbh = new DatabaseHelper(this);
    private Integer fac;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefactoropen);


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

    //*********************************************

    public void init() {


        Button addfactor = findViewById(R.id.PrefactoropenActivity_btn);
        Button refresh = findViewById(R.id.PrefactoropenActivity_refresh);
        Button dltempty = findViewById(R.id.PrefactoropenActivity_deleteempty);
        TextView tv = findViewById(R.id.PrefactoropenActivity_amount);
        RecyclerView re = findViewById(R.id.PrefactoropenActivity_recyclerView);


        ArrayList<PreFactor> preFactors = new ArrayList<>();
        if (fac != 0)//ba dokme
        {
            preFactors = dbh.getAllPrefactorHeaderopen();
            Prefactor_Header_adapter adapter = new Prefactor_Header_adapter(preFactors, PrefactoropenActivity.this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(PrefactoropenActivity.this, 1);//grid
            re.setLayoutManager(gridLayoutManager);
            re.setAdapter(adapter);
            re.setItemAnimator(new DefaultItemAnimator());
            tv.setText((Farsi_number.PerisanNumber("" + preFactors.size())));

        } else {//bedone dokme
            preFactors = dbh.getOpenPrefactorHeader();
            Prefactor_Header_Box_adapter adapter = new Prefactor_Header_Box_adapter(preFactors, PrefactoropenActivity.this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(PrefactoropenActivity.this, 1);//grid
            re.setLayoutManager(gridLayoutManager);
            re.setAdapter(adapter);
            re.setItemAnimator(new DefaultItemAnimator());
            tv.setText((Farsi_number.PerisanNumber("" + preFactors.size())));

        }
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                startActivity(getIntent());

            }
        });

        dltempty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbh.DeleteEmptyPreFactor();
                finish();
                startActivity(getIntent());

            }
        });


        addfactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(PrefactoropenActivity.this, CustomerActivity.class);
                intent.putExtra("edit", "0");
                intent.putExtra("factor_code", 0);
                startActivity(intent);
            }
        });


    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        fac = data.getInt("fac");
    }


}
