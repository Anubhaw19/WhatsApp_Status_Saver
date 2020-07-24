package com.teamvoyager.whatsappstatussaver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamvoyager.whatsappstatussaver.Fragments.VideoFragment;
import com.teamvoyager.whatsappstatussaver.Models.StatusModel;
import com.teamvoyager.whatsappstatussaver.R;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>  {
    private final List<StatusModel> videoList;
    Context context;
    VideoFragment videoFragment;

    public VideoAdapter(Context context,List<StatusModel> videoList,VideoFragment videoFragment) {
        this.context=context;
        this.videoList = videoList;
        this.videoFragment=videoFragment;
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        return new VideoAdapter.VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        StatusModel statusModel=videoList.get(position);
        holder.imageView_thumbnail.setImageBitmap(statusModel.getThumbnail());
        if(statusModel !=null)
        {
            holder.imageView_play.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView_thumbnail) ImageView imageView_thumbnail;
        @BindView(R.id.imageView_play) ImageView imageView_play;
        @BindView(R.id.btn_savetoGallery)
        Button imageButton_download;
        public VideoViewHolder(@NonNull View itemView) {

            super(itemView);
            ButterKnife.bind(this,itemView);
            imageView_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StatusModel statusModel=videoList.get(getAdapterPosition());
                    if(statusModel !=null)
                    {
                        videoFragment.watch(statusModel);
                    }
                }
            });
            imageButton_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StatusModel statusModel=videoList.get(getAdapterPosition());
                    if(statusModel !=null)
                    {
                        try {
                            videoFragment.downloadVideo(statusModel);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
    }
}
