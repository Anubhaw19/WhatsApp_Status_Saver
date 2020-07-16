package com.example.whatsappstatussaver;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappstatussaver.Adapters.GalleryAdapter;
import com.example.whatsappstatussaver.Adapters.ImageAdapter;
import com.example.whatsappstatussaver.Fragments.ImageFragment;
import com.example.whatsappstatussaver.Models.StatusModel;
import com.example.whatsappstatussaver.Utils.MyConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Gallery extends AppCompatActivity {
    @BindView(R.id.recylerView)
    RecyclerView recyclerView;
    @BindView(R.id.proBar)
    ProgressBar progressBar;
    @BindView(R.id.gallery_toolbar)
    Toolbar toolbar;

    ArrayList<StatusModel> imageModelArrayList;
    Handler handler = new Handler();

    GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        ButterKnife.bind(this);
        toolbar.setTitle("Saved Images-Videos");
        imageModelArrayList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        getStatus();
    }
    private void getStatus() { //this is the code for searching for status files.
        if (MyConstants.STATUS.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //listing all status files present in the [WhatsApp/Media/.Statuses] folder.
                    File[] statusFiles = MyConstants.STATUS.listFiles();
                    if(statusFiles.length==0)
                    {
                        Toast.makeText(Gallery.this,"Nothing is Saved",Toast.LENGTH_LONG).show();

                    }

                    if (statusFiles != null && statusFiles.length > 0) {
                        Arrays.sort(statusFiles);
                        for (final File status : statusFiles) {
                            StatusModel statusModel = new StatusModel(status, status.getName(), status.getAbsolutePath());

                            statusModel.setThumbnail(getThumbNail(statusModel));

                            //if (!statusModel.isVideo()) {
                                imageModelArrayList.add(statusModel);
                          //  }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                galleryAdapter = new GalleryAdapter(Gallery.this, imageModelArrayList, Gallery.this);
                                recyclerView.setAdapter(galleryAdapter);
                                galleryAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Gallery.this, "directory not found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private Bitmap getThumbNail(StatusModel statusModel) {
        if (statusModel.isVideo()) {/**[MediaStore.Video.Thumbnails.MICRO_KIND] this is deprecated*/
            return ThumbnailUtils.createVideoThumbnail(String.valueOf(statusModel.getFile().getAbsoluteFile()),
                    MediaStore.Video.Thumbnails.MICRO_KIND);
        } else {
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(statusModel.getFile().getAbsolutePath()),
                    MyConstants.THUMBSIZE, MyConstants.THUMBSIZE);
        }
    }

    public void click(StatusModel statusModel)
    {
            Toast.makeText(this,"click",Toast.LENGTH_SHORT).show();
    }

}