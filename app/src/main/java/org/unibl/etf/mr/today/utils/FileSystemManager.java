package org.unibl.etf.mr.today.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.DisplayMetrics;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public final class FileSystemManager {

    File environmentPicturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    private FileSystemManager() {
        environmentPicturesDirectory.mkdirs();
    }

    private static FileSystemManager Instance = null;

    public static FileSystemManager getInstance() {
        if(Instance == null){
            Instance = new FileSystemManager();
        }

        return Instance;
    }

    public String saveImage(String rootDir, InputStream in){
        File path = new File(environmentPicturesDirectory, rootDir);
        File file = new File(path, "Pic_" + System.currentTimeMillis() + "T.jpg");

        try{
            if(!path.exists()){
                path.mkdirs();
            }

            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytes;

            while((bytes = in.read(buffer)) != -1){
                out.write(buffer, 0, bytes);
            }

            return file.getAbsolutePath();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public File createEmptyFile(File storageDir) throws IOException {
        String filepath = "Pic_" + System.currentTimeMillis() + "T.jpg";
        File newFile = new File(storageDir, filepath);
        return newFile.createNewFile() ? newFile : null;
    }

}
