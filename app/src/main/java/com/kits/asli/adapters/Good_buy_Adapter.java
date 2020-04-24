package com.kits.asli.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.activity.BuyActivity;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.Good;
import com.kits.asli.webService.APIClient;
import com.kits.asli.webService.APIInterface;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Good_buy_Adapter extends RecyclerView.Adapter<Good_buy_Adapter.GoodViewHolder> {
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private APIInterface apiInterface = APIClient.getCleint().create(APIInterface.class);
    private byte[] imageByteArray;
    private Image_info image_info;
    private Context mContext;
    private ArrayList<Good> goods;
    private String SERVER_IP_ADDRESS;
    private long sum = 0;
    private DatabaseHelper dbh;

    public Good_buy_Adapter(ArrayList<Good> goods, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.dbh = new DatabaseHelper(mContext);
        SERVER_IP_ADDRESS = mContext.getString(R.string.SERVERIP);
    }

    @NonNull
    @Override
    public GoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy, parent, false);
        return new GoodViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodViewHolder holder, int position) {
        final Good goodView = goods.get(position);


        Integer maxsellprice = goodView.getMaxSellPrice();
        Integer sellprice = goodView.getPrice();
        final Integer fac_amount = goodView.getAmount();
        final Integer unit_value = goodView.getDefaultUnitValue();


        long maxprice = (long) maxsellprice * fac_amount * unit_value;
        final long price = (long) sellprice * fac_amount * unit_value;
        sum = sum + price;
        Integer ws = Integer.valueOf(goodView.getShortage());


        holder.goodnameTextView.setText(Farsi_number.PerisanNumber(goodView.getGoodName()));
        holder.amount.setText(Farsi_number.PerisanNumber(goodView.getAmount().toString()));


        holder.maxsellpriceTextView.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + goodView.getMaxSellPrice()))));
        holder.priceTextView.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + goodView.getPrice()))));
        holder.total.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + price))));
        holder.maxtotal.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + maxprice))));
        holder.offer.setText(Farsi_number.PerisanNumber((100 - ((sellprice * 100) / maxsellprice)) + " درصد تخفیف "));
        //Picasso.with(mContext).load("http://"+SERVER_IP_ADDRESS+"/login/img/"+goodView.getImageName()).into(holder.img);


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


        if (ws == 1) {
            holder.good_buy_shortage_f1.getLayoutParams().height = 30;
        } else {
            holder.good_buy_shortage_f1.getLayoutParams().height = 1;
        }

        if (ws == 2) {
            holder.good_buy_shortage_f2.getLayoutParams().height = 30;
        } else {
            holder.good_buy_shortage_f2.getLayoutParams().height = 1;
        }


        holder.btndlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا کالا از لیست حذف گردد؟")
                        .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences shPref;
                                shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                                final String prefactor_code = "prefactor_code";
                                int pfcode = Integer.parseInt(shPref.getString(prefactor_code, null));

                                dbh.DeletePreFactorRow(pfcode, goodView.getRowCode());
                                Toast.makeText(mContext, "از سبد خرید حذف گردید", Toast.LENGTH_SHORT).show();
                                Intent bag = new Intent(mContext, BuyActivity.class);
                                bag.putExtra("PreFac", pfcode);
                                bag.putExtra("showflag", 2);
                                ((Activity) mContext).finish();
                                ((Activity) mContext).overridePendingTransition(0, 0);
                                mContext.startActivity(bag);
                                ((Activity) mContext).overridePendingTransition(0, 0);
                            }
                        })
                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();


            }
        });


        holder.amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("تعداد کالا اصلاح گردد؟")
                        .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences shPref;
                                shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                                final String prefactor_code = "prefactor_code";
                                final int pfcode = Integer.parseInt(shPref.getString(prefactor_code, null));
                                int pri = dbh.getCustomerGoodSellPrice(pfcode, goodView.getGoodCode());
                                if (pri == 0) {
                                    pri = goodView.getMaxSellPrice();
                                }

                                Good gd = dbh.getGoodByCode(goodView.getGoodCode(), pfcode);


                                // buydialog(goodView.getGoodCode(),goodView.getMaxSellPrice(),goodView.getPrice(),gd.getFactorAmount(),goodView.getAmount(),goodView.getRowCode());

                                Action ac = new Action(mContext);
                                ac.buydialog_goodbuy(goodView.getGoodCode(), goodView.getMaxSellPrice(), goodView.getPrice(), gd.getFactorAmount(), goodView.getAmount(), goodView.getRowCode());
                            }
                        })
                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();

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
        private TextView priceTextView;
        private TextView total;
        private TextView maxtotal;
        private TextView amount;
        private TextView good_buy_shortage_f1;
        private TextView good_buy_shortage_f2;
        private TextView offer;
        private Button btndlt;

        private ImageView img;
        MaterialCardView rltv;

        GoodViewHolder(View itemView) {
            super(itemView);
            goodnameTextView = itemView.findViewById(R.id.good_buy_name);
            maxsellpriceTextView = itemView.findViewById(R.id.good_buy_maxprice);
            priceTextView = itemView.findViewById(R.id.good_buy_price);
            amount = itemView.findViewById(R.id.good_buy_amount);
            good_buy_shortage_f1 = itemView.findViewById(R.id.good_buy_shortage_false1);
            good_buy_shortage_f2 = itemView.findViewById(R.id.good_buy_shortage_false2);
            total = itemView.findViewById(R.id.good_buy_total);
            maxtotal = itemView.findViewById(R.id.good_buy_maxtotal);
            img = itemView.findViewById(R.id.good_buy_img);
            btndlt = itemView.findViewById(R.id.good_buy_btndlt);
            offer = itemView.findViewById(R.id.good_buy_offer);


            rltv = itemView.findViewById(R.id.good_buy);
        }
    }


}
