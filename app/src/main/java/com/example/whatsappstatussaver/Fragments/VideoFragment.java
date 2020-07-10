package com.example.whatsappstatussaver.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
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
import com.example.whatsappstatussaver.Adapters.VideoAdapter;
import com.example.whatsappstatussaver.Models.StatusModel;
import com.example.whatsappstatussaver.R;
import com.example.whatsappstatussaver.Utils.MyConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoFragment extends Fragment {

    @BindView(R.id.recylerView_vedio) RecyclerView recyclerView;
    @BindView(R.id.proBar_vedio) ProgressBar progressBar;
    ArrayList<StatusModel> videoModelArrayList;
    Handler handler=new Handler();
    VideoAdapter videoAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_vedio,container,false);
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        videoModelArrayList=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        getStatusVideo();
    }

    private void getStatusVideo() { //this is the code for searching for status files.(almost same as searching image.)

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

                            if(statusModel.isVideo())
                            {
                                videoModelArrayList.add(statusModel);
                                Log.d("inside video=", "run: status model ");
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                videoAdapter=new VideoAdapter(getContext(),videoModelArrayList, VideoFragment.this);
                                recyclerView.setAdapter(videoAdapter);
                                videoAdapter.notifyDataSetChanged();
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
