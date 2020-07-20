package com.test.whatsappstatussaver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.test.whatsappstatussaver.Fragments.ImageFragment;
import com.test.whatsappstatussaver.Models.StatusModel;
import com.test.whatsappstatussaver.R;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
private final List<StatusModel> imageList;
Context context;
ImageFragment imageFragment;

    public ImageAdapter(Context context,List<StatusModel> imageList,ImageFragment imageFragment) {
        this.context=context;
        this.imageList = imageList;
        this.imageFragment=imageFragment;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
       return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        StatusModel statusModel=imageList.get(position);
        holder.imageView_thumbnail.setImageBitmap(statusModel.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView_thumbnail) ImageView imageView_thumbnail;
        @BindView(R.id.btn_savetoGallery) ImageButton imageButton_download;
        public ImageViewHolder(@NonNull View itemView) {

            super(itemView);
            ButterKnife.bind(this,itemView);
            imageView_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StatusModel statusModel=imageList.get(getAdapterPosition());
                    if(statusModel !=null)
                    {
                        imageFragment.watch(statusModel);
                    }
                }
            });
            imageButton_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     imageButton_download.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done));
                    //imageButton_download.setBackgroundResource(R.drawable.ic_done);
                    StatusModel statusModel=imageList.get(getAdapterPosition());
                    if(statusModel !=null)
                    {
                        try {
                            imageFragment.downloadImage(statusModel);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });


        }
    }
}
