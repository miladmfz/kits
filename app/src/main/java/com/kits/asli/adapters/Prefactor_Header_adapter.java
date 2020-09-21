package com.kits.asli.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.activity.BuyActivity;
import com.kits.asli.activity.BuyhistoryActivity;
import com.kits.asli.activity.CustomerActivity;
import com.kits.asli.activity.NavActivity;
import com.kits.asli.activity.PrefactorActivity;
import com.kits.asli.activity.PrinterActivity;
import com.kits.asli.activity.SearchActivity;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.Good;
import com.kits.asli.model.PreFactor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class Prefactor_Header_adapter extends RecyclerView.Adapter<Prefactor_Header_adapter.facViewHolder> {

    private final Context mContext;
    private String SERVER_IP_ADDRESS;
    private int customer_change;
    private Intent intent;
    private SharedPreferences.Editor sEdit;
    private SharedPreferences shPref;
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<PreFactor> PreFactors;
    private DatabaseHelper dbh;
    private Action action;


    public Prefactor_Header_adapter(ArrayList<PreFactor> PreFactors, Context mContext) {
        this.mContext = mContext;
        this.PreFactors = PreFactors;
        SERVER_IP_ADDRESS = mContext.getString(R.string.SERVERIP);
        this.dbh = new DatabaseHelper(mContext);
        this.action = new Action(mContext);
    }

    @NonNull
    @Override
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prefactor_header, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, final int position) {

        final PreFactor facView = PreFactors.get(position);

        holder.fac_code.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getPreFactorCode())));
        holder.fac_date.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getPreFactorDate())));
        holder.fac_time.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getPreFactorTime())));
        holder.fac_kowsardate.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getPreFactorkowsarDate())));
        holder.fac_kowsarcode.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getPreFactorKowsarCode())));
        holder.fac_detail.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getPreFactorExplain())));
        holder.fac_customer.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getCustomer())));
        holder.fac_row.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getRowCount())));
        holder.fac_count.setText(Farsi_number.PerisanNumber(String.valueOf(facView.getSumAmount())));
        holder.fac_price.setText(Farsi_number.PerisanNumber(String.valueOf(decimalFormat.format(Integer.valueOf(String.valueOf(facView.getSumPrice()))))));


        if (facView.getPreFactorKowsarCode() > 0) {
            holder.fac_status.setVisibility(View.VISIBLE);
        } else {
            holder.fac_status.setVisibility(View.GONE);
        }

        holder.fac_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreFactor facView = PreFactors.get(position);
                shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                SharedPreferences.Editor sEdit = shPref.edit();
                sEdit.putString("prefactor_good", facView.getPreFactorCode().toString());
                sEdit.apply();
                intent = new Intent(mContext, BuyhistoryActivity.class);
                intent.putExtra("PreFac", facView.getPreFactorCode());// code morede nazar baraye safheye badi ersall mishavad
                intent.putExtra("amount", String.valueOf(facView.getSumAmount()));// code morede nazar baraye safheye badi ersall mishavad
                intent.putExtra("total", String.valueOf(facView.getSumPrice()));// code morede nazar baraye safheye badi ersall mishavad
                mContext.startActivity(intent);
            }
        });


        holder.fac_excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer kc = facView.getPreFactorKowsarCode();
                Integer kcf = facView.getPreFactorCode();
                String kd = facView.getPreFactorkowsarDate();
                String kdf = facView.getPreFactorDate();
                String cn = facView.getCustomer();
                if (kcf > 0) {
                    Integer I = 0;
                    Integer J = 0;
                    String filebody = facView.getCustomer() + "\n";
                    filebody = ",[کد اصلی]" +
                            ",[کد سیستمی]" +
                            ",[نام کتاب]" +
                            ",[تعداد]" +
                            ",[فی]" +
                            "[ناخالص]" +
                            "\n";
                    ArrayList<Good> goodsbd = new ArrayList<Good>();
                    kd = kd.replaceAll("/", "");
                    goodsbd = dbh.getAllGood_pfcode(facView.getPreFactorCode());
                    J = goodsbd.size();
                    Log.e("tr_PreSize : ", "" + J);
                    Good b;
                    for (I = 0; I < J; I++) {
                        b = goodsbd.get(I);
                        filebody = filebody
                                + b.getGoodMainCode() + ","
                                + b.getGoodCode() + ","
                                + b.getGoodName() + ","
                                + b.getFactorAmount() + ","
                                + b.getPrice() + ","
                                + b.getMaxSellPrice() + "\n";
                        Log.e("testanbar_PreFactorRows", "" + I + ":" + filebody);
                    }
                    Toast.makeText(mContext, "فایل ذخیره شد", Toast.LENGTH_SHORT).show();
                    File myFile;
                    String baseDir = Environment.getExternalStorageDirectory() + "/Kowsar/PreFactor_Excels";
                    String fileName = "PreFactor_" + kcf + "_" + cn + "_" + kdf + ".xlsx";
                    String filePath = baseDir + File.separator + fileName;
                    myFile = new File(filePath);
                    if (!myFile.exists()) {
                        if (!myFile.getParentFile().exists()) {
                            myFile.getParentFile().mkdirs();
                        }
                        if (!myFile.exists()) {
                            try {
                                myFile.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        FileOutputStream fOut = new FileOutputStream(myFile);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(filebody);
                        myOutWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent2 = new Intent(mContext, PrinterActivity.class);
                intent2.putExtra("PreFac", facView.getPreFactorCode());
                mContext.startActivity(intent2);
            }

        });


        holder.fac_dlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (facView.getPreFactorKowsarCode() != 0) {
                    Toast.makeText(mContext, "فاکتور بسته می باشد", Toast.LENGTH_SHORT).show();
                } else {
                    shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                    sEdit = shPref.edit();
                    sEdit.putString("prefactor_code", facView.getPreFactorCode().toString());
                    sEdit.apply();
                    ArrayList<Good> goods = dbh.getAllPreFactorRows(facView.getPreFactorCode());
                    if (goods.size() != 0) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("توجه")
                                .setMessage("فاکتور دارای کالا می باشد،کالاها حذف شود؟")
                                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(mContext, BuyActivity.class);
                                        intent.putExtra("PreFac", facView.getPreFactorCode());

                                        mContext.startActivity(intent);
                                    }
                                })
                                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .show();
                    } else {
                        dbh.DeletePreFactor(facView.getPreFactorCode());
                        Toast.makeText(mContext, "فاکتور حذف گردید", Toast.LENGTH_SHORT).show();
                        goods.size();
                        shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                        sEdit = shPref.edit();

                        if (Integer.parseInt(Objects.requireNonNull(shPref.getString("prefactor_code", null))) != facView.getPreFactorCode()) {
                            sEdit.putString("prefactor_code", "0");
                            sEdit.apply();

                            intent = new Intent(mContext, PrefactorActivity.class);
                            ((Activity) mContext).finish();
                            ((Activity) mContext).overridePendingTransition(0, 0);
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(0, 0);
                        } else {
                            sEdit.putString("prefactor_code", "0");
                            sEdit.apply();
                            intent = new Intent(mContext, PrefactorActivity.class);
                            ((Activity) mContext).finish();
                            ((Activity) mContext).overridePendingTransition(0, 0);
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(0, 0);
                        }


                    }


                }
            }
        });


        holder.fac_good_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (facView.getPreFactorKowsarCode() != 0) {
                    Toast.makeText(mContext, "فاکتور بسته می باشد", Toast.LENGTH_SHORT).show();
                } else {
                    PreFactor facView = PreFactors.get(position);
                    shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                    sEdit = shPref.edit();
                    sEdit.putString("prefactor_code", facView.getPreFactorCode().toString());
                    sEdit.apply();
                    Intent intent = new Intent(mContext, BuyActivity.class);
                    intent.putExtra("PreFac", facView.getPreFactorCode());

                    mContext.startActivity(intent);
                }
            }
        });


        holder.fac_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (facView.getPreFactorKowsarCode() != 0) {
                    Toast.makeText(mContext, "فاکتور بسته می باشد", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Good> goods = dbh.getAllPreFactorRows(facView.getPreFactorCode());
                    if (goods.size() != 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                .setTitle("توجه")
                                .setMessage("آیا فاکتور ارسال گردد؟")
                                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // sendfactor(facView.getPreFactorCode());
                                        action.sendfactor(facView.getPreFactorCode());
                                    }
                                })
                                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .show();
                    } else {
                        Toast.makeText(mContext, "فاکتور خالی می باشد", Toast.LENGTH_SHORT).show();
                        goods.size();
                    }
                }


            }
        });


        holder.fac_customer_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (facView.getPreFactorKowsarCode() != 0) {
                    Toast.makeText(mContext, "فاکتور بسته می باشد", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                            .setTitle("توجه")
                            .setMessage("آیا مایل به اصلاح مشتری می باشید؟")
                            .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    intent = new Intent(mContext, CustomerActivity.class);
                                    intent.putExtra("edit", "1");
                                    intent.putExtra("factor_code", facView.getPreFactorCode());

                                    ((Activity) mContext).finish();
                                    mContext.startActivity(intent);


                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                }
            }
        });


        holder.fac_explain_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (facView.getPreFactorKowsarCode() != 0) {
                    Toast.makeText(mContext, "فاکتور بسته می باشد", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                            .setTitle("توجه")
                            .setMessage("آیا مایل به اصلاح توضیحات می باشید؟")
                            .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    action.edit_explain(facView.getPreFactorCode());
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                }
            }
        });


        holder.fac_select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                PreFactor facView = PreFactors.get(position);
                shPref = mContext.getSharedPreferences("act", Context.MODE_PRIVATE);
                SharedPreferences.Editor sEdit = shPref.edit();
                if (facView.getPreFactorKowsarCode() != 0) {
                    Toast.makeText(mContext, "فاکتور بسته می باشد", Toast.LENGTH_SHORT).show();
                } else {
                    sEdit.putString("prefactor_code", facView.getPreFactorCode().toString());
                    sEdit.apply();
                    Toast.makeText(mContext, "فاکتور مورد نظر انتخاب شد", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, SearchActivity.class);
                    intent.putExtra("scan", " ");

                    ((Activity) mContext).overridePendingTransition(0, 0);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(0, 0);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return PreFactors.size();
    }

    class facViewHolder extends RecyclerView.ViewHolder {
        private TextView fac_code;
        private TextView fac_date;
        private TextView fac_time;
        private TextView fac_kowsardate;
        private TextView fac_kowsarcode;
        private TextView fac_detail;
        private TextView fac_row;
        private TextView fac_count;
        private TextView fac_price;
        private TextView fac_customer;
        private TextView fac_status;
        private Button fac_good;
        private Button fac_send;
        private Button fac_dlt;
        private Button fac_customer_edit;
        private Button fac_explain_edit;
        private Button fac_excel;
        private Button fac_select;
        private Button fac_good_edit;
        MaterialCardView fac_rltv;

        facViewHolder(View itemView) {
            super(itemView);
            fac_code = itemView.findViewById(R.id.pf_header_code);
            fac_date = itemView.findViewById(R.id.pf_header_date);
            fac_time = itemView.findViewById(R.id.pf_header_time);
            fac_kowsardate = itemView.findViewById(R.id.pf_header_kowsardate);
            fac_row = itemView.findViewById(R.id.pf_header_row);
            fac_count = itemView.findViewById(R.id.pf_header_count);
            fac_price = itemView.findViewById(R.id.pf_header_price);
            fac_kowsarcode = itemView.findViewById(R.id.pf_header_kowsarcode);
            fac_detail = itemView.findViewById(R.id.pf_header_detail);
            fac_customer = itemView.findViewById(R.id.pf_header_customer);
            fac_good = itemView.findViewById(R.id.pf_header_good);
            fac_send = itemView.findViewById(R.id.pf_header_send);
            fac_dlt = itemView.findViewById(R.id.pf_header_dlt);
            fac_customer_edit = itemView.findViewById(R.id.pf_header_customer_edit);
            fac_explain_edit = itemView.findViewById(R.id.pf_header_explain_edit);
            fac_excel = itemView.findViewById(R.id.pf_header__xls);
            fac_select = itemView.findViewById(R.id.pf_header_select);
            fac_status = itemView.findViewById(R.id.pf_header_status);
            fac_good_edit = itemView.findViewById(R.id.pf_header_good_edit);

            fac_rltv = itemView.findViewById(R.id.pf_header);
        }
    }


}
