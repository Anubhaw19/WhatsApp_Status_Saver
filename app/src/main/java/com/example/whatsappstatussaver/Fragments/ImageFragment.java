package com.example.whatsappstatussaver.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappstatussaver.Adapters.ImageAdapter;
import com.example.whatsappstatussaver.Models.StatusModel;
import com.example.whatsappstatussaver.R;
import com.example.whatsappstatussaver.Utils.MyConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageFragment extends Fragment {

    @BindView(R.id.recylerView) RecyclerView recyclerView;
    @BindView(R.id.proBar) ProgressBar progressBar;

    ArrayList<StatusModel> imageModelArrayList;
    Handler handler=new Handler();
    ImageAdapter imageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
View view=inflater.inflate(R.layout.fragment_image,container,false);
return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        imageModelArrayList=new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        getStatus();
    }

    private void getStatus() { //this is the code for searching for status files.
        if(MyConstants.STATUS_DIRECTORY.exists())
        {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //listing all status files present in the [WhatsApp/Media/.Statuses] folder.
                File[] statusFiles= MyConstants.STATUS_DIRECTORY.listFiles();

                if(statusFiles !=null && statusFiles.length>0)
                {
                    Arrays.sort(statusFiles);
                    for(final File status : statusFiles)
                    {
                        StatusModel statusModel=new StatusModel(status,status.getName(),status.getAbsolutePath());

                        statusModel.setThumbnail(getThumbNail(statusModel));

                        if(!statusModel.isVideo())
                        {
                            imageModelArrayList.add(statusModel);
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            imageAdapter=new ImageAdapter(getContext(),imageModelArrayList,ImageFragment.this);
                            recyclerView.setAdapter(imageAdapter);
                            imageAdapter.notifyDataSetChanged();
                        }
                    });
                }
                else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(),"directory not found",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
        }
    }

    private Bitmap getThumbNail(StatusModel statusModel) {
        if(statusModel.isVideo())
        {/**[MediaStore.Video.Thumbnails.MICRO_KIND] this is deprecated*/
            return ThumbnailUtils.createVideoThumbnail(String.valueOf(statusModel.getFile().getAbsoluteFile()),
                    MediaStore.Video.Thumbnails.MICRO_KIND);
        }
        else
        {
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(statusModel.getFile().getAbsolutePath()),
                    MyConstants.THUMBSIZE,MyConstants.THUMBSIZE);
        }
    }
}
