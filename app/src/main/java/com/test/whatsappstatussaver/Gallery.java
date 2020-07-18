package com.test.whatsappstatussaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.test.whatsappstatussaver.Adapters.GalleryAdapter;
import com.test.whatsappstatussaver.Models.StatusModel;
import com.test.whatsappstatussaver.Utils.MyConstants;

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
        else
        {
            Toast.makeText(Gallery.this,"Nothing is Saved",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
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
            Toast.makeText(this,"share",Toast.LENGTH_SHORT).show();

        Uri imgUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", statusModel.getFile());
        Intent Intent = new Intent(android.content.Intent.ACTION_SEND);
        Intent.putExtra(Intent.EXTRA_STREAM, imgUri);
        Intent.setType("image/jpeg");
        Intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


}