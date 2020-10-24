package com.kits.asli.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bixolon.printer.BixolonPrinter;
import com.kits.asli.R;
import com.kits.asli.adapters.Image_info;
import com.kits.asli.model.BluetoothUtil;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.Farsi_number;
import com.kits.asli.model.Good;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class PrinterActivity extends AppCompatActivity {


    //The columns of your printer. We only tried the Bixolon 300 and the Bixolon 200II, so there are the values.
    //    private final int LINE_CHARS = 42 + 22; // Bixolon 300
    private final int LINE_CHARS = 42; // Bixolon 200II


    //Some time to don't flood the printer with new commands. It's fine to wait a little after sending an image to the printer.
    private static final long PRINTING_SLEEP_TIME = 300;

    //The time the printer takes to print the ticket. It makes the print button to be enabled again after this time in millis.
    //Of course, you can get it in an empiric way... :D
    private static final long PRINTING_TIME = 2200;

    //Two constants that some Bixolon printers send, but aren't included in the Bixolon library. Probably some printers can send it? Don't know.
    static final int MESSAGE_START_WORK = Integer.MAX_VALUE - 2;
    static final int MESSAGE_END_WORK = Integer.MAX_VALUE - 3;

    //The core of the monster: managing the Bixolon printer connection lifecycle
    private List<String> pairedPrinters = new ArrayList<>();
    private Boolean connectedPrinter = false;
    private static BixolonPrinter bixolonPrinterApi;

    //View layer things
    private Animation rotation = null; //Caching an animation makes the world a better place to be
    private View layoutLoading;
    private View layoutThereArentPairedPrinters;
    private View layoutPrinterReady;
    private TextView debugTextView = null; //A hidden TextView where you can test things
    private Button printButton = null; //Guess it :P
    private Integer PreFac = 0;
    private DatabaseHelper dbh = new DatabaseHelper(PrinterActivity.this);
    LinearLayoutCompat main_layout;
    LinearLayoutCompat title_layout;
    LinearLayoutCompat boby_good_layout;
    LinearLayoutCompat good_layout;
    LinearLayoutCompat total_layout;
    ViewPager ViewPager, ViewPager_chap, ViewPager_rast;
    private DecimalFormat decimalFormat = new DecimalFormat("0,000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);

        if (rotation == null) {
            rotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
        }


        debugTextView = findViewById(R.id.debug);

        printButton = findViewById(R.id.print);

        layoutLoading = findViewById(R.id.layoutLoading);
        layoutThereArentPairedPrinters = findViewById(R.id.layoutNoExisteImpresora);
        layoutPrinterReady = findViewById(R.id.layoutImpresoraPreparada);

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                printButton.setEnabled(false);
                new Handler().postDelayed(new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        printButton.setEnabled(true);
                    }

                }, PRINTING_TIME);
                Thread t = new Thread() {
                    /** Where the actual print happens. BTW, the easyest code. */
                    public void run() {
                        try {


                            bixolonPrinterApi.setSingleByteFont(BixolonPrinter.CODE_PAGE_FARSI);
                            Thread.sleep(PRINTING_SLEEP_TIME); // Don't strees the printer while printing the Bitmap... it don't like it.

                            intent();
                            ArrayList<Good> goods = dbh.getAllPreFactorRows(PreFac);
                            main_layout = new LinearLayoutCompat(PrinterActivity.this);
                            title_layout = new LinearLayoutCompat(PrinterActivity.this);
                            boby_good_layout = new LinearLayoutCompat(PrinterActivity.this);
                            good_layout = new LinearLayoutCompat(PrinterActivity.this);
                            total_layout = new LinearLayoutCompat(PrinterActivity.this);
                            ViewPager = new ViewPager(PrinterActivity.this);
                            ViewPager_rast = new ViewPager(PrinterActivity.this);
                            ViewPager_chap = new ViewPager(PrinterActivity.this);

                            main_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                            title_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(351, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            boby_good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(352, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(352, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            total_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(353, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

                            main_layout.setOrientation(LinearLayoutCompat.VERTICAL);
                            main_layout.setBackgroundResource(R.color.white);
                            title_layout.setOrientation(LinearLayoutCompat.VERTICAL);
                            good_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);
                            boby_good_layout.setOrientation(LinearLayoutCompat.VERTICAL);
                            total_layout.setOrientation(LinearLayoutCompat.VERTICAL);

                            main_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                            title_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                            good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                            boby_good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                            total_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


                            ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(354, 3));
                            ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
                            ViewPager_rast.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                            ViewPager_rast.setBackgroundResource(R.color.colorPrimaryDark);
                            ViewPager_chap.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                            ViewPager_chap.setBackgroundResource(R.color.colorPrimaryDark);


                            TextView company_tv = new TextView(PrinterActivity.this);
                            company_tv.setText(Farsi_number.PerisanNumber("فاکتور فروش"));
                            company_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            company_tv.setTextSize(16);
                            company_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            company_tv.setGravity(Gravity.CENTER);
                            company_tv.setPadding(0, 0, 0, 20);


                            TextView customername_tv = new TextView(PrinterActivity.this);
                            customername_tv.setText(Farsi_number.PerisanNumber(" نام مشتری :   " + dbh.getFactorCustomer(PreFac)));
                            customername_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            customername_tv.setTextSize(10);
                            customername_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            customername_tv.setGravity(Gravity.RIGHT);
                            customername_tv.setPadding(0, 0, 0, 15);

                            TextView factorcode_tv = new TextView(PrinterActivity.this);
                            factorcode_tv.setText(Farsi_number.PerisanNumber(" کد فاکتور :   " + dbh.getFactorcode(PreFac)));
                            factorcode_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            factorcode_tv.setTextSize(10);
                            factorcode_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            factorcode_tv.setGravity(Gravity.RIGHT);
                            factorcode_tv.setPadding(0, 0, 0, 15);

                            TextView factordate_tv = new TextView(PrinterActivity.this);
                            factordate_tv.setText(Farsi_number.PerisanNumber(" تارخ فاکتور :   " + dbh.getFactordate(PreFac)));
                            factordate_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            factordate_tv.setTextSize(10);
                            factordate_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            factordate_tv.setGravity(Gravity.RIGHT);
                            factordate_tv.setPadding(0, 0, 0, 35);

                            title_layout.addView(company_tv);
                            title_layout.addView(customername_tv);
                            title_layout.addView(factorcode_tv);
                            title_layout.addView(factordate_tv);
                            title_layout.addView(ViewPager);

                            TextView total_amount_tv = new TextView(PrinterActivity.this);
                            total_amount_tv.setText(Farsi_number.PerisanNumber(" تعداد کل:   " + dbh.getFactorSumAmount(PreFac)));
                            total_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            total_amount_tv.setTextSize(14);
                            total_amount_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            total_amount_tv.setGravity(Gravity.RIGHT);
                            total_amount_tv.setPadding(0, 20, 0, 10);


                            TextView total_price_tv = new TextView(PrinterActivity.this);
                            total_price_tv.setText(Farsi_number.PerisanNumber(" قیمت کل : " + decimalFormat.format(dbh.getFactorSum(PreFac)) + " ریال"));
                            total_price_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            total_price_tv.setTextSize(12);
                            total_price_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            total_price_tv.setGravity(Gravity.RIGHT);

                            TextView phone_tv = new TextView(PrinterActivity.this);
                            phone_tv.setText(Farsi_number.PerisanNumber("۰۹۰۱۹۹۹۹۹۸۷\n تلفن سفارشات"));
                            phone_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            phone_tv.setTextSize(16);
                            phone_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            phone_tv.setGravity(Gravity.CENTER);
                            phone_tv.setPadding(0, 35, 0, 35);


                            TextView kowsar_tv = new TextView(PrinterActivity.this);
                            kowsar_tv.setText(Farsi_number.PerisanNumber("گروه نرم افزاری کوثر\n شماره تماس3–66569320"));
                            kowsar_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                            kowsar_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            kowsar_tv.setTextSize(8);
                            kowsar_tv.setGravity(Gravity.CENTER);
                            kowsar_tv.setPadding(0, 20, 0, 100);


                            total_layout.addView(total_amount_tv);
                            total_layout.addView(total_price_tv);
                            total_layout.addView(phone_tv);
                            total_layout.addView(kowsar_tv);

                            good_layout.addView(ViewPager_rast);
                            int j = 0;
                            for (Good g : goods) {
                                j++;
                                LinearLayoutCompat first_layout = new LinearLayoutCompat(PrinterActivity.this);
                                first_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(356, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                                first_layout.setOrientation(LinearLayoutCompat.VERTICAL);

                                LinearLayoutCompat name_detail = new LinearLayoutCompat(PrinterActivity.this);
                                name_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                                name_detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
                                name_detail.setWeightSum(6);
                                name_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

                                TextView radif = new TextView(PrinterActivity.this);
                                radif.setText(Farsi_number.PerisanNumber(String.valueOf(j)));
                                radif.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 5));
                                radif.setTextSize(10);
                                radif.setGravity(Gravity.CENTER);
                                radif.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                radif.setPadding(0, 10, 0, 10);

                                ViewPager ViewPager_goodname = new ViewPager(PrinterActivity.this);
                                ViewPager_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                                ViewPager_goodname.setBackgroundResource(R.color.colorPrimaryDark);
                                TextView good_name_tv = new TextView(PrinterActivity.this);

                                good_name_tv.setText(Farsi_number.PerisanNumber(g.getGoodName()));
                                good_name_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1));
                                good_name_tv.setTextSize(10);
                                good_name_tv.setGravity(Gravity.RIGHT);
                                good_name_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                good_name_tv.setPadding(0, 10, 5, 0);

                                name_detail.addView(radif);
                                name_detail.addView(ViewPager_goodname);
                                name_detail.addView(good_name_tv);

                                LinearLayoutCompat detail = new LinearLayoutCompat(PrinterActivity.this);
                                detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                                detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
                                detail.setWeightSum(9);
                                detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

                                TextView good_price_tv = new TextView(PrinterActivity.this);
                                good_price_tv.setText(Farsi_number.PerisanNumber(decimalFormat.format(g.getPrice())));
                                good_price_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
                                good_price_tv.setTextSize(9);
                                good_price_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                good_price_tv.setGravity(Gravity.CENTER);

                                TextView good_amount_tv = new TextView(PrinterActivity.this);
                                good_amount_tv.setText(Farsi_number.PerisanNumber(String.valueOf(g.getAmount())));
                                good_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
                                good_amount_tv.setTextSize(10);
                                good_amount_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                good_amount_tv.setGravity(Gravity.CENTER);

                                TextView good_totalprice_tv = new TextView(PrinterActivity.this);
                                good_totalprice_tv.setText(Farsi_number.PerisanNumber(decimalFormat.format(g.getAmount() * g.getPrice())));
                                good_totalprice_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
                                good_totalprice_tv.setTextSize(9);
                                good_totalprice_tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                good_totalprice_tv.setPadding(0, 0, 0, 10);
                                good_totalprice_tv.setGravity(Gravity.CENTER);

                                ViewPager ViewPager_sell1 = new ViewPager(PrinterActivity.this);
                                ViewPager_sell1.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                                ViewPager_sell1.setBackgroundResource(R.color.colorPrimaryDark);
                                ViewPager ViewPager_sell2 = new ViewPager(PrinterActivity.this);
                                ViewPager_sell2.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                                ViewPager_sell2.setBackgroundResource(R.color.colorPrimaryDark);

                                detail.addView(good_price_tv);
                                detail.addView(ViewPager_sell1);
                                detail.addView(good_amount_tv);
                                detail.addView(ViewPager_sell2);
                                detail.addView(good_totalprice_tv);

                                ViewPager extra_ViewPager = new ViewPager(PrinterActivity.this);
                                extra_ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, 2));
                                extra_ViewPager.setBackgroundResource(R.color.colorPrimaryDark);

                                ViewPager extra_ViewPager1 = new ViewPager(PrinterActivity.this);
                                extra_ViewPager1.setLayoutParams(new LinearLayoutCompat.LayoutParams(350, 2));
                                extra_ViewPager1.setBackgroundResource(R.color.colorPrimaryDark);


                                first_layout.addView(name_detail);
                                first_layout.addView(extra_ViewPager);
                                first_layout.addView(detail);
                                first_layout.addView(extra_ViewPager1);

                                boby_good_layout.addView(first_layout);


                            }
                            good_layout.addView(boby_good_layout);
                            good_layout.addView(ViewPager_chap);


                            main_layout.addView(title_layout);
                            main_layout.addView(good_layout);
                            main_layout.addView(total_layout);


                            bixolonPrinterApi.printBitmap(loadBitmapFromView(main_layout)
                                    , BixolonPrinter.ALIGNMENT_CENTER
                                    , 400
                                    , 70
                                    , false);
                            Image_info image_info = new Image_info(PrinterActivity.this);
                            image_info.SaveImage_factor(loadBitmapFromView(main_layout), PreFac);

                        } catch (Exception e) {
                            Log.e("ERROR", "Printing", e);
                        }
                    }
                };
                t.start();
            }
        });
        findViewById(R.id.pairPrinter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String mac : pairedPrinters) {
                    BluetoothUtil.unpairMac(mac);
                }
                pairedPrinters.clear();

                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(settingsIntent);
            }
        });

        updateScreenStatus(layoutLoading);

    }

    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        PreFac = data.getInt("PreFac");
    }

    private void updateScreenStatus(View viewToShow) {
        if (viewToShow == layoutLoading) {

            layoutLoading.setVisibility(View.VISIBLE);
            layoutThereArentPairedPrinters.setVisibility(View.GONE);
            layoutPrinterReady.setVisibility(View.GONE);
            iconLoadingStart();
        } else if (viewToShow == layoutThereArentPairedPrinters) {
            layoutLoading.setVisibility(View.GONE);
            layoutThereArentPairedPrinters.setVisibility(View.VISIBLE);
            layoutPrinterReady.setVisibility(View.GONE);
            iconLoadingStop();
        } else if (viewToShow == layoutPrinterReady) {
            layoutLoading.setVisibility(View.GONE);
            layoutThereArentPairedPrinters.setVisibility(View.GONE);
            layoutPrinterReady.setVisibility(View.VISIBLE);
            iconLoadingStop();
        }
        updatePrintButtonState();
    }

    PrinterActivity.PairWithPrinterTask task = null;


    @Override
    protected void onResume() {
        super.onResume();

        bixolonPrinterApi = new BixolonPrinter(this, handler, null);
        task = new PrinterActivity.PairWithPrinterTask();
        task.execute();
        updatePrintButtonState();
        BluetoothUtil.startBluetooth();
    }


    @Override
    protected void onPause() {

        if (task != null) {
            task.stop();
            task = null;
        }
        if (bixolonPrinterApi != null) {
            bixolonPrinterApi.disconnect();
        }
        super.onPause();
    }

    private void updatePrintButtonState() {
        printButton.setEnabled(connectedPrinter != null && connectedPrinter == true);
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            // Log.i("Handler", msg.what + " " + msg.arg1 + " " + msg.arg2);
            switch (msg.what) {
                case BixolonPrinter.MESSAGE_STATE_CHANGE:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_STATE_CHANGE");
                    switch (msg.arg1) {
                        case BixolonPrinter.STATE_CONNECTED:
                            updateScreenStatus(layoutPrinterReady);
                            Log.i("Handler", "BixolonPrinter.STATE_CONNECTED");
                            connectedPrinter = true;
                            updateScreenStatus(layoutPrinterReady);
                            break;
                        case BixolonPrinter.STATE_CONNECTING:
                            updateScreenStatus(layoutLoading);
                            Log.i("Handler", "BixolonPrinter.STATE_CONNECTING");
                            connectedPrinter = false;
                            break;
                        case BixolonPrinter.STATE_NONE:
                            updateScreenStatus(layoutLoading);
                            Log.i("Handler", "BixolonPrinter.STATE_NONE");
                            connectedPrinter = false;
                            break;
                    }
                    break;
                case BixolonPrinter.MESSAGE_WRITE:
                    switch (msg.arg1) {
                        case BixolonPrinter.PROCESS_SET_SINGLE_BYTE_FONT:
                            Log.i("Handler", "BixolonPrinter.PROCESS_SET_SINGLE_BYTE_FONT");
                            break;
                        case BixolonPrinter.PROCESS_SET_DOUBLE_BYTE_FONT:
                            Log.i("Handler", "BixolonPrinter.PROCESS_SET_DOUBLE_BYTE_FONT");
                            break;
                        case BixolonPrinter.PROCESS_DEFINE_NV_IMAGE:
                            Log.i("Handler", "BixolonPrinter.PROCESS_DEFINE_NV_IMAGE");
                            break;
                        case BixolonPrinter.PROCESS_REMOVE_NV_IMAGE:
                            Log.i("Handler", "BixolonPrinter.PROCESS_REMOVE_NV_IMAGE");
                            break;
                        case BixolonPrinter.PROCESS_UPDATE_FIRMWARE:
                            Log.i("Handler", "BixolonPrinter.PROCESS_UPDATE_FIRMWARE");
                            break;
                    }
                    break;
                case BixolonPrinter.MESSAGE_READ:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_READ");
                    break;
                case BixolonPrinter.MESSAGE_DEVICE_NAME:
                    debugTextView.setText(msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME));
                    Log.i("Handler", "BixolonPrinter.MESSAGE_DEVICE_NAME - " + msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME));
                    break;
                case BixolonPrinter.MESSAGE_TOAST:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_TOAST - " + msg.getData().getString("toast"));
                    // Toast.makeText(getApplicationContext(), msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
                    break;
                // The list of paired printers
                case BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET");
                    if (msg.obj == null) {
                        updateScreenStatus(layoutThereArentPairedPrinters);
                    } else {
                        Set<BluetoothDevice> pairedDevices = (Set<BluetoothDevice>) msg.obj;
                        for (BluetoothDevice device : pairedDevices) {
                            if (!pairedPrinters.contains(device.getAddress())) {
                                pairedPrinters.add(device.getAddress());
                            }
                            if (pairedPrinters.size() == 1) {
                                PrinterActivity.bixolonPrinterApi.connect(pairedPrinters.get(0));
                            }
                        }
                    }
                    break;
                case BixolonPrinter.MESSAGE_PRINT_COMPLETE:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_PRINT_COMPLETE");
                    break;
                case BixolonPrinter.MESSAGE_COMPLETE_PROCESS_BITMAP:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_COMPLETE_PROCESS_BITMAP");
                    break;
                case MESSAGE_START_WORK:
                    Log.i("Handler", "MESSAGE_START_WORK");
                    break;
                case MESSAGE_END_WORK:
                    Log.i("Handler", "MESSAGE_END_WORK");
                    break;
                case BixolonPrinter.MESSAGE_USB_DEVICE_SET:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_USB_DEVICE_SET");
                    if (msg.obj == null) {
                        Toast.makeText(getApplicationContext(), "No connected device", Toast.LENGTH_SHORT).show();
                    } else {
                        // DialogManager.showUsbDialog(MainActivity.this,
                        // (Set<UsbDevice>) msg.obj, mUsbReceiver);
                    }
                    break;
                case BixolonPrinter.MESSAGE_NETWORK_DEVICE_SET:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_NETWORK_DEVICE_SET");
                    if (msg.obj == null) {
                        Toast.makeText(getApplicationContext(), "No connectable device", Toast.LENGTH_SHORT).show();
                    }
                    // DialogManager.showNetworkDialog(PrintingActivity.this, (Set<String>) msg.obj);
                    break;
            }
        }
    };


    class PairWithPrinterTask extends AsyncTask<Void, Void, Void> {

        boolean running = true;

        public PairWithPrinterTask() {
        }

        public void stop() {
            running = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (running) {
                if (connectedPrinter == null || connectedPrinter == false) {
                    publishProgress();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        int action = 0;

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (action < 20) {
                bixolonPrinterApi.findBluetoothPrinters();
                action++;
            } else {
                bixolonPrinterApi.disconnect();
                action = 0;
            }
        }
    }

    private void printText(String textToPrint) {
        printText(textToPrint, BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A);
    }


    private void printText(String textToPrint, int alignment) {
        printText(textToPrint, alignment, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A);
    }

    private void printText(String textToPrint, int alignment, int attribute) {
        if (textToPrint.length() <= LINE_CHARS) {
            bixolonPrinterApi.printText(textToPrint, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);
        } else {
            String textToPrintInNextLine = null;
            while (textToPrint.length() > LINE_CHARS) {
                textToPrintInNextLine = textToPrint.substring(0, LINE_CHARS);
                textToPrintInNextLine = textToPrint.substring(0, LINE_CHARS);
                textToPrintInNextLine = textToPrintInNextLine.substring(0, textToPrintInNextLine.lastIndexOf(" ")).trim() + "\n";
                bixolonPrinterApi.printText(textToPrintInNextLine, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);
                textToPrint = textToPrint.substring(textToPrintInNextLine.length(), textToPrint.length());
            }
            bixolonPrinterApi.printText(textToPrint, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);
        }
    }

    /**
     * Print the common two columns ticket style text. Label+Value.
     *
     * @param leftText
     * @param rightText
     */
    private void printTextTwoColumns(String leftText, String rightText) {
        if (leftText.length() + rightText.length() + 1 > LINE_CHARS) { // If two Strings cannot fit in same line
            int alignment = BixolonPrinter.ALIGNMENT_LEFT;
            int attribute = 0;
            attribute |= BixolonPrinter.TEXT_ATTRIBUTE_FONT_C;
            bixolonPrinterApi.printText(leftText, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);
            alignment = BixolonPrinter.ALIGNMENT_RIGHT;
            attribute = 0;
            attribute |= BixolonPrinter.TEXT_ATTRIBUTE_FONT_C;
            bixolonPrinterApi.printText(rightText, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);
        } else {
            int padding = LINE_CHARS - leftText.length() - rightText.length();
            String paddingChar = " ";
            for (int i = 0; i < padding; i++) {
                paddingChar = paddingChar.concat(" ");
            }
            int alignment = BixolonPrinter.ALIGNMENT_LEFT;
            int attribute = 0;
            attribute |= BixolonPrinter.TEXT_ATTRIBUTE_FONT_C;
            bixolonPrinterApi.printText(leftText + paddingChar + rightText, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);
        }
    }

    boolean animated = false;

    public void iconLoadingStart() {

        View loading = findViewById(R.id.loading);
        if (loading != null && !animated) {
            loading.startAnimation(rotation);
            loading.setVisibility(View.VISIBLE);
        }


        if (loading == null) {
            setProgressBarIndeterminateVisibility(Boolean.TRUE);
        }
        animated = true;
    }


    public void iconLoadingStop() {
        setProgressBarIndeterminateVisibility(Boolean.FALSE);
        View loading = findViewById(R.id.loading);
        if (loading != null) {
            loading.clearAnimation();
            loading.setVisibility(View.INVISIBLE);
        }
        animated = false;
    }

    private Bitmap createBitmapFromLayout(View tv) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tv.measure(spec, spec);
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(tv.getMeasuredWidth(), tv.getMeasuredWidth(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate((-tv.getScrollX()), (-tv.getScrollY()));
        tv.draw(c);
        return b;
    }

    public Bitmap loadBitmapFromView(View v) {
        v.measure(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.draw(c);
        return b;
    }


}