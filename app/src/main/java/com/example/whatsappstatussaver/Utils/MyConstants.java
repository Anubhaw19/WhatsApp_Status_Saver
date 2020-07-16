package com.example.whatsappstatussaver.Utils;

import android.os.Environment;

import java.io.File;

public class MyConstants {
    public static final File  STATUS_DIRECTORY= new
            File(Environment.getExternalStorageDirectory()+File.separator+"WhatsApp/Media/.Statuses");
    public static final File  STATUS= new
            File(Environment.getExternalStorageDirectory()+File.separator+"WhatsAppStatusSaver");

    public  static  final String APP_DIRECTORY=Environment.getExternalStorageDirectory() +File.separator+"WhatsAppStatusSaver";
    public static final int THUMBSIZE=128;
}
