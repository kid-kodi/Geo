package com.aqs.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kodi on 3/12/2016.
 */
public class FileHelper {

    private Context cxt;
    public FileHelper(Context cxt){
        this.cxt = cxt;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /*create txt file*/
    public void createFile(String filename, String data) {
        String fullPath = Environment.getExternalStorageDirectory() + "/pacerelle+";
        try
        {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream fOut = null;
            File file = new File(fullPath, filename+".txt");
            if(file.exists())
                file.delete();
            file.createNewFile();
            fOut = new FileOutputStream(file);
            fOut.write(data.getBytes());
            fOut.close();
        }
        catch (Exception e)
        {
            Log.e("saveToExternalStorage()", e.getMessage());
        }
    }

    public String saveImageToExternalStorage(Bitmap image) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pacerelle+";

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_" + timeStamp + "_.png";

        try
        {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream fOut = null;
            File file = new File(fullPath, imageFileName);
            if(file.exists())
                file.delete();
            file.createNewFile();
            fOut = new FileOutputStream(file);
            // 100 means no compression, the lower you go, the stronger the compression
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        }
        catch (Exception e)
        {
            Log.e("saveToExternalStorage()", e.getMessage());
        }

        return imageFileName;
    }

    /*create directory*/
    public String createDir(){
        String fileAbsolutePath = "";
        File folder = new File(cxt.getExternalFilesDir(
                null), "pacerelle+");
        boolean success = true;

        fileAbsolutePath = folder.getAbsolutePath();

        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
            Toast.makeText(cxt, "success " + fileAbsolutePath, Toast.LENGTH_SHORT).show();
        } else {
            // Do something else on failure
            Toast.makeText(cxt,"already "+fileAbsolutePath,Toast.LENGTH_SHORT).show();
        }

        return fileAbsolutePath;
    }
}
