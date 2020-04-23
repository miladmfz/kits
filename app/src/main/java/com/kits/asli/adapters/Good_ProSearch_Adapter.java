package com.kits.asli.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.asli.R;
import com.kits.asli.activity.DetailActivity;
import com.kits.asli.activity.PrefactoropenActivity;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.Good;
import com.kits.asli.webService.APIClient;
import com.kits.asli.webService.APIInterface;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Good_ProSearch_Adapter extends RecyclerView.Adapter<Good_ProSearch_Adapter.GoodViewHolder> {

    private Context mContext;
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private ArrayList<Good> goods;
    private Intent intent;
    private String SERVER_IP_ADDRESS;
    private String UnitName;
    private SharedPreferences shPref;
    private APIInterface apiInterface = APIClient.getCleint().create(APIInterface.class);
    private Image_info image_info;
    private byte[] imageByteArray;


    public Good_ProSearch_Adapter(ArrayList<Good> goods, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        SERVER_IP_ADDRESS = mContext.getString(R.string.SERVERIP);
    }

    @NonNull
    @Override
    public GoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_prosearch, parent, false);
        return new GoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodViewHolder holder, final int position) {
        final Good goodView = goods.get(position);
        String img = goodView.getImageName();
        UnitName = goodView.getUnitName();
        holder.good_prosearch_amount.setText(Farsi_number.PerisanNumber("" + goodView.getAmount()));
        holder.goodnameTextView.setText(Farsi_number.PerisanNumber(goodView.getGoodName()));
        holder.maxsellpriceTextView.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + goodView.getMaxSellPrice()))));
        //Picasso.with(mContext).load("http://"+SERVER_IP_ADDRESS+"/login/img/"+img).centerInside().resize(1000, 1000).into(holder.img);


        image_info = new Image_info(mContext);

        if (image_info.Imgae_exist(goodView.getGoodCode().toString())) {

            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" + mContext.getString(R.string.app_img_name) + "/" + goodView.getGoodCode() + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            holder.img.setImageBitmap(myBitmap);


        } else {
            Call<String> call2 = apiInterface.GetImage("getImage", goodView.getGoodCode().toString(), 0);
            call2.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call2, Response<String> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().equals("no_photo")) {
                            byte[] imageByteArray1;
                            imageByteArray1 = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
                            holder.img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));

                        } else {
                            byte[] imageByteArray1;
                            imageByteArray1 = Base64.decode(response.body(), Base64.DEFAULT);
                            holder.img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));
                            image_info.SaveImage(BitmapFactory.decodeByteArray(Base64.decode(response.body(), Base64.DEFAULT), 0, Base64.decode(response.body(), Base64.DEFAULT).length), goodView.getGoodCode());
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call2, Throwable t) {
                    Log.e("onFailure", "" + t.toString());

                }
            });

        }


        holder.rltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.e("", "1");
                Good goodView = goods.get(position);
                intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("id", goodView.getGoodCode());
                intent.putExtra("ws", goodView.getShortage());
                intent.putExtra("ws", goodView.getShortage());
                mContext.startActivity(intent);
            }

        });

//
//        holder.rltv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//                holder.ggg.setBackgroundColor(Color.GRAY);
//                Log.e("", "" + goodView.getGoodCode());
//                return true;
//            }
//
//
//        });


        holder.btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);

                if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) != 0) {

                    final DatabaseHelper dbh = new DatabaseHelper(mContext);
                    int pri = dbh.getCustomerGoodSellPrice(Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))), goodView.getGoodCode());
                    if (pri == 0) {
                        pri = goodView.getMaxSellPrice();
                    }
                    Good gd = dbh.getGoodByCode(goodView.getGoodCode(), Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))));
                    Action ac = new Action(mContext);
                    ac.buydialog(goodView.getGoodCode(), goodView.getMaxSellPrice(), pri, gd.getFactorAmount(), UnitName);

                } else {

                    intent = new Intent(mContext, PrefactoropenActivity.class);
                    intent.putExtra("fac", 0);
                    mContext.startActivity(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    class GoodViewHolder extends RecyclerView.ViewHolder {
        private TextView goodnameTextView;
        private TextView maxsellpriceTextView;
        private TextView good_prosearch_amount;
        private Button btnadd;
        private ImageView img;
        private LinearLayout ggg;
        CardView rltv;

        GoodViewHolder(View itemView) {
            super(itemView);
            goodnameTextView = itemView.findViewById(R.id.good_prosearch_name);
            maxsellpriceTextView = itemView.findViewById(R.id.good_prosearch_price);
            good_prosearch_amount = itemView.findViewById(R.id.good_prosearch_amount);
            img = itemView.findViewById(R.id.good_prosearch_img);
            rltv = itemView.findViewById(R.id.good_prosearch);
            btnadd = itemView.findViewById(R.id.good_prosearch_btn);
            ggg = itemView.findViewById(R.id.proserch_ggg);
        }
    }
}
