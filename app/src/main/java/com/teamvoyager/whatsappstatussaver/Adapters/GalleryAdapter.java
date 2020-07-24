package com.teamvoyager.whatsappstatussaver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamvoyager.whatsappstatussaver.Gallery;
import com.teamvoyager.whatsappstatussaver.Models.StatusModel;
import com.teamvoyager.whatsappstatussaver.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {
    private final List<StatusModel> imageList;
    Context context;
    Gallery gallery;
    ArrayList<String> selected_items = new ArrayList<String>();
    boolean test = false;

    public GalleryAdapter(Context context, List<StatusModel> imageList, Gallery gallery) {
        this.context = context;
        this.imageList = imageList;
        this.gallery = gallery;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        StatusModel statusModel = imageList.get(position);
        if (statusModel != null) {
            if (statusModel.getPath().contains(".mp4")) {
                holder.imageView_play.setVisibility(View.VISIBLE);
            } else {
                holder.imageView_play.setVisibility(View.INVISIBLE);
            }
        }
        holder.imageView_thumbnail.setImageBitmap(statusModel.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView_thumbnail)
        ImageView imageView_thumbnail;
        @BindView(R.id.btn_savetoGallery)
        ImageButton imageButton_download;
        @BindView(R.id.imageView_play)
        ImageView imageView_play;
        @BindView(R.id.checkBox)
        CheckBox checkbox;

        public ImageViewHolder(@NonNull View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox.setChecked(true);
                    StatusModel statusModel = imageList.get(getAdapterPosition());
                    if(!selected_items.contains(statusModel.getPath()))
                    {
                        selected_items.add(statusModel.getPath());
                        checkbox.setVisibility(View.VISIBLE);
                        checkbox.setChecked(true);
                        gallery.selectedItems(selected_items);
                        Toast.makeText(context,"selected items :"+selected_items.size(),Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        checkbox.setVisibility(View.INVISIBLE);
                        checkbox.setChecked(false);
                        selected_items.remove(statusModel.getPath());
                        if(selected_items.size()==0)
                        {
                            test=false;
                        }
                        gallery.selectedItems(selected_items);
                        Toast.makeText(context,"selected items :"+selected_items.size(),Toast.LENGTH_SHORT).show();
                    }

                }
            });
            imageView_thumbnail.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    checkbox.setVisibility(View.VISIBLE);
                    checkbox.setChecked(true);
                    StatusModel statusModel = imageList.get(getAdapterPosition());
                    if (statusModel != null) {
                        selected_items.add(statusModel.getPath());
                        gallery.selectedItems(selected_items);
                    }
                    test = true;

                    return true;
                }
            });

            imageView_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StatusModel statusModel = imageList.get(getAdapterPosition());
                    if(!test)
                    {
                        if (statusModel != null && !test) {
                            gallery.watch(statusModel);
                        }
                    }


                    else
                    {
                        if(!selected_items.contains(statusModel.getPath()))
                        {
                            selected_items.add(statusModel.getPath());
                            checkbox.setVisibility(View.VISIBLE);
                            checkbox.setChecked(true);
                            gallery.selectedItems(selected_items);
                            Toast.makeText(context,"selected items :"+selected_items.size(),Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            checkbox.setVisibility(View.INVISIBLE);
                            checkbox.setChecked(false);
                            selected_items.remove(statusModel.getPath());
                            if(selected_items.size()==0)
                            {
                                test=false;
                            }
                            gallery.selectedItems(selected_items);
                            Toast.makeText(context,"selected items :"+selected_items.size(),Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
            imageButton_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    StatusModel statusModel = imageList.get(getAdapterPosition());
                    if (statusModel != null) {
                        gallery.share(statusModel);
                    }

                }
            });


        }
    }
}
