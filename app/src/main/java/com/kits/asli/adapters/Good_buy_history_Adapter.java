package com.kits.asli.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
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

public class Good_buy_history_Adapter extends RecyclerView.Adapter<Good_buy_history_Adapter.GoodViewHolder> {
    private Context mContext;
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private ArrayList<Good> goods;
    private String SERVER_IP_ADDRESS;
    private long sum = 0;
    private APIInterface apiInterface = APIClient.getCleint().create(APIInterface.class);
    private byte[] imageByteArray;
    private Image_info image_info;

    public Good_buy_history_Adapter(ArrayList<Good> goods, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        SERVER_IP_ADDRESS = mContext.getString(R.string.SERVERIP);
    }

    @NonNull
    @Override
    public GoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy_history, parent, false);
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
        long price = (long) sellprice * fac_amount * unit_value;
        sum = sum + price;


        holder.goodnameTextView.setText(Farsi_number.PerisanNumber(goodView.getGoodName()));
        holder.maxsellpriceTextView.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + goodView.getMaxSellPrice()))));
        holder.priceTextView.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + goodView.getPrice()))));
        holder.amount.setText(Farsi_number.PerisanNumber(goodView.getAmount().toString()));
        holder.code.setText(Farsi_number.PerisanNumber(goodView.getGoodCode().toString()));
        holder.maxtotal.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + maxprice))));
        holder.total.setText(Farsi_number.PerisanNumber(decimalFormat.format(Integer.valueOf("" + price))));
        //Picasso.with(mContext).load("http://"+SERVER_IP_ADDRESS+"/login/img/"+goodView.getImageName()).resize(100,100).centerInside().into(holder.img);


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


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    class GoodViewHolder extends RecyclerView.ViewHolder {
        private TextView goodnameTextView;
        private TextView maxsellpriceTextView;
        private TextView maxtotal;
        private TextView priceTextView;
        private TextView total;
        private TextView amount;
        private TextView code;

        private ImageView img;
        MaterialCardView rltv;

        GoodViewHolder(View itemView) {
            super(itemView);
            goodnameTextView = itemView.findViewById(R.id.good_buy_history_name);
            maxsellpriceTextView = itemView.findViewById(R.id.good_buy_history_maxprice);
            maxtotal = itemView.findViewById(R.id.good_buy_history_maxtotal);
            priceTextView = itemView.findViewById(R.id.good_buy_history_price);
            total = itemView.findViewById(R.id.good_buy_history_total);
            amount = itemView.findViewById(R.id.good_buy_history_amount);
            code = itemView.findViewById(R.id.good_buy_history_code);
            img = itemView.findViewById(R.id.good_buy_history_img);
            rltv = itemView.findViewById(R.id.good_buy_history);
        }
    }


}
