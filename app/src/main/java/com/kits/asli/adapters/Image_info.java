package com.kits.asli.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.kits.asli.R;

import java.io.File;
import java.io.FileOutputStream;

public class Image_info {


    byte[] imageByteArray = null;
    private final Context mContext;
    private String app_img_name;

    public Image_info(Context mContext) {
        this.mContext = mContext;
        this.app_img_name = mContext.getString(R.string.app_img_name);
    }


    public void SaveImage(Bitmap finalBitmap, Integer code) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/Kowsar/" + app_img_name + "/");
        myDir.mkdirs();

        String fname = code + ".jpg";
        File file = new File(myDir, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Boolean Imgae_exist(String code) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File imagefile = new File(root + "/Kowsar/" + app_img_name + "/" + code + ".jpg");
        Log.e("kowsar_imagefile.exists", imagefile.exists() + "");
        return imagefile.exists();

    }


}
