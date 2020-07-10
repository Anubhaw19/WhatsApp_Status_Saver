package com.example.whatsappstatussaver.Models;

import android.graphics.Bitmap;

import java.io.File;

public class StatusModel {
    private  static  final String Mp4=".mp4";
    private final  File file;
    private Bitmap thumbnail;
    private  final String title,path;
    private boolean isVideo;

    public StatusModel(File file, String title, String path) {
        this.file = file;
        this.title = title;
        this.path = path;
        this.isVideo=file.getName().endsWith(Mp4);
    }

    public File getFile() {
        return file;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }
}
