package com.kits.asli.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.adapters.Action;
import com.kits.asli.adapters.Customer_Adapter;
import com.kits.asli.adapters.Replication;
import com.kits.asli.model.Customer;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.GoodResponse;
import com.kits.asli.model.UserInfo;
import com.kits.asli.webService.APIClient;
import com.kits.asli.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CustomerActivity extends AppCompatActivity {
    APIInterface apiInterface;

    private Integer factor_target = 0;
    private String edit = "0";
    private Action action;
    private DatabaseHelper dbh = new DatabaseHelper(CustomerActivity.this);
    private RecyclerView rc_customer;
    ArrayList<Customer> customers = new ArrayList<Customer>();
    ArrayList<Customer> citys = new ArrayList<Customer>();
    Customer_Adapter adapter;
    GridLayoutManager gridLayoutManager;
    LinearLayoutCompat li_search, li_new;
    Replication replication;
    String srch = "";
    Integer id = 0;
    Spinner spinner;
    Intent intent;
    EditText edtsearch;
    Button Customer_new, kodemeli_check, customer_reg_btn;
    ArrayList<String> city_array = new ArrayList<>();
    TextView kodemeli_statu;
    EditText ekodemelli, ecitycode, ename, efamily, eaddress, ephone, emobile, eemail, epostcode, ezipcode;
    String kodemelli, citycode = "", name, family, address, phone, mobile, email, postcode, zipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);


        intent();


        init();


    }

//*****************************************************************************************


    public void init() {

        action = new Action(CustomerActivity.this);
        replication = new Replication(CustomerActivity.this);
        apiInterface = APIClient.getCleint().create(APIInterface.class);

        Toolbar toolbar = findViewById(R.id.CustomerActivity_toolbar);
        setSupportActionBar(toolbar);
        rc_customer = findViewById(R.id.Customer_R1);
        edtsearch = findViewById(R.id.Customer_edtsearch);
        Customer_new = findViewById(R.id.Customer_new_btn);
        li_search = findViewById(R.id.customer_search_line);
        li_new = findViewById(R.id.customer_new_line);

        ekodemelli = findViewById(R.id.customer_new_kodemelli);
        kodemeli_check = findViewById(R.id.customer_new_kodemelli_check);
        kodemeli_statu = findViewById(R.id.customer_new_kodemelli_status);

        spinner = findViewById(R.id.customer_city_spinner);
        ename = findViewById(R.id.customer_new_name);
        efamily = findViewById(R.id.customer_new_family);
        eaddress = findViewById(R.id.customer_new_address);
        ephone = findViewById(R.id.customer_new_phone);
        emobile = findViewById(R.id.customer_new_mobile);
        eemail = findViewById(R.id.customer_new_email);
        epostcode = findViewById(R.id.customer_new_postcode);
        ezipcode = findViewById(R.id.customer_new_zipcode);
        customer_reg_btn = findViewById(R.id.customer_new_register_btn);


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
        id = data.getInt("id");

    }

    public void Customer_search() {
        li_search.setVisibility(View.VISIBLE);
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


        Customer_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CustomerActivity.this, CustomerActivity.class);
                intent.putExtra("edit", "0");
                intent.putExtra("factor_code", 0);
                intent.putExtra("id", 1);
                startActivity(intent);
            }
        });


        allCustomer();

    }

    public void Customer_new() {
        li_new.setVisibility(View.VISIBLE);
        replication.replicate_customer();


        citys = dbh.city();
        for (Customer city : citys) {
            city_array.add(city.getCityName());
        }
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(CustomerActivity.this,
                android.R.layout.simple_spinner_item, city_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setSelection(0);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citycode = citys.get(position).getCityCode();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        kodemeli_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dbh.Customer_check(ekodemelli.getText().toString()) > 0) {

                    kodemeli_statu.setText("کد ملی ثبت شده است");
                    kodemeli_statu.setTextColor(getResources().getColor(R.color.red_300));
                } else {

                    kodemeli_statu.setText("کد ملی ثبت نشده است");
                    kodemeli_statu.setTextColor(getResources().getColor(R.color.green_900));

                }
            }
        });

        customer_reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dbh.Customer_check(ekodemelli.getText().toString()) > 0) {

                    kodemeli_statu.setText("کد ملی ثبت شده است");
                    kodemeli_statu.setTextColor(getResources().getColor(R.color.red_300));
                } else {

                    UserInfo auser = dbh.LoadPersonalInfo();
                    if (Integer.parseInt(auser.getBrokerCode()) > 0) {
                        kodemelli = action.arabicToenglish(ekodemelli.getText().toString());
                        name = action.arabicToenglish(ename.getText().toString());
                        family = action.arabicToenglish(efamily.getText().toString());
                        address = action.arabicToenglish(eaddress.getText().toString());
                        phone = action.arabicToenglish(ephone.getText().toString());
                        mobile = action.arabicToenglish(emobile.getText().toString());
                        email = action.arabicToenglish(eemail.getText().toString());
                        postcode = action.arabicToenglish(epostcode.getText().toString());
                        zipcode = action.arabicToenglish(ezipcode.getText().toString());

                        Call<GoodResponse> call = apiInterface.customer_insert("CustomerInsert", auser.getBrokerCode(), citycode, kodemelli, name, family, address, phone, mobile, email, postcode, zipcode);
                        call.enqueue(new Callback<GoodResponse>() {
                            @Override
                            public void onResponse(Call<GoodResponse> call, Response<GoodResponse> response) {
                                if (response.isSuccessful()) {
                                    ArrayList<Customer> Customes = response.body().getCustomers();
                                    Toast.makeText(CustomerActivity.this, Customes.get(0).getErrDesc(), Toast.LENGTH_SHORT).show();
                                    intent = new Intent(CustomerActivity.this, CustomerActivity.class);
                                    intent.putExtra("edit", "0");
                                    intent.putExtra("factor_code", 0);
                                    intent.putExtra("id", 0);
                                    startActivity(intent);
                                }


                            }

                            @Override
                            public void onFailure(Call<GoodResponse> call, Throwable t) {

                            }
                        });

                    } else {
                        intent = new Intent(CustomerActivity.this, ConfigActivity.class);
                        Toast toast = Toast.makeText(CustomerActivity.this, "کد بازاریاب را وارد کنید", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 10, 10);
                        toast.show();
                        startActivity(intent);

                    }
                }

            }
        });


    }


    public void allCustomer() {
        customers = dbh.AllCustomer(srch);
        adapter = new Customer_Adapter(customers, CustomerActivity.this, edit, factor_target);
        gridLayoutManager = new GridLayoutManager(CustomerActivity.this, 1);//grid
        rc_customer.setLayoutManager(gridLayoutManager);
        rc_customer.setAdapter(adapter);
        rc_customer.setItemAnimator(new DefaultItemAnimator());
    }


}
