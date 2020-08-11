package com.kits.asli.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.kits.asli.R;
import com.kits.asli.adapters.Action;
import com.kits.asli.adapters.Customer_Adapter;
import com.kits.asli.model.Customer;
import com.kits.asli.model.DatabaseHelper;

import java.util.ArrayList;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class CustomerActivity extends AppCompatActivity {

    private Integer factor_target = 0;
    private String edit = "0";
    private Action action;
    private DatabaseHelper dbh = new DatabaseHelper(CustomerActivity.this);
    private RecyclerView rc_customer;
    ArrayList<Customer> customers = new ArrayList<Customer>();
    Customer_Adapter adapter;
    GridLayoutManager gridLayoutManager;

    String srch = "";
    Integer id = 0;

    EditText edtsearch;
    Button Customer_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

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

//*****************************************************************************************


    public void init() {

        action = new Action(CustomerActivity.this);
        Toolbar toolbar = findViewById(R.id.CustomerActivity_toolbar);
        setSupportActionBar(toolbar);
        rc_customer = findViewById(R.id.Customer_R1);
        edtsearch = findViewById(R.id.Customer_edtsearch);
        Customer_new = findViewById(R.id.Customer_new);

        if (id == 0) {
            Customer_search();
        }

        if (id == 1) {
            Customer_new();
        }

    }

    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        edit = data.getString("edit");
        factor_target = data.getInt("factor_code");

    }

    public void Customer_search() {
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                srch = action.arabicToenglish(editable.toString());
                allCustomer();
            }
        });
        allCustomer();
    }

    public void Customer_new() {


    }

    public void allCustomer() {
        customers = dbh.AllCustomer(srch);
        adapter = new Customer_Adapter(customers, CustomerActivity.this, edit, factor_target);
        gridLayoutManager = new GridLayoutManager(CustomerActivity.this, 1);//grid
        rc_customer.setLayoutManager(gridLayoutManager);
        rc_customer.setAdapter(adapter);
        rc_customer.setItemAnimator(new DefaultItemAnimator());

        Customer_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


}
