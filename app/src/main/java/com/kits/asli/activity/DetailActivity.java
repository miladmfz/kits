package com.kits.asli.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.kits.asli.R;
import com.kits.asli.adapters.Action;
import com.kits.asli.adapters.Image_info;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.Good;
import com.kits.asli.webService.APIClient;
import com.kits.asli.webService.APIInterface;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailActivity extends AppCompatActivity {

    private Integer id, code;
    private String ImageName;
    private APIInterface apiInterface = APIClient.getCleint().create(APIInterface.class);
    private Image_info image_info;
    private String SERVER_IP_ADDRESS;
    private Intent intent;
    private SharedPreferences shPref;
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private DatabaseHelper dbh = new DatabaseHelper(DetailActivity.this);
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_t);

        intent();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 100);


    }

    //*********************************************************************


    public void init() {


        shPref = getSharedPreferences("act", Context.MODE_PRIVATE);


        final Good gd = dbh.getGoodByCode(id, Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))));

        SERVER_IP_ADDRESS = getString(R.string.SERVERIP);
        Toolbar toolbar = findViewById(R.id.DetailActivity_toolbar);
        TextView customer = findViewById(R.id.DetailActivity_customer);
        TextView sumfac = findViewById(R.id.DetailActivity_sum_factor);
        TextView customer_code = findViewById(R.id.DetailActivity_customer_code);
        TextView gcode = findViewById(R.id.DetailActivity_code);
        TextView gname = findViewById(R.id.DetailActivity_tv1);
        TextView amount = findViewById(R.id.DetailActivity_amount);
        TextView Reserveamount = findViewById(R.id.DetailActivity_Reserveamount);
        LinearLayoutCompat Reserveamount_line = findViewById(R.id.DetailActivity_Reserveamount_line);
        TextView ex1 = findViewById(R.id.DetailActivity_ex1);
        TextView isbn = findViewById(R.id.DetailActivity_isbn);
        TextView date2 = findViewById(R.id.DetailActivity_date2);
        TextView float1 = findViewById(R.id.DetailActivity_float1);
        TextView nvar1 = findViewById(R.id.DetailActivity_nvarchar1);
        TextView nvar2 = findViewById(R.id.DetailActivity_nvarchar2);
        TextView float5 = findViewById(R.id.DetailActivity_float5);
        TextView nvar13 = findViewById(R.id.DetailActivity_nvarchar13);
        TextView nvar20 = findViewById(R.id.DetailActivity_nvarchar20);
        TextView price = findViewById(R.id.DetailActivity_tv13);
        TextView grp = findViewById(R.id.DetailActivity_grp);
        img = findViewById(R.id.DetailActivity_img);
        Button btnbuy = findViewById(R.id.DetailActivity_btnbuy);

        code = gd.getGoodCode();
        setSupportActionBar(toolbar);

        if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) == 0) {
            customer.setText("فاکتوری انتخاب نشده");
            sumfac.setText("0");

        } else {
            customer.setText(dbh.getFactorCustomer(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)))));
            sumfac.setText(Farsi_number.PerisanNumber(decimalFormat.format(dbh.getFactorSum(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null)))))));
            customer_code.setText(Farsi_number.PerisanNumber(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) + ""));
        }
        gcode.setText(Farsi_number.PerisanNumber(gd.getGoodCode() + ""));
        gname.setText(Farsi_number.PerisanNumber(gd.getGoodName() + ""));
        isbn.setText(Farsi_number.PerisanNumber(gd.getIsbn() + ""));
        date2.setText(Farsi_number.PerisanNumber(gd.getDate2() + ""));
        float1.setText(Farsi_number.PerisanNumber(gd.getFloat1() + ""));
        nvar1.setText(Farsi_number.PerisanNumber(gd.getNvarchar1() + ""));
        nvar2.setText(Farsi_number.PerisanNumber(gd.getNvarchar2() + ""));
        float5.setText(Farsi_number.PerisanNumber(gd.getFloat5() + ""));
        nvar13.setText(Farsi_number.PerisanNumber(gd.getNvarchar13() + ""));
        nvar20.setText(Farsi_number.PerisanNumber(gd.getNvarchar20() + ""));
        ex1.setText(Farsi_number.PerisanNumber("" + gd.getGoodExplain1()));
        grp.setText(Farsi_number.PerisanNumber("" + dbh.getgoodgroups(id)));
        price.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + gd.getMaxSellPrice()))));
        ImageName = gd.getImageName();
        if (getString(R.string.app_name).equals("آسیم")) {

            date2.setText(Farsi_number.PerisanNumber(gd.getDate1() + ""));
        }

        if (shPref.getBoolean("real_amount", true)) {
            amount.setText(Farsi_number.PerisanNumber("" + (gd.getAmount() - gd.getReservedAmount())));

        } else {
            Reserveamount_line.setVisibility(View.VISIBLE);
            amount.setText(Farsi_number.PerisanNumber("" + gd.getAmount()));
            Reserveamount.setText(Farsi_number.PerisanNumber("" + gd.getReservedAmount()));
        }


        //Picasso.with(DetailActivity.this).load("http://"+SERVER_IP_ADDRESS+"/login/img/"+gd.getImageName()).centerInside().resize(700, 1000).into(img);


        img.setBackground(ContextCompat.getDrawable(DetailActivity.this, R.drawable.no_photo));


        image_info = new Image_info(DetailActivity.this);

        if (image_info.Imgae_exist(gd.getGoodCode().toString())) {


            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" + getString(R.string.app_img_name) + "/" + gd.getGoodCode() + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            img.setImageBitmap(myBitmap);

        } else {

            Call<String> call2 = apiInterface.GetImage("getImage", gd.getGoodCode().toString(), 0);
            call2.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().equals("no_photo")) {
                            byte[] imageByteArray1;
                            imageByteArray1 = Base64.decode(getString(R.string.no_photo), Base64.DEFAULT);
                            img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));

                        } else {
                            byte[] imageByteArray1;
                            imageByteArray1 = Base64.decode(response.body(), Base64.DEFAULT);
                            img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));
                            image_info.SaveImage(BitmapFactory.decodeByteArray(Base64.decode(response.body(), Base64.DEFAULT), 0, Base64.decode(response.body(), Base64.DEFAULT).length), gd.getGoodCode());
                        }
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("asli_onFailure", "" + t.toString());

                }
            });

        }


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageview(gd.getGoodCode());
            }
        });
        btnbuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Good gdd = dbh.getGoodByCode(id, Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))));

                if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) != 0) {
                    int pri = dbh.getCustomerGoodSellPrice(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))), code);
                    if (pri == 0) {
                        pri = gd.getMaxSellPrice();
                    }


                    Action ac = new Action(DetailActivity.this);
                    ac.buydialog(code, gd.getMaxSellPrice(), pri, gdd.getFactorAmount(), gd.getUnitName());

                } else {
                    intent = new Intent(DetailActivity.this, PrefactoropenActivity.class);
                    intent.putExtra("fac", 0);
                    startActivity(intent);
                }
            }
        });
    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        id = data.getInt("id");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override//option menu
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) != 0) {
                intent = new Intent(DetailActivity.this, BuyActivity.class);
                intent.putExtra("PreFac", Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))));
                intent.putExtra("showflag", 2);
                startActivity(intent);
            } else {
                if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) != 0) {
                    intent = new Intent(DetailActivity.this, SearchActivity.class);
                    intent.putExtra("scan", " ");
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "سبد خرید خالی می باشد", Toast.LENGTH_SHORT).show();
                    intent = new Intent(DetailActivity.this, PrefactoropenActivity.class);
                    startActivity(intent);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void imageview(Integer code) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//title laye nadashte bashim
        dialog.setContentView(R.layout.image_zoom);
        final ImageView imageView = dialog.findViewById(R.id.image_zoom_view);

        //Picasso.with(DetailActivity.this).load("http://"+SERVER_IP_ADDRESS+"/login/img/"+ImageName).centerInside().resize(2000, 2000).into(imageView);

        if (image_info.Imgae_exist(code.toString())) {

            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" + getString(R.string.app_img_name) + "/" + code + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imagefile.getAbsolutePath()), BitmapFactory.decodeFile(imagefile.getAbsolutePath()).getWidth() * 4, BitmapFactory.decodeFile(imagefile.getAbsolutePath()).getHeight() * 4, false));


        } else {

            imageView.setBackground(ContextCompat.getDrawable(DetailActivity.this, R.drawable.no_photo));
        }

        dialog.show();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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











